package nl.devon.pickles.jdbc;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import nl.devon.pickles.steps.DelayedVerification;

public class JdbcDelayedVerificationStoreShould {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private Connection connection;

	@Before
	public void givenInMemoryDatabase() throws SQLException, LiquibaseException {
		connection = DriverManager.getConnection("jdbc:h2:mem:delayed_verification");
		Database database = DatabaseFactory.getInstance()
				.findCorrectDatabaseImplementation(new JdbcConnection(connection));
		Liquibase liquibase = new Liquibase("liquibase/delayed-verification.xml", new ClassLoaderResourceAccessor(),
				database);
		liquibase.update("");

		liquibase = new Liquibase("nl/devon/pickles/jdbc/delayed-verification-testdata.xml",
				new ClassLoaderResourceAccessor(), database);
		liquibase.update("");
	}

	@After
	public void closeConnection() throws SQLException {
		connection.close();
	}

	@Test
	public void getDelayedVerificationById() {
		String validId = "2a5b82fd-6436-429b-8793-3cdc28d47600";
		DelayedVerification aVerification = getBy(validId);

		assertThat(aVerification.getId(), is(validId));
		assertThat(aVerification.getScenarioChecksum(), is("checksum for scenario 1"));
	}

	@Test
	public void getAllFieldsFromDatabase() {
		String validId = "73dd7faa-8685-4e81-8f26-6e8757a2ada1";
		DelayedVerification aVerification = getBy(validId);

		assertThat(aVerification.getId(), is(validId));
		assertThat(aVerification.getScenarioChecksum(), is("checksum for scenario 2"));
		assertThat(aVerification.getFeature(), is("Test feature 1"));
		assertThat(aVerification.getCreatedAt(), is(DateTime.parse("2016-12-26T00:00:00")));
		assertThat(aVerification.getVerifyAt(), is(DateTime.parse("2016-12-26T00:00:00")));
		assertThat(aVerification.getProcessedAt(), is(DateTime.parse("2016-12-26T12:00:00")));
	}

	@Test
	public void signalWhenDelayedVerificationNotFound() {
		thrown.expect(DelayedVerificationStoreException.class);
		thrown.expectMessage("No Delayed Verification found for id=non existing delayed verification id");

		getBy("non existing delayed verification id");
	}

	@Test
	public void getAllForChecksum() {
		String checksumWithOneEntry = "checksum for scenario 1";
		List<DelayedVerification> results = getAllForChecksum(checksumWithOneEntry);
		assertThat(results, hasSize(1));

		String checksumWithThreeEntries = "checksum for scenario 2";
		results = getAllForChecksum(checksumWithThreeEntries);
		assertThat(results, hasSize(3));
	}

	@Test
	public void getAllToBeVerifiedForChecksum() {
		String checksumWithThreeEntriesOneVerified = "checksum for scenario 2";
		List<DelayedVerification> results = getAllForChecksum(checksumWithThreeEntriesOneVerified);
		assertThat(results, hasSize(3));

		results = getAllToVerify(checksumWithThreeEntriesOneVerified);
		assertThat(results, hasSize(2));
	}

	@Test
	public void saveDelayedVerification() {
		DelayedVerification aVerification = new DelayedVerification(DateTime.parse("2016-12-26T00:00:00"),
				"save checksum", "save feature");
		save(aVerification);

		DelayedVerification verificationFromDb = getBy(aVerification.getId());
		assertThat(verificationFromDb, notNullValue());
	}

	@Test
	public void updateDelayedVerification() {
		String validId = "b4d08bf9-cb55-497d-b39e-5ada14462107";
		DelayedVerification original = getBy(validId);
		DelayedVerification verification = new DelayedVerification(validId, DateTime.now().minusHours(1),
				DateTime.now(), DateTime.now().plusHours(1), "updated checksum", "updated feature");
		update(verification);

		DelayedVerification updated = getBy(validId);
		assertThat(updated.getCreatedAt(), is(not(original.getCreatedAt())));
		assertThat(updated.getCreatedAt(), is(verification.getCreatedAt()));

		assertThat(updated.getVerifyAt(), is(not(original.getVerifyAt())));
		assertThat(updated.getVerifyAt(), is(verification.getVerifyAt()));

		assertThat(updated.getProcessedAt(), is(not(original.getProcessedAt())));
		assertThat(updated.getProcessedAt(), is(verification.getProcessedAt()));
	}

	private DelayedVerification getBy(String id) {
		JdbcDelayedVerificationStore store = new JdbcDelayedVerificationStore();
		return store.read(id);
	}

	private List<DelayedVerification> getAllForChecksum(String checksum) {
		JdbcDelayedVerificationStore store = new JdbcDelayedVerificationStore();
		return store.readAllForChecksum(checksum);
	}

	private List<DelayedVerification> getAllToVerify(String checksum) {
		JdbcDelayedVerificationStore store = new JdbcDelayedVerificationStore();
		return store.readAllToVerify(checksum);
	}

	private void save(DelayedVerification verification) {
		JdbcDelayedVerificationStore store = new JdbcDelayedVerificationStore();
		store.create(verification);
	}

	private void update(DelayedVerification verification) {
		JdbcDelayedVerificationStore store = new JdbcDelayedVerificationStore();
		store.update(verification);
	}
}

package nl.devon.cucumber.jdbc;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
import nl.devon.DelayedVerification;

public class JdbcDelayedVerificationStoreShould {

	/*
	 * getAll(checksum)
	 *
	 * getAllToBeVerified(checksum)
	 *
	 * getAndSetVerifiedById(id) + update field
	 */

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

		liquibase = new Liquibase("nl/devon/cucumber/jdbc/delayed-verification-testdata.xml",
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
	public void shouldSignalWhenDelayedVerificationNotFound() {
		thrown.expect(DelayedVerificationStoreException.class);
		thrown.expectMessage("No Delayed Verification found for id=non existing delayed verification id");

		getBy("non existing delayed verification id");
	}

	@Test
	public void shouldSaveDelayedVerification() {
		DelayedVerification aVerification = new DelayedVerification(DateTime.parse("2016-12-26T00:00:00"),
				"save checksum", "save feature");
		save(aVerification);

		DelayedVerification verificationFromDb = getBy(aVerification.getId());
		assertThat(verificationFromDb, notNullValue());
	}

	private DelayedVerification getBy(String id) {
		JdbcDelayedVerificationStore store = new JdbcDelayedVerificationStore();
		DelayedVerification delayedVerification = store.get(id);
		return delayedVerification;
	}

	private void save(DelayedVerification verification) {
		JdbcDelayedVerificationStore store = new JdbcDelayedVerificationStore();
		store.save(verification);
	}
}

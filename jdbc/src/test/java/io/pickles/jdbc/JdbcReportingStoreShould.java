package io.pickles.jdbc;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
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

import io.pickles.preprocessor.model.TestRun;
import io.pickles.reporting.ReportingStore;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

public class JdbcReportingStoreShould {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private Connection connection;

	@Before
	public void givenInMemoryDatabase() throws SQLException, LiquibaseException {
		connection = DriverManager.getConnection("jdbc:h2:mem:pickles");
		Database database = DatabaseFactory.getInstance()
				.findCorrectDatabaseImplementation(new JdbcConnection(connection));
		Liquibase liquibase = new Liquibase("liquibase/pickles.xml", new ClassLoaderResourceAccessor(), database);
		liquibase.update("");

		liquibase = new Liquibase("io/pickles/jdbc/reporting-testdata.xml", new ClassLoaderResourceAccessor(),
				database);
		liquibase.update("");
	}

	@After
	public void closeConnection() throws SQLException {
		connection.close();
	}

	@Test
	public void getTestRunByName() {
		String validName = "ROBERT";
		TestRun run = getRunBy(validName);

		assertThat(run.getName(), is(validName));
	}

	@Test
	public void saveTestRun() {
		TestRun run = new TestRun("PICKLES", "DESCRIPTION", DateTime.now(), DateTime.now().plusMinutes(5));
		save(run);

		assertThat(getRunBy("PICKLES").getId(), notNullValue());
	}

	private TestRun getRunBy(String name) {
		ReportingStore store = new PropertiesReportingStore();
		return store.getTestRunBy(name);
	}

	private void save(TestRun run) {
		ReportingStore store = new PropertiesReportingStore();
		store.createTestRun(run);
	}
}

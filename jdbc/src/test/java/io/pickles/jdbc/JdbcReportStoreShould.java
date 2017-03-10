package io.pickles.jdbc;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.pickles.preprocessor.model.FeatureModel;
import io.pickles.preprocessor.model.TestRun;
import io.pickles.reporting.ReportStore;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

public class JdbcReportStoreShould {

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

		liquibase = new Liquibase("io/pickles/jdbc/report-testdata.xml", new ClassLoaderResourceAccessor(), database);
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
	public void getFeatureById() {
		FeatureModel model;
		ReportStore store = new PropertiesReportStore();
		model = store.readFeature(999999);

		assertThat(model.getId(), equalTo(999999));
	}

	@Test
	public void getFeatureByIdWithNullJson() {
		thrown.expect(ReportingStoreException.class);
		thrown.expectMessage("Feature without json");
		ReportStore store = new PropertiesReportStore();
		store.readFeature(999998);
	}

	private TestRun getRunBy(String name) {
		ReportStore store = new PropertiesReportStore();
		return store.readTestRun(name);
	}
}

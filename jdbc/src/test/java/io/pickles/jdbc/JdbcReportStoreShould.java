package io.pickles.jdbc;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.iterableWithSize;
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

import io.pickles.model.FeatureModel;
import io.pickles.model.ScenarioModel;
import io.pickles.model.TestRun;
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

		liquibase = new Liquibase("liquibase/report-testdata.xml", new ClassLoaderResourceAccessor(), database);
		liquibase.update("");
	}

	@After
	public void closeConnection() throws SQLException {
		connection.close();
	}

	@Test
	public void getTestRunByName() {
		String validName = "ROBERT";
		TestRun run = readTestRun(validName);

		assertThat(run.getName(), is(validName));
	}

	@Test
	public void signalNoTestRunForName() {
		thrown.expect(ReportStoreException.class);
		thrown.expectMessage("No single Test Run found for name=invalid name");
		readTestRun("invalid name");
	}

	@Test
	public void signalNoTestRunForId() {
		thrown.expect(ReportStoreException.class);
		thrown.expectMessage("No single Test Run found for id=-1");
		readTestRun(-1);
	}

	@Test
	public void getTestRunById() {
		Integer id = 999;
		TestRun run = readTestRun(id);

		assertThat(run.getId(), is(id));
	}

	@Test
	public void getTestRunsByDateTime() {
		DateTime startDate = DateTime.parse("2017-03-01");

		List<TestRun> runs = readTestRuns(startDate, DateTime.parse("2017-03-02"));
		assertThat(runs, iterableWithSize(0));

		runs = readTestRuns(startDate, DateTime.parse("2017-03-10"));
		assertThat(runs, iterableWithSize(1));

		runs = readTestRuns(startDate, DateTime.parse("2017-03-11"));
		assertThat(runs, iterableWithSize(3));
	}

	@Test
	public void getFeatureById() {
		FeatureModel model = readFeature(999);

		assertThat(model.getId(), equalTo(999));
		assertThat(model.getScenarios(), iterableWithSize(1));
		assertThat(model.getScenario(0).getSteps(), iterableWithSize(2));
	}

	@Test
	public void signalNoJsonForFeature() {
		thrown.expect(ReportStoreException.class);
		thrown.expectMessage("Feature without json");
		ReportStore store = new PropertiesReportStore();
		store.readFeature(999999);
	}

	@Test
	public void getFeaturesForTestRuns() {
		DateTime startDate = DateTime.parse("2017-03-09");

		List<TestRun> runs = readTestRuns(startDate, DateTime.parse("2017-03-10"));
		List<FeatureModel> features = readAllFor(runs);
		assertThat(features, iterableWithSize(2));
		assertThat(features.get(1).getScenarios(), iterableWithSize(1));
		assertThat(features.get(1).getScenario(0).getSteps(), iterableWithSize(2));

		runs = readTestRuns(startDate, DateTime.parse("2017-03-11"));
		features = readAllFor(runs);
		assertThat(features, iterableWithSize(3));
	}

	@Test
	public void signalWhenFeatureTemplateNotFound() {
		thrown.expect(ReportStoreException.class);
		thrown.expectMessage("No single Feature template found for hash key =non existing");
		ReportStore store = new PropertiesReportStore();
		store.readTemplate("non existing");
	}

	@Test
	public void readFeatureTemplate() {
		ReportStore store = new PropertiesReportStore();
		String template = store.readTemplate("SomeFakeHashKey");

		assertThat(template, equalTo("Feature Template contents"));
	}

	@Test
	public void readScenarioWithFeatures() {
		FeatureModel feature = readFeature(999);
		assertThat(feature.getScenarios(), iterableWithSize(1));
	}

	@Test
	public void restoreFeaturesWithUniqueFeatureId() {
		FeatureModel feature = readFeature(999);
		assertThat(feature.getFeatureId(), endsWith("-" + feature.getId()));
	}

	@Test
	public void restoreScenariosWithUniqueScenarioId() {
		FeatureModel feature = readFeature(999);
		ScenarioModel scenario = feature.getScenario(0);
		assertThat(scenario.getScenarioId(), endsWith("-" + scenario.getId()));
	}

	@Test
	public void readStepsWithFeatures() {
		FeatureModel feature = readFeature(999);
		assertThat(feature.getScenarios(), iterableWithSize(1));
		assertThat(feature.getScenario(0).getSteps(), iterableWithSize(2));
	}

	private TestRun readTestRun(String name) {
		ReportStore store = new PropertiesReportStore();
		return store.readTestRun(name);
	}

	private TestRun readTestRun(Integer id) {
		ReportStore store = new PropertiesReportStore();
		return store.readTestRun(id);
	}

	private List<TestRun> readTestRuns(DateTime startDate, DateTime endDate) {
		ReportStore store = new PropertiesReportStore();
		return store.readTestRuns(startDate, endDate);
	}

	private FeatureModel readFeature(int id) {
		ReportStore store = new PropertiesReportStore();
		return store.readFeature(id);
	}

	private List<FeatureModel> readAllFor(List<TestRun> runs) {
		ReportStore store = new PropertiesReportStore();
		return store.readAllFeaturesFor(runs);
	}
}

package io.pickles.jdbc;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertThat;

import gherkin.formatter.model.Comment;
import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Step;
import gherkin.formatter.model.Tag;
import io.pickles.model.FeatureModel;
import io.pickles.model.ScenarioModel;
import io.pickles.model.StepModel;
import io.pickles.model.TestRun;
import io.pickles.reporting.ReportStore;
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
	}

	@After
	public void closeConnection() throws SQLException {
		connection.close();
	}

	@Test
	public void saveTestRun() {
		TestRun run = new TestRun("PICKLES", "DESCRIPTION", DateTime.now(), DateTime.now().plusMinutes(5));
		reportingStore().create(run);

		assertThat(reportStore().readTestRun("PICKLES").getId(), notNullValue());
	}

	@Test
	public void updateTestRun() {
		TestRun run = new TestRun("Cukes", "Description", DateTime.now());
		reportingStore().create(run);
		assertThat(reportStore().readTestRun("Cukes").getId(), notNullValue());

		run.setFinishedAt(DateTime.now().plusMinutes(5));
		reportingStore().update(run);
		assertThat(reportStore().readTestRun("Cukes").getFinishedAt(), equalTo(run.getFinishedAt()));
	}

	@Test
	public void saveFeature() {
		FeatureModel feature = featureWithTestRun();

		reportingStore().create(feature);

		assertThat(feature.getId(), notNullValue());
	}

	@Test
	public void saveScenariosWithFeature() {
		FeatureModel feature = featureWithScenariosAndSteps();

		reportingStore().create(feature);

		assertThat(feature.getId(), notNullValue());
		for (ScenarioModel scenario : feature.getScenarios()) {
			assertThat(scenario.getId(), notNullValue());
		}
	}

	@Test
	public void saveStepsWithFeature() {
		FeatureModel feature = featureWithScenariosAndSteps();

		reportingStore().create(feature);

		assertThat(feature.getId(), notNullValue());
		for (ScenarioModel scenario : feature.getScenarios()) {
			for (StepModel step : scenario.getSteps()) {
				assertThat(step.getId(), notNullValue());
			}
		}
	}

	private FeatureModel featureWithScenariosAndSteps() {
		FeatureModel feature = featureWithTestRun();

		ScenarioModel scenario = new ScenarioModel(new Scenario(Collections.emptyList(), Collections.emptyList(),
				"Scenario", "first scenario", "", 3, ""));
		scenario.setStartedAt(DateTime.now());
		feature.addScenario(scenario);
		Step step = new Step(Collections.emptyList(), "Given ", "first scenario - first", 4, Collections.emptyList(),
				null);
		scenario.addStep(new StepModel(step));
		step = new Step(Collections.emptyList(), "Then ", "first scenario - second", 5, Collections.emptyList(), null);
		scenario.addStep(new StepModel(step));

		scenario = new ScenarioModel(new Scenario(Collections.emptyList(), Collections.emptyList(), "Scenario",
				"second scenario", "", 7, ""));
		scenario.setStartedAt(DateTime.now());
		feature.addScenario(scenario);
		step = new Step(Collections.emptyList(), "Given ", "second scenario - first", 8, Collections.emptyList(), null);
		scenario.addStep(new StepModel(step));
		step = new Step(Collections.emptyList(), "Then ", "second scenario - second", 9, Collections.emptyList(), null);
		scenario.addStep(new StepModel(step));

		return feature;
	}

	private FeatureModel featureWithTestRun() {
		FeatureModel feature = new FeatureModel();
		List<Tag> tags = Arrays.asList(new Tag("tag1", 2), new Tag("tag2", 3));
		List<Comment> comments = Arrays.asList(new Comment("comment", 3));
		feature.setFeature(new Feature(comments, tags, "Feature", "cucumber feature", "a very nice feature", 4,
				"cucumber-feature"));
		feature.setUri("test uri");
		feature.setStartedAt(DateTime.now());
		feature.setTestRun(new TestRun(1, "", "", DateTime.now(), null));
		return feature;
	}

	private ReportingStore reportingStore() {
		return new PropertiesReportingStore();
	}

	private ReportStore reportStore() {
		return new PropertiesReportStore();
	}

}

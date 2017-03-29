package io.pickles.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.joda.time.DateTime;

import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Scenario;
import io.pickles.model.FeatureModel;
import io.pickles.model.ScenarioModel;
import io.pickles.model.StepModel;
import io.pickles.model.TestRun;
import io.pickles.reporting.ReportStore;

public class JdbcReportStore implements ReportStore {

	private DataSource dataSource;
	private Connection connection;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	Connection getConnection() {
		try {
			if (connection == null || connection.isClosed()) {
				connection = dataSource.getConnection();
			}
		} catch (SQLException e) {
			throw new ReportStoreException("Unable to get a connection for ReportStore", e);
		}
		return connection;
	}

	@Override
	public TestRun readTestRun(String name) {
		List<TestRun> results;

		try (PreparedStatement statement = getConnection().prepareStatement(
				"SELECT ID, NAME, DESCRIPTION, STARTED_AT, FINISHED_AT FROM PICKLES_TEST_RUN WHERE NAME = ?")) {
			statement.setString(1, name);
			results = testRunsFrom(statement.executeQuery());
		} catch (SQLException ex) {
			throw new ReportStoreException("Could not retrieve Test Run for name=" + name, ex);
		}

		if (results.size() != 1) {
			throw new ReportStoreException("No single Test Run found for name=" + name);
		}
		return results.get(0);
	}

	@Override
	public TestRun readTestRun(Integer key) {
		List<TestRun> results;

		try (PreparedStatement statement = getConnection().prepareStatement(
				"SELECT ID, NAME, DESCRIPTION, STARTED_AT, FINISHED_AT FROM PICKLES_TEST_RUN WHERE ID = ?")) {
			statement.setInt(1, key);
			results = testRunsFrom(statement.executeQuery());
		} catch (SQLException ex) {
			throw new ReportStoreException("Could not retrieve Test Run for id=" + key, ex);
		}

		if (results.size() != 1) {
			throw new ReportStoreException("No single Test Run found for id=" + key);
		}
		return results.get(0);
	}

	@Override
	public List<TestRun> readTestRuns(DateTime from, DateTime until) {
		List<TestRun> results;

		try (PreparedStatement statement = getConnection().prepareStatement(
				"SELECT ID, NAME, DESCRIPTION, STARTED_AT, FINISHED_AT FROM PICKLES_TEST_RUN WHERE STARTED_AT >= ? AND FINISHED_AT < ?")) {
			statement.setTimestamp(1, new Timestamp(from.getMillis()));
			statement.setTimestamp(2, new Timestamp(until.getMillis()));
			results = testRunsFrom(statement.executeQuery());
		} catch (SQLException ex) {
			throw new ReportStoreException("Could not retrieve Test Runs from " + from + " until " + until, ex);
		}

		return results;
	}

	private List<TestRun> testRunsFrom(ResultSet resultSet) throws SQLException {
		List<TestRun> results = new ArrayList<>();
		while (resultSet.next()) {
			Integer id = resultSet.getInt(1);
			String name = resultSet.getString(2);
			String description = resultSet.getString(3);
			DateTime startedAt = new DateTime(resultSet.getTimestamp(4));
			DateTime finishedAt = new DateTime(resultSet.getTimestamp(5));
			results.add(new TestRun(id, name, description, startedAt, finishedAt));
		}
		return results;
	}

	@Override
	public String readTemplate(String hashKey) {
		List<String> results;

		try (PreparedStatement statement = getConnection()
				.prepareStatement("SELECT CONTENTS FROM PICKLES_FEATURE_TEMPLATE WHERE HASH_KEY = ?")) {
			statement.setString(1, hashKey);
			results = templatesFrom(statement.executeQuery());

		} catch (SQLException ex) {
			throw new ReportStoreException("Could not read feature template for hash key =" + hashKey, ex);
		}

		if (results.size() != 1) {
			throw new ReportStoreException("No single Feature template found for hash key =" + hashKey);
		}

		return results.get(0);
	}

	private List<String> templatesFrom(ResultSet resultSet) throws SQLException {
		List<String> results = new ArrayList<>();
		while (resultSet.next()) {
			String template = resultSet.getString(1);
			results.add(template);
		}

		return results;
	}

	@Override
	public FeatureModel readFeature(Integer id) {
		List<FeatureModel> results;

		try (PreparedStatement statement = getConnection().prepareStatement(
				"SELECT ID, TEST_RUN_ID, STARTED_AT, FINISHED_AT, JSON FROM PICKLES_FEATURE WHERE ID = ?")) {
			statement.setInt(1, id);
			results = featuresFrom(statement.executeQuery());
		} catch (SQLException ex) {
			throw new ReportStoreException("Could not retrieve Feature for id=" + id, ex);
		}

		if (results.size() != 1) {
			throw new ReportStoreException("No single Feature found for id =" + id);
		}
		FeatureModel feature = results.get(0);
		readAllScenariosFor(feature);
		return feature;
	}

	private List<FeatureModel> featuresFrom(ResultSet resultSet) throws SQLException {
		List<FeatureModel> results = new ArrayList<>();
		while (resultSet.next()) {
			Integer id = resultSet.getInt(1);
			DateTime startedAt = new DateTime(resultSet.getTimestamp(3));
			DateTime finishedAt = new DateTime(resultSet.getTimestamp(4));
			String json = resultSet.getString(5);
			if (json == null || json.isEmpty()) {
				throw new ReportStoreException("Feature without json");
			}
			FeatureModel tmp = FeatureModel.fromJson(json);
			Feature feature = new Feature(tmp.getComments(), tmp.getTags(), tmp.getKeyword(), tmp.getName(),
					tmp.getDescription(), tmp.getLine(), tmp.getFeatureId() + "-" + id);
			FeatureModel model = new FeatureModel();
			model.setFeature(feature);
			model.setId(id);
			model.setStartedAt(startedAt);
			model.setFinishedAt(finishedAt);
			model.setUri(tmp.getUri());
			results.add(model);
		}
		return results;
	}

	@Override
	public List<FeatureModel> readAllFeaturesFor(List<TestRun> testRuns) {
		List<FeatureModel> results = new ArrayList<>();
		for (TestRun run : testRuns) {

			try (PreparedStatement statement = getConnection()
					.prepareStatement("SELECT ID FROM PICKLES_FEATURE WHERE TEST_RUN_ID = ?")) {
				statement.setInt(1, run.getId());
				for (Integer featureId : idsFrom(statement.executeQuery())) {
					FeatureModel feature = readFeature(featureId);
					feature.setTestRun(run);
					results.add(feature);
				}
			} catch (SQLException ex) {
				throw new ReportStoreException("Could not retrieve Feature for TestRuns", ex);
			}

		}

		return results;
	}

	public ScenarioModel readScenario(Integer id) {
		List<ScenarioModel> results;

		try (PreparedStatement statement = getConnection().prepareStatement(
				"SELECT ID, FEATURE_ID, STARTED_AT, FINISHED_AT, TRIGGERED_BY_DV_ID, NEXT_DV_ID, JSON FROM PICKLES_SCENARIO WHERE ID = ?")) {
			statement.setInt(1, id);
			results = scenariosFrom(statement.executeQuery());
		} catch (SQLException ex) {
			throw new ReportStoreException("Could not retrieve scenario for id=" + id, ex);
		}

		if (results.size() != 1) {
			throw new ReportStoreException("No single scenario found for id =" + id);
		}
		ScenarioModel scenario = results.get(0);
		readAllStepsFor(scenario);
		return scenario;
	}

	@Override
	public ScenarioModel findScenarioTriggeredBy(String dvId) {
		List<Integer> ids;

		try (PreparedStatement statement = getConnection()
				.prepareStatement("SELECT ID FROM PICKLES_SCENARIO WHERE TRIGGERED_BY_DV_ID = ?")) {
			statement.setString(1, dvId);
			ids = idsFrom(statement.executeQuery());
		} catch (SQLException ex) {
			throw new ReportStoreException("Could not retrieve scenario for trigggering dv id=" + dvId, ex);
		}
		if (ids.isEmpty()) {
			return null;
		}

		return readScenario(ids.get(0));
	}

	private List<ScenarioModel> scenariosFrom(ResultSet resultSet) throws SQLException {
		List<ScenarioModel> results = new ArrayList<>();
		while (resultSet.next()) {
			Integer id = resultSet.getInt(1);
			DateTime startedAt = new DateTime(resultSet.getTimestamp(3));
			DateTime finishedAt = new DateTime(resultSet.getTimestamp(4));
			String triggeringDvId = resultSet.getString(5);
			String nextDvId = resultSet.getString(6);
			String json = resultSet.getString(7);
			if (json == null || json.isEmpty()) {
				throw new ReportStoreException("Scenario without json");
			}
			ScenarioModel tmp = ScenarioModel.fromJson(json);
			Scenario scenario = new Scenario(tmp.getComments(), tmp.getTags(), tmp.getKeyword(), tmp.getName(),
					tmp.getDescription(), tmp.getLine(), tmp.getScenarioId() + "-" + id);
			ScenarioModel model = new ScenarioModel(scenario);
			model.setId(id);
			model.setStartedAt(startedAt);
			model.setFinishedAt(finishedAt);
			model.setTriggeringDvId(triggeringDvId);
			model.setNextDvId(nextDvId);
			results.add(model);
		}
		return results;
	}

	private List<ScenarioModel> readAllScenariosFor(FeatureModel feature) {
		ArrayList<ScenarioModel> scenarios = new ArrayList<>();

		try (PreparedStatement statement = getConnection()
				.prepareStatement("SELECT ID FROM PICKLES_SCENARIO WHERE FEATURE_ID = ?")) {
			statement.setInt(1, feature.getId());
			for (Integer scenarioId : idsFrom(statement.executeQuery())) {
				ScenarioModel scenario = readScenario(scenarioId);
				feature.addScenario(scenario);
				scenarios.add(scenario);
			}
		} catch (SQLException ex) {
			throw new ReportStoreException("Could not retrieve scenario for Feature " + feature.getId(), ex);
		}

		return scenarios;
	}

	public StepModel readStep(Integer id) {
		List<StepModel> results;

		try (PreparedStatement statement = getConnection()
				.prepareStatement("SELECT ID, SCENARIO_ID, JSON FROM PICKLES_STEP WHERE ID = ?")) {
			statement.setInt(1, id);
			results = stepsFrom(statement.executeQuery());
		} catch (SQLException ex) {
			throw new ReportStoreException("Could not retrieve step for id=" + id, ex);
		}

		if (results.size() != 1) {
			throw new ReportStoreException("No single step found for id =" + id);
		}
		return results.get(0);
	}

	private List<StepModel> stepsFrom(ResultSet resultSet) throws SQLException {
		List<StepModel> results = new ArrayList<>();
		while (resultSet.next()) {
			Integer id = resultSet.getInt(1);
			String json = resultSet.getString(3);
			if (json == null || json.isEmpty()) {
				throw new ReportStoreException("Step without json");
			}
			StepModel model = StepModel.fromJson(json);
			model.setId(id);
			results.add(model);
		}
		return results;
	}

	private List<StepModel> readAllStepsFor(ScenarioModel scenario) {
		ArrayList<StepModel> steps = new ArrayList<>();
		try {
			Connection connection = getConnection();
			PreparedStatement statement = connection
					.prepareStatement("SELECT ID FROM PICKLES_STEP WHERE SCENARIO_ID = ?");
			statement.setInt(1, scenario.getId());
			for (Integer scenarioId : idsFrom(statement.executeQuery())) {
				StepModel step = readStep(scenarioId);
				scenario.addStep(step);
				steps.add(step);
			}
		} catch (SQLException ex) {
			throw new ReportStoreException("Could not retrieve Step for Scenario : " + scenario.getId(), ex);
		}
		return steps;
	}

	private List<Integer> idsFrom(ResultSet resultSet) throws SQLException {
		List<Integer> results = new ArrayList<>();
		while (resultSet.next()) {
			Integer id = resultSet.getInt(1);
			results.add(id);
		}
		return results;
	}
}

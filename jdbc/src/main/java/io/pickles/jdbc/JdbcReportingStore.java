package io.pickles.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import io.pickles.preprocessor.model.FeatureModel;
import io.pickles.preprocessor.model.ScenarioModel;
import io.pickles.preprocessor.model.StepModel;
import io.pickles.preprocessor.model.TestRun;
import io.pickles.reporting.ReportingStore;

public class JdbcReportingStore implements ReportingStore {

	private DataSource dataSource;
	private Connection dbConnection;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	Connection getConnection() throws SQLException {
		try {
			if (dbConnection == null || dbConnection.isClosed()) {
				dbConnection = dataSource.getConnection();
				dbConnection.setAutoCommit(true);
			}
		} catch (SQLException e) {
			throw new ReportingStoreException("Unable to get a connection for ReportingStore", e);
		}
		return dbConnection;
	}

	@Override
	public void create(TestRun run) {
		try {
			PreparedStatement statement = getConnection().prepareStatement(
					"INSERT INTO PICKLES_TEST_RUN (NAME, DESCRIPTION, STARTED_AT, FINISHED_AT) VALUES (?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, run.getName());
			statement.setString(2, run.getDescription());
			statement.setTimestamp(3, new Timestamp(run.getStartedAt().getMillis()));
			if (run.getFinishedAt() != null) {
				statement.setTimestamp(4, new Timestamp(run.getFinishedAt().getMillis()));
			} else {
				statement.setTimestamp(4, null);
			}

			statement.execute();
			ResultSet rs = statement.getGeneratedKeys();
			if (rs.next()) {
				run.setId(rs.getInt(1));
			}
		} catch (SQLException ex) {
			throw new ReportingStoreException("Could not save Test Run for name = " + run.getName(), ex);
		}
	}

	@Override
	public void update(TestRun run) {
		{
			try {
				PreparedStatement statement = getConnection().prepareStatement(
						"UPDATE PICKLES_TEST_RUN SET NAME=?, DESCRIPTION=?, STARTED_AT=?, FINISHED_AT=? WHERE ID = ?");
				statement.setString(1, run.getName());
				statement.setString(2, run.getDescription());
				statement.setTimestamp(3, new Timestamp(run.getStartedAt().getMillis()));
				if (run.getFinishedAt() != null) {
					statement.setTimestamp(4, new Timestamp(run.getFinishedAt().getMillis()));
				} else {
					statement.setTimestamp(4, null);
				}
				statement.setInt(5, run.getId());

				statement.execute();
			} catch (SQLException e) {
				throw new ReportingStoreException("Could not update Test Run for id = " + run.getId(), e);
			}
		}
	}

	@Override
	public void create(FeatureModel feature) {
		try {
			PreparedStatement statement = getConnection().prepareStatement(
					"INSERT INTO PICKLES_FEATURE (TEST_RUN_ID, STARTED_AT, FINISHED_AT, JSON) VALUES (?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			statement.setInt(1, feature.getTestRun().getId());
			statement.setTimestamp(2, new Timestamp(feature.getStartedAt().getMillis()));
			if (feature.getFinishedAt() != null) {
				statement.setTimestamp(3, new Timestamp(feature.getFinishedAt().getMillis()));
			} else {
				statement.setTimestamp(3, null);
			}
			statement.setString(4, feature.toJsonObject().toString());

			statement.execute();
			ResultSet rs = statement.getGeneratedKeys();
			if (rs.next()) {
				feature.setId(rs.getInt(1));
			}
		} catch (SQLException ex) {
			throw new ReportingStoreException("Could not save Feture = " + feature.getName(), ex);
		}

		createScenarios(feature);
		createSteps(feature);
	}

	private void createScenarios(FeatureModel feature) {
		try {
			Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(
					"INSERT INTO PICKLES_SCENARIO (FEATURE_ID, STARTED_AT, FINISHED_AT, TRIGGERED_BY_DV_ID, NEXT_DV_ID, JSON) VALUES (?, ?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			connection.setAutoCommit(false);

			for (ScenarioModel scenario : feature.getScenarios()) {

				statement.setInt(1, scenario.getFeature().getId());
				statement.setTimestamp(2, new Timestamp(scenario.getStartedAt().getMillis()));
				if (scenario.getFinishedAt() != null) {
					statement.setTimestamp(3, new Timestamp(scenario.getFinishedAt().getMillis()));
				} else {
					statement.setTimestamp(3, null);
				}
				statement.setString(4, scenario.getTriggeringDvId());
				statement.setString(5, scenario.getNextDvId());
				statement.setString(6, scenario.toJsonObject().toString());
				statement.addBatch();
			}

			statement.executeBatch();
			ResultSet rs = statement.getGeneratedKeys();
			for (ScenarioModel scenario : feature.getScenarios()) {
				if (rs.next()) {
					scenario.setId(rs.getInt(1));
				}
			}
			connection.commit();
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			throw new ReportingStoreException("Could not save scenario in feature = " + feature.getName(), e);
		}
	}

	private void createSteps(FeatureModel feature) {
		try {
			Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(
					"INSERT INTO PICKLES_STEP (SCENARIO_ID, JSON) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
			connection.setAutoCommit(false);

			List<StepModel> steps = feature.getScenarios().stream().flatMap(s -> s.getSteps().stream())
					.collect(Collectors.toList());
			for (StepModel step : steps) {

				statement.setInt(1, step.getScenario().getId());
				statement.setString(2, step.toJsonObject().toString());
				statement.addBatch();
			}

			statement.executeBatch();
			ResultSet rs = statement.getGeneratedKeys();
			for (StepModel step : steps) {
				if (rs.next()) {
					step.setId(rs.getInt(1));
				}
			}
			connection.commit();
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			throw new ReportingStoreException("Could not save step in feature = " + feature.getName(), e);
		}
	}
}

package io.pickles.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import io.pickles.model.FeatureModel;
import io.pickles.model.ScenarioModel;
import io.pickles.model.StepModel;

public class JdbcBatchReportingStore extends JdbcReportingStore {

	@Override
	public void create(FeatureModel feature) {
		save(feature);

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

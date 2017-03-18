package io.pickles.jdbc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

import javax.sql.DataSource;

import io.pickles.model.FeatureModel;
import io.pickles.model.ScenarioModel;
import io.pickles.model.StepModel;
import io.pickles.model.TestRun;
import io.pickles.preprocessor.TemplateTransformerException;
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
		save(feature);
		for (ScenarioModel scenario : feature.getScenarios()) {
			create(scenario);
		}
	}

	protected void save(FeatureModel feature) {
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
	}

	@Override
	public void create(ScenarioModel scenario) {
		save(scenario);
		for (StepModel step : scenario.getSteps()) {
			create(step);
		}
	}

	private void save(ScenarioModel scenario) {
		try {
			PreparedStatement statement = getConnection().prepareStatement(
					"INSERT INTO PICKLES_SCENARIO (FEATURE_ID, STARTED_AT, FINISHED_AT, TRIGGERED_BY_DV_ID, NEXT_DV_ID, JSON) VALUES (?, ?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);

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

			statement.execute();

			ResultSet rs = statement.getGeneratedKeys();
			if (rs.next()) {
				scenario.setId(rs.getInt(1));
			}
		} catch (

		SQLException e) {
			throw new ReportingStoreException("Could not save scenario=" + scenario.getName(), e);
		}
	}

	@Override
	public void create(StepModel step) {
		try {
			PreparedStatement statement = getConnection().prepareStatement(
					"INSERT INTO PICKLES_STEP (SCENARIO_ID, JSON) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);

			statement.setInt(1, step.getScenario().getId());
			statement.setString(2, step.toJsonObject().toString());
			statement.execute();

			ResultSet rs = statement.getGeneratedKeys();
			if (rs.next()) {
				step.setId(rs.getInt(1));
			}
		} catch (SQLException e) {
			throw new ReportingStoreException("Could not save step=" + step.getKeyword() + step.getName(), e);
		}
	}

	@Override
	public void create(FeatureModel template, String uri, List<String> lines) {
		String hashKey = checksum(lines);
		if (findTemplate(hashKey) != null) {
			String contents = String.join(System.getProperty("line.separator"), lines);
			try {
				PreparedStatement statement = getConnection().prepareStatement(
						"INSERT INTO PICKLES_FEATURE_TEMPLATE (HASH_KEY, NAME, URI, CONTENTS) VALUES (?, ?, ?, ?)");

				statement.setString(1, hashKey);
				statement.setString(2, template.getName());
				statement.setString(3, uri);
				statement.setString(4, contents);
				statement.execute();

				template.setTemplateHashKey(hashKey);
			} catch (SQLException e) {
				throw new ReportingStoreException("Could not save step=" + template.getName(), e);
			}
		}
	}

	private String findTemplate(String hashKey) {
		try {
			PreparedStatement statement = getConnection()
					.prepareStatement("SELECT CONTENTS FROM PICKLES_FEATURE_TEMPLATE WHERE HASH_KEY = ?");
			statement.setString(1, hashKey);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				return rs.getString(1);
			}

		} catch (SQLException ex) {
			throw new ReportingStoreException("Could not retrieve find feature template for hash key =" + hashKey, ex);
		}

		return null;
	}

	private String checksum(List<String> lines) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		for (String line : lines) {
			write(stream, line);
		}

		byte[] digest;
		try {
			digest = MessageDigest.getInstance("SHA-1").digest(stream.toByteArray());
		} catch (NoSuchAlgorithmException ex) {
			throw new ReportingStoreException(ex.getMessage(), ex);
		}

		return new BigInteger(1, digest).toString();
	}

	private void write(ByteArrayOutputStream stream, String value) {
		try {
			stream.write(value.getBytes("UTF-8"));
		} catch (IOException ex) {
			throw new TemplateTransformerException("Error calculating checksum for " + value, ex);
		}
	}

}
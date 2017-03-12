package io.pickles.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.joda.time.DateTime;

import io.pickles.model.FeatureModel;
import io.pickles.model.TestRun;
import io.pickles.reporting.ReportStore;

public class JdbcReportStore implements ReportStore {

	private DataSource dataSource;
	private Connection connection;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	Connection getConnection() throws SQLException {
		try {
			if (connection == null || connection.isClosed()) {
				connection = dataSource.getConnection();
			}
		} catch (SQLException e) {
			throw new ReportingStoreException("Unable to get a connection for ReportStore", e);
		}
		return connection;
	}

	@Override
	public TestRun readTestRun(String name) {
		List<TestRun> results;
		try {
			PreparedStatement statement = getConnection().prepareStatement(
					"SELECT ID, NAME, DESCRIPTION, STARTED_AT, FINISHED_AT FROM PICKLES_TEST_RUN WHERE NAME = ?");
			statement.setString(1, name);
			results = testRunsFrom(statement.executeQuery());
		} catch (SQLException ex) {
			throw new ReportingStoreException("Could not retrieve Test Run for name=" + name, ex);
		}

		if (results.size() != 1) {
			throw new ReportingStoreException("No single Test Run found for name=" + name);
		}
		return results.get(0);
	}

	@Override
	public TestRun readTestRun(Integer key) {
		List<TestRun> results;
		try {
			PreparedStatement statement = getConnection().prepareStatement(
					"SELECT ID, NAME, DESCRIPTION, STARTED_AT, FINISHED_AT FROM PICKLES_TEST_RUN WHERE ID = ?");
			statement.setInt(1, key);
			results = testRunsFrom(statement.executeQuery());
		} catch (SQLException ex) {
			throw new ReportingStoreException("Could not retrieve Test Run for id=" + key, ex);
		}

		if (results.size() != 1) {
			throw new ReportingStoreException("No single Test Run found for id=" + key);
		}
		return results.get(0);
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
	public FeatureModel readFeature(Integer id) {
		List<FeatureModel> results;
		try {
			PreparedStatement statement = getConnection().prepareStatement(
					"SELECT ID, TEST_RUN_ID, STARTED_AT, FINISHED_AT, JSON FROM PICKLES_FEATURE WHERE ID = ?");
			statement.setInt(1, id);
			results = featuresFrom(statement.executeQuery());
		} catch (SQLException ex) {
			throw new ReportingStoreException("Could not retrieve Feature for id=" + id, ex);
		}

		if (results.size() != 1) {
			throw new ReportingStoreException("No single Feature found for id =" + id);
		}
		return results.get(0);
	}

	private List<FeatureModel> featuresFrom(ResultSet resultSet) throws SQLException {
		List<FeatureModel> results = new ArrayList<>();
		while (resultSet.next()) {
			Integer id = resultSet.getInt(1);
			Integer testRunId = resultSet.getInt(2);
			DateTime startedAt = new DateTime(resultSet.getTimestamp(3));
			DateTime finishedAt = new DateTime(resultSet.getTimestamp(4));
			String json = resultSet.getString(5);
			if (json == null || json.isEmpty()) {
				throw new ReportingStoreException("Feature without json");
			}
			FeatureModel model = FeatureModel.fromJson(json);
			model.setId(id);
			model.setStartedAt(startedAt);
			model.setFinishedAt(finishedAt);
			model.setTestRun(readTestRun(testRunId));
			results.add(model);
		}
		return results;

	}

	@Override
	public List<FeatureModel> readAllFor(TestRun run) {
		// TODO Auto-generated method stub
		return null;
	}

}

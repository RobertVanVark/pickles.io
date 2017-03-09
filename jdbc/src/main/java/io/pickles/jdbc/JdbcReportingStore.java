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

import io.pickles.preprocessor.model.FeatureModel;
import io.pickles.preprocessor.model.TestRun;
import io.pickles.reporting.ReportingStore;

public class JdbcReportingStore implements ReportingStore {

	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	Connection getConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			throw new DelayedVerificationStoreException("Unable to get a connection for DelayedVerificationStore", e);
		}
	}

	@Override
	public TestRun getTestRunBy(String name) {
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
			throw new ReportingStoreException("No Test Run found for name=" + name);
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
	public void createTestRun(TestRun run) {
		try {
			PreparedStatement statement = getConnection().prepareStatement(
					"INSERT INTO PICKLES_TEST_RUN (NAME, DESCRIPTION, STARTED_AT, FINISHED_AT) VALUES (?, ?, ?, ?)");
			statement.setString(1, run.getName());
			statement.setString(2, run.getDescription());
			statement.setTimestamp(3, new Timestamp(run.getStartedAt().getMillis()));
			if (run.getFinishedAt() != null) {
				statement.setTimestamp(4, new Timestamp(run.getFinishedAt().getMillis()));
			} else {
				statement.setTimestamp(4, null);
			}

			statement.execute();
		} catch (SQLException ex) {
			throw new ReportingStoreException("Could not save Test Run for name =" + run.getName(), ex);
		}

	}

	@Override
	public void storeFeture(FeatureModel feature) {
		// TODO Auto-generated method stub

	}

	@Override
	public void readFeature(FeatureModel feature) {
		// TODO Auto-generated method stub

	}

	@Override
	public void readAllFor(TestRun run) {
		// TODO Auto-generated method stub

	}
}

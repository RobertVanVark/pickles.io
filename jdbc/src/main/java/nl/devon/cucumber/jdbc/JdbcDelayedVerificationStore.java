package nl.devon.cucumber.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.joda.time.DateTime;

import nl.devon.DelayedVerification;

public class JdbcDelayedVerificationStore {

	private String url;
	private String username;
	private String password;

	public JdbcDelayedVerificationStore() {
		InputStream stream = ClassLoader.getSystemResourceAsStream("delayed-verification-store.properties");
		if (stream == null) {
			throw new DelayedVerificationStoreException(
					"JdbcDelayedVerificationStore cannot be inititaed. Could not find 'delayed-verification-store.properties' on the classpath");
		}
		getProperties(stream);
	}

	private void getProperties(InputStream stream) {
		Properties properties = new Properties();
		try {
			properties.load(stream);
		} catch (IOException e) {
			throw new DelayedVerificationStoreException(
					"JdbcDelayedVerificationStore cannot be inititaed. 'delayed-verification-store.properties' is ill-formated");
		}
		url = properties.getProperty("url");
		username = properties.getProperty("username");
		password = properties.getProperty("password");
	}

	public DelayedVerification get(String id) {
		List<DelayedVerification> results;
		try {
			PreparedStatement statement = setupConnection().prepareStatement(
					"SELECT ID, CREATED_AT, VERIFY_AT, PROCESSED_AT, CHECKSUM, FEATURE FROM DELAYED_VERIFICATION WHERE ID = ?");
			statement.setString(1, id);
			results = delayedVerificationsFrom(statement.executeQuery());
		} catch (SQLException ex) {
			throw new DelayedVerificationStoreException("Could not retrieve Delayed Verification for id=" + id, ex);
		}

		if (results.size() != 1) {
			throw new DelayedVerificationStoreException("No Delayed Verification found for id=" + id);
		}
		return results.get(0);
	}

	private List<DelayedVerification> delayedVerificationsFrom(ResultSet resultSet) throws SQLException {
		List<DelayedVerification> results = new ArrayList<>();
		if (resultSet.next()) {
			String id = resultSet.getString(1);
			DateTime createdAt = new DateTime(resultSet.getTimestamp(2));
			DateTime verifyAt = new DateTime(resultSet.getTimestamp(3));
			DateTime processedAt = new DateTime(resultSet.getTimestamp(4));
			String checksum = resultSet.getString(5);
			String feature = resultSet.getString(6);
			results.add(new DelayedVerification(id, createdAt, verifyAt, processedAt, checksum, feature));
		}
		return results;
	}

	private Connection setupConnection() {
		Connection connection = null;

		try {
			connection = DriverManager.getConnection(url);
		} catch (SQLException e) {
			e.printStackTrace(System.out);
		}

		return connection;
	}

	public void save(DelayedVerification verification) {
		try {
			PreparedStatement statement = setupConnection()
					.prepareStatement("INSERT INTO DELAYED_VERIFICATION VALUES (?, ?, ?, ?, ?, ?)");
			statement.setString(1, verification.getId());
			statement.setTimestamp(2, new Timestamp(verification.getCreatedAt().getMillis()));
			statement.setTimestamp(3, new Timestamp(verification.getVerifyAt().getMillis()));
			if (verification.getProcessedAt() != null) {
				statement.setTimestamp(4, new Timestamp(verification.getProcessedAt().getMillis()));
			} else {
				statement.setTimestamp(4, null);
			}
			statement.setString(5, verification.getScenarioChecksum());
			statement.setString(6, verification.getFeature());

			statement.execute();
		} catch (SQLException ex) {
			throw new DelayedVerificationStoreException(
					"Could not save Delayed Verification for id=" + verification.getId(), ex);
		}
	}

}

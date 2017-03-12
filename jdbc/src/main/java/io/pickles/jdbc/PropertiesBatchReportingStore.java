package io.pickles.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PropertiesBatchReportingStore extends JdbcBatchReportingStore {

	private String url;
	private String username;
	private String password;

	public PropertiesBatchReportingStore() {
		InputStream stream = ClassLoader.getSystemResourceAsStream("delayed-verification-store.properties");
		if (stream == null) {
			throw new DelayedVerificationStoreException(
					"PropertiesDelayedVerificationStore cannot be inititaed. Could not find 'delayed-verification-store.properties' on the classpath");
		}
		getProperties(stream);
	}

	private void getProperties(InputStream stream) {
		Properties properties = new Properties();
		try {
			properties.load(stream);
		} catch (IOException e) {
			throw new DelayedVerificationStoreException(
					"PropertiesDelayedVerificationStore cannot be inititaed. 'delayed-verification-store.properties' is ill-formated",
					e);
		}
		url = properties.getProperty("url");
		username = properties.getProperty("username");
		password = properties.getProperty("password");
	}

	@Override
	Connection getConnection() {
		Connection connection = null;

		try {
			connection = DriverManager.getConnection(url);
		} catch (SQLException e) {
			throw new DelayedVerificationStoreException(
					"Unable to get a connection for PropertiesDelayedVerificationStore", e);
		}

		return connection;
	}
}

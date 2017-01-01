package co.ke.workpoint.store.dao;

import java.sql.Connection;
import java.sql.DriverManager;

import co.ke.workpoint.store.helpers.ApplicationSettings;

public class JDBCConnection {

	Connection conn = null;

	public JDBCConnection() {
	}

	/**
	 * Get Connection based of name of the configuration
	 * 
	 * @param configName
	 * @return
	 */
	public Connection getConnection() {

		if (conn != null) {
			return conn;
		}

		ApplicationSettings settings = ApplicationSettings.getInstance();
		String username = settings.getProperty("db.user");
		String password = settings.getProperty("db.password");
		String url = settings.getProperty("db.url");
		String driver = settings.getProperty("db.driver");

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, username, password);
			return conn;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public void dispose() {
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		conn = null;
	}
}

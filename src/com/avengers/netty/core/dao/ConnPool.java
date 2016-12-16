package com.avengers.netty.core.dao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author LamHa
 *
 */
public class ConnPool extends BasePool {
	private static final String READ_CONNECTION_NAME = "poolread";
	private static final String WRITE_CONNECTION_NAME = "poolwrite";
	public static final String POOL_LOG_NAME = "poollog";
	public static final String POOL_MONEY = "poolmoney";
	private static ConnPool instance;

	public ConnPool(String pathFile) {
		super(pathFile);
	}

	public static ConnPool init(String pathFile) {
		if (instance == null)
			instance = new ConnPool(pathFile);

		return instance;
	}

	public static ConnPool getInstance() {
		return instance;
	}

	/**
	 * Chỉ dùng để đọc từ db
	 */
	public Connection getConnectionRead() throws SQLException {
		Connection conn = getConnection(READ_CONNECTION_NAME, DEFAULT_CONNECTION_TIME_OUT);
		if (conn != null) {
			return conn;
		}

		throw new SQLException("Null connection from db pool");
	}

	/**
	 * Chỉ dùng để write từ db
	 */
	public Connection getConnectionWrite() throws SQLException {
		Connection conn = getConnection(WRITE_CONNECTION_NAME, DEFAULT_CONNECTION_TIME_OUT);
		if (conn != null) {
			return conn;
		}

		throw new SQLException("null connection from db pool");
	}

	public int sizeReadConnection() {
		return getSize(READ_CONNECTION_NAME);
	}

	public int sizeWriteConnection() {
		return getSize(WRITE_CONNECTION_NAME);
	}

	public int sizeMoneyConnection() {
		return getSize(POOL_MONEY);
	}

	public int sizeLogConnection() {
		return getSize(POOL_LOG_NAME);
	}

	public Connection getConnectionMoney() throws SQLException {
		return getConnection(POOL_MONEY, DEFAULT_CONNECTION_TIME_OUT);
	}

	public Connection getConnectionLog() throws SQLException {
		return getConnection(POOL_LOG_NAME, DEFAULT_CONNECTION_TIME_OUT);
	}

}

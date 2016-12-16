package com.avengers.netty.core.dao;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.LoggerFactory;

import com.avengers.netty.socket.util.LoggerNames;

import snaq.db.ConnectionPool;
import snaq.db.ConnectionPoolManager;

/**
 * @author LamHa
 *
 */
public class BasePool {
	public static final int DEFAULT_CONNECTION_TIME_OUT = 1000;
	private ConnectionPoolManager poolManager;

	public BasePool(String pathFile) {
		try {
			poolManager = ConnectionPoolManager.getInstance(new File(pathFile));
		} catch (IOException ex) {
			LoggerFactory.getLogger(LoggerNames.ERROR_DB_LOG_NAME).error("Error init pool manager", ex);
		}
	}

	/**
	 * 
	 * @param poolName
	 *            : lowcase
	 * @return
	 */
	public int getSize(String poolName) {
		try {
			ConnectionPool pool = poolManager.getPool(poolName);
			if (pool == null) {
				return 0;
			}

			return pool.getSize();
		} catch (Exception ex) {
			LoggerFactory.getLogger(LoggerNames.ERROR_DB_LOG_NAME).error("Error get connection size : " + poolName, ex);
		}

		return 0;
	}

	public Connection getConnection(String poolName, int checkoutTimeout) throws SQLException {
		return poolManager.getConnection(poolName, checkoutTimeout);
	}
}

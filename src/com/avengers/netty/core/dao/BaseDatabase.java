package com.avengers.netty.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.LoggerFactory;

import com.avengers.netty.socket.util.LoggerNames;

/**
 * @author LamHa
 *
 */
public class BaseDatabase {

	protected void closeStatement(Statement st, ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
			if (st != null) {
				st.close();
			}
		} catch (SQLException e) {
			LoggerFactory.getLogger(LoggerNames.ERROR_DB_LOG_NAME).error(null, e);
		}
	}

	protected void closePreparedStatement(PreparedStatement ps, ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
		} catch (SQLException e) {
			LoggerFactory.getLogger(LoggerNames.ERROR_DB_LOG_NAME).error(null, e);
		}
	}

	protected void closeStatement(Statement st) {
		try {
			if (st != null) {
				st.close();
			}
		} catch (SQLException e) {
			LoggerFactory.getLogger(LoggerNames.ERROR_DB_LOG_NAME).error(null, e);
		}
	}

	protected void closePreparedStatement(PreparedStatement ps) {
		try {
			if (ps != null) {
				ps.close();
			}
		} catch (SQLException e) {
			LoggerFactory.getLogger(LoggerNames.ERROR_DB_LOG_NAME).error(null, e);
		}
	}

	protected void free(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			LoggerFactory.getLogger(LoggerNames.ERROR_DB_LOG_NAME).error("error close connection.", e);
		}
	}
}

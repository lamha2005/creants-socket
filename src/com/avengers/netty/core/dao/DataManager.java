package com.avengers.netty.core.dao;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;

import org.slf4j.LoggerFactory;

import com.avengers.netty.core.om.ServerConfig;
import com.avengers.netty.core.util.CoreTracer;
import com.avengers.netty.socket.gate.wood.User;
import com.avengers.netty.socket.util.LoggerNames;

/**
 * Class xử lý tất cả truy vấn đến Database
 * 
 * @author LamHa
 *
 */

public class DataManager {
	private final HashMap<String, Integer> dbCounter;

	private static DataManager instance;

	public static DataManager getInstance() {
		if (instance == null) {
			instance = new DataManager();
			ConnPool.init(ServerConfig.DATABASE_CONFIG_FILE_NAME);
		}
		return instance;
	}

	private DataManager() {
		dbCounter = new HashMap<String, Integer>();
		Method[] methods = DataManager.class.getDeclaredMethods();
		for (Method m : methods) {
			dbCounter.put(m.getName(), 0);
		}
	}

	public static HashMap<String, Integer> getDbCounter() {
		return instance.dbCounter;
	}

	public static String dumpDbCounterLog() {
		StringBuilder dump = new StringBuilder();
		try {
			instance.countDbLog("dumpDbCounterLog");
			synchronized (instance.dbCounter) {
				Iterator<String> keys = instance.dbCounter.keySet().iterator();
				while (keys.hasNext()) {
					String funName = (String) keys.next();
					dump.append(funName);
					dump.append("\t");
					dump.append(instance.dbCounter.get(funName));
					dump.append("\t");
				}
			}
		} catch (Exception e) {
			CoreTracer.error(DataManager.class, "error when dump DB counter log", e);
		}
		return dump.toString();
	}

	public static void logDbCounter() {
		try {
			instance.countDbLog("logDbCounter");
			CoreTracer.info(DataManager.class, dumpDbCounterLog());
			synchronized (instance.dbCounter) {
				instance.dbCounter.clear();
				Method[] methods = DataManager.class.getDeclaredMethods();
				for (Method m : methods) {
					instance.dbCounter.put(m.getName(), 0);
				}
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(LoggerNames.ERROR_DB_LOG_NAME).error("logDbCounter got exception ", e);
		}
	}

	/**
	 * Dùng để đếm số lần function được gọi
	 *
	 * @param functionName
	 *            tên function được gọi
	 */
	protected void countDbLog(String functionName) {
		try {
			synchronized (dbCounter) {
				int count = dbCounter.get(functionName) == null ? 0 : dbCounter.get(functionName);
				dbCounter.put(functionName, ++count);
			}
		} catch (Exception e) {
			// // Ignore this error.
			LoggerFactory.getLogger(LoggerNames.ERROR_DB_LOG_NAME).error("logDbCounter got exception ", e);
		}
	}

	// TEST truy vấn DB
	public boolean checkUserNameExist(String userName) {
		countDbLog("checkUserExist");
		Connection conn = null;
		CallableStatement cs = null;
		ResultSet rs = null;

		try {
			conn = ConnPool.getInstance().getConnectionRead();
			cs = conn.prepareCall("{ call sp_user_checkUsernameExists(?) }");
			cs.setString(1, userName);
			rs = cs.executeQuery();

			while (rs.next()) {
				return (rs.getInt(1) == 1) ? true : false;
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(LoggerNames.ERROR_DB_LOG_NAME).error("checkUserExist", e);
		}

		return false;
	}

	public User getUserInfoByUserName(String userName) {
		countDbLog("getUserInfoByUserName");
		ResultSet rs = null;
		try (Connection conn = ConnPool.getInstance().getConnectionRead();
				CallableStatement cs = conn.prepareCall("{ call sp_user_getInfoByUserName(?) }");) {
			cs.setString(1, userName);
			rs = cs.executeQuery();
			while (rs.next()) {
				User userProfile = new User();
				userProfile.setUserId(rs.getInt(DataKey.user_id));
				userProfile.setUserName(rs.getString(DataKey.username));
				userProfile.setMoney(rs.getLong(DataKey.coin));
				userProfile.setAvatar(rs.getString(DataKey.avatar));
				userProfile.setLanguage(rs.getByte(DataKey.lang));
				return userProfile;
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(LoggerNames.ERROR_DB_LOG_NAME).error("getUserInfoByUserName", e);
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public User getUserInfoByUserId(String userId) {
		ResultSet rs = null;
		try (Connection conn = ConnPool.getInstance().getConnectionRead();
				CallableStatement cs = conn.prepareCall("{ call sp_account_login_uid(?) }");) {
			cs.setString(1, userId);
			rs = cs.executeQuery();
			if (rs.next()) {
				User user = new User();
				user.setUid(rs.getString("user_id"));
				user.setUserName(rs.getString("username"));
				user.setAvatar(rs.getString("avatar"));
				return user;
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(LoggerNames.ERROR_DB_LOG_NAME).error("getUserInfoByUserId", e);
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public User insertUser(String deviceId, int userId, String userName, int serverId, String password) {
		countDbLog("insertUser");
		ResultSet rs = null;
		try (Connection conn = ConnPool.getInstance().getConnectionWrite();
				CallableStatement cs = conn.prepareCall("{ call sp_user_insert(?, ?, ?, ?, ?) }")) {
			cs.setString(1, deviceId);
			cs.setString(2, userName);
			cs.setInt(3, userId);
			cs.setInt(4, serverId);
			cs.setString(5, password);
			rs = cs.executeQuery();
			while (rs.next()) {
				User userProfile = new User();
				userProfile.setUserId(rs.getInt(DataKey.user_id));
				userProfile.setUserName(rs.getString(DataKey.username));
				userProfile.setMoney(rs.getLong(DataKey.coin));
				userProfile.setAvatar(ServerConfig.DEFAULT_AVATAR);
				return userProfile;
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(LoggerNames.ERROR_DB_LOG_NAME).error("insertUser", e);
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Hàm này để test, sau này gọi sang hệ thông khác để kiểm tra
	 * 
	 * @return 0: ko tồn tại, 1: đúng tài khoản, -1: sai mật khẩu
	 */
	public int checkLogin(String username, String password) {
		ResultSet rs = null;
		try (Connection conn = ConnPool.getInstance().getConnectionRead();
				CallableStatement cs = conn.prepareCall("{ call sp_account_test_checkLogin(?,?) }")) {
			cs.setString(1, username);
			cs.setString(2, password);
			rs = cs.executeQuery();
			while (rs.next()) {
				return rs.getInt("result");
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(LoggerNames.ERROR_DB_LOG_NAME).error("getUserInfoByDeviceId", e);
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	public int checkLoginV2(String username, String password) {
		try (Connection conn = ConnPool.getInstance().getConnectionRead();
				CallableStatement cs = conn.prepareCall("{ call sp_account_test_checkLogin(?,?) }")) {
			cs.setString(1, username);
			cs.setString(2, password);
			// cũng phải đóng luôn resultSet để tránh leak bộ nhớ, và unexpected
			// pooling behaviour
			try (ResultSet rs = cs.executeQuery()) {
				while (rs.next()) {
					return rs.getInt("result");
				}
			} catch (Exception e) {
				LoggerFactory.getLogger(LoggerNames.ERROR_DB_LOG_NAME).error("getUserInfoByDeviceId", e);
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(LoggerNames.ERROR_DB_LOG_NAME).error("getUserInfoByDeviceId", e);
		}
		return 0;
	}

	/**
	 * 
	 * @param userId
	 * @param serviceId
	 * @param timeOnline
	 */
	public void checkLogout(int userId, int serviceId, long timeOnline) {
		countDbLog("checkLogout");

		try (Connection conn = ConnPool.getInstance().getConnectionWrite();
				CallableStatement cs = conn.prepareCall("{ call sp_board_user_logoutServer(?, ?) }");) {
			cs.setInt(1, userId);
			cs.setInt(2, serviceId);
			cs.executeUpdate();
		} catch (SQLException ex) {
			LoggerFactory.getLogger(LoggerNames.ERROR_DB_LOG_NAME).error("error when update time online", ex);
		}
	}

	/**
	 * Đếm số lần login thất bại
	 * 
	 * @param ip
	 * @param increase
	 * @return
	 */
	public boolean countLoginFail(String ip, int increase) {
		countDbLog("countLoginFail");
		ResultSet rs = null;

		try (Connection conn = ConnPool.getInstance().getConnectionRead();
				CallableStatement cs = conn.prepareCall("{ call sp_count_login_fail(?, ?, ?) }");) {
			cs.setString(1, ip);
			cs.setInt(2, increase);
			cs.setInt(3, increase);
			rs = cs.executeQuery();

			if (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(LoggerNames.ERROR_DB_LOG_NAME).error("insertDeviceWithUsername", e);
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	/**
	 * Insert log ccu theo game
	 * 
	 * @param serverId
	 * @param serviceId
	 * @param maxCCU
	 * @param minCCU
	 */
	public void insertGameCCULog(int serverId, int serviceId, int maxCCU, int minCCU) {
		countDbLog("insertGameCCULog");

		try (Connection conn = ConnPool.getInstance().getConnectionLog();
				CallableStatement cs = conn.prepareCall("{ call sp_service_user_log_insert(?, ?, ?, ?) }");) {
			cs.setInt(1, serverId);
			cs.setInt(2, serviceId);
			cs.setLong(3, minCCU);
			cs.setInt(4, maxCCU);
			cs.executeQuery();
		} catch (SQLException ex) {
			LoggerFactory.getLogger(LoggerNames.ERROR_DB_LOG_NAME).error("insertGameCCULog", ex);
		}
	}

	public long incrementUserMoney(String uid, long value) {
		ResultSet rs = null;
		long result = 0;
		try (Connection conn = ConnPool.getInstance().getConnectionMoney();
				CallableStatement statement = conn.prepareCall("{ call sp_user_money_update(?, ?) }")) {
			statement.setString(1, uid);
			statement.setLong(2, value);

			rs = statement.executeQuery();
			if (rs.next()) {
				result = rs.getLong("result");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LoggerFactory.getLogger(LoggerNames.ERROR_DB_LOG_NAME).error("updateUserMoney fail!", e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}

	public boolean checkEnoughMoney(String uid, long money) {
		ResultSet rs = null;
		boolean result = false;
		try (Connection conn = ConnPool.getInstance().getConnectionMoney();
				CallableStatement statement = conn.prepareCall("{ call sp_user_money_check(?, ?) }")) {
			statement.setString(1, uid);
			statement.setLong(2, money);
			rs = statement.executeQuery();
			if (rs.next()) {
				result = rs.getInt("result") > 0;
			}

		} catch (Exception e) {
			LoggerFactory.getLogger(LoggerNames.ERROR_DB_LOG_NAME).error("", e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}
}

package com.avengers.netty.core.om;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.avengers.netty.core.util.CoreTracer;

import io.netty.util.Version;

/**
 * Class chứa các config của server
 * 
 * @author LamHa
 *
 */
public class ServerConfig {
	// số lượng user tối đa ở phòng chờ
	public static int LOBBY_CAPACITY = 99999;

	// Config All Services Run
	public static final String SERVICE_CONFIG_FILE_NAME = "configs/services.json";
	public static final String DATABASE_CONFIG_FILE_NAME = "configs/dbconfig.properties";
	public static final String ELO_CONFIG_FILE_NAME = "configs/eloconfig.json";
	public static final long SLOW_PROCESS_MSG_TIME = 200;
	public static boolean isLogSlowProcess = false;
	public static String serverIp;
	public static int serverPort;

	// Netty
	public static int connectTimeoutMillis;
	public static int soBackLog;
	public static boolean soKeepAlive;
	public static int systemMsgThreadPoolSize;
	public static int msgThreadPoolSize;
	public static boolean enableCluster;

	// Couchbase
	public static long couchBaseOpTimeout;
	public static List<URI> cacheHosts;
	public static String bucketName;
	public static String cachePassword;

	// version app
	public static Version version;
	public static Version clientIOSVersion;
	public static Version clientAndroidVersion;
	public static Version clientIOSVersionReview;
	public static Version clientMACVersionReview;

	public static int threadPoolLogSize;
	public static int threadPoolAPISize;
	public static int threadPoolBoardInsertSize;
	public static int threadPoolBoardInsert_CacheTime = 10;
	public static int apiTimeOut = 10000;

	// config logic game
	public static int MAX_BET = 500000;
	public static String DEFAULT_AVATAR = "avatar_default.png";
	public static int delayTimeMillis;
	// for timer schedule
	public static int threadPoolTimerSize;
	public static List<String> debugUsers;
	public static int serverId;
	public static int delayLoginTime;
	public static int roomCountPerPage;
	public static int userCountPerPage;
	public static Properties properties;

	public static void init(Properties prop) {
		if (prop == null) {
			CoreTracer.error(ServerConfig.class, "Can't load config of Server, Check the path file");
			return;
		}

		serverIp = prop.getProperty("server.ip", "localhost");
		serverPort = Integer.parseInt(prop.getProperty("server.port", "8000"));
		soBackLog = Integer.parseInt(prop.getProperty("soBacklog", "1000"));
		connectTimeoutMillis = Integer.parseInt(prop.getProperty("connectTimeoutMillis", "30000"));
		soKeepAlive = Boolean.parseBoolean(prop.getProperty("soKeepAlive", "true"));
		systemMsgThreadPoolSize = Integer.parseInt(prop.getProperty("systemMessageExecutor.threadPoolSize", "1"));
		msgThreadPoolSize = Integer.parseInt(prop.getProperty("messageExecutor.threadPoolSize", "1"));

		// cluster
		enableCluster = Boolean.parseBoolean(prop.getProperty("enableCluster", "false"));

		// couchbase
		couchBaseOpTimeout = Long.parseLong(prop.getProperty("couchbase.opTimeout", "1000"));
		cacheHosts = getCacheHosts(prop.getProperty("couchbase.cacheHosts"));
		bucketName = prop.getProperty("couchbase.bucketName");
		cachePassword = prop.getProperty("couchbase.pass");

		isLogSlowProcess = prop.getProperty("isLogSlowProcess").equals("1");
		threadPoolLogSize = Integer.parseInt(prop.getProperty("ThreadPoolLogSize"));
		threadPoolAPISize = Integer.parseInt(prop.getProperty("ThreadPoolAPISize"));
		threadPoolBoardInsertSize = Integer.parseInt(prop.getProperty("threadPoolBoardInsertSize"));
		apiTimeOut = Integer.parseInt(prop.getProperty("API.Timeout"));
		// timer schedule config
		threadPoolTimerSize = Integer.parseInt(prop.getProperty("ThreadPoolTimerSize", "1"));
		// gameconfig
		delayTimeMillis = Integer.parseInt(prop.getProperty("delayTimeMillis", "1000"));
		parseDebugUser(prop.getProperty("debugUser"));
		delayLoginTime = Integer.parseInt(prop.getProperty("delayLoginTime"));
		serverId = Integer.parseInt(prop.getProperty("serverId"));
		roomCountPerPage = Integer.parseInt(prop.getProperty("roomCountPerPage"));
		userCountPerPage = Integer.parseInt(prop.getProperty("userCountPerPage"));
		MAX_BET = Integer.parseInt(prop.getProperty("xyz.client.maxBet", "500000"));
		DEFAULT_AVATAR = prop.getProperty("xyz.client.defaultAvatar", "avatar_default.png");
		// TODO nếu sau này url api nhiều thì tách ra file riêng
		properties = new Properties();
		properties.putAll(prop);
	}

	private static void parseDebugUser(String property) {
		debugUsers = new ArrayList<>();

		String[] strValue = property.split(",");
		int length = strValue.length;
		for (int i = 0; i < length; i++) {
			debugUsers.add(strValue[i]);
		}
	}

	public static String getProperty(String key) {
		return properties.getProperty(key);
	}

	public static String getServerHost() {
		return serverIp + ":" + serverPort;
	}

	private static List<URI> getCacheHosts(String hostsConfig) {
		String[] data = hostsConfig.split(";");
		List<URI> hosts = new ArrayList<URI>(data.length);
		for (String string : data) {
			try {
				hosts.add(new URI(string));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}

		return hosts;
	}

}

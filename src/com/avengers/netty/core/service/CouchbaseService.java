package com.avengers.netty.core.service;

/**
 * @author LamHa
 *
 */
public class CouchbaseService {
	// private static final Logger LOG =
	// LoggerFactory.getLogger(CouchbaseService.class);
	// private static final int TOKEN_SECOND_TTL = 86400;
	// private CouchbaseClient couchbaseClient;
	// private static CouchbaseService instance;
	//
	// public static CouchbaseService getInstance() {
	// if (instance == null) {
	// instance = new CouchbaseService();
	// }
	//
	// return instance;
	// }
	//
	// public CouchbaseService() {
	// try {
	// LOG.info("---------------------- INIT COUCHBASE --------------------");
	// CouchbaseConnectionFactoryBuilder cfb = new
	// CouchbaseConnectionFactoryBuilder();
	// cfb.setObsTimeout(AppConfig.couchBaseOpTimeout);
	// CouchbaseConnectionFactory connection =
	// cfb.buildCouchbaseConnection(AppConfig.cacheHosts,
	// AppConfig.bucketName, AppConfig.cachePassword);
	// couchbaseClient = new CouchbaseClient(connection);
	// LOG.info("::::::::::::::::::::::::::::::::::::::::::::::::::::");
	// LOG.info(">> COUCHBASE STARTED");
	// LOG.info(String.format(">> [cacheHosts=%s, bucketName=%s]",
	// AppConfig.cacheHosts.toString(),
	// AppConfig.bucketName));
	// LOG.info("::::::::::::::::::::::::::::::::::::::::::::::::::::");
	// } catch (IOException e) {
	// LOG.error("COUCHBASE START FAIL!", e);
	// }
	// }
	//
	// /**
	// * Lưu token
	// *
	// * @param token
	// * @param userId
	// */
	// public void saveToken(String token, int userId) {
	// // set không quan tâm đã tồn tại token này hay chưa, add thực hiện check
	// // xem đã tồn tại chưa
	// couchbaseClient.set(token, TOKEN_SECOND_TTL, userId);
	// }
	//
	// /**
	// * Kiểm tra có tồn tại token của user này trên server không
	// *
	// * @param token
	// * @return
	// */
	// public boolean existToken(String token) {
	// return couchbaseClient.get(token) != null;
	// }
	//
	// public Integer getUserIdByToken(String token) {
	// return (Integer) couchbaseClient.get(token);
	// }
	//
	// public void shutdown() {
	// LOG.info("Shutdown Couchbase");
	// if (couchbaseClient != null)
	// couchbaseClient.shutdown();
	// }

}

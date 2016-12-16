package com.avengers.netty.core.queue;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avengers.netty.core.queue.constant.QueueConfig;
import com.avengers.netty.core.queue.constant.QueueLogKey;
import com.avengers.netty.core.util.CoreTracer;
import com.google.common.base.Splitter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * @author LamHa
 *
 */
public class RabitQueueService {

	private static RabitQueueService instance;
	private final Logger logDebug = LoggerFactory.getLogger("QueueServiceDebug");
	private final Logger logBackup = LoggerFactory.getLogger("QueueServiceBackup");

	private QueueConfig config;
	private Connection conn;
	private Channel channel;

	private AMQP.BasicProperties basicProp;

	private final ConcurrentHashMap<String, Integer> counter = new ConcurrentHashMap<>();

	public static RabitQueueService getInstance() {
		return instance;
	}

	public static void init(Properties prop) {
		if (instance == null) {
			instance = new RabitQueueService(prop);
		}
	}

	private RabitQueueService(Properties prop) {
		try {
			config = new QueueConfig(prop);

			ConnectionFactory factory = new ConnectionFactory();
			factory.setUsername(config.username);
			factory.setPassword(config.pass);
			factory.setAutomaticRecoveryEnabled(true);

			ExecutorService es = Executors.newFixedThreadPool(config.poolSize,
					new ThreadFactoryBuilder().setNameFormat("QueueService-%d").build());
			conn = factory.newConnection(es, getHosts());

			conn.addShutdownListener(new ShutdownListener() {
				@Override
				public void shutdownCompleted(ShutdownSignalException paramShutdownSignalException) {
					CoreTracer.error(RabitQueueService.class, "- RabbitMQ Service shutdown complete",
							paramShutdownSignalException);
				}
			});

			channel = conn.createChannel();
			channel.exchangeDeclare(QueueLogKey.GENERAL_EXCHANGE, QueueLogKey.EXCHANGE_TYPE, true);
			channel.exchangeDeclare(QueueLogKey.MONEY_EXCHANGE, QueueLogKey.EXCHANGE_TYPE, true);
			channel.exchangeDeclare(QueueLogKey.BOARD_EXCHANGE, QueueLogKey.EXCHANGE_TYPE, true);

			String generalQueue = QueueLogKey.QUEUE_PREFIX + QueueLogKey.GENERAL_EXCHANGE;
			String moneyQueue = QueueLogKey.QUEUE_PREFIX + QueueLogKey.MONEY_EXCHANGE;
			String boardQueue = QueueLogKey.QUEUE_PREFIX + QueueLogKey.BOARD_EXCHANGE;

			channel.queueDeclare(generalQueue, true, false, false, null);
			channel.queueDeclare(moneyQueue, true, false, false, null);
			channel.queueDeclare(boardQueue, true, false, false, null);

			channel.queueBind(generalQueue, QueueLogKey.GENERAL_EXCHANGE, QueueLogKey.Session);
			channel.queueBind(generalQueue, QueueLogKey.GENERAL_EXCHANGE, QueueLogKey.Devices);
			channel.queueBind(generalQueue, QueueLogKey.GENERAL_EXCHANGE, QueueLogKey.DevicesMap);

			channel.queueBind(moneyQueue, QueueLogKey.MONEY_EXCHANGE, QueueLogKey.UserMoney);
			channel.queueBind(boardQueue, QueueLogKey.BOARD_EXCHANGE, QueueLogKey.UserBoard);

		} catch (Exception e) {
			CoreTracer.error(RabitQueueService.class, e);
		}

		AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
		basicProp = builder.deliveryMode(2).build();

		CoreTracer.info(RabitQueueService.class,
				"Init Rabbit MQ Service done, connected to: " + conn.getAddress() + ":" + conn.getPort());
	}

	/**
	 * Send message log to queue
	 * 
	 * @param exchangeName
	 *            : using QueueLogKey definition
	 * @param routingKey
	 *            : using QueueLogKey definition
	 * @param isPersistance
	 *            : boolean, persistance data on disk
	 * @param dataLogs:
	 *            Object Array
	 */
	public void sendLog(String exchangeName, String routingKey, boolean isPersistance, String dataLogs) {

		if (dataLogs == null || dataLogs.isEmpty()) {
			throw new Error("Data log is empty: " + exchangeName + ", rt:" + routingKey);
		}

		try {
			channel.basicPublish(exchangeName, routingKey, isPersistance ? basicProp : null,
					dataLogs.toString().getBytes(Charset.forName("UTF-8")));
			debugDataToFile(routingKey, dataLogs);
			increaseSentMessage(routingKey);
		} catch (Exception ex) {
			backupDataToFile(routingKey, dataLogs);
			CoreTracer.error(RabitQueueService.class, ex);
		}

	}

	private void backupDataToFile(String routingKey, String datas) {
		logBackup.info(datas + " : " + routingKey);
	}

	private void debugDataToFile(String routingKey, String datas) {
		if (logDebug.isDebugEnabled()) {
			logDebug.debug(routingKey + " : " + datas);
		}
	}

	public void shutDown() {
		try {
			if (channel != null && channel.isOpen()) {
				channel.close();
			}
			if (conn != null && conn.isOpen()) {
				conn.close();
			}
			CoreTracer.info(RabitQueueService.class, "------------ RabbitMQ Service stopped-------------");
		} catch (Exception e) {
			CoreTracer.error(RabitQueueService.class, "Shutdown fail!", e);
		}
	}

	private void increaseSentMessage(String routingKey) {
		Object c = counter.get(routingKey);
		if (c == null) {
			counter.put(routingKey, 1);
		} else {
			counter.put(routingKey, (Integer) c + 1);
		}
	}

	public void dumpCounter() {
		StringBuilder builder = new StringBuilder("Queue service counter: ");

		try {

			Iterator<String> keys = counter.keySet().iterator();
			String key;
			while (keys.hasNext()) {
				key = keys.next();
				builder.append(key).append(" : ").append(counter.get(key)).append(", ");
			}

			counter.clear();

			CoreTracer.info(RabitQueueService.class, builder.toString());
		} catch (Exception ex) {
			CoreTracer.error(RabitQueueService.class, "dumpCounter fail!", ex);
		}
	}

	private Address[] getHosts() {
		List<String> listHost = Splitter.on(";").trimResults().omitEmptyStrings().splitToList(config.host);
		if (listHost.size() > 0) {
			Address[] queueHosts = new Address[listHost.size()];
			for (int i = 0; i < listHost.size(); i++) {
				queueHosts[i] = new Address(listHost.get(i), config.port);
			}
			return queueHosts;
		}
		return null;
	}

}

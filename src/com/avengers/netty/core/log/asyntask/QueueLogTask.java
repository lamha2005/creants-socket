package com.avengers.netty.core.log.asyntask;

import com.avengers.netty.core.queue.RabitQueueService;

/**
 * @author LamHa
 *
 */
public class QueueLogTask implements Runnable {
	private final String exchangeName;
	private final String routingKey;
	private final boolean isPersistance;
	private final String data;

	public QueueLogTask(String exchangeName, String routingKey, String data) {
		this(exchangeName, routingKey, false, data);
	}

	/**
	 * 
	 * @param exchangeName
	 * @param routingKey
	 * @param isPersistance,
	 *            persistance on disk. (use for log money)
	 * @param data
	 */
	public QueueLogTask(String exchangeName, String routingKey, boolean isPersistance, String data) {
		this.exchangeName = exchangeName;
		this.routingKey = routingKey;
		this.isPersistance = isPersistance;
		this.data = data;
	}

	@Override
	public void run() {
		RabitQueueService.getInstance().sendLog(exchangeName, routingKey, isPersistance, data);
	}
}

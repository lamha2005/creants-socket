package com.avengers.netty.core.event.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avengers.netty.socket.gate.IMessage;
import com.avengers.netty.socket.gate.wood.Message;
import com.avengers.netty.socket.gate.wood.RequestComparator;
import com.avengers.netty.socket.gate.wood.User;

/**
 * Class xử lý các message từ queue.<br>
 * Mọi message được nhận từ MessageHandler được push vào queue và chờ
 * MesssageExecutor xử lý
 * 
 * @author LamHa
 *
 */
public class MessageExecutor {
	private static final Logger LOG = LoggerFactory.getLogger(MessageExecutor.class);
	private int threadPoolSize;
	private ExecutorService threadPool;
	private BlockingQueue<IMessage> requestQueue;

	public MessageExecutor() {
		// threadPoolSize = ServerConfig.msgThreadPoolSize;
		threadPoolSize = 10;
		requestQueue = new PriorityBlockingQueue<IMessage>(50, new RequestComparator());
		threadPool = Executors.newFixedThreadPool(threadPoolSize,
				new MessageExecutorThreadFactory("extension-message-executor"));

		for (int i = 0; i < threadPoolSize; i++) {
			threadPool.execute(new MessageRunnable());
		}
	}

	private class MessageRunnable implements Runnable {
		private IMessage message = null;

		@Override
		public void run() {
			while (true) {
				try {
					message = requestQueue.take();
					User user = message.getUser();
					System.out.println(user);
				} catch (RuntimeException ex) {
					LOG.error(ex.toString());
				} catch (Exception e) {
					LOG.error(e.toString());
				}
			}
		}
	}

	public void addExtensionMessage(Message message) {
		try {
			// TODO check max pool size
			requestQueue.put(message);
		} catch (InterruptedException e) {
			LOG.error(e.toString());
		}
	}

	public void shutdown() {
		threadPool.shutdown();
	}

}

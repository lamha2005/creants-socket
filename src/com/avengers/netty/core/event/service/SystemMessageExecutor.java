package com.avengers.netty.core.event.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avengers.netty.core.event.handler.AbstractRequestHandler;
import com.avengers.netty.core.event.handler.SystemHandlerManager;
import com.avengers.netty.socket.gate.IMessage;
import com.avengers.netty.socket.gate.wood.Message;
import com.avengers.netty.socket.gate.wood.RequestComparator;

/**
 * Class xử lý tất cả các message của system.
 * 
 * @author LamHa
 *
 */
public class SystemMessageExecutor {
	private static final Logger LOG = LoggerFactory.getLogger(SystemMessageExecutor.class);
	private int threadPoolSize;
	private ExecutorService threadPool;
	private SystemHandlerManager systemEventManager;
	private BlockingQueue<IMessage> messageQueue;

	public SystemMessageExecutor() {
		// threadPoolSize = ServerConfig.systemMsgThreadPoolSize;
		threadPoolSize = 100;
		messageQueue = new PriorityBlockingQueue<IMessage>(50, new RequestComparator());
		threadPool = Executors.newFixedThreadPool(threadPoolSize,
				new MessageExecutorThreadFactory("system-message-executor"));

		for (int i = 0; i < threadPoolSize; i++) {
			threadPool.execute(new SystemMessageRunnable());
		}
	}

	private class SystemMessageRunnable implements Runnable {
		private IMessage message = null;

		@Override
		public void run() {
			while (true) {
				try {
					message = messageQueue.take();
					AbstractRequestHandler findSystemHandler = systemEventManager.getHandler(message.getCommandId());
					if (findSystemHandler != null) {
						findSystemHandler.perform(message.getUser(), message);
					}

				} catch (RuntimeException ex) {
					LOG.error(ex.toString());
				} catch (InterruptedException e) {
					LOG.error(e.toString());
				} catch (Exception e) {
					LOG.error(e.toString());
				}
			}
		}
	}

	public void addSystemMessage(Message message) {
		try {
			messageQueue.put(message);
		} catch (InterruptedException e) {
			LOG.error(e.toString());
		}
	}

	public void shutdown() {
		threadPool.shutdown();
	}

	public void initSystemHandlerManager(SystemHandlerManager systemEventManager) {
		this.systemEventManager = systemEventManager;
	}

}

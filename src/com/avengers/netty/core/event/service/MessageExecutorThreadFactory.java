package com.avengers.netty.core.event.service;

import java.util.concurrent.ThreadFactory;

/**
 * @author LamHa
 *
 */
public class MessageExecutorThreadFactory implements ThreadFactory {
	private int counter = 0;
	private String prefix = "";


	public MessageExecutorThreadFactory(String prefix) {
		this.prefix = prefix;
	}


	@Override
	public Thread newThread(Runnable r) {
		return new Thread(r, prefix + "-" + System.currentTimeMillis() + "-" + counter++);
	}

}

package com.avengers.netty.core.queue.constant;

import java.util.Properties;

import com.avengers.netty.core.util.CoreTracer;

/**
 * @author LamHa
 *
 */
public class QueueConfig {

	public String host;
	public int port;
	public String username;
	public String pass;

	public int poolSize;

	public QueueConfig(Properties props) {
		init(props);
	}

	private void init(Properties props) {
		host = props.getProperty("host");
		port = Integer.parseInt(props.getProperty("port"));
		username = props.getProperty("username");
		pass = props.getProperty("password");
		poolSize = Integer.parseInt(props.getProperty("poolSize"));

		props.clear();

		CoreTracer.info(QueueConfig.class, new StringBuilder("RabbitMQ Service config: ").append(host).append(", port:")
				.append(port).append(", user:").append(username).append(", pass:").append(pass).toString());
	}

}
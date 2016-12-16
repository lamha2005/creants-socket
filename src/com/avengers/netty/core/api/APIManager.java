package com.avengers.netty.core.api;

import com.avengers.netty.SocketServer;

/**
 * @author LamHa
 *
 */
public class APIManager {
	private final String serviceName = "APIManager";
	private SocketServer socketServer;
	private ICoreAPI coreApi;

	public APIManager(SocketServer socketServer) {
		this.socketServer = socketServer;
		coreApi = new CoreAPI(this.socketServer);
	}

	public ICoreAPI getCoreApi() {
		return coreApi;
	}

	public void destroy(Object arg0) {
	}

	public String getName() {
		return serviceName;
	}

	public void handleMessage(Object msg) {
		throw new UnsupportedOperationException("Not supported");
	}

	public void setName(String name) {
		throw new UnsupportedOperationException("Not supported");
	}
}

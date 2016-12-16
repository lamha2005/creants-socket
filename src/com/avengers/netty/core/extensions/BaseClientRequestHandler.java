package com.avengers.netty.core.extensions;

import java.util.List;

import com.avengers.netty.core.api.ICoreAPI;
import com.avengers.netty.socket.gate.IMessage;
import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public abstract class BaseClientRequestHandler implements IClientRequestHandler {
	private CoreExtension parentExtension;

	public BaseClientRequestHandler() {
		init();
	}

	protected abstract void init();

	public CoreExtension getParentExtension() {
		return parentExtension;
	}

	public void setParentExtension(CoreExtension ext) {
		parentExtension = ext;
	}

	protected ICoreAPI getApi() {
		return parentExtension.coreApi;
	}

	protected void send(IMessage message, User recipient) {
		parentExtension.send(message, recipient);
	}

	protected void send(IMessage message, List<User> recipients) {
		parentExtension.send(message, recipients);
	}
}

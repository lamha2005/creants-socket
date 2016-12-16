package com.avengers.netty.core.extensions;

import java.util.List;

import com.avengers.netty.core.api.ICoreAPI;
import com.avengers.netty.socket.gate.IMessage;
import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public abstract class BaseServerEventHandler implements IServerEventHandler {
	private CoreExtension parentExtension;

	public CoreExtension getParentExtension() {
		return parentExtension;
	}

	public void setParentExtension(CoreExtension ext) {
		parentExtension = ext;
	}

	protected ICoreAPI getApi() {
		return parentExtension.getApi();
	}

	protected void send(IMessage message, User recipient) {
		parentExtension.send(message, recipient);
	}

	protected void send(IMessage message, List<User> recipients) {
		parentExtension.send(message, recipients);
	}

	protected void trace(Object... args) {
		parentExtension.trace(args);
	}

}

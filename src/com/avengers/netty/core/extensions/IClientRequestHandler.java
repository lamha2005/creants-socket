package com.avengers.netty.core.extensions;

import com.avengers.netty.socket.gate.IMessage;
import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public abstract interface IClientRequestHandler {
	public abstract void handleClientRequest(User user, IMessage message);
	
	public abstract void setParentExtension(CoreExtension ext);
}

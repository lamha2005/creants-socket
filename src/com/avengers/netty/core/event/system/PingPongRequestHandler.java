package com.avengers.netty.core.event.system;

import com.avengers.netty.core.event.SystemNetworkConstant;
import com.avengers.netty.core.event.handler.AbstractRequestHandler;
import com.avengers.netty.core.util.DefaultMessageFactory;
import com.avengers.netty.socket.gate.IMessage;
import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public class PingPongRequestHandler extends AbstractRequestHandler {

	@Override
	public void initialize() {
	}

	@Override
	public void perform(User user, IMessage message) {
		writeMessage(user, DefaultMessageFactory.responseMessage(SystemNetworkConstant.COMMAND_PING_PONG));
	}

}

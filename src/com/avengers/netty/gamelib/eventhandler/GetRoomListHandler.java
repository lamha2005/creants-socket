package com.avengers.netty.gamelib.eventhandler;

import com.avengers.netty.core.extensions.BaseClientRequestHandler;
import com.avengers.netty.core.util.DefaultMessageFactory;
import com.avengers.netty.gamelib.key.NetworkConstant;
import com.avengers.netty.socket.gate.IMessage;
import com.avengers.netty.socket.gate.wood.Message;
import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public class GetRoomListHandler extends BaseClientRequestHandler {

	@Override
	protected void init() {
	}

	@Override
	public void handleClientRequest(User user, IMessage message) {
		Message response = DefaultMessageFactory.responseMessage(NetworkConstant.COMMAND_GET_ROOM_LIST);
		send(response, user);
	}

}

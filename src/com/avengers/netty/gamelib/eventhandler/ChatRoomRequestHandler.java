package com.avengers.netty.gamelib.eventhandler;

import java.util.List;

import com.avengers.netty.core.event.SystemNetworkConstant;
import com.avengers.netty.core.extensions.BaseClientRequestHandler;
import com.avengers.netty.core.om.IRoom;
import com.avengers.netty.core.util.DefaultMessageFactory;
import com.avengers.netty.gamelib.key.NetworkConstant;
import com.avengers.netty.socket.gate.IMessage;
import com.avengers.netty.socket.gate.wood.Message;
import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public class ChatRoomRequestHandler extends BaseClientRequestHandler {

	@Override
	protected void init() {

	}

	@Override
	public void handleClientRequest(User user, IMessage message) {
		String chatText = message.getString(SystemNetworkConstant.KEYS_MESSAGE);
		IRoom room = user.getLastJoinedRoom();
		if (room.isGame()) {
			List<User> userList = room.getListUser();
			userList.remove(user);
			Message response = DefaultMessageFactory.responseMessage(NetworkConstant.COMMAND_CHAT_ROOM);
			response.putString(SystemNetworkConstant.KEYS_USERNAME, user.getUserName());
			response.putString(SystemNetworkConstant.KEYS_MESSAGE, chatText);
			send(response, userList);
		}
	}

}

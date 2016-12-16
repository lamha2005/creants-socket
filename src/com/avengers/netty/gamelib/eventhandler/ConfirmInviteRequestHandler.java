package com.avengers.netty.gamelib.eventhandler;

import com.avengers.netty.core.event.SystemNetworkConstant;
import com.avengers.netty.core.exception.JoinRoomException;
import com.avengers.netty.core.extensions.BaseClientRequestHandler;
import com.avengers.netty.core.util.CoreTracer;
import com.avengers.netty.core.util.DefaultMessageFactory;
import com.avengers.netty.gamelib.key.ErrorCode;
import com.avengers.netty.gamelib.key.NetworkConstant;
import com.avengers.netty.socket.gate.IMessage;
import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public class ConfirmInviteRequestHandler extends BaseClientRequestHandler {

	@Override
	protected void init() {

	}

	@Override
	public void handleClientRequest(User user, IMessage message) {
		Integer roomId = message.getInt(SystemNetworkConstant.KEYI_ROOM_ID);
		if (roomId == null) {
			send(DefaultMessageFactory.createErrorMessage(NetworkConstant.COMMAND_INVITE_TO_PLAY_CONFIRM,
					ErrorCode.ROOM_NOT_FOUND, "Thiếu thông tin room"), user);
			return;
		}

		try {
			getApi().joinRoom(user, roomId, false, null);
		} catch (JoinRoomException e) {
			CoreTracer.error(this.getClass(),
					String.format("[DEBUG] [user:%s] request confirm invite join room [%s] fail! %s",
							user.getUserName(), "confirm invite error", e));
		}
	}

}

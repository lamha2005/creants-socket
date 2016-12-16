package com.avengers.netty.core.event.system;

import com.avengers.netty.core.event.handler.AbstractRequestHandler;
import com.avengers.netty.core.om.IRoom;
import com.avengers.netty.socket.gate.IMessage;
import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public class DisconnectRequestHandler extends AbstractRequestHandler {

	@Override
	public void initialize() {

	}

	@Override
	public void perform(User user, IMessage message) {
		// TODO xóa hoàn toàn đối tượng này ra khỏi hệ thống
		// Dispatch sự kiện cho game implement
		IRoom lastJoinedRoom = user.getLastJoinedRoom();
		if (lastJoinedRoom != null) {
			if (lastJoinedRoom.isGame() && lastJoinedRoom.getPlayerSize() > 1) {
				coreApi.disconnectUser(user, lastJoinedRoom);
				// TODO xử lý logout
			} else {
				coreApi.leaveRoom(user, lastJoinedRoom, false, false);
			}
		}

		channelService.disconnect(user);
	}

}

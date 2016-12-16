package com.avengers.netty.core.event.system;

import com.avengers.netty.core.event.handler.AbstractRequestHandler;
import com.avengers.netty.core.om.IRoom;
import com.avengers.netty.core.util.CoreTracer;
import com.avengers.netty.socket.gate.IMessage;
import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public class LeaveRoomRequestHandler extends AbstractRequestHandler {

	@Override
	public void initialize() {

	}

	@Override
	public void perform(User user, IMessage message) {
		IRoom lastJoinedRoom = user.getLastJoinedRoom();
		if (lastJoinedRoom != null) {
			CoreTracer.debug(LeaveRoomRequestHandler.class, "[DEBUG] user leave room.",
					"username:" + user.getUserName(), "room:" + lastJoinedRoom.getName());
			coreApi.leaveRoom(user, lastJoinedRoom);
		}
	}

}

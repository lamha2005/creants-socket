package com.avengers.netty.core.event.system;

import java.util.List;

import com.avengers.netty.core.event.SystemNetworkConstant;
import com.avengers.netty.core.event.handler.AbstractRequestHandler;
import com.avengers.netty.core.exception.JoinRoomException;
import com.avengers.netty.core.om.IRoom;
import com.avengers.netty.core.util.CoreTracer;
import com.avengers.netty.gamelib.eventhandler.JoinRoomExtensionHandler;
import com.avengers.netty.gamelib.key.ErrorCode;
import com.avengers.netty.socket.gate.IMessage;
import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public class JoinRoomRequestHandler extends AbstractRequestHandler {

	@Override
	public void initialize() {

	}

	@Override
	public void perform(User user, IMessage message) {
		if (user.getName() == null) {
			writeErrorMessage(user, SystemNetworkConstant.COMMAND_USER_JOIN_ROOM, ErrorCode.LOGIN_IS_REQUIRED,
					"Vui lòng đăng nhập trước khi join room.");
			return;
		}

		Integer roomId = message.getInt(SystemNetworkConstant.KEYI_ROOM_ID);
		if (roomId == null) {
			writeErrorMessage(user, SystemNetworkConstant.COMMAND_USER_JOIN_ROOM, ErrorCode.ROOM_NOT_FOUND,
					"Thiếu thông tin room");
			return;
		}

		IRoom room = roomService.getRoomById(roomId);
		if (room == null) {
			writeErrorMessage(user, SystemNetworkConstant.COMMAND_USER_JOIN_ROOM, ErrorCode.ROOM_NOT_FOUND,
					"Không tìm thấy room này");
			CoreTracer.error(JoinRoomExtensionHandler.class, String.format(
					"[DEBUG] [user:%s] request join room [%d] fail! Not exist this room.", user.getUserName(), roomId));
			return;
		}

		List<User> players = room.getPlayersList();
		if (players.size() >= room.getMaxUsers()) {
			writeErrorMessage(user, SystemNetworkConstant.COMMAND_USER_JOIN_ROOM, ErrorCode.OUT_OF_PLAYER,
					"Phòng đã đủ người chơi");
			return;
		}

		try {
			coreApi.joinRoom(user, room, false, null, true);
		} catch (JoinRoomException e) {
			CoreTracer.error(JoinRoomExtensionHandler.class, String.format(
					"[DEBUG] [user:%s] request join room [%s] fail! %s", user.getUserName(), room.getName(), e));
		}
	}

}

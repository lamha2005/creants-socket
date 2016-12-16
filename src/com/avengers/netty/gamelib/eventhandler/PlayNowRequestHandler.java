package com.avengers.netty.gamelib.eventhandler;

import com.avengers.netty.core.exception.JoinRoomException;
import com.avengers.netty.core.extensions.BaseClientRequestHandler;
import com.avengers.netty.core.om.IRoom;
import com.avengers.netty.core.service.GameManager;
import com.avengers.netty.core.util.CoreTracer;
import com.avengers.netty.core.util.DefaultMessageFactory;
import com.avengers.netty.gamelib.key.ErrorCode;
import com.avengers.netty.gamelib.key.NetworkConstant;
import com.avengers.netty.socket.gate.IMessage;
import com.avengers.netty.socket.gate.wood.User;

/**
 * 
 * Tính năng chơi Ngay - Chọn phòng bất kì phù hợp cho user join vào
 * 
 * @author LamHa
 *
 */
public class PlayNowRequestHandler extends BaseClientRequestHandler {

	private GameManager gameManager;

	@Override
	protected void init() {
		gameManager = GameManager.getInstance();
	}

	@Override
	public void handleClientRequest(User user, IMessage message) {
		CoreTracer.debug(this.getClass(), user.getUserName() + " request play now!");
		byte gameId = user.getCurrentGameId();
		if (gameId == -1) {
			send(DefaultMessageFactory.createErrorMessage(NetworkConstant.COMMAND_AUTO_JOIN_ROOM,
					ErrorCode.USER_NOT_JOIN_GAME, "Bạn chưa join vào game"), user);
			return;
		}

		long money = 1000;
		IRoom room = gameManager.findRoom(gameId, money);
		if (room == null) {
			send(DefaultMessageFactory.createErrorMessage(NetworkConstant.COMMAND_AUTO_JOIN_ROOM,
					ErrorCode.ROOM_NOT_FOUND, "Không tìm thấy room này trên hệ thống"), user);
			return;
		}

		CoreTracer.debug(this.getClass(), "Find room:" + room.getName());
		try {
			getParentExtension().getApi().joinRoom(user, room, false, null, true);
		} catch (JoinRoomException e) {
			send(DefaultMessageFactory.createErrorMessage(NetworkConstant.COMMAND_AUTO_JOIN_ROOM,
					ErrorCode.ROOM_NOT_FOUND, "Không tìm thấy room này trên hệ thống"), user);
			CoreTracer.error(PlayNowRequestHandler.class, "[ERROR] handleClientRequest fail!", e);
		}
	}

}

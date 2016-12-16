package com.avengers.netty.core.event.system;

import java.util.ArrayList;
import java.util.List;

import com.avengers.netty.core.event.SystemNetworkConstant;
import com.avengers.netty.core.event.handler.AbstractRequestHandler;
import com.avengers.netty.core.exception.JoinRoomException;
import com.avengers.netty.core.om.IRoom;
import com.avengers.netty.core.om.RoomPage;
import com.avengers.netty.core.service.GameManager;
import com.avengers.netty.core.util.DefaultMessageFactory;
import com.avengers.netty.core.util.GsonUtils;
import com.avengers.netty.gamelib.key.ErrorCode;
import com.avengers.netty.gamelib.key.NetworkConstant;
import com.avengers.netty.gamelib.om.RoomInfo;
import com.avengers.netty.socket.gate.IMessage;
import com.avengers.netty.socket.gate.wood.Message;
import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public class JoinGameRequestHandler extends AbstractRequestHandler {

	private GameManager gameManager;

	@Override
	public void initialize() {
		gameManager = GameManager.getInstance();
	}

	@Override
	public void perform(User user, IMessage message) {
		Byte gameId = message.getByte(SystemNetworkConstant.KEYB_GAME_ID);
		boolean containGame = gameManager.containGame(gameId);
		if (!containGame) {
			writeErrorMessage(user, SystemNetworkConstant.COMMAND_USER_JOIN_GAME, ErrorCode.GAME_EXT_LOBBY_NOT_FOUND,
					"Not exist this game!");
			return;
		}

		// join to lobby
		IRoom room = GameManager.getInstance().getLobbyGame(gameId);
		user.setCurrentGameId(gameId);
		try {
			IRoom lastJoinedRoom = user.getLastJoinedRoom();
			if (lastJoinedRoom == null) {
				coreApi.joinRoom(user, room);
			}
		} catch (JoinRoomException e) {
			writeErrorMessage(user, SystemNetworkConstant.COMMAND_USER_JOIN_GAME, ErrorCode.ROOM_NOT_FOUND,
					"Not exist this room!");
			return;
		}

		List<IRoom> roomList = gameManager.getAllRoomGame(gameId);
		RoomPage<RoomInfo> roomPage = new RoomPage<RoomInfo>();
		List<RoomInfo> rooms = new ArrayList<RoomInfo>();
		roomPage.setRooms(rooms);
		for (IRoom iRoom : roomList) {
			if (iRoom.isGame()) {
				RoomInfo roomInfo = (RoomInfo) iRoom.getProperty(NetworkConstant.ROOM_INFO);
				roomInfo.setId(iRoom.getId());
				roomInfo.setName(iRoom.getName());
				roomInfo.setMaxPlayer(iRoom.getMaxUsers());
				roomInfo.setPlayerNo(iRoom.getSize().getUserCount());

				rooms.add(roomInfo);
			}
		}

		Message response = DefaultMessageFactory.responseMessage(SystemNetworkConstant.COMMAND_USER_JOIN_GAME);
		response.putString(SystemNetworkConstant.KEYS_JSON_DATA, GsonUtils.toGsonString(roomPage));
		writeMessage(user, response);
	}

}

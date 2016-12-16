package com.avengers.netty.gamelib.eventhandler;

import java.util.List;

import com.avengers.netty.core.event.SystemNetworkConstant;
import com.avengers.netty.core.extensions.BaseClientRequestHandler;
import com.avengers.netty.core.om.IRoom;
import com.avengers.netty.core.service.GameManager;
import com.avengers.netty.core.util.DefaultMessageFactory;
import com.avengers.netty.gamelib.key.ErrorCode;
import com.avengers.netty.gamelib.key.NetworkConstant;
import com.avengers.netty.gamelib.om.RoomInfo;
import com.avengers.netty.socket.gate.IMessage;
import com.avengers.netty.socket.gate.wood.User;

/**
 * 
 * @author LamHa
 *
 */
public class InviteRequestHandler extends BaseClientRequestHandler {

	private GameManager gameManager;

	@Override
	protected void init() {
		gameManager = GameManager.getInstance();
	}

	@Override
	public void handleClientRequest(User user, IMessage message) {
		byte gameId = user.getCurrentGameId();
		if (gameId == -1) {
			send(DefaultMessageFactory.createErrorMessage(NetworkConstant.COMMAND_INVITE_TO_PLAY,
					ErrorCode.USER_NOT_JOIN_GAME, "Bạn chưa join vào game nào"), user);
			return;
		}

		IRoom room = user.getLastJoinedRoom();
		if (room == null || !room.isGame() || room.isFull()) {
			send(DefaultMessageFactory.createErrorMessage(NetworkConstant.COMMAND_INVITE_TO_PLAY,
					ErrorCode.USER_NOT_JOIN_GAME, "Bạn chưa join vào game nào"), user);
			return;
		}

		Integer userId = message.getInt(SystemNetworkConstant.KEYI_USER_ID);
		User receiver = null;
		if (userId == null) {
			IRoom lobby = gameManager.getLobbyGame(gameId);
			List<User> listUser = lobby.getListUser();
			if (listUser == null)
				return;
			int moneyBet = 0;
			Object roomInfo = room.getProperty(NetworkConstant.ROOM_INFO);
			if (roomInfo != null) {
				moneyBet = ((RoomInfo) roomInfo).getBetCoin();
			}

			for (User u : listUser) {
				if (u.getMoney() >= moneyBet) {
					receiver = u;
					break;
				}
			}
		} else {
			receiver = getApi().getUserById(userId);
		}

		message = DefaultMessageFactory.createMessage(NetworkConstant.COMMAND_INVITE_TO_PLAY);
		if (receiver == null) {
			message.putByte(SystemNetworkConstant.KEYB_STATUS, (byte) 0);
			message.putString(SystemNetworkConstant.KEYS_MESSAGE, "Không tìm thấy người chơi thích hợp");
			send(message, user);
			return;
		}

		message.putByte(SystemNetworkConstant.KEYB_STATUS, (byte) 2);
		send(message, user);

		message = DefaultMessageFactory.createMessage(NetworkConstant.COMMAND_INVITE_TO_PLAY);
		message.putByte(SystemNetworkConstant.KEYB_STATUS, (byte) 1);
		message.putInt(SystemNetworkConstant.KEYI_ROOM_ID, room.getId());
		message.putString(SystemNetworkConstant.KEYS_USERNAME, user.getUserName());
		message.putString(SystemNetworkConstant.KEYS_ROOM_NAME, room.getName());

		RoomInfo roomInfo = (RoomInfo) room.getProperty(NetworkConstant.ROOM_INFO);
		message.putLong(SystemNetworkConstant.KEYL_MONEY, roomInfo.getBetCoin());
		message.putString(SystemNetworkConstant.KEYS_MESSAGE, "Mời bạn vào phòng chơi.");

		send(message, receiver);
	}

}

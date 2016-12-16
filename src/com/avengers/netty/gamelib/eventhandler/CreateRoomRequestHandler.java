package com.avengers.netty.gamelib.eventhandler;

import java.util.HashMap;
import java.util.Map;

import com.avengers.netty.SocketServer;
import com.avengers.netty.core.exception.CreateRoomException;
import com.avengers.netty.core.extensions.BaseClientRequestHandler;
import com.avengers.netty.core.om.CreateRoomSettings;
import com.avengers.netty.core.om.GameSettings;
import com.avengers.netty.core.service.GameManager;
import com.avengers.netty.core.service.RoomManager;
import com.avengers.netty.core.util.CoreTracer;
import com.avengers.netty.core.util.DefaultMessageFactory;
import com.avengers.netty.gamelib.key.ErrorCode;
import com.avengers.netty.gamelib.key.NetworkConstant;
import com.avengers.netty.gamelib.om.RoomInfo;
import com.avengers.netty.socket.gate.IMessage;
import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public class CreateRoomRequestHandler extends BaseClientRequestHandler {
	private GameManager gameManager;
	private RoomManager roomService;

	@Override
	protected void init() {
		gameManager = GameManager.getInstance();
		roomService = SocketServer.getInstance().getRoomService();
	}

	@Override
	public void handleClientRequest(User user, IMessage message) {
		Integer betCoin = message.getInt(NetworkConstant.KEYI_BETCOIN);
		if (betCoin == null)
			betCoin = 0;

		CoreTracer.debug(this.getClass(),
				String.format("[DEBUG] [user: %s] request create room with betcon = %d", user.getUserName(), betCoin));

		boolean checkEnoughMoney = user.getMoney() >= betCoin;
		if (!checkEnoughMoney) {
			send(DefaultMessageFactory.createErrorMessage(NetworkConstant.COMMAND_CREATE_ROOM,
					ErrorCode.NOT_ENOUGH_MONEY, "Bạn không đủ tiền"), user);
			CoreTracer.debug(this.getClass(),
					String.format("[DEBUG] [user: %s] Not enough money to create room!", user.getUserName()));
			return;
		}

		GameSettings gameSettings = gameManager.getGameSettings(user.getCurrentGameId());

		CreateRoomSettings settings = new CreateRoomSettings();
		settings.setGame(true);
		settings.setGameId(gameSettings.gameId);
		settings.setGroupId(gameSettings.groupId);
		settings.setExtension(gameSettings.extension);
		Integer maxUser = message.getInt(NetworkConstant.KEYI_MAX_USER);
		if (maxUser != null) {
			settings.setMaxUsers(maxUser);
		}

		RoomInfo roomInfo = new RoomInfo(user.getCurrentGameId());
		roomInfo.setBetCoin(betCoin);
		roomInfo.setOwner(user.getUserName());
		roomInfo.setTimeOut(90);
		roomInfo.setName(roomService.generateRoomName());

		Map<Object, Object> roomProperties = new HashMap<Object, Object>();
		roomProperties.put(NetworkConstant.ROOM_INFO, roomInfo);
		settings.setRoomProperties(roomProperties);

		try {
			getApi().createRoom(settings, user, true, false, false);
		} catch (CreateRoomException e) {
			send(DefaultMessageFactory.createErrorMessage(NetworkConstant.COMMAND_CREATE_ROOM,
					ErrorCode.CREATE_ROOM_FAILED, e.getMessage()), user);

			CoreTracer.error(this.getClass(),
					String.format("[ERROR] [user: %s] %s!", user.getUserName(), e.getMessage()));
		}
	}

}

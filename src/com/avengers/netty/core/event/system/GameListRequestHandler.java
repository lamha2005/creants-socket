package com.avengers.netty.core.event.system;

import java.util.ArrayList;
import java.util.List;

import com.avengers.netty.core.event.SystemNetworkConstant;
import com.avengers.netty.core.event.handler.AbstractRequestHandler;
import com.avengers.netty.core.om.GameSettings;
import com.avengers.netty.core.om.ext.GameInfo;
import com.avengers.netty.core.service.GameManager;
import com.avengers.netty.core.util.DefaultMessageFactory;
import com.avengers.netty.core.util.GsonUtils;
import com.avengers.netty.socket.gate.IMessage;
import com.avengers.netty.socket.gate.wood.Message;
import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public class GameListRequestHandler extends AbstractRequestHandler {
	private GameManager gameManager;

	@Override
	public void initialize() {
		gameManager = GameManager.getInstance();
	}

	@Override
	public void perform(User user, IMessage message) {
		Message response = DefaultMessageFactory.responseMessage(SystemNetworkConstant.COMMAND_GET_GAME_LIST);
		List<GameSettings> allGameSettings = gameManager.getAllGameSettings();
		List<GameInfo> gameList = new ArrayList<GameInfo>(allGameSettings.size());
		GameInfo gameInfo;
		for (GameSettings gameSettings : allGameSettings) {
			gameInfo = new GameInfo();
			gameInfo.setGameId(gameSettings.gameId);
			// TODO tùy vào location của user mà trả về anh hay việt. Có thể
			// cache lại danh sách game và trả luôn anh và việt cho client tự xử
			gameInfo.setGameName(gameSettings.gameNameVi);
			gameInfo.setImageUrl("https://mockingbot.in/uploads/images/97014/raw_1458551337.png");
			gameList.add(gameInfo);
		}

		response.putString(SystemNetworkConstant.KEYS_JSON_DATA, GsonUtils.toGsonString(gameList));
		response.putString(SystemNetworkConstant.KEYS_USERNAME, user.getUserName());
		response.putLong(SystemNetworkConstant.KEYL_MONEY, user.getMoney());
		response.putString(SystemNetworkConstant.KEYS_AVATAR, user.getAvatar());
		
		writeMessage(user, response);
	}

}

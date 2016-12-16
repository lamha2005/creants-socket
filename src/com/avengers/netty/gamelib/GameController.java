/**
 * 
 */
package com.avengers.netty.gamelib;

import com.avengers.netty.core.log.BoardLog;
import com.avengers.netty.core.om.IRoom;
import com.avengers.netty.gamelib.om.PlayerIdGenerator;
import com.avengers.netty.gamelib.result.IPlayMoveResult;
import com.avengers.netty.socket.gate.wood.Message;
import com.avengers.netty.socket.gate.wood.User;
import com.google.gson.JsonObject;

/**
 * Đối tượng điều phối workflow khi user chơi game. <br>
 * Mỗi room sẽ có một GameController tương ứng
 * 
 * @author LamHa
 *
 */
public class GameController {
	private final IRoom room;
	private final GameInterface iGame;
	private final GameAPI gameApi;
	private final PlayerIdGenerator playerIdGenerator;
	private BoardLog boardLog;

	public GameController(IRoom room, GameInterface gamgeInterface) {
		this.room = room;
		iGame = gamgeInterface;
		iGame.setApi((gameApi = new GameAPI(this)));
		playerIdGenerator = new PlayerIdGenerator(room.getMaxUsers());
	}

	public IRoom getRoom() {
		return room;
	}

	public GameAPI getApi() {
		return gameApi;
	}

	public GameInterface getGameInterface() {
		return iGame;
	}

	public void dispatchEvent(short commandId, User user, Message message) {
		iGame.dispatchEvent(commandId, user, message);
	}

	public void onPlayMove(User sender, Message data) {
		IPlayMoveResult result = iGame.onPlayMoveHandle(sender, data);
		if (result != null) {
			// Xử lý kết quả sau khi thực hiện 1 nước đi trong game
			result.handleResult(this);
		}
	}

	public JsonObject getGameData() {
		return iGame.getGameData();
	}

	public void leaveRoom(User user, IRoom room) {
		iGame.leaveRoom(user, room);
	}

	public BoardLog getBoardLog() {
		return boardLog;
	}

	public PlayerIdGenerator getPlayerIdGenerator() {
		return playerIdGenerator;
	}

}

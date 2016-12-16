package com.avengers.netty.gamelib;

import java.util.List;

import com.avengers.netty.core.dao.DataManager;
import com.avengers.netty.core.event.SystemNetworkConstant;
import com.avengers.netty.core.exception.RoomException;
import com.avengers.netty.core.om.IRoom;
import com.avengers.netty.core.om.ServerConfig;
import com.avengers.netty.core.util.CoreTracer;
import com.avengers.netty.core.util.DefaultMessageFactory;
import com.avengers.netty.gamelib.key.NetworkConstant;
import com.avengers.netty.gamelib.om.RoomInfo;
import com.avengers.netty.gamelib.scheduler.ITimerScheduler;
import com.avengers.netty.gamelib.scheduler.SchedulerManager;
import com.avengers.netty.gamelib.scheduler.TimeoutScheduler;
import com.avengers.netty.socket.gate.wood.Message;
import com.avengers.netty.socket.gate.wood.User;
import com.couchbase.client.java.document.json.JsonObject;

/**
 * FLOW: GAME -> GAMELIB<br>
 * Các API cung cấp các tính năng Game cần gọi ngược ra GAMELIB xử lý
 * 
 * @author LamHa
 *
 */
public class GameAPI {
	private final GameController gameController;

	public GameAPI(GameController gameLogic) {
		this.gameController = gameLogic;
	}

	public GameController getController() {
		return this.gameController;
	}

	public void startTimeOut(long timeMillis, Message params) {
		if (timeMillis > 0) {
			String scheduleId = TimeoutScheduler.generateId(gameController.getRoom());
			ITimerScheduler timeoutScheduler = SchedulerManager.getInstance().getTimerScheduler(scheduleId);
			if (timeoutScheduler != null) {
				timeoutScheduler.cancelTimeout();
				timeoutScheduler.startCountdown(timeMillis + ServerConfig.delayTimeMillis);
			}
		}
	}

	public long getRemainTimeOut() {
		String scheduleId = TimeoutScheduler.generateId(gameController.getRoom());
		ITimerScheduler timerScheduler = SchedulerManager.getInstance().getTimerScheduler(scheduleId);
		if (timerScheduler != null)
			return timerScheduler.getTimeMillis();

		return 0;
	}

	public void cancelTimeOut() {
		String scheduleId = TimeoutScheduler.generateId(gameController.getRoom());
		ITimerScheduler timerScheduler = SchedulerManager.getInstance().getTimerScheduler(scheduleId);
		if (timerScheduler != null)
			timerScheduler.cancelTimeout();
	}

	public void sendToUser(Message message, User user) {
		((GameExtension) (gameController.getRoom().getExtension())).sendResponse(message, user);
	}

	public void sendAllInRoomExceptUser(Message data, User user) {
		List<User> users = gameController.getRoom().getListUser();
		users.remove(user);
		sendResponseForListUser(data, users);
	}

	public void sendAllInRoomExceptUsers(Message data, List<Integer> userIds) {
		List<User> users = gameController.getRoom().getListUser();
		for (int userId : userIds) {
			users.remove(gameController.getRoom().getUserById(userId));
		}

		sendResponseForListUser(data, users);
	}

	public void sendAllInRoom(Message message) {
		List<User> users = gameController.getRoom().getListUser();
		sendResponseForListUser(message, users);
	}

	public void sendResponseForListUser(Message message, List<User> users) {
		GameExtension gameExtension = (GameExtension) (gameController.getRoom().getExtension());
		gameExtension.sendResponseForListUser(message, users);
	}

	public long getBetCoin() {
		return ((RoomInfo) gameController.getRoom().getProperty(NetworkConstant.ROOM_INFO)).getBetCoin();
	}

	public int getTimeOut() {
		return ((RoomInfo) gameController.getRoom().getProperty(NetworkConstant.ROOM_INFO)).getTimeOut();
	}

	public String getRules() {
		return ((RoomInfo) gameController.getRoom().getProperty(NetworkConstant.ROOM_INFO)).getRules();
	}

	/**
	 * Cập nhật tiền của User ở server, db, và ghi log lịch sử tiền
	 * 
	 * @param user
	 * @param value
	 * @param reasonId
	 */
	public void updateUserMoney(User user, long value, int reasonId, String description) {
		// UserMoneyLog moneyLog = new UserMoneyLog();
		// moneyLog.setUserId(user.getUserId());
		// moneyLog.setUserName(user.getUserName());
		// moneyLog.setMoney(user.getMoney());
		// moneyLog.setValue(value);
		// moneyLog.setServerId(ServerConfig.serverId);
		// moneyLog.setReasonId(reasonId);
		// moneyLog.setReasonText("");
		// moneyLog.setDescription(description);
		// moneyLog.setBoardId(getController().getBoardLog().getId());

		user.setMoney(DataManager.getInstance().incrementUserMoney(user.getUid(), value));
		JsonObject jo = JsonObject.create();
		jo.put("current_money", user.getMoney());
		jo.put("add_money", value);
		jo.put("reason_id", reasonId);
		jo.put("desc", description);

		Message message = DefaultMessageFactory.createMessage(SystemNetworkConstant.COMMAND_MONEY_CHANGE);
		message.putString(SystemNetworkConstant.KEYS_JSON_DATA, jo.toString());
		sendToUser(message, user);
		CoreTracer.debug(GameAPI.class, "[INFO] ================ updateUserMoney:" + user.getUserName() + "/uid:"
				+ user.getUid() + " /data: " + jo.toString());
	}

	/**
	 * Cập nhật level của User ở server, db khi end game
	 * 
	 * @param User
	 * @param resultType
	 * @param newXp
	 * @param extraData
	 */
	public void updateUserLevel(User user, byte resultType, int newXp, String extraData) {
		// Level userLevel = user.getLevel();
		// userLevel.updateResult(resultType);
		// userLevel.setXp(newXp);
		// userLevel.setExtraData(extraData);
		// DataManager.instance.updateUserData(user.getId(), userLevel);
	}

	/**
	 * Chuyển player
	 * 
	 * @param userId
	 */
	public void switchPlayerToSpectator(int userId) {
		User player = gameController.getRoom().getUserById(userId);
		if (player.isPlayer()) {
			try {
				gameController.getRoom().switchPlayerToSpectator(player);
			} catch (RoomException e) {
				CoreTracer.error(GameAPI.class, "[ERROR] switchPlayerToSpectator fail!", e);
			}
		}
	}

	public void switchSpectatorToPlayer(int userId) {
		User spectator = gameController.getRoom().getUserById(userId);
		if (spectator.isSpectator()) {
			try {
				gameController.getRoom().switchSpectatorToPlayer(spectator);
				if (getOwner() == null) {
					gameController.getRoom().setOwner(spectator);
				}

				Message message = DefaultMessageFactory
						.createMessage(NetworkConstant.COMMAND_SWITCH_SPECTATOR_TO_PLAYER);
				message.putInt(SystemNetworkConstant.KEYI_USER_ID, spectator.getCreantUserId());
				message.putString(SystemNetworkConstant.KEYS_USERNAME, spectator.getUserName());
				message.putString(SystemNetworkConstant.KEYS_AVATAR, spectator.getAvatar());
				message.putLong(SystemNetworkConstant.KEYL_MONEY, spectator.getMoney());
				message.putByte(NetworkConstant.KEYB_IS_OWNER, (spectator == getOwner()) ? (byte) 1 : (byte) 0);
				message.putInt(NetworkConstant.KEYI_PLAYER_ID, spectator.getPlayerId());
				sendAllInRoom(message);
			} catch (RoomException e) {
				CoreTracer.error(GameAPI.class, "[ERROR] switchSpectatorToPlayer fail!", e);
			}
		}
	}

	public void leaveRoom(int userId) {
		IRoom room = gameController.getRoom();
		User user = room.getUserById(userId);
		if (user != null) {
			GameExtension gameExtension = (GameExtension) (room.getExtension());
			gameExtension.getApi().leaveRoom(user, room);
		}
	}

	public User getOwner() {
		return gameController.getRoom().getOwner();
	}

}
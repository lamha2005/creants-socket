package com.avengers.netty.gamelib.eventhandler;

import java.util.ArrayList;
import java.util.List;

import com.avengers.netty.core.event.SystemNetworkConstant;
import com.avengers.netty.core.extensions.BaseClientRequestHandler;
import com.avengers.netty.core.om.IRoom;
import com.avengers.netty.core.service.GameManager;
import com.avengers.netty.core.util.DefaultMessageFactory;
import com.avengers.netty.gamelib.key.ErrorCode;
import com.avengers.netty.gamelib.key.NetworkConstant;
import com.avengers.netty.socket.gate.IMessage;
import com.avengers.netty.socket.gate.wood.Message;
import com.avengers.netty.socket.gate.wood.User;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;

/**
 * 
 * Tính năng chơi Ngay - Chọn phòng bất kì phù hợp cho user join vào
 * 
 * @author LamHa
 *
 */
public class GetUsersRequestHandler extends BaseClientRequestHandler {

	private GameManager gameManager;

	@Override
	protected void init() {
		gameManager = GameManager.getInstance();
	}

	@Override
	public void handleClientRequest(User user, IMessage message) {
		byte gameId = user.getCurrentGameId();
		if (gameId == -1) {
			send(DefaultMessageFactory.createErrorMessage(NetworkConstant.COMMAND_GET_USERS_IN_LOBBY,
					ErrorCode.USER_NOT_JOIN_GAME, "Bạn chưa join vào game nào"), user);
			return;
		}

		IRoom curRoom = user.getLastJoinedRoom();
		if (curRoom == null || !curRoom.isGame() || curRoom.isFull()) {
			send(DefaultMessageFactory.createErrorMessage(NetworkConstant.COMMAND_GET_USERS_IN_LOBBY,
					ErrorCode.USER_NOT_JOIN_GAME, "Thao tác không hợp lệ"), user);
			return;
		}
		long money = 1000;

		IRoom lobby = gameManager.getLobbyGame(gameId);
		List<User> listUser = lobby.getListUser();
		List<User> users = new ArrayList<User>();
		if (listUser != null) {
			for (User u : listUser) {
				if (u.getMoney() >= money) {
					users.add(u);
				}
			}
		}

		JsonObject jsonData = JsonObject.create();
		jsonData.put("next_page", false);

		JsonArray ja = JsonArray.create();
		for (User u : users) {
			JsonObject jo = JsonObject.create();
			jo.put("full_name", u.getUserName());
			jo.put("user_id", u.getUserId());
			jo.put("avatar", u.getAvatar());
			ja.add(jo);
		}

		jsonData.put("users", ja);

		Message response = DefaultMessageFactory.responseMessage(NetworkConstant.COMMAND_GET_USERS_IN_LOBBY);
		response.putString(SystemNetworkConstant.KEYS_JSON_DATA, jsonData.toString());
		send(response, user);
	}

	public static void main(String[] args) {

		List<User> users = new ArrayList<>();
		User user1 = new User();
		user1.setAvatar(
				"http://icons.iconarchive.com/icons/hopstarter/superhero-avatar/256/Avengers-Iron-Man-icon.png");
		user1.setUserId(100);
		user1.setUserName("lamha");

		User user2 = new User();
		user2.setAvatar(
				"http://icons.iconarchive.com/icons/hopstarter/superhero-avatar/256/Avengers-Iron-Man-icon.png");
		user2.setUserId(101);
		user2.setUserName("dungchuoi");

		users.add(user1);
		users.add(user2);

		JsonObject jsonData = JsonObject.create();
		jsonData.put("next_page", false);

		JsonArray ja = JsonArray.create();
		for (User u : users) {
			JsonObject jo = JsonObject.create();
			jo.put("full_name", u.getUserName());
			jo.put("user_id", u.getUserId());
			jo.put("avatar", u.getAvatar());
			ja.add(jo);
		}
		jsonData.put("users", ja);

		System.out.println(jsonData.toString());
	}

}

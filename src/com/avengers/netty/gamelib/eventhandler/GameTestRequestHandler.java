package com.avengers.netty.gamelib.eventhandler;

import com.avengers.netty.core.event.SystemNetworkConstant;
import com.avengers.netty.core.extensions.BaseClientRequestHandler;
import com.avengers.netty.core.util.CoreTracer;
import com.avengers.netty.gamelib.GameController;
import com.avengers.netty.gamelib.GameExtension;
import com.avengers.netty.socket.gate.IMessage;
import com.avengers.netty.socket.gate.wood.User;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;

/**
 * @author LamHa
 *
 */
public class GameTestRequestHandler extends BaseClientRequestHandler {

	@Override
	protected void init() {

	}

	@Override
	public void handleClientRequest(User user, IMessage message) {
		CoreTracer.debug(this.getClass(),
				"[DEBUG] test game data: " + message.getString(SystemNetworkConstant.KEYS_JSON_DATA));

		GameExtension gameExt = (GameExtension) getParentExtension();
		GameController gameController = gameExt.gameController;
		gameController.getGameInterface().test(user, message);
	}

	public static void main(String[] args) {

		JsonObject jo = JsonObject.create();
		jo.put("gameId", 2);
		jo.put("service", 1);
		JsonArray ja = JsonArray.create();
		JsonObject player1 = JsonObject.create();
		player1.put("name", "Lam Ha");
		player1.put("cards", "34,21,21,1,3,2");
		ja.add(player1);

		JsonObject player2 = JsonObject.create();
		player2.put("name", "User 1");
		player2.put("cards", "35,22,21,1,3,12");
		ja.add(player2);
		jo.put("data", ja);

		System.out.println(jo.toString());
	}

}

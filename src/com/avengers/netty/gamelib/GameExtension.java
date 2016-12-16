package com.avengers.netty.gamelib;

import java.util.List;

import com.avengers.netty.core.event.SystemNetworkConstant;
import com.avengers.netty.core.extensions.CoreExtension;
import com.avengers.netty.core.util.CoreTracer;
import com.avengers.netty.gamelib.eventhandler.ChatRoomRequestHandler;
import com.avengers.netty.gamelib.eventhandler.ConfirmInviteRequestHandler;
import com.avengers.netty.gamelib.eventhandler.CreateRoomRequestHandler;
import com.avengers.netty.gamelib.eventhandler.GameTestRequestHandler;
import com.avengers.netty.gamelib.eventhandler.GetRoomListHandler;
import com.avengers.netty.gamelib.eventhandler.GetUsersRequestHandler;
import com.avengers.netty.gamelib.eventhandler.InGameRequestHandler;
import com.avengers.netty.gamelib.eventhandler.InviteRequestHandler;
import com.avengers.netty.gamelib.eventhandler.JoinRoomExtensionHandler;
import com.avengers.netty.gamelib.eventhandler.LoginExtensionHandler;
import com.avengers.netty.gamelib.eventhandler.PlayNowRequestHandler;
import com.avengers.netty.gamelib.eventhandler.UserDisconnectExtensionHandler;
import com.avengers.netty.gamelib.eventhandler.UserLeaveRoomExtensionHandler;
import com.avengers.netty.gamelib.key.NetworkConstant;
import com.avengers.netty.socket.gate.wood.Message;
import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public abstract class GameExtension extends CoreExtension {

	public GameController gameController;
	private static GameExtension instance;

	public static GameExtension getExtension() {
		return instance;
	}

	public GameExtension() {
		if (instance == null) {
			instance = this;
		}
	}

	@Override
	public void init() {
		CoreTracer.info(GameExtension.class, "[INFO] init GameLib Extension");
		gameController = new GameController(getCurrentRoom(), initInterface());
		addEventHandler();
	}

	private void addEventHandler() {
		// FIXME hiện tại chỗ này chưa đúng, mỗi game chỉ có 1 extension này
		// thôi
		CoreTracer.info(GameExtension.class, "[INFO] Init Default Event In Game");
		addEventHandler(SystemNetworkConstant.COMMAND_USER_LOGIN, LoginExtensionHandler.class);
		addEventHandler(SystemNetworkConstant.COMMAND_USER_JOIN_ROOM, JoinRoomExtensionHandler.class);
		addEventHandler(SystemNetworkConstant.COMMAND_USER_LEAVE_ROOM, UserLeaveRoomExtensionHandler.class);
		addEventHandler(SystemNetworkConstant.COMMAND_USER_DISCONNECT, UserDisconnectExtensionHandler.class);

		addRequestHandler(NetworkConstant.COMMAND_REQUEST_IN_GAME, InGameRequestHandler.class);
		addRequestHandler(NetworkConstant.COMMAND_AUTO_JOIN_ROOM, PlayNowRequestHandler.class);
		addRequestHandler(NetworkConstant.COMMAND_CHAT_ROOM, ChatRoomRequestHandler.class);
		addRequestHandler(NetworkConstant.COMMAND_GET_ROOM_LIST, GetRoomListHandler.class);
		addRequestHandler(NetworkConstant.COMMAND_GET_USERS_IN_LOBBY, GetUsersRequestHandler.class);
		addRequestHandler(NetworkConstant.COMMAND_INVITE_TO_PLAY, InviteRequestHandler.class);
		addRequestHandler(NetworkConstant.COMMAND_INVITE_TO_PLAY_CONFIRM, ConfirmInviteRequestHandler.class);
		addRequestHandler(NetworkConstant.COMMAND_TEST, GameTestRequestHandler.class);

		addRequestHandler(NetworkConstant.COMMAND_CREATE_ROOM, CreateRoomRequestHandler.class);
	}

	/**
	 * Khởi tạo RoomLogic bàn do hệ thống tạo
	 */
	public abstract GameInterface initInterface();

	/**
	 * Handle message forward from CoreExtension
	 */
	public void handleInternalMessage() {

	}

	/**
	 * Send response cho client phai di qua ham nay để encrypt Data
	 * 
	 * @param msgId
	 * @param message
	 * @param user
	 */
	public void sendResponse(Message message, User user) {
		getExtension().getApi().sendExtensionResponse(message, user);
	}

	public void sendResponseForListUser(Message message, List<User> users) {
		getExtension().getApi().sendExtensionResponse(message, users);
	}

}

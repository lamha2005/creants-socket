package com.avengers.netty.core.event.handler;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.avengers.netty.SocketServer;
import com.avengers.netty.core.event.SystemNetworkConstant;
import com.avengers.netty.core.event.system.ConnectRequestHandler;
import com.avengers.netty.core.event.system.DisconnectRequestHandler;
import com.avengers.netty.core.event.system.GameListRequestHandler;
import com.avengers.netty.core.event.system.JoinGameRequestHandler;
import com.avengers.netty.core.event.system.JoinRoomRequestHandler;
import com.avengers.netty.core.event.system.LeaveRoomRequestHandler;
import com.avengers.netty.core.event.system.LoginRequestHandler;
import com.avengers.netty.core.event.system.LogoutRequestHandler;
import com.avengers.netty.core.event.system.PingPongRequestHandler;
import com.avengers.netty.core.service.IUserManager;
import com.avengers.netty.core.service.RoomManager;
import com.avengers.netty.socket.gate.IMessage;
import com.avengers.netty.socket.gate.IMessageWriter;

/**
 * 
 * @author LamHa
 *
 */
public class SystemHandlerManager {
	private Map<Short, AbstractRequestHandler> systemHandler;

	// TODO review EventManager SFS
	public SystemHandlerManager() {
		systemHandler = new ConcurrentHashMap<Short, AbstractRequestHandler>();
		systemHandler.put(SystemNetworkConstant.COMMAND_PING_PONG, new PingPongRequestHandler());
		systemHandler.put(SystemNetworkConstant.COMMAND_USER_CONNECT, new ConnectRequestHandler());
		systemHandler.put(SystemNetworkConstant.COMMAND_USER_DISCONNECT, new DisconnectRequestHandler());
		systemHandler.put(SystemNetworkConstant.COMMAND_USER_LOGIN, new LoginRequestHandler());
		systemHandler.put(SystemNetworkConstant.COMMAND_USER_LOGOUT, new LogoutRequestHandler());
		systemHandler.put(SystemNetworkConstant.COMMAND_USER_JOIN_GAME, new JoinGameRequestHandler());
		systemHandler.put(SystemNetworkConstant.COMMAND_GET_GAME_LIST, new GameListRequestHandler());
		systemHandler.put(SystemNetworkConstant.COMMAND_USER_JOIN_ROOM, new JoinRoomRequestHandler());
		systemHandler.put(SystemNetworkConstant.COMMAND_USER_LEAVE_ROOM, new LeaveRoomRequestHandler());
	}

	public void init(IUserManager userManager, IMessageWriter messageWriter, RoomManager roomService) {
		for (Entry<Short, AbstractRequestHandler> handlerEntry : systemHandler.entrySet()) {
			AbstractRequestHandler handler = handlerEntry.getValue();
			handler.setUserManager(userManager);
			handler.setMessageWriter(messageWriter);
			handler.setCoreApi(SocketServer.getInstance().getAPIManager().getCoreApi());
			handler.setRoomService(roomService);
		}
	}

	public AbstractRequestHandler getHandler(Short commandId) {
		return systemHandler.get(commandId);
	}

	public void dispatchEvent(IMessage message) {
		// TODO validate
		AbstractRequestHandler requestHandler = systemHandler.get(message.getCommandId());
		requestHandler.perform(message.getUser(), message);
	}
}

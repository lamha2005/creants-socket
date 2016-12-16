package com.avengers.netty.gamelib.eventhandler;

import java.util.List;

import com.avengers.netty.SocketServer;
import com.avengers.netty.core.exception.JoinRoomException;
import com.avengers.netty.core.extensions.BaseClientRequestHandler;
import com.avengers.netty.core.om.IRoom;
import com.avengers.netty.core.om.ServerConfig;
import com.avengers.netty.core.om.cluster.ClusterRoom;
import com.avengers.netty.core.service.GameManager;
import com.avengers.netty.core.service.IClusterService;
import com.avengers.netty.core.util.CoreTracer;
import com.avengers.netty.core.util.DefaultMessageFactory;
import com.avengers.netty.gamelib.key.ErrorCode;
import com.avengers.netty.gamelib.key.NetworkConstant;
import com.avengers.netty.socket.gate.IMessage;
import com.avengers.netty.socket.gate.wood.User;

/**
 * 
 * Tính năng chơi Ngay - Chọn phòng bất kì phù hợp cho user join vào
 * 
 * @author LamHa
 *
 */
public class AutoJoinRoomRequestHandler extends BaseClientRequestHandler {

	private IClusterService clusterService;
	private GameManager gameManager;

	@Override
	protected void init() {
		clusterService = SocketServer.getInstance().getClusterService();
		gameManager = GameManager.getInstance();
	}

	@Override
	public void handleClientRequest(User user, IMessage message) {
		byte gameId = user.getCurrentGameId();
		if (gameId == -1) {
			send(DefaultMessageFactory.createErrorMessage(NetworkConstant.COMMAND_AUTO_JOIN_ROOM,
					ErrorCode.USER_NOT_JOIN_GAME, "User has not joined game yet"), user);
			return;
		}

		List<IRoom> rooms = gameManager.getAvailableRooms(gameId, user.getMoney());
		if (rooms.isEmpty()) {
			send(DefaultMessageFactory.createErrorMessage(NetworkConstant.COMMAND_AUTO_JOIN_ROOM,
					ErrorCode.ROOM_LIST_EMPTY, "No available rooms for user"), user);
			return;
		}

		IRoom room = rooms.get(0);
		// trường hợp local còn phòng trống
		if (room != null) {
			try {
				getParentExtension().getApi().joinRoom(user, room, true, "", true);
			} catch (JoinRoomException e) {
				CoreTracer.error(AutoJoinRoomRequestHandler.class, "[ERROR] handleClientRequest fail!", e);
			}
		} else {
			// trường hợp local hết phòng, tìm phòng trống trên cluster
			ClusterRoom clusterRoom = clusterService.findRoom(ServerConfig.getServerHost());
			if (clusterRoom != null) {
				clusterRoom.setClusterId(user.getCurrentGameId(), clusterRoom.getId());
			}
		}
	}

}

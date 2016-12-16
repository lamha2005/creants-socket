/**
 * 
 */
package com.avengers.netty.gamelib.eventhandler;

import com.avengers.netty.core.event.CoreEventParam;
import com.avengers.netty.core.event.ICoreEvent;
import com.avengers.netty.core.event.SystemNetworkConstant;
import com.avengers.netty.core.extensions.BaseServerEventHandler;
import com.avengers.netty.core.om.IRoom;
import com.avengers.netty.core.util.CoreTracer;
import com.avengers.netty.core.util.DefaultMessageFactory;
import com.avengers.netty.gamelib.GameExtension;
import com.avengers.netty.gamelib.key.ErrorCode;
import com.avengers.netty.gamelib.key.NetworkConstant;
import com.avengers.netty.gamelib.om.RoomInfo;
import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public class JoinRoomExtensionHandler extends BaseServerEventHandler {

	@Override
	public void handleServerEvent(ICoreEvent event) {
		User joiner = (User) event.getParameter(CoreEventParam.USER);
		IRoom room = (IRoom) event.getParameter(CoreEventParam.ROOM);
		RoomInfo roomInfo = (RoomInfo) room.getProperty(NetworkConstant.ROOM_INFO);

		CoreTracer.debug(this.getClass(),
				String.format("[DEBUG] [user:%s] request join room [%s]!", joiner.getUserName(), roomInfo.getName()));

		GameExtension gameExtension = (GameExtension) room.getExtension();
		// TODO bắt lỗi
		boolean result = gameExtension.gameController.getGameInterface().joinRoom(joiner, room);
		if (!result) {
			room.removeUser(joiner);
			send(DefaultMessageFactory.createErrorMessage(SystemNetworkConstant.COMMAND_USER_JOIN_ROOM,
					ErrorCode.NOT_ENOUGH_MONEY, "Bạn không đủ tiền"), joiner);
			CoreTracer.debug(this.getClass(),
					String.format("[DEBUG] [user:%s] request join room [%s] fail! Not enough money",
							joiner.getUserName(), roomInfo.getName()));
		}
	}

}

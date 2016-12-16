/**
 * 
 */
package com.avengers.netty.gamelib.eventhandler;

import com.avengers.netty.core.event.CoreEventParam;
import com.avengers.netty.core.event.ICoreEvent;
import com.avengers.netty.core.event.SystemNetworkConstant;
import com.avengers.netty.core.exception.JoinRoomException;
import com.avengers.netty.core.extensions.BaseServerEventHandler;
import com.avengers.netty.core.om.IRoom;
import com.avengers.netty.core.service.GameManager;
import com.avengers.netty.core.util.CoreTracer;
import com.avengers.netty.core.util.DefaultMessageFactory;
import com.avengers.netty.gamelib.GameExtension;
import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public class UserLeaveRoomExtensionHandler extends BaseServerEventHandler {

	@Override
	public void handleServerEvent(ICoreEvent event) {
		User user = (User) event.getParameter(CoreEventParam.USER);
		IRoom room = (IRoom) event.getParameter(CoreEventParam.ROOM);
		System.out.println("[ERROR]---------------- leave room" + room.getPlayersList() );

		GameExtension gameExtension = (GameExtension) room.getExtension();
		gameExtension.getApi().sendExtensionResponse(
				DefaultMessageFactory.createMessage(SystemNetworkConstant.COMMAND_USER_LEAVE_ROOM), user);

		if (!room.isGame() || room.isEmpty()) {
			return;
		}

		// rời game trở về lobby
		IRoom lobby = GameManager.getInstance().getLobbyGame(user.getCurrentGameId());
		CoreTracer.debug(this.getClass(), "User: " + user.getName() + " rejoin to lobby");

		try {
			gameExtension.getApi().joinRoom(user, lobby);
		} catch (JoinRoomException e) {
			e.printStackTrace();
		}

		// gọi tới game để xử lý rời phòng
		gameExtension.gameController.leaveRoom(user, room);
	}

}

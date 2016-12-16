package com.avengers.netty.gamelib.eventhandler;

import com.avengers.netty.core.event.CoreEventParam;
import com.avengers.netty.core.event.ICoreEvent;
import com.avengers.netty.core.extensions.BaseServerEventHandler;
import com.avengers.netty.core.om.IRoom;
import com.avengers.netty.core.util.CoreTracer;
import com.avengers.netty.gamelib.GameExtension;
import com.avengers.netty.gamelib.GameInterface;
import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public class UserDisconnectExtensionHandler extends BaseServerEventHandler {

	@Override
	public void handleServerEvent(ICoreEvent event) {
		User joiner = (User) event.getParameter(CoreEventParam.USER);
		IRoom room = (IRoom) event.getParameter(CoreEventParam.ROOM);

		CoreTracer.debug(this.getClass(), String.format("[DEBUG] [user:%s] disconnected!", joiner.getUserName()));
		// check and remove room
		if (room.isGame()) {
			GameExtension gameExtension = (GameExtension) room.getExtension();
			GameInterface gameInterface = gameExtension.gameController.getGameInterface();
			if (room.getPlayerSize() < 1) {
				room.removeUser(joiner);
				gameExtension.getApi().removeRoom(room);
				return;
			}

			if (gameInterface.isPlaying()) {
				room.removeUser(joiner);
				gameInterface.disconnect(joiner);
				return;
			}

			getApi().leaveRoom(joiner, room);
		}
	}

}

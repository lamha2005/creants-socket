package com.avengers.netty.core.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avengers.netty.SocketServer;
import com.avengers.netty.core.event.CoreEvent;
import com.avengers.netty.core.event.CoreEventParam;
import com.avengers.netty.core.event.ICoreEventParam;
import com.avengers.netty.core.event.SystemNetworkConstant;
import com.avengers.netty.core.exception.CoreErrorCode;
import com.avengers.netty.core.exception.CoreErrorData;
import com.avengers.netty.core.exception.CoreRuntimeException;
import com.avengers.netty.core.exception.CreateRoomException;
import com.avengers.netty.core.exception.JoinRoomException;
import com.avengers.netty.core.exception.RoomException;
import com.avengers.netty.core.om.CreateRoomSettings;
import com.avengers.netty.core.om.IRoom;
import com.avengers.netty.core.service.IUserManager;
import com.avengers.netty.core.service.RoomManager;
import com.avengers.netty.core.util.CoreTracer;
import com.avengers.netty.core.util.DefaultMessageFactory;
import com.avengers.netty.gamelib.GameExtension;
import com.avengers.netty.gamelib.key.ErrorCode;
import com.avengers.netty.gamelib.key.NetworkConstant;
import com.avengers.netty.gamelib.service.CacheService;
import com.avengers.netty.socket.gate.IMessage;
import com.avengers.netty.socket.gate.wood.Message;
import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public class CoreAPI implements ICoreAPI {
	protected final SocketServer socketServer;
	protected IUserManager globalUserManager;
	private RoomManager roomService;

	public CoreAPI(SocketServer socketServer) {
		this.socketServer = socketServer;
		globalUserManager = socketServer.getUserManager();
		roomService = socketServer.getRoomService();
	}

	@Override
	public void logout(User user) {
		IRoom lastJoinedRoom = user.getLastJoinedRoom();
		if (lastJoinedRoom != null) {
			leaveRoom(user, lastJoinedRoom, false, false);
		}
		disconnectUser(user);
	}

	public void removeUser(int userId) {
		globalUserManager.removeUser(userId);
	}

	@Override
	public void login(User user) {
		globalUserManager.addUser(user);
		// fireEvent cho extension xu ly tiep
		Map<ICoreEventParam, Object> params = new HashMap<ICoreEventParam, Object>();
		params.put(CoreEventParam.USER, user);
		GameExtension.getExtension().handleServerEvent(new CoreEvent(SystemNetworkConstant.COMMAND_USER_LOGIN, params));
	}

	@Override
	public void kickUser(User Owner, User kickedUser, String paramString, int paramInt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnectUser(User user) {
		globalUserManager.removeUser(user);
	}

	@Override
	public void disconnectUser(User user, IRoom room) {
		Map<ICoreEventParam, Object> params = new HashMap<ICoreEventParam, Object>();
		params.put(CoreEventParam.USER, user);
		params.put(CoreEventParam.ROOM, room);
		CoreEvent event = new CoreEvent(SystemNetworkConstant.COMMAND_USER_DISCONNECT, params);
		room.getExtension().handleServerEvent(event);
	}

	@Override
	public IRoom createRoom(CreateRoomSettings setting, User owner) throws CreateRoomException {
		return createRoom(setting, owner, false, true, true);
	}

	@Override
	public IRoom createRoom(CreateRoomSettings setting, User owner, boolean joinAfterCreated, boolean fireClientEvent,
			boolean fireServerEvent) throws CreateRoomException {
		// TODO check money
		IRoom newRoom = null;
		try {
			String groupId = setting.getGroupId();
			if (groupId == null || groupId.length() == 0) {
				setting.setGroupId("default");
			}

			newRoom = roomService.createRoom(setting, owner);
			if (owner != null) {
				CoreTracer.debug(this.getClass(), String.format("[DEBUG] [user: %s] Created New Room [%s]",
						owner.getUserName(), newRoom.toString()));
				owner.updateLastRequestTime();
			} else {
				CoreTracer.debug(this.getClass(), String.format("[DEBUG] Created New Room [%s]", newRoom.getName()));
			}

			if (fireServerEvent) {
				// notify to Room Extension
				Map<ICoreEventParam, Object> params = new HashMap<ICoreEventParam, Object>();
				params.put(CoreEventParam.USER, owner);
				params.put(CoreEventParam.ROOM, newRoom);
				CoreEvent event = new CoreEvent(SystemNetworkConstant.COMMAND_USER_CREATE_ROOM, params);
				newRoom.getExtension().handleServerEvent(event);
			}
		} catch (CreateRoomException err) {
			String message = String.format("Room creation error. %s, %s", new Object[] { err.getMessage(), owner });
			Message msg = DefaultMessageFactory.createErrorMessage(NetworkConstant.COMMAND_CREATE_ROOM,
					ErrorCode.CREATE_ROOM_FAILED, message);
			sendExtensionResponse(msg, owner);
			throw new CreateRoomException(message);
		}

		// join sau khi taọ
		if (newRoom != null && owner != null && joinAfterCreated) {
			try {
				joinRoom(owner, newRoom, false, newRoom.getPassword(), true);
			} catch (JoinRoomException e) {
				CoreTracer.error(CoreAPI.class,
						"Unable to join the just created Room: " + newRoom + ", reason: " + e.getMessage());
				CoreTracer.error(CoreAPI.class,
						String.format("[DEBUG] [user: %s] Unable to join the just created Room [%s], msg: %s",
								owner.getUserName(), newRoom.toString(), e.getMessage()));
			}
		}

		return newRoom;
	}

	@Override
	public User getUserById(int userId) {
		return globalUserManager.getUser(userId);
	}

	@Override
	public User getUserByName(String name) {
		return globalUserManager.getUser(name);
	}

	@Override
	public void joinRoom(User user, IRoom roomToJoin) throws JoinRoomException {
		joinRoom(user, roomToJoin, true, "", true);
	}

	@Override
	public void joinRoom(User user, int roomId, boolean joinAsSpectator, String password) throws JoinRoomException {
		joinRoom(user, roomService.getRoomById(roomId), joinAsSpectator, password, true);
	}

	@Override
	public void joinRoom(User user, IRoom roomToJoin, boolean joinAsSpectator, String password, boolean fireToExtension)
			throws JoinRoomException {
		try {
			// Đang thực hiện join tới phòng khác
			if (user.isJoining()) {
				throw new CoreRuntimeException(
						"Join request discarded. User is already in a join transaction: " + user);
			}

			user.setJoining(true);
			if (roomToJoin == null) {
				throw new JoinRoomException("Requested room doesn't exist",
						new CoreErrorData(CoreErrorCode.JOIN_BAD_ROOM));
			}

			if (roomToJoin.containsUser(user)) {
				throw new JoinRoomException("Room already joined!",
						new CoreErrorData(CoreErrorCode.ROOM_ALREADY_JOINED));
			}

			if (!roomToJoin.isActive()) {
				String message = String.format("Room is currently locked, %s", new Object[] { roomToJoin });
				CoreTracer.error(CoreAPI.class, message);
				CoreErrorData errData = new CoreErrorData(CoreErrorCode.JOIN_ROOM_LOCKED);
				errData.addParameter(roomToJoin.getName());
				throw new JoinRoomException(message, errData);
			}

			boolean doorIsOpen = true;
			if (roomToJoin.isPasswordProtected()) {
				doorIsOpen = roomToJoin.getPassword().equals(password);
			}

			if (!doorIsOpen) {
				String message = String.format("Room password is wrong, %s", new Object[] { roomToJoin });
				CoreErrorData data = new CoreErrorData(CoreErrorCode.JOIN_BAD_PASSWORD);
				data.addParameter(roomToJoin.getName());
				CoreTracer.error(CoreAPI.class, "[ERROR] doorIsOpen fail!", message);
				throw new JoinRoomException(message, data);
			}

			CacheService.getInstace().joinRoom(user.getCreantUserId(), roomToJoin);

			// TODO check số lượng user luôn

			// rời phòng trước đó
			IRoom roomToLeave = user.getLastJoinedRoom();
			if (roomToLeave != null) {
				leaveRoom(user, roomToLeave);
			}

			roomToJoin.addUser(user, joinAsSpectator);
			user.updateLastRequestTime();

			CoreTracer.debug(CoreAPI.class, String.format("[DEBUG] [user:%s] join to room [%s] success!",
					user.getUserName(), roomToJoin.getName()));

			// fireEvent cho extension xu ly tiep
			if (fireToExtension) {
				Map<ICoreEventParam, Object> params = new HashMap<ICoreEventParam, Object>();
				params.put(CoreEventParam.USER, user);
				params.put(CoreEventParam.ROOM, roomToJoin);
				CoreEvent event = new CoreEvent(roomToJoin.isGame() ? SystemNetworkConstant.COMMAND_USER_JOIN_ROOM
						: SystemNetworkConstant.COMMAND_USER_JOIN_GAME, params);
				roomToJoin.getExtension().handleServerEvent(event);
			}

		} catch (JoinRoomException e) {
			String message = String.format("Join Error - %s", new Object[] { e.getMessage() });
			CoreTracer.error(CoreAPI.class, message, e);
			Message msg = DefaultMessageFactory.createErrorMessage(SystemNetworkConstant.COMMAND_USER_JOIN_ROOM,
					ErrorCode.JOIN_ROOM_FAILED, message);
			sendExtensionResponse(msg, user);
			CoreTracer.error(CoreAPI.class, String.format("[DEBUG] [user:%s] join to room [%s] fail! %s",
					user.getUserName(), roomToJoin.getName(), e.getMessage()));
		} finally {
			user.setJoining(false);
		}
	}

	public void leaveRoom(User user, IRoom room) {
		leaveRoom(user, room, true, true);
	}

	@Override
	public void leaveRoom(User user, IRoom room, boolean fireClientEvent, boolean fireServerEvent) {
		if (room == null) {
			room = user.getLastJoinedRoom();
			if (room == null) {
				throw new CoreRuntimeException("LeaveRoom failed: user is not joined in any room. " + user);
			}
		}

		CoreTracer.debug(CoreAPI.class,
				String.format("[DEBUG] [user:%s] leaveRoom %s", user.getUserName(), room.getName()));

		// không tồn tại user trong room đó
		if (!room.containsUser(user)) {
			CoreTracer.debug(CoreAPI.class,
					String.format("[DEBUG] [user:%s] leaveRoom fail! Not exist user in this room: %s",
							user.getUserName(), room.getName()));
			return;
		}

		room.removeUser(user);
		if (room.isEmpty() && room.isGame()) {
			roomService.removeRoom(room);
			CoreTracer.debug(CoreAPI.class, String.format("[DEBUG] remove room: %s", room.getName()));
		}

		user.updateLastRequestTime();

		// fireEvent cho extension xu ly tiep
		Map<ICoreEventParam, Object> params = new HashMap<ICoreEventParam, Object>();
		params.put(CoreEventParam.USER, user);
		params.put(CoreEventParam.ROOM, room);
		CoreEvent event = new CoreEvent(SystemNetworkConstant.COMMAND_USER_LEAVE_ROOM, params);
		room.getExtension().handleServerEvent(event);

	}

	@Override
	public void removeRoom(IRoom room) {
		roomService.removeRoom(room);
		CoreTracer.debug(CoreAPI.class, String.format("[DEBUG] [room:%s] Removed!", room.getName()));
	}

	@Override
	public void changeRoomName(User owner, IRoom room, String newName) throws RoomException {
		// TODO Auto-generated method stub

	}

	@Override
	public void changeRoomPassword(User owner, IRoom room, String newPassword) throws RoomException {
		// TODO Auto-generated method stub

	}

	@Override
	public void changeRoomCapacity(User owner, IRoom room, int maxUsers, int maxSpectators) throws RoomException {
		// TODO Auto-generated method stub

	}

	@Override
	public void spectatorToPlayer(User user, IRoom room) throws RoomException {
		if (room == null) {
			throw new IllegalArgumentException("A target room was not specified (null)");
		}
		if (user == null) {
			throw new IllegalArgumentException("A user was not specified (null)");
		}
		user.updateLastRequestTime();
		try {
			room.switchSpectatorToPlayer(user);
			// TODO notify to Room Extension

		} catch (RoomException err) {
			String message = String.format("SpectatorToPlayer Error - %s", new Object[] { err.getMessage() });
			throw new RoomException(message, err.getErrorData());
		}
	}

	@Override
	public void playerToSpectator(User user, IRoom room) throws RoomException {
		if (room == null) {
			throw new IllegalArgumentException("A target room was not specified (null)");
		}
		if (user == null) {
			throw new IllegalArgumentException("A user was not specified (null)");
		}
		user.updateLastRequestTime();
		try {
			room.switchPlayerToSpectator(user);
			// TODO notify to Room Extension

		} catch (RoomException err) {
			String message = String.format("PlayerToSpectator Error - %s", new Object[] { err.getMessage() });
			throw new RoomException(message, err.getErrorData());
		}
	}

	@Override
	public void sendExtensionResponse(IMessage message, List<User> recipients) {
		socketServer.getMessageHandler().send(recipients, message);
	}

	@Override
	public void sendExtensionResponse(IMessage message, User recipient) {
		socketServer.getMessageHandler().send(recipient, message);
	}

}
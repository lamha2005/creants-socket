package com.avengers.netty.core.api;

import java.util.List;

import com.avengers.netty.core.exception.CreateRoomException;
import com.avengers.netty.core.exception.JoinRoomException;
import com.avengers.netty.core.exception.RoomException;
import com.avengers.netty.core.om.CreateRoomSettings;
import com.avengers.netty.core.om.IRoom;
import com.avengers.netty.socket.gate.IMessage;
import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public abstract interface ICoreAPI {

	/**
	 * User thực hiện logout
	 * 
	 * @param user
	 */
	abstract void logout(User user);

	/**
	 * Thực hiện login
	 * 
	 * @param user
	 */
	abstract void login(User user);

	/**
	 * Kích người chơi khỏi bàn
	 * 
	 * @param owner
	 *            chủ bàn
	 * @param kickedUser
	 *            user bị kick
	 * @param paramString
	 * @param paramInt
	 */
	abstract void kickUser(User owner, User kickedUser, String paramString, int paramInt);

	abstract void disconnectUser(User user);

	abstract void disconnectUser(User user, IRoom room);

	abstract IRoom createRoom(CreateRoomSettings setting, User owner) throws CreateRoomException;

	abstract IRoom createRoom(CreateRoomSettings setting, User owner, boolean joinAfterCreated, boolean fireClientEvent,
			boolean fireServerEvent) throws CreateRoomException;

	abstract User getUserById(int userId);

	abstract User getUserByName(String name);

	abstract void joinRoom(User user, IRoom roomToJoin) throws JoinRoomException;

	abstract void joinRoom(User user, IRoom roomToJoin, boolean joinAsSpectator, String password, boolean fireToExtension)
			throws JoinRoomException;

	abstract void joinRoom(User user, int roomId, boolean joinAsSpectator, String password)
			throws JoinRoomException;

	abstract void leaveRoom(User user, IRoom roomToLeave);

	abstract void leaveRoom(User user, IRoom room, boolean fireClientEvent, boolean fireServerEvent);

	abstract void removeRoom(IRoom room);

	abstract void changeRoomName(User owner, IRoom room, String newName) throws RoomException;

	abstract void changeRoomPassword(User owner, IRoom room, String newPassword) throws RoomException;

	abstract void changeRoomCapacity(User owner, IRoom room, int maxUsers, int maxSpectators) throws RoomException;

	abstract void spectatorToPlayer(User user, IRoom room) throws RoomException;

	abstract void playerToSpectator(User user, IRoom room) throws RoomException;

	abstract void sendExtensionResponse(IMessage message, List<User> recipients);

	abstract void sendExtensionResponse(IMessage message, User recipient);
}

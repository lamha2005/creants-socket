package com.avengers.netty.core.service;

import java.util.List;

import com.avengers.netty.core.exception.CreateRoomException;
import com.avengers.netty.core.exception.RoomException;
import com.avengers.netty.core.om.CreateRoomSettings;
import com.avengers.netty.core.om.IRoom;
import com.avengers.netty.core.om.RoomPage;
import com.avengers.netty.core.util.IPlayerIdGenerator;
import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public abstract interface IRoomManager

{
	public abstract void addRoom(IRoom room);

	public abstract IRoom createRoom(CreateRoomSettings roomSetting) throws CreateRoomException;

	public abstract IRoom createRoom(CreateRoomSettings roomSetting, User user) throws CreateRoomException;

	public abstract List<String> getGroups();

	public abstract void addGroup(String groupName);

	public abstract void removeGroup(String groupName);

	public abstract boolean containsGroup(String groupName);

	public abstract boolean containsRoom(int roomId);

	public abstract boolean containsRoom(String roomName);

	public abstract boolean containsRoom(IRoom room);

	public abstract boolean containsRoom(int roomId, String roomName);

	public abstract boolean containsRoom(String name, String groupId);

	public abstract boolean containsRoom(IRoom room, String groupId);

	public abstract IRoom getRoomById(int roomId);

	public abstract IRoom getRoomByName(String name);

	public abstract List<IRoom> getRoomList();

	public abstract RoomPage<IRoom> getRoomPage(int page, boolean getClusterRoom);

	public abstract List<IRoom> getRoomListFromGroup(String groupName);

	public abstract int getGameRoomCount();

	public abstract int getTotalRoomCount();

	public abstract void setDefaultRoomPlayerIdGeneratorClass(Class<? extends IPlayerIdGenerator> playerGeneratorId);

	public abstract Class<? extends IPlayerIdGenerator> getDefaultRoomPlayerIdGenerator();

	public abstract void checkAndRemove(IRoom room);

	public abstract void removeRoom(IRoom room);

	public abstract void removeRoom(int roomId);

	public abstract void removeRoom(String roomName);

	public abstract void removeUser(User user);

	public abstract void removeUser(User user, IRoom room);

	public abstract void changeRoomName(IRoom room, String roomName) throws RoomException;

	public abstract void changeRoomPasswordState(IRoom room, String password);

	public abstract void changeRoomCapacity(IRoom room, int newMaxUsers, int newMaxSpect);

	public abstract void setClusterService(IClusterService clusterService);
}

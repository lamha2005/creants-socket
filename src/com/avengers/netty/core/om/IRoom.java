package com.avengers.netty.core.om;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.avengers.netty.core.exception.JoinRoomException;
import com.avengers.netty.core.exception.RoomException;
import com.avengers.netty.core.extensions.ICRAExtension;
import com.avengers.netty.core.service.IUserManager;
import com.avengers.netty.core.util.IPlayerIdGenerator;
import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public abstract interface IRoom {
	public abstract int getId();

	public abstract void setGameId(byte gameId);

	public abstract byte getGameId();

	public abstract String getGroupId();

	public abstract void setGroupId(String groupId);

	public abstract String getName();

	public abstract void setName(String roomName);

	public abstract String getPassword();

	public abstract void setPassword(String password);

	public abstract boolean isPasswordProtected();

	public abstract boolean isPublic();

	public abstract int getCapacity();

	public abstract void setCapacity(int maxPlayer, int maxSpectator);

	public abstract int getMaxUsers();

	public abstract void setMaxUsers(int maxPlayer);

	public abstract int getMaxSpectators();

	public abstract void setMaxSpectators(int maxSpectators);

	public abstract User getOwner();

	public abstract void setOwner(User user);

	public abstract RoomSize getSize();

	public abstract int countPlayer();

	public abstract IUserManager getUserManager();

	public abstract void setUserManager(IUserManager paramIUserManager);

	public abstract boolean isDynamic();

	public abstract boolean isGame();

	public abstract boolean isHidden();

	public abstract void setDynamic(boolean paramBoolean);

	public abstract void setGame(boolean paramBoolean);

	public abstract void setGame(boolean paramBoolean, Class<? extends IPlayerIdGenerator> paramClass);

	public abstract void setHidden(boolean paramBoolean);

	public abstract void setFlags(Set<RoomSettings> paramSet);

	public abstract void setFlag(RoomSettings paramRoomSettings, boolean paramBoolean);

	public abstract boolean isFlagSet(RoomSettings paramSFSRoomSettings);

	public abstract RoomRemoveMode getAutoRemoveMode();

	public abstract void setAutoRemoveMode(RoomRemoveMode paramSFSRoomRemoveMode);

	public abstract boolean isEmpty();

	public abstract boolean isFull();

	public abstract boolean isActive();

	public abstract boolean isOwner(User user);

	public abstract void setActive(boolean paramBoolean);

	public abstract ICRAExtension getExtension();

	public abstract void setExtension(ICRAExtension paramISFSExtension);

	public abstract Object getProperty(Object paramObject);

	public abstract Map<Object, Object> getProperties();

	public abstract void setProperty(Object paramObject1, Object paramObject2);

	public abstract boolean containsProperty(Object paramObject);

	public abstract void removeProperty(Object paramObject);

	public abstract User getUserById(int paramInt);

	public abstract User getUserByName(String paramString);

	public abstract User getUserByPlayerId(int paramInt);

	/**
	 * Lấy danh sách user trong phòng
	 * 
	 * @return
	 */
	public abstract List<User> getListUser();

	/**
	 * Lấy danh sách người chơi trong phòng
	 * 
	 * @return
	 */
	public abstract List<User> getPlayersList();
	
	public abstract int getPlayerSize();

	/**
	 * Lấy danh sách người xem trong phòng
	 * 
	 * @return
	 */
	public abstract List<User> getSpectatorsList();

	public abstract void addUser(User paramUser, boolean paramBoolean) throws JoinRoomException;

	public abstract void addUser(User paramUser) throws JoinRoomException;

	public abstract void removeUser(User paramUser);

	public abstract boolean containsUser(User paramUser);

	public abstract boolean containsUser(String paramString);

	public abstract void switchPlayerToSpectator(User paramUser) throws RoomException;

	public abstract void switchSpectatorToPlayer(User paramUser) throws RoomException;

	public abstract long getLifeTime();

	public abstract String getDump();

	public abstract void destroy();

	public abstract String getPlayerIdGeneratorClassName();

	public abstract String getHost();
}

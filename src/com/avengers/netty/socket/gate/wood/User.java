package com.avengers.netty.socket.gate.wood;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.avengers.netty.core.om.Device;
import com.avengers.netty.core.om.IRoom;
import com.avengers.netty.core.util.CoreTracer;
import com.avengers.netty.socket.gate.IUser;

/**
 * @author LamHa
 *
 */
public class User implements IUser {
	private long sessionId;
	private int userId;
	private int creantUserId = -1;
	private String uid;
	private long createTime;
	private String userName;
	private String name;
	private long money;
	private String avatar;
	private byte language;
	private boolean isJoiningARoom = false;
	private IRoom lastJoinedRoom;
	private long loginMoney;
	private long loginTime;
	private Device device;
	private byte currentGameId = -1;
	private final ConcurrentMap<Integer, Integer> playerIdByRoomId;

	public User() {
		userId = -1;
		playerIdByRoomId = new ConcurrentHashMap<Integer, Integer>();
	}

	@Override
	public <V> V getAttribute(Object key, Class<V> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Object> getAttributeKeys() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public long getCreatedTime() {
		// TODO Auto-generated method stub
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	@Override
	public byte getDeviceType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getSessionId() {
		return sessionId;
	}

	public String getClientIp() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setClientIp(String clientIp) {
		// TODO Auto-generated method stub
	}

	@Override
	public String getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPlatformInformation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte getProtocolVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getScreenSize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getUserId() {
		return userId;
	}

	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getCreantUserId() {
		return creantUserId;
	}

	public void setCreantUserId(int creantUserId) {
		this.creantUserId = creantUserId;
	}

	@Override
	public String getUserName() {
		return userName;
	}

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize(String version, long sessionId, long clientId, byte deviceType, long createTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeAttribute(Object key) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAttribute(Object key, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setVersion(String version) {
		// TODO Auto-generated method stub

	}

	public boolean isSpectator() {
		return isSpectator(lastJoinedRoom);
	}

	public boolean isSpectator(IRoom room) {
		return getPlayerId(room) < 0;
	}

	public boolean isPlayer() {
		return isPlayer(lastJoinedRoom);
	}

	public boolean isPlayer(IRoom room) {
		return getPlayerId(room) > 0;
	}

	public int getPlayerId() {
		return getPlayerId(lastJoinedRoom);
	}

	public int getPlayerId(IRoom room) {
		if (room == null) {
			return 0;
		}
		Integer playerId = playerIdByRoomId.get(room.getId());
		if (playerId == null) {
			CoreTracer.debug(User.class,
					"Can't find playerID -- User: " + userName + " is not joined in the requested Room: " + room);
			playerId = 0;
		}
		return playerId.intValue();
	}

	public void setPlayerId(int id, IRoom room) {
		playerIdByRoomId.put(room.getId(), id);
	}

	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	public long getMoney() {
		return money;
	}

	public void setMoney(long money) {
		this.money = money;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public byte getLanguage() {
		return language;
	}

	public void setLanguage(byte language) {
		this.language = language;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public IRoom getLastJoinedRoom() {
		return lastJoinedRoom;
	}

	public void setLastJoinedRoom(IRoom room) {
		lastJoinedRoom = room;
	}

	public void removeLastJoinedRoom() {
		lastJoinedRoom = null;
	}

	public synchronized boolean isJoining() {
		return isJoiningARoom;
	}

	public synchronized void setJoining(boolean flag) {
		isJoiningARoom = flag;
	}

	public long getLastRequestTime() {
		return 0;
	}

	public synchronized void updateLastRequestTime() {
		setLastRequestTime(System.currentTimeMillis());
	}

	public void setLastRequestTime(long lastRequestTime) {
		// TODO set lastrequestTime cua 1 seesion user
	}

	public void setLoginInfo(User user) {
		userId = user.getUserId();
		userName = user.getUserName();
		avatar = user.getAvatar();
		language = user.getLanguage();
		money = user.getMoney();
		loginTime = System.currentTimeMillis();
	}

	public long getLoginMoney() {
		return loginMoney;
	}

	public void setLoginMoney(long loginMoney) {
		this.loginMoney = loginMoney;
	}

	public long getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(long loginTime) {
		this.loginTime = loginTime;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public long getTimeOnline() {
		return (System.currentTimeMillis() - getLoginTime()) / 1000;
	}

	@Override
	public byte getCurrentGameId() {
		return currentGameId;
	}

	@Override
	public void setCurrentGameId(byte currentGameId) {
		this.currentGameId = currentGameId;
	}

	@Override
	public String toString() {
		return String.format("[sessionId: %d, userId: %s, username: %s, currentGame: %s, money: %d]", sessionId, userId,
				userName, currentGameId, money);
	}

}

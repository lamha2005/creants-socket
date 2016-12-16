package com.avengers.netty.socket.gate;

import java.util.Iterator;

import com.avengers.netty.core.om.IRoom;

/**
 * @author LamHa
 *
 */
public interface IUser {

	<V> V getAttribute(Object key, Class<V> clazz);

	// TODO lưu avatar thông tin cơ bản của user ở đây
	Iterator<Object> getAttributeKeys();

	long getCreatedTime();

	String getUid();

	byte getDeviceType();

	long getSessionId();

	String getLocale();

	String getPlatformInformation();

	byte getProtocolVersion();

	String getScreenSize();

	int getUserId();

	String getUserName();

	String getVersion();

	void initialize(String version, long sessionId, long clientId, byte deviceType, long createTime);

	void removeAttribute(Object key);

	void setAttribute(Object key, Object value);

	void setVersion(String version);

	byte getCurrentGameId();

	void setCurrentGameId(byte currentGameId);

	IRoom getLastJoinedRoom();

}

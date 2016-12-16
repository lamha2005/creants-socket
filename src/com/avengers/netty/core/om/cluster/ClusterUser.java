package com.avengers.netty.core.om.cluster;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * @author LamHa
 *
 */
public class ClusterUser implements Externalizable {
	private int userId;
	private long sessionId;
	private String userName;
	private long money;
	private String avatar;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public long getSessionId() {
		return sessionId;
	}

	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(userId);
		out.writeLong(sessionId);
		out.writeUTF(userName);
		out.writeLong(money);
		out.writeUTF(avatar);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		userId = in.readInt();
		sessionId = in.readLong();
		userName = in.readUTF();
		money = in.readLong();
		avatar = in.readUTF();
	}

	@Override
	public String toString() {
		return String.format("[username: %s, id: %d, sessionId: %d]", userName, userId, sessionId);
	}

}

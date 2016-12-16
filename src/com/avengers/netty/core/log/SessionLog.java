package com.avengers.netty.core.log;

import com.avengers.netty.core.om.Device;
import com.avengers.netty.core.om.ServerConfig;
import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public class SessionLog {

	private String userName;
	private long timeLogin;
	private long timeLogout;
	private long moneyLogin;
	private long moneyLogout;
	private int platformId;
	private String clientVersion;
	private String ipClient;
	private String clientPhone;
	private int serverId;

	public SessionLog(User user, Device device) {
		this.userName = user.getUserName();
		this.timeLogin = user.getLoginTime();
		this.timeLogout = System.currentTimeMillis();
		this.moneyLogin = user.getLoginMoney();
		this.moneyLogout = user.getMoney();
		this.platformId = device.getPlatformId();
		this.clientVersion = device.getVersion().toString();
		this.ipClient = user.getClientIp();
		this.serverId = ServerConfig.serverId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public long getTimeLogin() {
		return timeLogin;
	}

	public void setTimeLogin(long timeLogin) {
		this.timeLogin = timeLogin;
	}

	public long getTimeLogout() {
		return timeLogout;
	}

	public void setTimeLogout(long timeLogout) {
		this.timeLogout = timeLogout;
	}

	public long getMoneyLogin() {
		return moneyLogin;
	}

	public void setMoneyLogin(long moneyLogin) {
		this.moneyLogin = moneyLogin;
	}

	public long getMoneyLogout() {
		return moneyLogout;
	}

	public void setMoneyLogout(long moneyLogout) {
		this.moneyLogout = moneyLogout;
	}

	public int getPlatformId() {
		return platformId;
	}

	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}

	public String getClientVersion() {
		return clientVersion;
	}

	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}

	public String getIpClient() {
		return ipClient;
	}

	public void setIpClient(String ipClient) {
		this.ipClient = ipClient;
	}

	public String getClientPhone() {
		return clientPhone;
	}

	public void setClientPhone(String clientPhone) {
		this.clientPhone = clientPhone;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
}

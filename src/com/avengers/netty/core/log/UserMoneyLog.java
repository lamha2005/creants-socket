package com.avengers.netty.core.log;

/**
 * @author LamHa
 *
 */
public class UserMoneyLog {
	private int userId;
	private String userName;
	/**
	 * Tiền hiện tại của user
	 */
	private long money;
	/**
	 * Tiền cộng thêm
	 */
	private long value;
	private int serviceId;
	private int reasonId;
	private String description = "";
	private String reasonText = "";
	private int boardId;

	/**
	 * ngày ghi boardlog: ddMMyyyy
	 */
	private String boardLogDate = "";

	public UserMoneyLog() {
	}

	public String getBoardLogDate() {
		return boardLogDate;
	}

	public void setBoardLogDate(String date) {
		this.boardLogDate = date;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public long getMoney() {
		return money;
	}

	public void setMoney(long _money) {
		this.money = _money;
	}

	public int getServerId() {
		return serviceId;
	}

	public void setServerId(int _serverId) {
		this.serviceId = _serverId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String _userName) {
		this.userName = _userName;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long _value) {
		this.value = _value;
	}

	public int getReasonId() {
		return reasonId;
	}

	public void setReasonId(int _reasonId) {
		this.reasonId = _reasonId;
	}

	public String getDescription() {
		if (description != null && description.length() > 250) {
			description = description.substring(0, 250);
		}
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getReasonText() {
		return reasonText;
	}

	public void setReasonText(String _reasonText) {
		this.reasonText = _reasonText;
	}

	public int getBoardId() {
		return boardId;
	}

	public void setBoardId(int _boardId) {
		this.boardId = _boardId;
	}

	@Override
	public String toString() {
		return "UserMoneyLog{" + "_userId=" + userId + "_userName=" + userName + "_money=" + money + "_value=" + value
				+ "_serverId=" + serviceId + "_reasonId=" + reasonId + "description=" + description + "_reasonText="
				+ reasonText + "_boardLogDate=" + boardLogDate + "_boardId" + boardId + '}';
	}
}

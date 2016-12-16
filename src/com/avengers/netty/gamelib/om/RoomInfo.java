package com.avengers.netty.gamelib.om;

/**
 * Đối tượng chứa thông tin của 1 Room Level, MoneyBet, TimeOut, Owner
 * 
 * @author LamHa
 *
 */
public class RoomInfo {
	private int id;
	private byte gameId;
	private String name;
	private int betCoin;
	private int timeOut;
	// số lượng tối đa người chơi
	private int maxPlayer;
	private String owner;
	// Số người chơi trong bàn
	private int playerNo;
	// Luật chơi do người chơi đặt ra
	private String rules;

	public RoomInfo(byte gameId) {
		this.gameId = gameId;
	}

	public RoomInfo(int id, String name, byte gameId) {
		this.id = id;
		this.name = name;
		this.gameId = gameId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMaxPlayer() {
		return maxPlayer;
	}

	public void setMaxPlayer(int maxPlayer) {
		this.maxPlayer = maxPlayer;
	}

	public String getName() {
		return name;
	}

	public int getPlayerNo() {
		return playerNo;
	}

	public void setPlayerNo(int playerNo) {
		this.playerNo = playerNo;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getBetCoin() {
		return betCoin;
	}

	public void setBetCoin(int betCoin) {
		this.betCoin = betCoin;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public void setLevelRoom(byte level) {
		this.id = level;
	}

	public byte getGameId() {
		return gameId;
	}

	public void setGameId(byte gameId) {
		this.gameId = gameId;
	}

	public String getRules() {
		return rules;
	}

	public void setRules(String rules) {
		this.rules = rules;
	}

	public void updateRoomInfo(Integer betCoin, Integer timeout, String rules) {
		if (betCoin != null) {
			setBetCoin(betCoin);
		}

		if (timeout != null) {
			setTimeOut(timeout);
		}

		if (rules != null) {
			setRules(rules);
		}
	}

	@Override
	public String toString() {
		return String.format("[Room: id:%d, name:%s, betCoin: %d, timeout: %d, owner: %s, maxPlayer: %d]", id, name, betCoin, timeOut,
				owner, maxPlayer);
	}

}

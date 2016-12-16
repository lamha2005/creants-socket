package com.avengers.netty.gamelib.om;

import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public class IGamePlayer extends User {
	private int playerNo; // Thứ tự lượt đi trong ván chơi
	private int winRank; // Thứ tự tới
	private boolean isAvaible; // Trạng thái của user trong ván chơi
	private long moneyWin; // Số tiền user thắng được trong ván chơi
	private int expAdd; // Điểm kinh nghiệm đạt được khi kết thúc ván
	private byte position;

	public IGamePlayer(byte position) {
		this.winRank = -1;
		this.isAvaible = true;
		this.moneyWin = 0;
		this.expAdd = 0;
	}

	public IGamePlayer() {
		this((byte) -1);
	}

	public int getPlayerNo() {
		return playerNo;
	}

	public void setPlayerNo(int playerNo) {
		this.playerNo = playerNo;
	}

	public int getWinRank() {
		return winRank;
	}

	public void setWinRank(int winRank) {
		this.winRank = winRank;
	}

	public boolean isAvaible() {
		return isAvaible;
	}

	public void setAvaible(boolean isAvaible) {
		this.isAvaible = isAvaible;
	}

	public long getMoneyWin() {
		return moneyWin;
	}

	public void setMoneyWin(long moneyWin) {
		this.moneyWin = moneyWin;
	}

	public int getExpAdd() {
		return expAdd;
	}

	public void setExpAdd(int expAdd) {
		this.expAdd = expAdd;
	}

	public byte getPosition() {
		return position;
	}

	public void setPosition(byte position) {
		this.position = position;
	}

}

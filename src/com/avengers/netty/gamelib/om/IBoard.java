/**
 * 
 */
package com.avengers.netty.gamelib.om;

import java.util.Date;

/**
 * @author LamHa
 *
 */
public class IBoard {
	private Date dateTimeStart; // Thời gian khi bắt đầu ván
	private Date dateTimeEnd; // Thời gian khi kết thúc ván
	private IGamePlayer firstPlayer; // Người đi đầu tiên của ván
	private int previousPlayerId; // UserId của người vừa đi lúc trước
	private int nextPlayerId; // UserId của người đi tiếp theo
	private int currentPlayerId; // UserId của người đi hiện tại

	public Date getDateTimeStart() {
		return dateTimeStart;
	}

	public void setDateTimeStart(Date dateTimeStart) {
		this.dateTimeStart = dateTimeStart;
	}

	public Date getDateTimeEnd() {
		return dateTimeEnd;
	}

	public void setDateTimeEnd(Date dateTimeEnd) {
		this.dateTimeEnd = dateTimeEnd;
	}

	public IGamePlayer getFirstPlayer() {
		return firstPlayer;
	}

	public void setFirstPlayer(IGamePlayer firstPlayer) {
		this.firstPlayer = firstPlayer;
	}

	public int getPreviousPlayerId() {
		return previousPlayerId;
	}

	public void setPreviousPlayerId(int previousPlayerId) {
		this.previousPlayerId = previousPlayerId;
	}

	public int getNextPlayerId() {
		return nextPlayerId;
	}

	public void setNextPlayerId(int nextPlayerId) {
		this.nextPlayerId = nextPlayerId;
	}

	public int getCurrentPlayerId() {
		return currentPlayerId;
	}

	public void setCurrentPlayerId(int currentPlayerId) {
		this.currentPlayerId = currentPlayerId;
	}

}

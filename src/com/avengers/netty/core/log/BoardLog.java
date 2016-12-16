package com.avengers.netty.core.log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.avengers.netty.socket.util.DateUtils;

/**
 * @author LamHa
 *
 */
public class BoardLog {
	private AtomicInteger _id;
	private int _roomId;
	private int _totalUserInBoard;
	private long _startTime;
	private int _tax;
	/**
	 * List userId của user trong bàn
	 */
	private List<Integer> _playersInBoard;
	/**
	 * Tổng tiền cược trong game
	 */
	private long _money;
	private int _serviceId;
	private String date;

	public BoardLog() {
		_playersInBoard = new ArrayList<>();
		_id = new AtomicInteger(-1);
		date = DateUtils.formatDate(new Date());
	}

	public String getDate() {
		return date;
	}

	public int getTax() {
		return _tax;
	}

	public void setTax(int _tax) {
		this._tax = _tax;
	}

	public void addTax(int tax) {
		this._tax += tax;
	}

	public long getStartTime() {
		return _startTime;
	}

	public void setStartTime(long _startTime) {
		this._startTime = _startTime;
	}

	public int getServiceId() {
		return _serviceId;
	}

	public void setServiceId(int serviceId) {
		this._serviceId = serviceId;
	}

	public int getId() {
		return _id.get();
	}

	public void setId(int _id) {
		this._id.set(_id);
	}

	public long getMoney() {
		return _money;
	}

	public void setMoney(long _money) {
		this._money = _money;
	}

	public int getRoomId() {
		return _roomId;
	}

	public void setRoomId(int _roomId) {
		this._roomId = _roomId;
	}

	public int getTotalUserInBoard() {
		return _totalUserInBoard;
	}

	public void setTotalUserInBoard(int _totalUserInBoard) {
		this._totalUserInBoard = _totalUserInBoard;
	}

	public List<Integer> getPlayersInBoard() {
		return _playersInBoard;
	}

	@Override
	public String toString() {
		return "BoardLog{" + "_id=" + _id + ", _roomId=" + _roomId + ", _totalUserInBoard=" + _totalUserInBoard
				+ ", _startTime=" + _startTime + ", _playersInBoard=" + _playersInBoard + ", _money=" + _money
				+ ", _serviceId=" + _serviceId + ", date=" + date + '}';
	}

	public void addPlayerId(int playerId) {
		_playersInBoard.add(playerId);
	}
}

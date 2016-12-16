/**
 * 
 */
package com.avengers.netty.gamelib.result;

/**
 * @author LamHa
 *
 */
public class UserResult {
	/**
	 * User id
	 */
	private int userId;
	/**
	 * Số tiền thắng / thua
	 */
	private long money;
	/**
	 * Số điểm kinh nghiệm đạt được, ý nghĩa khác nhau với từng game
	 */
	private int exp;
	/**
	 * Thứ hạng tới (1,2,3...)
	 */
	private int winRank;
	/**
	 * Loại kết quả (Thắng thua bình thường, thắng trắng, thoát giữa chừng,..),
	 * xem trong class {@link ResultType}
	 */
	private byte resultType;
	/**
	 * User name
	 */
	private String userName;
	/**
	 * Thông tin thêm để tính toán xp cho từng game
	 */
	private String extraDataLevel;

	public UserResult() {

	}

	public UserResult(int userId, String userName) {
		this.userId = userId;
		this.userName = userName;
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

	public void setMoney(long money) {
		this.money = money;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getWinRank() {
		return winRank;
	}

	public void setWinRank(int winRank) {
		this.winRank = winRank;
	}

	public byte getResultType() {
		return resultType;
	}

	public void setResultType(byte resultType) {
		this.resultType = resultType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getExtraDataLevel() {
		return extraDataLevel;
	}

	public void setExtraDataLevel(String extraDataLevel) {
		this.extraDataLevel = extraDataLevel;
	}
}

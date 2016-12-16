package com.avengers.netty.core.om;

/**
 * @author LamHa
 *
 */
public class RoomSize {
	private int userCount;
	private int spectatorCount;

	public RoomSize(int userCount, int spectatorCount) {
		this.userCount = userCount;
		this.spectatorCount = spectatorCount;
	}

	/**
	 * Lấy số lượng User
	 * 
	 * @return
	 */
	public int getUserCount() {
		return this.userCount;
	}

	/**
	 * Lấy Số lượng View
	 * 
	 * @return
	 */
	public int getSpectatorCount() {
		return this.spectatorCount;
	}

	public int getTotalUsers() {
		return getUserCount() + getSpectatorCount();
	}

	public String toString() {
		return String.format("{ u: %s, s: %s }",
				new Object[] { Integer.valueOf(this.userCount), Integer.valueOf(this.spectatorCount) });
	}
}

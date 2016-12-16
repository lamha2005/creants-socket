package com.avengers.netty.gamelib.scheduler;

/**
 * @author LamHa
 *
 */
public interface ITimeoutListener {
	/**
	 * Xử lý kết thúc game khi hết giờ
	 */
	void timeout();
}

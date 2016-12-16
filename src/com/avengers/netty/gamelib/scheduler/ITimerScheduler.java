package com.avengers.netty.gamelib.scheduler;

/**
 * @author LamHa
 *
 */
public interface ITimerScheduler {
	String getId();

	long getTimeMillis();

	void startCountdown(long millis);

	void timeout();

	void cancelTimeout();

	void registerListener(ITimeoutListener listener);
}

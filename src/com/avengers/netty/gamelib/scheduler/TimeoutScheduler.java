package com.avengers.netty.gamelib.scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.avengers.netty.core.om.IRoom;
import com.avengers.netty.core.om.ServerConfig;

/**
 * Class quản lý thời gian đếm ngược của một trận đấu.<br>
 * Khi người chơi thực hiện move thì cập nhật thời gian còn lại cho người chơi
 * đó, chờ phản hồi của các người chơi còn lại đã render xong chưa giúp hạn chế
 * thời gian client và server khác nhau<br>
 * và bắt đầu đếm với người chơi tiếp theo thời gian còn lại của người đó<br>
 * Schedule có 2 trạng thái là ON_TURN(đang chờ một lượt đi) FEEDBACK(đang chờ
 * phản hồi của các người chơi khác)
 * 
 * @author LamHa
 *
 */
public class TimeoutScheduler implements ITimerScheduler {

	private final ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(ServerConfig.threadPoolTimerSize);
	private String id;
	private Long timeMillis;
	private ITimeoutListener timeoutListener;
	private ScheduledFuture<?> countdownHandle;

	public TimeoutScheduler(String id) {
		this.id = id;
	}

	/**
	 * Sinh id với bàn tương ứng
	 */
	public static String generateId(IRoom room) {
		return String.valueOf(room.getId());
	}

	public long getTimeMillis() {
		return timeMillis;
	}

	public String getId() {
		return id;
	}

	public void startCountdown(long millis) {
		timeMillis = millis;
		countdownHandle = scheduler.scheduleAtFixedRate(new Runnable() {
			public void run() {
				synchronized (timeMillis) {
					if (timeMillis > 0) {
						timeMillis -= 1000;
						// TODO TIMEOUT SCHEDULER remove after test
						// System.out.println(String.format("[DEBUG] roomId_%s
						// tick=%d", id, timeMillis / 1000));
					} else {
						timeout();
					}
				}
			}
		}, 0, 1, TimeUnit.SECONDS);
	}

	public void timeout() {
		cancelTimeout();
		if (timeoutListener != null) {
			timeoutListener.timeout();
		}
	}

	public void cancelTimeout() {
		if (countdownHandle != null)
			countdownHandle.cancel(true);
	}

	public void registerListener(ITimeoutListener listener) {
		this.timeoutListener = listener;
	}

}

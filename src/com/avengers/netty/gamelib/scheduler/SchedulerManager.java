package com.avengers.netty.gamelib.scheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class quản lý tất cả các timeout scheduler của các trận đấu
 * 
 * @author LamHa
 *
 */
public class SchedulerManager {

	private static SchedulerManager instance = null;

	// TODO using ehcache to management idle time, live time
	private Map<String, ITimerScheduler> timerSchedulers;

	private SchedulerManager() {
		timerSchedulers = new ConcurrentHashMap<String, ITimerScheduler>();
	}

	public static SchedulerManager getInstance() {
		if (instance == null)
			instance = new SchedulerManager();

		return instance;
	}

	public ITimerScheduler getTimerScheduler(String id) {
		return timerSchedulers.get(id);
	}

	public void registerScheduler(ITimerScheduler timerScheduler) {
		timerSchedulers.put(timerScheduler.getId(), timerScheduler);
	}

	public void deleteScheduler(String id) {
		timerSchedulers.remove(id);
	}

}

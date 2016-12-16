package com.avengers.netty.core.log.asyntask;

import com.avengers.netty.core.log.SessionLog;
import com.avengers.netty.core.queue.RabitQueueService;
import com.avengers.netty.core.queue.constant.QueueLogKey;

/**
 * @author LamHa
 *
 */
public class SessionLogTask implements Runnable {

	private SessionLog sessionLog;

	public SessionLogTask(SessionLog sessionLog) {
		this.sessionLog = sessionLog;
	}

	@Override
	public void run() {
		String data = sessionLog.getUserName() + QueueLogKey.SEPERATE_CHARS + sessionLog.getTimeLogin() / 1000
				+ QueueLogKey.SEPERATE_CHARS + sessionLog.getTimeLogout() / 1000 + QueueLogKey.SEPERATE_CHARS
				+ sessionLog.getMoneyLogin() + QueueLogKey.SEPERATE_CHARS + sessionLog.getMoneyLogout()
				+ QueueLogKey.SEPERATE_CHARS + sessionLog.getClientVersion() + QueueLogKey.SEPERATE_CHARS
				+ sessionLog.getClientPhone() + QueueLogKey.SEPERATE_CHARS + sessionLog.getIpClient()
				+ QueueLogKey.SEPERATE_CHARS + sessionLog.getServerId() + QueueLogKey.SEPERATE_CHARS
				+ sessionLog.getPlatformId();
		RabitQueueService.getInstance().sendLog(QueueLogKey.GENERAL_EXCHANGE, QueueLogKey.Session, false, data);
	}
}

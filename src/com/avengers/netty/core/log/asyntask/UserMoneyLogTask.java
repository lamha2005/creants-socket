package com.avengers.netty.core.log.asyntask;

import com.avengers.netty.core.log.UserMoneyLog;
import com.avengers.netty.core.queue.RabitQueueService;
import com.avengers.netty.core.queue.constant.QueueLogKey;

/**
 * @author LamHa
 *
 */
public class UserMoneyLogTask implements Runnable {

	private final UserMoneyLog moneyLog;

	public UserMoneyLogTask(UserMoneyLog moneyLog) {
		this.moneyLog = moneyLog;
	}

	@Override
	public void run() {

		StringBuilder dataLogs = new StringBuilder().append((int) (System.currentTimeMillis() / 1000))
				.append(QueueLogKey.SEPERATE_CHARS).append(moneyLog.getUserName()).append(QueueLogKey.SEPERATE_CHARS)
				.append(moneyLog.getMoney()).append(QueueLogKey.SEPERATE_CHARS).append(moneyLog.getValue())
				.append(QueueLogKey.SEPERATE_CHARS).append(moneyLog.getServerId()).append(QueueLogKey.SEPERATE_CHARS)
				.append(moneyLog.getReasonId()).append(QueueLogKey.SEPERATE_CHARS).append(moneyLog.getBoardId())
				.append(QueueLogKey.SEPERATE_CHARS).append(moneyLog.getDescription());

		RabitQueueService.getInstance().sendLog(QueueLogKey.MONEY_EXCHANGE, QueueLogKey.UserMoney, true,
				dataLogs.toString());
	}

}

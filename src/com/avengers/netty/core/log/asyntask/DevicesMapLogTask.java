package com.avengers.netty.core.log.asyntask;

import com.avengers.netty.core.om.ClientDevice;
import com.avengers.netty.core.queue.RabitQueueService;
import com.avengers.netty.core.queue.constant.QueueLogKey;

/**
 * @author LamHa
 *
 */
public class DevicesMapLogTask implements Runnable {

	private String userName;
	private ClientDevice clientDevice;

	public DevicesMapLogTask(String userName, ClientDevice clientDevice) {
		this.userName = userName;
		this.clientDevice = clientDevice;
	}

	@Override
	public void run() {
		if (clientDevice == null || clientDevice.getNotifyKey() == null || clientDevice.getNotifyKey().isEmpty()) {
			return;
		}
		String dataLogs = userName + QueueLogKey.SEPERATE_CHARS + clientDevice.getNotifyKey()
				+ QueueLogKey.SEPERATE_CHARS + clientDevice.getOs() + QueueLogKey.SEPERATE_CHARS
				+ clientDevice.getCarrier() + QueueLogKey.SEPERATE_CHARS + clientDevice.getNet()
				+ QueueLogKey.SEPERATE_CHARS + clientDevice.getLang();
		RabitQueueService.getInstance().sendLog(QueueLogKey.GENERAL_EXCHANGE, QueueLogKey.DevicesMap, false, dataLogs);
	}
}
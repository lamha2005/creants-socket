package com.avengers.netty.core.log.asyntask;

import com.avengers.netty.core.om.ClientDevice;
import com.avengers.netty.core.queue.RabitQueueService;
import com.avengers.netty.core.queue.constant.QueueLogKey;

/**
 * @author LamHa
 *
 */
public class DevicesLogTask implements Runnable {

	private ClientDevice clientDevice;

	public DevicesLogTask(ClientDevice clientDevice) {
		this.clientDevice = clientDevice;
	}

	@Override
	public void run() {
		String dataLogs = clientDevice.getNotifyKey() + QueueLogKey.SEPERATE_CHARS + clientDevice.getPhone()
				+ QueueLogKey.SEPERATE_CHARS + clientDevice.getDevice() + QueueLogKey.SEPERATE_CHARS
				+ clientDevice.getOs() + QueueLogKey.SEPERATE_CHARS + clientDevice.getOsVersion()
				+ QueueLogKey.SEPERATE_CHARS + clientDevice.getVersion() + QueueLogKey.SEPERATE_CHARS
				+ String.valueOf(clientDevice.getIsJaibreak());
		RabitQueueService.getInstance().sendLog(QueueLogKey.GENERAL_EXCHANGE, QueueLogKey.Devices, false, dataLogs);
	}
}
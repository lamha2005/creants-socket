package com.avengers.netty.core.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author LamHa
 *
 */
public class CoreTracer {
	private static final Logger CREANTS_LOG = LogManager.getLogger("CreantsLogger");
	private static final Logger LOG_SECURITY_LOG = LogManager.getLogger("LogSecurityLogger");
	private static final Logger PAYMENT_LOG = LogManager.getLogger("PaymentLogger");
	private static final Logger MSG_QUEUE_LOG = LogManager.getLogger("MessageQueueLogger");

	public static void debug(Class<?> clazz, Object... msgs) {
		if (CREANTS_LOG.isDebugEnabled()) {
			CREANTS_LOG.debug(getTraceMessage(clazz, msgs));
		}
	}

	public static void info(Class<?> clazz, Object... msgs) {
		CREANTS_LOG.info(getTraceMessage(clazz, msgs));
	}

	/**
	 * Log thông tin lỗi
	 * 
	 * @param clazz
	 *            class nào xảy ra lỗi
	 * @param msgs
	 *            thông tin kèm theo lỗi - nên kèm theo tên hàm
	 */
	public static void error(Class<?> clazz, Object... msgs) {
		CREANTS_LOG.error(getTraceMessage(clazz, msgs));
	}

	/**
	 * Log thông tin cảnh báo
	 * 
	 * @param clazz
	 *            class nào xảy ra lỗi
	 * @param msgs
	 *            thông tin kèm theo lỗi - nên kèm theo tên hàm
	 */
	public static void warn(Class<?> clazz, Object... msgs) {
		CREANTS_LOG.warn(getTraceMessage(clazz, msgs));
	}

	/**
	 * log lịch sử nhận data từ RabbitMQ
	 * 
	 * @param clazz
	 *            class xử lý nhận, xử lý data từ RabbitMQ
	 * @param msgs
	 *            thông tin kèm theo - nên kèm theo têm hàm xử lý
	 */
	public static void debugMsgQueue(Class<?> clazz, Object... msgs) {
		if (MSG_QUEUE_LOG.isDebugEnabled()) {
			MSG_QUEUE_LOG.debug(getTraceMessage(clazz, msgs));
		}
	}

	/**
	 * log thông tin login
	 * 
	 * @param clazz
	 *            class xử lý nhận, xử lý data từ RabbitMQ
	 * @param msgs
	 *            thông tin kèm theo - nên kèm theo têm hàm xử lý
	 */
	public static void debugLogin(Class<?> clazz, Object... msgs) {
		if (LOG_SECURITY_LOG.isDebugEnabled()) {
			LOG_SECURITY_LOG.debug(getTraceMessage(clazz, msgs));
		}
	}

	/**
	 * log thông tin login
	 * 
	 * @param clazz
	 *            class xử lý nhận, xử lý data từ RabbitMQ
	 * @param msgs
	 *            thông tin kèm theo - nên kèm theo têm hàm xử lý
	 */
	public static void debugPayment(Class<?> clazz, Object... msgs) {
		if (PAYMENT_LOG.isDebugEnabled()) {
			PAYMENT_LOG.debug(getTraceMessage(clazz, msgs));
		}
	}

	private static String getTraceMessage(Class<?> clazz, Object[] msgs) {
		StringBuilder traceMsg = new StringBuilder().append("{").append(clazz.getSimpleName()).append("}: ");
		Object[] arrayOfObject;
		int j = (arrayOfObject = msgs).length;
		for (int i = 0; i < j; i++) {
			traceMsg.append(arrayOfObject[i].toString()).append(" ");
		}

		return traceMsg.toString();
	}
}

package com.avengers.netty.socket.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author LamHa
 *
 */
public class DateUtils {
	private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");

	public static String formatDateTime(Date date) {
		return dateTimeFormat.format(date);
	}

	public static String formatDate(Date date) {
		return dateFormat.format(date);
	}
}

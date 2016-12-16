package com.avengers.netty.core.extensions;

/**
 * @author LamHa
 *
 */
public abstract interface IHandlerFactory {
	/**
	 * Add handler vào extension
	 * 
	 * @param commandId
	 *            handler id
	 * @param handlerClass
	 *            class xử lý logic handler
	 */
	public abstract void addHandler(short commandId, Class<?> handlerClass);

	public abstract void addHandler(short commandId, Object handler);

	/**
	 * Xóa handler khỏi extension
	 * 
	 * @param commandId
	 *            handler id
	 */
	public abstract void removeHandler(short commandId);

	/**
	 * Get handler by id
	 * 
	 * @param commandId
	 *            handler id
	 */
	public abstract Object findHandler(short commandId);

	public abstract void clearAll();
}

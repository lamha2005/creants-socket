package com.avengers.netty.core.exception;

/**
 * @author LamHa
 *
 */
public class RoomException extends CoreException {

	private static final long serialVersionUID = -6274741862565682229L;

	public RoomException() {
	}

	public RoomException(String message) {
		super(message);
	}

	public RoomException(String message, CoreErrorData data) {
		super(message, data);
	}
}
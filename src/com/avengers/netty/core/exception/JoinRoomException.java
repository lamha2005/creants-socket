package com.avengers.netty.core.exception;

/**
 * @author LamHa
 *
 */
public class JoinRoomException extends CoreException {
	private static final long serialVersionUID = 6384101728401558209L;

	public JoinRoomException() {
	}

	public JoinRoomException(String message) {
		super(message);
	}

	public JoinRoomException(String message, CoreErrorData data) {
		super(message, data);
	}
}

package com.avengers.netty.core.exception;

/**
 * @author LamHa
 *
 */
public class CreateRoomException extends CoreException {
	private static final long serialVersionUID = 4751733417642191809L;

	public CreateRoomException() {
	}

	public CreateRoomException(String message) {
		super(message);
	}

	public CreateRoomException(String message, CoreErrorData data) {
		super(message, data);
	}
}

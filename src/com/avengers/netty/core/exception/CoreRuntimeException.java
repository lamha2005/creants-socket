package com.avengers.netty.core.exception;

/**
 * @author LamHa
 *
 */
public class CoreRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -6433280068682786254L;

	public CoreRuntimeException() {
	}

	public CoreRuntimeException(String message) {
		super(message);
	}

	public CoreRuntimeException(Throwable t) {
		super(t);
	}

}

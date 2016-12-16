package com.avengers.netty.core.exception;

/**
 * @author LamHa
 *
 */
public class CoreException extends Exception {
	private static final long serialVersionUID = 6052949605652105170L;
	CoreErrorData errorData;

	public CoreException() {
		this.errorData = null;
	}

	public CoreException(String message) {
		super(message);
		this.errorData = null;
	}

	public CoreException(String message, CoreErrorData data) {
		super(message);
		this.errorData = data;
	}

	public CoreException(Throwable t) {
		super(t);
		this.errorData = null;
	}

	public CoreErrorData getErrorData() {
		return this.errorData;
	}
}

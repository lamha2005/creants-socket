package com.avengers.netty.core.exception;

/**
 * @author LamHa
 *
 */
public class CoreExtensionException extends CoreException {

	private static final long serialVersionUID = -5341357290605347548L;

	public CoreExtensionException() {
	}

	public CoreExtensionException(String message) {
		super(message);
	}

	public CoreExtensionException(String message, CoreErrorData data) {
		super(message, data);
	}
}

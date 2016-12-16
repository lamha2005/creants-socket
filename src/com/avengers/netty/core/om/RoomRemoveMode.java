package com.avengers.netty.core.om;

/**
 * @author LamHa
 *
 */
public enum RoomRemoveMode {
	DEFAULT, WHEN_EMPTY, NEVER_REMOVE;

	public static RoomRemoveMode fromString(String id) {
		return valueOf(id.toUpperCase());
	}
}

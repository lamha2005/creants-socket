package com.avengers.netty.core.util;

import com.avengers.netty.core.om.IRoom;

/**
 * @author LamHa
 *
 */
public abstract interface IPlayerIdGenerator {
	public abstract void init();

	public abstract int getPlayerSlot();

	/**
	 * Giải phóng 1 slot chỉ định
	 * 
	 * @param paramInt
	 */
	public abstract void freePlayerSlot(int paramInt);

	public abstract void onRoomResize();

	public abstract void setParentRoom(IRoom paramRoom);

	public abstract IRoom getParentRoom();
}

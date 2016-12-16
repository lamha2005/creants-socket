package com.avengers.netty.core.util;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avengers.netty.core.om.IRoom;

/**
 * @author LamHa
 *
 */
public class DefaultPlayerIdGenerator implements IPlayerIdGenerator {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultPlayerIdGenerator.class);
	private IRoom parentRoom;
	private volatile Boolean[] playerSlots;

	public void init() {
		playerSlots = new Boolean[parentRoom.getMaxUsers() + 1];
		Arrays.fill(playerSlots, Boolean.FALSE);
	}

	public int getPlayerSlot() {
		int playerId = 0;
		synchronized (playerSlots) {
			for (int i = 1; i < playerSlots.length; i++) {
				if (!playerSlots[i].booleanValue()) {
					playerId = i;
					playerSlots[i] = Boolean.TRUE;
					break;
				}
			}
		}

		if (playerId < 1) {
			LOG.warn("No player slot found in " + this.parentRoom);
		}

		return playerId;
	}

	public void freePlayerSlot(int playerId) {
		if (playerId == -1) {
			return;
		}

		if (playerId >= playerSlots.length) {
			return;
		}

		synchronized (playerSlots) {
			playerSlots[playerId] = Boolean.FALSE;
		}
	}

	public void onRoomResize() {
		Boolean[] newPlayerSlots = new Boolean[parentRoom.getMaxUsers() + 1];
		synchronized (playerSlots) {
			for (int i = 1; i < newPlayerSlots.length; i++) {
				if (i < playerSlots.length) {
					newPlayerSlots[i] = playerSlots[i];
				} else {
					newPlayerSlots[i] = Boolean.FALSE;
				}
			}
		}
		playerSlots = newPlayerSlots;
	}

	public IRoom getParentRoom() {
		return parentRoom;
	}

	public void setParentRoom(IRoom room) {
		parentRoom = room;
	}
}
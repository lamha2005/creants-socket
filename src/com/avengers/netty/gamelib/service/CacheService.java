package com.avengers.netty.gamelib.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.avengers.netty.core.om.IRoom;

/**
 * @author LamHa
 *
 */
public class CacheService {
	private static CacheService instance;
	private Map<Integer, IRoom> userInRoomMap;

	public static CacheService getInstace() {
		if (instance == null)
			instance = new CacheService();
		return instance;
	}

	private CacheService() {
		userInRoomMap = new ConcurrentHashMap<Integer, IRoom>();
	}

	public IRoom getLastRoomByUser(int creantUserId) {
		return userInRoomMap.get(creantUserId);
	}

	public void joinRoom(int creantUserId, IRoom room) {
		userInRoomMap.put(creantUserId, room);
	}

	public void freeLastRoom(int creantUserId) {
		userInRoomMap.remove(creantUserId);
	}
}

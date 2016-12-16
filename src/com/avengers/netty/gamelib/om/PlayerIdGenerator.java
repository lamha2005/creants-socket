package com.avengers.netty.gamelib.om;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.avengers.netty.core.exception.RoomException;
import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public class PlayerIdGenerator {
	private volatile long[] playerSlots;

	public PlayerIdGenerator(int maxPlayer) {
		init(maxPlayer);
	}

	private void init(int maxPlayer) {
		playerSlots = new long[maxPlayer + 1];
		Arrays.fill(playerSlots, 0L);
	}

	/**
	 * Tìm 1 chỗ ngồi free
	 * 
	 * @return
	 */
	public int getOneFreeSlot() {
		int playerId = 0;

		synchronized (playerSlots) {
			for (int i = 1; i < playerSlots.length; i++) {
				if (playerSlots[i] == 0) {
					playerId = i;
					break;
				}
			}
		}

		return playerId;
	}

	/**
	 * Cho user ngồi vào vị trí free trong bàn
	 * 
	 * @param playerId
	 * @param user
	 * @return
	 * @throws SFSRoomException
	 */
	public int sitDownSlot(int playerId, User user) throws RoomException {
		if (playerId == -1 || playerId >= playerSlots.length) {
			return 0;
		}
		synchronized (playerSlots) {
			if (playerSlots[playerId] != 0) {
				throw new RoomException("This slot already have user");
			}
			playerSlots[playerId] = user.getUserId();
		}
		return playerId;
	}

	/**
	 * Giải phóng position khi user leave
	 * 
	 * @param playerId
	 */
	public void freePlayerSlot(int playerId) {
		if (playerId == -1 || playerId >= playerSlots.length) {
			return;
		}
		synchronized (playerSlots) {
			playerSlots[playerId] = 0L;
		}
	}

	/**
	 * 
	 * @param userId
	 * @return
	 */
	public int freeUserSlot(long userId) {
		int result = 0;
		synchronized (playerSlots) {
			for (int i = 1; i < playerSlots.length; i++) {
				if (playerSlots[i] == userId) {
					playerSlots[i] = 0L;
					result = i;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Lấy userId theo position
	 * 
	 * @param playerId
	 * @return
	 */
	public long getUserIdByPlayerID(int playerId) {
		if (playerId == -1 || playerId >= playerSlots.length) {
			return 0;
		}

		synchronized (playerSlots) {
			return playerSlots[playerId];
		}
	}

	/**
	 * Lấy danh sách position
	 * 
	 * @return
	 */
	public List<Long> getUserIdList() {
		List<Long> result = new ArrayList<Long>();
		synchronized (playerSlots) {
			for (int i = 1; i < playerSlots.length; i++) {
				if (playerSlots[i] != 0) {
					result.add(playerSlots[i]);
				}
			}
		}
		return result;
	}

	/**
	 * Lấy position theo user
	 * 
	 * @param user
	 * @return
	 */
	public int getPlayersIdOf(User user) {

		synchronized (playerSlots) {
			for (int i = 1; i < playerSlots.length; i++) {
				if (user.getUserId() == playerSlots[i]) {
					return i;
				}
			}
		}

		return 0;
	}
}

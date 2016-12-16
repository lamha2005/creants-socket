package com.avengers.netty.core.om;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LamHa
 *
 * @param <T>
 */
public class RoomPage<T> {
	public static final int MAX_SIZE = 10;
	private List<T> rooms;

	private int nextPage;

	private boolean getOnCluster;

	public RoomPage() {
		rooms = new ArrayList<T>();
		nextPage = -1;
	}

	public List<T> getRooms() {
		return rooms;
	}

	public void setRooms(List<T> rooms) {
		this.rooms = rooms;
	}

	public int getNextPage() {
		return nextPage;
	}

	public void setNextPage(int nextPage) {
		this.nextPage = nextPage;
	}

	public boolean isGetOnCluster() {
		return getOnCluster;
	}

	public void setGetOnCluster(boolean getOnCluster) {
		this.getOnCluster = getOnCluster;
	}

}

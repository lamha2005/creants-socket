package com.avengers.netty.core.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avengers.netty.core.dao.cluster.repository.ClusterRoomRepository;
import com.avengers.netty.core.dao.cluster.repository.ClusterUserRepository;
import com.avengers.netty.core.om.RoomPage;
import com.avengers.netty.core.om.ServerConfig;
import com.avengers.netty.core.om.cluster.ClusterRoom;
import com.avengers.netty.core.om.cluster.ClusterUser;

/**
 * @author LamHa
 *
 */
public class IgniteService implements IClusterService {
	private static final Logger LOG = LoggerFactory.getLogger(IgniteService.class);
	private Ignite grid;
	private ClusterRoomRepository roomRepository;
	private ClusterUserRepository userRepository;

	public IgniteService() {
		if (!ServerConfig.enableCluster)
			return;

		LOG.info("---------------------- INIT GRIDGAIN --------------------");
		grid = Ignition.start("configs/gridgain.xml");
		// roomRepository = new
		// ClusterRoomRepository(grid.cache("clusterRoom"));
		// userRepository = new
		// ClusterUserRepository(grid.cache("clusterUser"));

		String nodeId = grid.cluster().localNode().id().toString();
		String nodeIp = "";
		LOG.info("::::::::::::::::::::::::::::::::::::::::::::::::::::");
		LOG.info(">> GridGain STARTED");
		LOG.info(String.format(">> [serverIp=%s, nodeId=%s", nodeIp, nodeId));
		LOG.info("::::::::::::::::::::::::::::::::::::::::::::::::::::");
	}

	@Override
	public ClusterRoom getRoom(String roomId) {
		return roomRepository.find(roomId);
	}

	@Override
	public void saveRoom(ClusterRoom room) {
		roomRepository.save(room.getClusterId(), room);
	}

	@Override
	public void saveUser(ClusterUser user) {
		userRepository.save(user.getUserId(), user);
	}

	@Override
	public void removeRoom(String roomId) {
		roomRepository.delete(roomId);
	}

	@Override
	public void removeUser(int userId) {
		userRepository.delete(userId);
	}

	@Override
	public ClusterUser getUser(int userId) {
		return userRepository.find(userId);
	}

	@Override
	public Collection<ClusterUser> getUsers(Set<Integer> ids) {
		return userRepository.find(ids);
	}

	public Collection<ClusterRoom> filterRoom(int roomId) {
		return roomRepository.filter(roomId);
	}

	@Override
	public List<ClusterRoom> getRoomList(int page, boolean getOnCluster) {
		return roomRepository.getRoomList(page, getOnCluster);
	}

	@Override
	public RoomPage<ClusterRoom> getRoomPage(int page, boolean getOnCluster) {
		List<ClusterRoom> roomList = getRoomList(page, getOnCluster);
		if (roomList != null) {
			RoomPage<ClusterRoom> roomPage = new RoomPage<ClusterRoom>();
			roomPage.setRooms(roomList);
			if (roomList.size() >= RoomPage.MAX_SIZE) {
				roomPage.setNextPage(page);
			}
			roomPage.setGetOnCluster(getOnCluster);
			return roomPage;
		}

		return null;
	}

	@Override
	public List<ClusterRoom> searchRoomByOwnerOrRoomName(int page, String text) {
		return roomRepository.searchRoomByOwnerOrRoomName(page, text);
	}

	@Override
	public ClusterRoom findRoom(String exceptHost) {
		return roomRepository.findRoom(exceptHost);
	}

}

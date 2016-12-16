package com.avengers.netty.core.dao.cluster.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.cache.Cache.Entry;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlQuery;

import com.avengers.netty.core.dao.cluster.IgniteRepository;
import com.avengers.netty.core.om.RoomPage;
import com.avengers.netty.core.om.ServerConfig;
import com.avengers.netty.core.om.cluster.ClusterRoom;
import com.avengers.netty.core.service.IClusterService;

/**
 * @author LamHa
 *
 */
public class ClusterRoomRepository extends IgniteRepository<ClusterRoom, String> {

	public ClusterRoomRepository(IgniteCache<String, ClusterRoom> igniteCache) {
		super(igniteCache);
	}

	public Collection<ClusterRoom> filter(int id) {
		SqlQuery<String, ClusterRoom> query = new SqlQuery<>(ClusterRoom.class, "id > ?");
		QueryCursor<Entry<String, ClusterRoom>> result = igniteCache.query(query.setArgs(id));
		Collection<ClusterRoom> rooms = null;
		if (result != null) {
			rooms = new ArrayList<ClusterRoom>();
			for (Entry<String, ClusterRoom> entry : result) {
				rooms.add(entry.getValue());
			}
		}

		return rooms;
	}

	public ClusterRoom findRoom(String host) {
		List<Entry<String, ClusterRoom>> result = igniteCache
				.query(new SqlQuery<String, ClusterRoom>(ClusterRoom.class, "ipAddress != ? AND maxUsers < 9 LIMIT ?")
						.setArgs(host, 1))
				.getAll();
		if (result == null || result.size() <= 0)
			return null;

		return result.get(0).getValue();
	}

	/**
	 * @see IClusterService#getRoomList(int page, boolean getOnCluster)
	 */
	public List<ClusterRoom> getRoomList(int page, boolean getOnCluster) {
		String sql = "";
		if (getOnCluster) {
			sql = "ipAddress != ? LIMIT ? OFFSET ?";
		} else {
			sql = "idAddress == ? LIMIT ? OFFSET ?";
		}

		// optimize có thể trả luôn List entry
		List<Entry<String, ClusterRoom>> result = igniteCache
				.query(new SqlQuery<String, ClusterRoom>(ClusterRoom.class, sql).setArgs(ServerConfig.getServerHost(),
						RoomPage.MAX_SIZE, page * RoomPage.MAX_SIZE))
				.getAll();
		if (result != null && result.size() > 0) {
			List<ClusterRoom> rooms = new ArrayList<ClusterRoom>(result.size());
			for (Entry<String, ClusterRoom> entry : result) {
				rooms.add(entry.getValue());
			}
			return rooms;
		}

		return null;
	}

	/**
	 * @see IClusterService#searchRoomByOwnerOrRoomName(int page, String text)
	 */
	public List<ClusterRoom> searchRoomByOwnerOrRoomName(int page, String text) {
		List<Entry<String, ClusterRoom>> result = igniteCache.query(
				new SqlQuery<String, ClusterRoom>(ClusterRoom.class, "owner LIKE ? OR name LIKE ? LIMIT ? OFFSET ?")
						.setArgs("%" + text + "%", "%" + text + "%", RoomPage.MAX_SIZE, page * RoomPage.MAX_SIZE))
				.getAll();

		if (result != null && result.size() > 0) {
			List<ClusterRoom> rooms = new ArrayList<ClusterRoom>(result.size());
			for (Entry<String, ClusterRoom> entry : result) {
				rooms.add(entry.getValue());
			}
			return rooms;
		}

		return null;
	}
}

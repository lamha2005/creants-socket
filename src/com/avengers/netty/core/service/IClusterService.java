package com.avengers.netty.core.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.avengers.netty.core.om.RoomPage;
import com.avengers.netty.core.om.cluster.ClusterRoom;
import com.avengers.netty.core.om.cluster.ClusterUser;

/**
 * @author LamHa
 *
 */
public interface IClusterService {

	/**
	 * Lấy room theo id
	 * 
	 * @param roomId
	 *            cluster id của room
	 * @return
	 */
	ClusterRoom getRoom(String roomId);

	/**
	 * Lưu room trên cluster
	 * 
	 * @param room
	 *            room cần lưu trên cluster
	 */
	void saveRoom(ClusterRoom room);

	/**
	 * Xóa room khỏi cluster
	 * 
	 * @param roomId
	 */
	void removeRoom(String roomId);

	/**
	 * Lưu user trên cluster
	 * 
	 * @param user
	 *            đối tượng cần lưu trên cluster
	 */
	void saveUser(ClusterUser user);

	/**
	 * Xóa user khỏi hệ thống
	 * 
	 * @param userId
	 */
	void removeUser(int userId);

	/**
	 * Lấy user theo user id
	 * 
	 * @param userId
	 * @return
	 */
	ClusterUser getUser(int userId);

	/**
	 * Lấy danh sách user
	 * 
	 * @param ids
	 *            danh sách id của user cần lấy
	 * @return
	 */
	Collection<ClusterUser> getUsers(Set<Integer> ids);

	/**
	 * Lấy danh sách room. Ưu tiên local.<br>
	 * Để phân biệt local và cluster thực hiện lấy theo ipAddress.<br>
	 * Khi lấy danh sách room trường hợp lấy không đủ số lượng ở local thực hiện
	 * lấy trên cluster bằng cách gửi <code>page = 0</code>,
	 * <code>getOnCluster = true</code>. Server gửi nextPage và getOnCluster cho
	 * client, những lần request page tiếp theo sẽ gửi 2 tham số này.
	 * 
	 * @param page
	 *            trang cần lấy
	 * @param getOnCluster
	 *            <code>true</code> nếu lấy các room trên cluster.<br>
	 *            <code>false</code> nếu muốn lấy từ local
	 * @return
	 */
	List<ClusterRoom> getRoomList(int page, boolean getOnCluster);

	RoomPage<ClusterRoom> getRoomPage(int page, boolean getOnCluster);

	/**
	 * Tìm room theo tên chủ phòng hoặc tên phòng.
	 * 
	 * @param page
	 *            trang cần lấy
	 * @param text
	 *            tên người tạo phòng hoặc tên phòng.
	 * @return
	 */
	List<ClusterRoom> searchRoomByOwnerOrRoomName(int page, String text);

	/**
	 * Tìm room còn trống ngoại từ host
	 * 
	 * @param exceptHost
	 *            host cần loại trừ
	 * @return
	 */
	ClusterRoom findRoom(String exceptHost);
}

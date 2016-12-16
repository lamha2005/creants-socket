package com.avengers.netty.core.dao.cluster.igniteloader;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;

import org.apache.ignite.cache.store.CacheStore;
import org.apache.ignite.lang.IgniteBiInClosure;

import com.avengers.netty.SocketServer;
import com.avengers.netty.core.om.cluster.ClusterUser;
import com.avengers.netty.socket.gate.wood.User;

/**
 * Class xử lý trường hợp lấy user từ cluster mà không có thì thực hiện lấy từ
 * db
 * 
 * @author LamHa
 *
 */
public class IgniteClusterUserLoader implements CacheStore<Integer, ClusterUser>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public ClusterUser load(Integer userId) throws CacheLoaderException {
		User user = SocketServer.getInstance().getUserManager().getUser(userId);
		if (user != null) {
			ClusterUser clusterUser = new ClusterUser();
			clusterUser.setUserId(user.getUserId());
			clusterUser.setUserName(user.getUserName());
			clusterUser.setSessionId(user.getSessionId());
			clusterUser.setAvatar(user.getAvatar());
			clusterUser.setMoney(user.getMoney());
			return clusterUser;
		}

		// TODO log error
		return null;
	}

	@Override
	public Map<Integer, ClusterUser> loadAll(Iterable<? extends Integer> arg0) throws CacheLoaderException {
		return null;
	}

	@Override
	public void delete(Object arg0) throws CacheWriterException {

	}

	@Override
	public void deleteAll(Collection<?> arg0) throws CacheWriterException {

	}

	@Override
	public void loadCache(IgniteBiInClosure<Integer, ClusterUser> clo, Object... args) throws CacheLoaderException {

	}

	@Override
	public void sessionEnd(boolean commit) throws CacheWriterException {

	}

	@Override
	public void write(javax.cache.Cache.Entry<? extends Integer, ? extends ClusterUser> arg0)
			throws CacheWriterException {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeAll(Collection<javax.cache.Cache.Entry<? extends Integer, ? extends ClusterUser>> arg0)
			throws CacheWriterException {
		// TODO Auto-generated method stub

	}

}

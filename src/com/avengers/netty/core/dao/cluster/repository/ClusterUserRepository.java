package com.avengers.netty.core.dao.cluster.repository;

import org.apache.ignite.IgniteCache;

import com.avengers.netty.core.dao.cluster.IgniteRepository;
import com.avengers.netty.core.om.cluster.ClusterUser;

/**
 * @author LamHa
 *
 */
public class ClusterUserRepository extends IgniteRepository<ClusterUser, Integer> {

	public ClusterUserRepository(IgniteCache<Integer, ClusterUser> igniteCache) {
		super(igniteCache);
	}

}

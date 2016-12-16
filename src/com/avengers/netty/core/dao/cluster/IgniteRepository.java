package com.avengers.netty.core.dao.cluster;

import java.io.Externalizable;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.ignite.IgniteCache;

/**
 * @author LamHa
 *
 */
public class IgniteRepository<T extends Externalizable, ID extends Serializable> implements CrudRepository<T, ID> {
	protected IgniteCache<ID, T> igniteCache;

	public IgniteRepository(IgniteCache<ID, T> igniteCache) {
		this.igniteCache = igniteCache;
	}

	@Override
	public long count() {
		return igniteCache.localSize();
	}

	@Override
	public void delete(ID id) {
		igniteCache.remove(id);
	}

	@Override
	public void delete(Set<? extends ID> keys) {
		igniteCache.removeAll(keys);
	}

	@Override
	public void deleteAll() {
		igniteCache.removeAll();
	}

	@Override
	public boolean exists(ID id) {
		return igniteCache.containsKey(id);
	}

	@Override
	public Collection<T> find(Set<ID> ids) {
		Map<ID, T> all = igniteCache.getAll(ids);
		if (all != null)
			return all.values();

		return null;
	}

	@Override
	public T find(ID id) {
		return igniteCache.get(id);
	}

	@Override
	public void save(ID id, T entity) {
		igniteCache.put(id, entity);
	}

	@Override
	public void save(Map<ID, T> entities) {
		igniteCache.putAll(entities);
	}

}

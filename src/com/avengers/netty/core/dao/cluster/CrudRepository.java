package com.avengers.netty.core.dao.cluster;

import java.io.Externalizable;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Interface định nghĩa các action cơ bản trên cluster<br>
 * Create, Read, Update, Delete...
 * 
 * @author LamHa
 *
 * @param <T>
 *            Đối tượng cần lưu trên cluster
 * @param <ID>
 *            Kiểu key của đối tượng
 */
public interface CrudRepository<T extends Externalizable, ID extends Serializable> {
	long count();

	void delete(ID id);

	void delete(Set<? extends ID> keys);

	void deleteAll();

	boolean exists(ID id);

	Collection<T> find(Set<ID> ids);

	T find(ID id);

	void save(ID id, T entity);

	void save(Map<ID, T> entities);
}

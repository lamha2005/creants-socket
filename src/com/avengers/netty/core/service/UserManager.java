package com.avengers.netty.core.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public final class UserManager implements IUserManager {
	private static final Logger LOG = LoggerFactory.getLogger(UserManager.class);
	private final ConcurrentMap<String, User> usersByName;
	private final ConcurrentMap<Integer, User> usersById;

	public UserManager() {
		usersByName = new ConcurrentHashMap<String, User>();
		usersById = new ConcurrentHashMap<Integer, User>();

	}

	public void addUser(User user) {
		if (containsId(user.getUserId())) {
			throw new RuntimeException("Can't add User: " + user.getUserName() + " - Already exists in Room: ");
		}

		usersById.put(user.getUserId(), user);
		usersByName.put(user.getName(), user);
	}

	public User getUser(int id) {
		return usersById.get(id);
	}

	public User getUser(String name) {
		return usersByName.get(name);
	}

	public void removeUser(int userId) {
		User user = usersById.get(userId);
		if (user == null) {
			LOG.warn("Can't remove user with ID: " + userId + ". User was not found.");
		} else {
			removeUser(user);
		}
	}

	public void removeUser(String name) {
		User user = usersByName.get(name);
		if (user == null) {
			LOG.warn("Can't remove user with name: " + name + ". User was not found.");
		} else {
			removeUser(user);
		}
	}

	public void removeUser(User user) {
		usersById.remove(user.getUserId());
		usersByName.remove(user.getName());
	}

	public boolean containsId(int userId) {
		return usersById.containsKey(userId);
	}

	public boolean containsName(String name) {
		return usersByName.containsKey(name);
	}

	public boolean containsUser(User user) {
		return usersById.containsValue(user);
	}

	public List<User> getAllUsers() {
		return new ArrayList<User>(usersById.values());
	}

	public Collection<User> getDirectUserList() {
		return Collections.unmodifiableCollection(usersById.values());
	}

	public int getUserCount() {
		return usersById.size();
	}

	public void disconnectUser(int userId) {
		User user = usersById.get(userId);
		if (user == null) {
			LOG.warn("Can't disconnect user with id: " + userId + ". User was not found.");
		} else {
			disconnectUser(user);
		}
	}

	public void disconnectUser(String name) {
		User user = usersByName.get(name);
		if (user == null) {
			LOG.warn("Can't disconnect user with name: " + name + ". User was not found.");
		} else {
			disconnectUser(user);
		}
	}

	public void disconnectUser(User user) {
		removeUser(user);
	}

}

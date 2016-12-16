package com.avengers.netty.core.service;

import java.util.Collection;
import java.util.List;

import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public abstract interface IUserManager {
	public abstract User getUser(String name);

	public abstract User getUser(int userId);

	public abstract List<User> getAllUsers();

	public abstract Collection<User> getDirectUserList();

	public abstract void addUser(User user);

	public abstract void removeUser(User user);

	public abstract void removeUser(String name);

	public abstract void removeUser(int userId);

	public abstract void disconnectUser(User user);

	public abstract void disconnectUser(String name);

	public abstract void disconnectUser(int userId);

	public abstract boolean containsId(int userId);

	public abstract boolean containsName(String name);

	public abstract boolean containsUser(User paramUser);

	public abstract int getUserCount();

}

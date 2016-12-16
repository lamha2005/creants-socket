package com.avengers.netty.socket.gate;

import java.util.List;

import com.avengers.netty.socket.gate.wood.Message;
import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public interface IMessageWriter {
	/**
	 * P2P Send message đến user
	 * 
	 * @param user
	 * @param message
	 */
	public void writeMessage(User user, Message message);

	/**
	 * P2G Send message đến danh sách user
	 * 
	 * @param user
	 * @param message
	 */
	public void writeMessage(List<User> user, Message message);

}

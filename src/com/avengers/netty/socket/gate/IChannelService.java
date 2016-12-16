package com.avengers.netty.socket.gate;

import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public interface IChannelService {
	void disconnect(long sessionId);

	void disconnect(User user);
}

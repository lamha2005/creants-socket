package com.avengers.netty.core.event.handler;

import java.util.List;

import com.avengers.netty.core.api.ICoreAPI;
import com.avengers.netty.core.service.IUserManager;
import com.avengers.netty.core.service.RoomManager;
import com.avengers.netty.core.util.DefaultMessageFactory;
import com.avengers.netty.socket.gate.IChannelService;
import com.avengers.netty.socket.gate.IMessageWriter;
import com.avengers.netty.socket.gate.wood.Message;
import com.avengers.netty.socket.gate.wood.User;

/**
 * Lớp trừu tượng của một RequestHandler
 * 
 * @author LamHa
 *
 */
public abstract class AbstractRequestHandler implements IRequestHandler {
	protected IChannelService channelService;
	private IMessageWriter messageWriter;
	protected ICoreAPI coreApi;
	protected IUserManager userManager;
	protected RoomManager roomService;

	public AbstractRequestHandler() {
		initialize();
	}

	protected void writeMessage(User user, Message message) {
		messageWriter.writeMessage(user, message);
	}

	protected void writeMessage(List<User> users, Message message) {
		messageWriter.writeMessage(users, message);
	}

	protected void writeErrorMessage(User user, short serviceId, short errorCode, String errorMessage) {
		messageWriter.writeMessage(user, DefaultMessageFactory.createErrorMessage(serviceId, errorCode, errorMessage));
	}

	public void setMessageWriter(IMessageWriter messageWriter) {
		this.messageWriter = messageWriter;
	}

	public IChannelService getChannelService() {
		return channelService;
	}

	public void setUserManager(IUserManager userManager) {
		this.userManager = userManager;
	}

	public void setCoreApi(ICoreAPI coreApi) {
		this.coreApi = coreApi;
	}

	public void setRoomService(RoomManager roomService) {
		this.roomService = roomService;
	}

}

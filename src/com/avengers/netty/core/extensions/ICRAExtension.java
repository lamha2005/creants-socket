package com.avengers.netty.core.extensions;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import com.avengers.netty.core.event.ICoreEvent;
import com.avengers.netty.core.exception.CoreException;
import com.avengers.netty.core.om.IRoom;
import com.avengers.netty.socket.gate.IMessage;
import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public abstract interface ICRAExtension {
	public abstract void init();

	public abstract void destroy();

	public abstract String getName();

	public abstract void setName(String paramString);

	public abstract String getExtensionFileName();

	public abstract void setExtensionFileName(String paramString);

	public abstract String getPropertiesFileName();

	public abstract void setPropertiesFileName(String paramString) throws IOException;

	public abstract Properties getConfigProperties();

	public abstract boolean isActive();

	public abstract void setActive(boolean paramBoolean);

	public abstract IRoom getCurrentRoom();

	public abstract void setCurrentRoom(IRoom paramRoom);

	public abstract ExtensionReloadMode getReloadMode();

	public abstract void setReloadMode(ExtensionReloadMode paramExtensionReloadMode);

	/**
	 * Phương thức xử lý của mỗi controller
	 * 
	 * @param user
	 * @param message
	 * @throws CoreException
	 */
	public abstract void handleClientRequest(User user, IMessage message) throws CoreException;

	/**
	 * Nhận và xử lý event từ system
	 * 
	 * @param event
	 */
	public abstract void handleServerEvent(ICoreEvent event);

	public abstract Object handleInternalMessage(String paramString, Object paramObject);

	/**
	 * Gửi một message đến một player
	 * 
	 * @param message
	 * @param receiver
	 */
	public abstract void send(IMessage message, User receiver);

	/**
	 * Gửi một message đến nhiều player
	 * 
	 * @param message
	 * @param receivers
	 */
	public abstract void send(IMessage message, List<User> receivers);
}

package com.avengers.netty.core.extensions;

import com.avengers.netty.core.event.ICoreEvent;

/**
 * @author LamHa
 *
 */
public abstract interface IServerEventHandler {
	public abstract void handleServerEvent(ICoreEvent event);

	public abstract void setParentExtension(CoreExtension meExtesion);

	public abstract CoreExtension getParentExtension();
}

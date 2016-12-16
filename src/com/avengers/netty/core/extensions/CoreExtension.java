package com.avengers.netty.core.extensions;

import com.avengers.netty.core.event.ICoreEvent;
import com.avengers.netty.core.util.CoreTracer;
import com.avengers.netty.socket.gate.IMessage;
import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public abstract class CoreExtension extends BaseExtension {
	public static final String MULTIHANDLER_REQUEST_ID = "__[[REQUEST_ID]]__";
	private final IHandlerFactory handlerFactory;

	public CoreExtension() {
		handlerFactory = new HandlerFactory(this);
	}

	public void destroy() {
		handlerFactory.clearAll();
		removeEventsForListener();
	}

	protected void addRequestHandler(short commandId, Class<?> handlerClass) {
		// trường hợp add không đúng kiểu
		if (!IClientRequestHandler.class.isAssignableFrom(handlerClass)) {
			throw new RuntimeException(
					String.format("Provided Request Handler does not implement IClientRequestHandler: %s, Cmd: %s",
							handlerClass, commandId));
		}

		CoreTracer.info(CoreExtension.class, "+ [" + handlerClass.getName() + "]");
		handlerFactory.addHandler(commandId, handlerClass);
	}

	protected void addRequestHandler(short requestId, IClientRequestHandler requestHandler) {
		handlerFactory.addHandler(requestId, requestHandler);
	}

	protected void addEventHandler(short commandId, Class<?> handler) {
		// trường hợp add không đúng kiểu
		if (!IServerEventHandler.class.isAssignableFrom(handler)) {
			throw new RuntimeException(String.format(
					"Provided Event Handler does not implement IServerEventHandler: %s, Cmd: %s", handler, commandId));
		}

		// addEventListener(eventType, this);

		handlerFactory.addHandler(commandId, handler);
	}

	protected void addEventHandler(short commandId, IServerEventHandler handler) {
		// addEventListener(eventType, this);

		handlerFactory.addHandler(commandId, handler);
	}

	protected void removeRequestHandler(short commandId) {
		handlerFactory.removeHandler(commandId);
	}

	protected void removeEventHandler(short commandId) {
		// removeEventListener(eventType, this);
		handlerFactory.removeHandler(commandId);
	}

	protected void clearAllHandlers() {
		this.handlerFactory.clearAll();
	}

	public void handleClientRequest(User sender, IMessage message) {
		try {
			short commandId = message.getCommandId();
			IClientRequestHandler handler = (IClientRequestHandler) handlerFactory.findHandler(commandId);
			if (handler == null) {
				throw new RuntimeException("Request handler not found: '" + commandId
						+ "'. Make sure the handler is registered in your extension using addRequestHandler()");
			}
			handler.handleClientRequest(sender, message);
		} catch (Exception err) {
			CoreTracer.warn(this.getClass(), "Cannot instantiate handler class:", err);
			err.printStackTrace();
		}
	}

	public void handleServerEvent(ICoreEvent event) {
		short handlerId = event.getEventId();
		try {
			Object findHandler = handlerFactory.findHandler(handlerId);
			// trường hợp không có custom event
			if (findHandler == null)
				return;

			IServerEventHandler handler = (IServerEventHandler) findHandler;
			handler.handleServerEvent(event);
		} catch (Exception err) {
			CoreTracer.warn(this.getClass(), "Cannot instantiate handler class:", err);
			err.printStackTrace();
		}
	}

}

package com.avengers.netty.core.extensions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LamHa
 *
 */
public class HandlerFactory implements IHandlerFactory {
	public static final String DOT_SEPARATOR = ".";
	private final Map<Short, Class<?>> handlers;
	private final Map<Short, Object> cachedHandlers;
	private final CoreExtension parentExtension;

	public HandlerFactory(CoreExtension parentExtension) {
		handlers = new ConcurrentHashMap<Short, Class<?>>();
		cachedHandlers = new ConcurrentHashMap<Short, Object>();
		this.parentExtension = parentExtension;
	}

	@Override
	public void addHandler(short commandId, Class<?> handlerClass) {
		handlers.put(commandId, handlerClass);
	}

	@Override
	public void addHandler(short commandId, Object handler) {
		setHandlerParentExtension(handler);
		cachedHandlers.put(commandId, handler);
	}

	@Override
	public synchronized void clearAll() {
		handlers.clear();
		cachedHandlers.clear();
	}

	@Override
	public synchronized void removeHandler(short handlerKey) {
		handlers.remove(handlerKey);
		if (cachedHandlers.containsKey(handlerKey)) {
			cachedHandlers.remove(handlerKey);
		}
	}

	@Override
	public Object findHandler(short key) {
		return getHandlerInstance(key);
	}

	private Object getHandlerInstance(short key) {
		Object handler = cachedHandlers.get(key);
		if (handler != null) {
			return handler;
		}

		Class<?> handlerClass = handlers.get(key);
		if (handlerClass == null) {
			return null;
		}

		try {
			handler = handlerClass.newInstance();
			setHandlerParentExtension(handler);
			cachedHandlers.put(key, handler);
		} catch (InstantiationException e) {
			// TODO nên throw lại exeception cho client xử lý
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return handler;

	}

	private void setHandlerParentExtension(Object handler) {
		if ((handler instanceof IClientRequestHandler)) {
			((IClientRequestHandler) handler).setParentExtension(parentExtension);
		} else if ((handler instanceof IServerEventHandler)) {
			((IServerEventHandler) handler).setParentExtension(parentExtension);
		}
	}

}

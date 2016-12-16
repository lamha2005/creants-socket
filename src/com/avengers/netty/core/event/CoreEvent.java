package com.avengers.netty.core.event;

import java.util.Map;

/**
 * @author LamHa
 *
 */
public class CoreEvent implements ICoreEvent {
	private final short eventId;
	private final Map<ICoreEventParam, Object> params;

	public CoreEvent(short eventId) {
		this(eventId, null);
	}

	public CoreEvent(short eventId, Map<ICoreEventParam, Object> params) {
		this.eventId = eventId;
		this.params = params;
	}

	public short getEventId() {
		return eventId;
	}

	public Object getParameter(ICoreEventParam id) {
		Object param = null;
		if (params != null) {
			param = params.get(id);
		}

		return param;
	}

	public String toString() {
		return String.format("{EventId: %s, Params: %s }", eventId, params != null ? params.keySet() : "none");
	}
}

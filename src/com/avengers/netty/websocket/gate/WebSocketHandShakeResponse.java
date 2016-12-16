package com.avengers.netty.websocket.gate;

/**
 * @author LamHa
 *
 */
public class WebSocketHandShakeResponse {

	private String response;

	public WebSocketHandShakeResponse(String response) {
		this.response = response;
	}

	public String getResponse() {
		return this.response;
	}
}

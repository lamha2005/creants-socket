package com.avengers.netty.core.om.ext;

/**
 * @author LamHa
 *
 */
public class GameInfo {
	private Byte gameId;
	private String gameName;
	private String imageUrl;

	public Byte getGameId() {
		return gameId;
	}

	public void setGameId(Byte gameId) {
		this.gameId = gameId;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@Override
	public String toString() {
		return String.format("GameInfo [gameId: %d, gameName: %s, imageUrl: %s]", gameId, gameName, imageUrl);
	}

}

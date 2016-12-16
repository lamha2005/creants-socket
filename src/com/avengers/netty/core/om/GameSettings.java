package com.avengers.netty.core.om;

import com.avengers.netty.core.om.CreateRoomSettings.RoomExtensionSettings;

/**
 * @author LamHa
 *
 */
public class GameSettings {

	public byte gameId;
	public byte gameType = 1;
	public String gameNameEn = "";
	public String gameNameVi = "";
	public String groupId = "default";
	public RoomExtensionSettings extension;
	public CreateRoomSettings lobby;
	public int quantityRoom = 10;
	public int maxUserInGame = 2;
	public int maxSpectatorInGame = 0;

}
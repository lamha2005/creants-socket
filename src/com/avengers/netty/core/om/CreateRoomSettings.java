package com.avengers.netty.core.om;

import java.util.Map;
import java.util.Set;

import com.avengers.netty.core.util.IPlayerIdGenerator;

/**
 * @author LamHa
 *
 */
public class CreateRoomSettings {
	public static final class RoomExtensionSettings {
		private String fileName;
		private String className;
		private String propertiesFile;

		public RoomExtensionSettings() {

		}

		public RoomExtensionSettings(byte id, String className) {
			this.className = className;
		}

		public String getFileName() {
			return this.fileName;
		}

		public String getClassName() {
			return this.className;
		}

		public void setPropertiesFile(String propertiesFile) {
			this.propertiesFile = propertiesFile;
		}

		public String getPropertiesFile() {
			return this.propertiesFile;
		}
	}

	private String name = null;
	private byte gameId;
	private String groupId = "default";
	private String password = null;
	private int maxUsers = 20;
	private int maxSpectators = 0;
	private int maxVariablesAllowed = 5;
	private boolean isDynamic = false;
	private boolean isGame = false;
	private boolean isHidden = false;
	private RoomRemoveMode autoRemoveMode = RoomRemoveMode.DEFAULT;
	private Set<RoomSettings> roomSettings;
	private boolean useWordsFilter = true;
	private RoomExtensionSettings extension;
	private Class<? extends IPlayerIdGenerator> customPlayerIdGeneratorClass;
	private Map<Object, Object> roomProperties;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getGameId() {
		return gameId;
	}

	public void setGameId(byte gameId) {
		this.gameId = gameId;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getMaxUsers() {
		return this.maxUsers;
	}

	public void setMaxUsers(int maxUsers) {
		this.maxUsers = maxUsers;
	}

	public int getMaxSpectators() {
		return this.maxSpectators;
	}

	public void setMaxSpectators(int maxSpectators) {
		this.maxSpectators = maxSpectators;
	}

	public boolean isDynamic() {
		return this.isDynamic;
	}

	public void setDynamic(boolean isDynamic) {
		this.isDynamic = isDynamic;
	}

	public boolean isGame() {
		return this.isGame;
	}

	public void setGame(boolean isGame) {
		this.isGame = isGame;
	}

	public boolean isHidden() {
		return this.isHidden;
	}

	public void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}

	public RoomRemoveMode getAutoRemoveMode() {
		return this.autoRemoveMode;
	}

	public void setAutoRemoveMode(RoomRemoveMode autoRemoveMode) {
		this.autoRemoveMode = autoRemoveMode;
	}

	public Set<RoomSettings> getRoomSettings() {
		return this.roomSettings;
	}

	public void setRoomSettings(Set<RoomSettings> roomSettings) {
		this.roomSettings = roomSettings;
	}

	public boolean isUseWordsFilter() {
		return this.useWordsFilter;
	}

	public void setUseWordsFilter(boolean useWordsFilter) {
		this.useWordsFilter = useWordsFilter;
	}

	public RoomExtensionSettings getExtension() {
		return this.extension;
	}

	public void setExtension(RoomExtensionSettings extension) {
		this.extension = extension;
	}

	public int getMaxVariablesAllowed() {
		return this.maxVariablesAllowed;
	}

	public void setMaxVariablesAllowed(int maxVariablesAllowed) {
		this.maxVariablesAllowed = maxVariablesAllowed;
	}

	public Class<? extends IPlayerIdGenerator> getCustomPlayerIdGeneratorClass() {
		return this.customPlayerIdGeneratorClass;
	}

	public void setCustomPlayerIdGeneratorClass(Class<? extends IPlayerIdGenerator> customPlayerIdGeneratorClass) {
		this.customPlayerIdGeneratorClass = customPlayerIdGeneratorClass;
	}

	public Map<Object, Object> getRoomProperties() {
		return this.roomProperties;
	}

	public void setRoomProperties(Map<Object, Object> roomProperties) {
		this.roomProperties = roomProperties;
	}
}
package com.avengers.netty.core.om;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avengers.netty.core.exception.JoinRoomException;
import com.avengers.netty.core.exception.CoreErrorCode;
import com.avengers.netty.core.exception.CoreErrorData;
import com.avengers.netty.core.exception.RoomException;
import com.avengers.netty.core.extensions.ICRAExtension;
import com.avengers.netty.core.service.IUserManager;
import com.avengers.netty.core.service.UserManager;
import com.avengers.netty.core.util.IPlayerIdGenerator;
import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public class Room implements IRoom {
	private static final Logger LOG = LoggerFactory.getLogger(Room.class);
	private static AtomicInteger autoID = new AtomicInteger(0);
	private int id;
	private String groupId;
	private String name;
	private byte gameId;
	private String password;
	private boolean passwordProtected;
	private int maxUsers;
	private int maxSpectators;
	private User owner;
	private IUserManager userManager;
	private volatile ICRAExtension extension;
	private boolean dynamic;
	private boolean game;
	private boolean hidden;
	private volatile boolean active;
	private RoomRemoveMode autoRemoveMode;
	private IPlayerIdGenerator playerIdGenerator;
	private final long lifeTime;
	private final Map<Object, Object> properties;
	private Set<RoomSettings> flags;
	private boolean isGameFlagInited = false;

	private static int getNewID() {
		return autoID.getAndIncrement();
	}

	public Room(String name) {
		this(name, null);
	}

	public Room(String name, Class<?> customPlayerIdGeneratorClass) {
		id = getNewID();
		this.name = name;
		active = false;

		properties = new ConcurrentHashMap<Object, Object>();
		userManager = new UserManager();

		lifeTime = System.currentTimeMillis();
	}

	public int getId() {
		return id;
	}

	public String getGroupId() {
		if (this.groupId != null && this.groupId.length() > 0) {
			return groupId;
		}

		return "default";
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getName() {
		return name;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
		passwordProtected = password != null && password.length() > 0;
	}

	public boolean isPasswordProtected() {
		return passwordProtected;
	}

	public boolean isPublic() {
		return !passwordProtected;
	}

	public int getMaxUsers() {
		return maxUsers;
	}

	public void setMaxUsers(int maxUsers) {
		this.maxUsers = maxUsers;
		if (isGame() && playerIdGenerator != null) {
			playerIdGenerator.onRoomResize();
		}
	}

	public int getMaxSpectators() {
		return maxSpectators;
	}

	public void setMaxSpectators(int maxSpectators) {
		this.maxSpectators = maxSpectators;
	}

	public User getOwner() {
		return owner;
	}

	public boolean isOwner(User user) {
		return StringUtils.equals(user.getUserName(), owner.getUserName());
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public IUserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(IUserManager userManager) {
		this.userManager = userManager;
	}

	public boolean isDynamic() {
		return dynamic;
	}

	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}

	public boolean isGame() {
		return game;
	}

	public void setGame(boolean game, Class<? extends IPlayerIdGenerator> customPlayerIdGeneratorClass) {
		if (isGameFlagInited) {
			throw new IllegalStateException(toString() + ", isGame flag cannot be reset");
		}
		this.game = game;
		isGameFlagInited = true;
		if (this.game) {
			try {
				playerIdGenerator = ((IPlayerIdGenerator) customPlayerIdGeneratorClass.newInstance());

				playerIdGenerator.setParentRoom(this);
				playerIdGenerator.init();
			} catch (InstantiationException err) {
				LOG.warn(String.format(
						"Cannot instantiate Player ID Generator: %s, Reason: %s -- Room might not function correctly.",
						new Object[] { customPlayerIdGeneratorClass, err }));
			} catch (IllegalAccessException err) {
				LOG.warn(String.format(
						"Illegal Access to Player ID Generator Class: %s, Reason: %s -- Room might not function correctly.",
						new Object[] { customPlayerIdGeneratorClass, err }));
			}
		}
	}

	public void setGame(boolean game) {
		setGame(game, null);
	}

	public boolean isHidden() {
		return this.hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean flag) {
		active = flag;
	}

	public RoomRemoveMode getAutoRemoveMode() {
		return autoRemoveMode;
	}

	public void setAutoRemoveMode(RoomRemoveMode autoRemoveMode) {
		this.autoRemoveMode = autoRemoveMode;
	}

	public List<User> getPlayersList() {
		List<User> playerList = new ArrayList<User>();
		for (User user : userManager.getAllUsers()) {
			if (user.isPlayer(this)) {
				playerList.add(user);
			}
		}
		return playerList;
	}

	@Override
	public int countPlayer() {
		return userManager.getUserCount();
	}

	public Object getProperty(Object key) {
		return properties.get(key);
	}

	@Override
	public Map<Object, Object> getProperties() {
		return properties;
	}

	public RoomSize getSize() {
		int uCount = 0;
		int sCount = 0;
		if (game) {
			for (User user : userManager.getAllUsers()) {
				if (user.isSpectator(this)) {
					sCount++;
				} else {
					uCount++;
				}
			}
		} else {
			uCount = userManager.getUserCount();
		}
		return new RoomSize(uCount, sCount);
	}

	public int getPlayerSize() {
		return userManager.getAllUsers().size();
	}

	public void removeProperty(Object key) {
		properties.remove(key);
	}

	public List<User> getSpectatorsList() {
		List<User> specList = new ArrayList<User>();
		for (User user : userManager.getAllUsers()) {
			if (user.isSpectator(this)) {
				specList.add(user);
			}
		}
		return specList;
	}

	public User getUserById(int id) {
		return userManager.getUser(id);
	}

	public User getUserByName(String name) {
		return userManager.getUser(name);
	}

	public User getUserByPlayerId(int playerId) {
		User user = null;
		for (User u : userManager.getAllUsers()) {
			if (u.getPlayerId(this) == playerId) {
				user = u;
				break;
			}
		}
		return user;
	}

	public List<User> getListUser() {
		return userManager.getAllUsers();
	}

	public boolean containsProperty(Object key) {
		return properties.containsKey(key);
	}

	public int getCapacity() {
		return maxUsers + maxSpectators;
	}

	public void setCapacity(int maxUser, int maxSpectators) {
		this.maxUsers = maxUser;
		this.maxSpectators = maxSpectators;
	}

	public void setFlags(Set<RoomSettings> settings) {
		flags = settings;
	}

	public boolean isFlagSet(RoomSettings flag) {
		return flags.contains(flag);
	}

	public void setFlag(RoomSettings flag, boolean state) {
		if (state) {
			flags.add(flag);
		} else {
			flags.remove(flag);
		}
	}

	public void setProperty(Object key, Object value) {
		properties.put(key, value);
	}

	public void destroy() {
	}

	public boolean containsUser(String name) {
		return userManager.containsName(name);
	}

	public boolean containsUser(User user) {
		return userManager.containsUser(user);
	}

	public void addUser(User user) throws JoinRoomException {
		addUser(user, false);
	}

	public void addUser(User user, boolean asSpectator) throws JoinRoomException {
		if (userManager.containsId(user.getUserId())) {
			String message = String.format("User already joined: %s, Room: %s", new Object[] { user, this });
			CoreErrorData data = new CoreErrorData(CoreErrorCode.ROOM_ALREADY_JOINED);
			data.addParameter(name);
			throw new JoinRoomException(message, data);
		}

		boolean okToAdd = false;
		synchronized (this) {
			RoomSize roomSize = getSize();
			if (isGame() && asSpectator) {
				okToAdd = roomSize.getSpectatorCount() < maxSpectators;
			} else {
				okToAdd = roomSize.getUserCount() < maxUsers;
			}
			if (!okToAdd) {
				String message = String.format("Room is full: %s - Can't add User: %s ", new Object[] { name, user });
				CoreErrorData data = new CoreErrorData(CoreErrorCode.JOIN_ROOM_FULL);
				data.addParameter(name);

				throw new JoinRoomException(message, data);
			}
			userManager.addUser(user);
		}

		user.setLastJoinedRoom(this);
		if (isGame()) {
			if (asSpectator) {
				user.setPlayerId(-1, this);
			} else {
				user.setPlayerId(playerIdGenerator.getPlayerSlot(), this);
			}
		} else {
			user.setPlayerId(0, this);
		}
	}

	public void removeUser(User user) {
		if (isGame()) {
			playerIdGenerator.freePlayerSlot(user.getPlayerId(this));
		}
		userManager.removeUser(user);
		user.removeLastJoinedRoom();
	}

	public void switchPlayerToSpectator(User user) throws RoomException {
		if (!isGame()) {
			CoreErrorData errData = new CoreErrorData(CoreErrorCode.SWITCH_NOT_A_GAME_ROOM);
			errData.addParameter(name);

			throw new RoomException("Not supported in a non-game room", errData);
		}

		if (!userManager.containsUser(user)) {
			CoreErrorData errData = new CoreErrorData(CoreErrorCode.SWITCH_NOT_JOINED_IN_ROOM);
			errData.addParameter(name);

			throw new RoomException(String.format("%s is not joined in %s", new Object[] { user, this }));
		}

		if (user.isSpectator(this)) {
			LOG.warn(String.format("PlayerToSpectator refused, %s is already a spectator in %s",
					new Object[] { user, this }));
			return;
		}
		// this.switchUserLock.lock();
		try {
			if (getSize().getSpectatorCount() < maxSpectators) {
				int currentPlayerId = user.getPlayerId(this);
				user.setPlayerId(-1, this);
				playerIdGenerator.freePlayerSlot(currentPlayerId);
			} else {
				CoreErrorData errData = new CoreErrorData(CoreErrorCode.SWITCH_NO_SPECTATOR_SLOTS_AVAILABLE);
				errData.addParameter(this.name);
				throw new RoomException("All Spectators slots are already occupied!", errData);
			}
		} finally {
			// this.switchUserLock.unlock();
		}
		// this.switchUserLock.unlock();
	}

	public void switchSpectatorToPlayer(User user) throws RoomException {
		if (!isGame()) {
			CoreErrorData errData = new CoreErrorData(CoreErrorCode.SWITCH_NOT_A_GAME_ROOM);
			errData.addParameter(name);

			throw new RoomException("Not supported in a non-game room", errData);
		}
		if (!this.userManager.containsUser(user)) {
			CoreErrorData errData = new CoreErrorData(CoreErrorCode.SWITCH_NOT_JOINED_IN_ROOM);
			errData.addParameter(name);

			throw new RoomException(String.format("%s is not joined in %s", new Object[] { user, this }));
		}
		if (user.isPlayer(this)) {
			LOG.warn(String.format("SpectatorToPlayer refused, %s is already a player in %s",
					new Object[] { user, this }));
			return;
		}
		// this.switchUserLock.lock();
		try {
			if (getSize().getUserCount() < maxUsers) {
				user.setPlayerId(playerIdGenerator.getPlayerSlot(), this);
			} else {
				CoreErrorData errData = new CoreErrorData(CoreErrorCode.SWITCH_NO_PLAYER_SLOTS_AVAILABLE);
				errData.addParameter(name);
				throw new RoomException("All Player slots are already occupied!", errData);
			}
		} finally {
			// this.switchUserLock.unlock();
		}
		// this.switchUserLock.unlock();
	}

	public long getLifeTime() {
		return System.currentTimeMillis() - lifeTime;
	}

	public boolean isEmpty() {
		return userManager.getUserCount() == 0;
	}

	public boolean isFull() {
		if (isGame()) {
			return getSize().getUserCount() == maxUsers;
		}
		return userManager.getUserCount() == maxUsers;
	}

	public String toString() {
		return String.format("[ Room: %s, Id: %s, Group: %s, isGame: %s ]",
				new Object[] { this.name, Integer.valueOf(this.id), this.groupId, Boolean.valueOf(this.game) });
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof IRoom)) {
			return false;
		}

		IRoom room = (IRoom) obj;
		return room.getId() == id;
	}

	public ICRAExtension getExtension() {
		return extension;
	}

	public void setExtension(ICRAExtension extension) {
		this.extension = extension;
	}

	public String getDump() {
		StringBuilder sb = new StringBuilder("/////////////// Room Dump ////////////////").append("\n");
		sb.append("\tName: ").append(this.name).append("\n").append("\tId: ").append(this.id).append("\n")
				.append("\tGroupId: ").append(this.groupId).append("\n").append("\tPassword: ").append(this.password)
				.append("\n").append("\tOwner: ").append(this.owner == null ? "[[ SERVER ]]" : this.owner.toString())
				.append("\n").append("\tisDynamic: ").append(this.dynamic).append("\n").append("\tisGame: ")
				.append(this.game).append("\n").append("\tisHidden: ").append(this.hidden).append("\n")
				.append("\tsize: ").append(getSize()).append("\n").append("\tMaxUser: ").append(this.maxUsers)
				.append("\n").append("\tMaxSpect: ").append(this.maxSpectators).append("\n").append("\tMaxVars: ")
				.append("\tRemoveMode: ").append(this.autoRemoveMode).append("\n").append("\tPlayerIdGen: ")
				.append(this.playerIdGenerator).append("\n").append("\tSettings: ").append("\n");
		for (RoomSettings setting : RoomSettings.values()) {
			sb.append("\t\t").append(setting).append(": ").append(this.flags.contains(setting)).append("\n");
		}

		if (this.properties.size() > 0) {
			sb.append("\tProperties: ").append("\n");
			for (Object key : this.properties.keySet()) {
				sb.append("\t\t").append(key).append(": ").append(this.properties.get(key)).append("\n");
			}
		}
		if (extension != null) {
			sb.append("\tExtension: ").append("\n");

			sb.append("\t\t").append("Name: ").append(extension.getName()).append("\n");
			sb.append("\t\t").append("Class: ").append(extension.getExtensionFileName()).append("\n");
			sb.append("\t\t").append("Props: ").append(extension.getPropertiesFileName()).append("\n");
		}
		sb.append("/////////////// End Dump /////////////////").append("\n");

		return sb.toString();
	}

	public String getPlayerIdGeneratorClassName() {
		return playerIdGenerator.getClass().getName();
	}

	@Override
	public String getHost() {
		return ServerConfig.getServerHost();
	}

	public void setProperties(Map<Object, Object> props) {
		properties.clear();
		properties.putAll(props);
	}

}

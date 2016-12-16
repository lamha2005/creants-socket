package com.avengers.netty.core.service;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import com.avengers.netty.SocketServer;
import com.avengers.netty.core.exception.CoreErrorCode;
import com.avengers.netty.core.exception.CoreErrorData;
import com.avengers.netty.core.exception.CoreExtensionException;
import com.avengers.netty.core.exception.CoreRuntimeException;
import com.avengers.netty.core.exception.CreateRoomException;
import com.avengers.netty.core.exception.RoomException;
import com.avengers.netty.core.extensions.ExtensionLevel;
import com.avengers.netty.core.extensions.ExtensionSettings;
import com.avengers.netty.core.extensions.ExtensionType;
import com.avengers.netty.core.extensions.ICRAExtension;
import com.avengers.netty.core.om.CreateRoomSettings;
import com.avengers.netty.core.om.CreateRoomSettings.RoomExtensionSettings;
import com.avengers.netty.core.om.IRoom;
import com.avengers.netty.core.om.Room;
import com.avengers.netty.core.om.RoomPage;
import com.avengers.netty.core.om.RoomSettings;
import com.avengers.netty.core.om.cluster.ClusterRoom;
import com.avengers.netty.core.util.CoreTracer;
import com.avengers.netty.core.util.DefaultPlayerIdGenerator;
import com.avengers.netty.core.util.GsonUtils;
import com.avengers.netty.core.util.IPlayerIdGenerator;
import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public final class RoomManager implements IRoomManager {
	private final Map<Integer, IRoom> roomsById;
	private final Map<String, IRoom> roomsByName;
	private final Map<String, List<IRoom>> roomsByGroup;
	private final List<String> groups;
	private final AtomicInteger gameRoomCounter;
	private SocketServer socketServer;
	private IClusterService clusterService;
	private List<String> rooms;
	private Random random;

	private Class<? extends IPlayerIdGenerator> playerIdGeneratorClass = DefaultPlayerIdGenerator.class;

	public RoomManager(SocketServer socketServer) {
		this.socketServer = socketServer; // cheat để có thể gọi được

		roomsById = new ConcurrentHashMap<Integer, IRoom>();
		roomsByName = new ConcurrentHashMap<String, IRoom>();
		roomsByGroup = new ConcurrentHashMap<String, List<IRoom>>();
		groups = new ArrayList<String>();
		gameRoomCounter = new AtomicInteger();
		rooms = GsonUtils.fromGsonFile("/configs/rooms.json", GsonUtils.LIST_STRING_TYPE);
		random = new Random();
	}

	public IRoom createRoom(CreateRoomSettings params) throws CreateRoomException {
		return createRoom(params, null);
	}

	public String generateRoomName() {
		return rooms.get(random.nextInt(rooms.size()));
	}

	public IRoom createRoom(CreateRoomSettings roomSetting, User owner) throws CreateRoomException {
		String roomName = roomSetting.getName();
		// validateRoomName(roomName);
		if (StringUtils.isBlank(roomName)) {
			roomName = generateRoomName();
		}

		Room newRoom = new Room(roomName);
		newRoom.setGameId(roomSetting.getGameId());
		newRoom.setGroupId(roomSetting.getGroupId());
		newRoom.setPassword(roomSetting.getPassword());
		newRoom.setDynamic(roomSetting.isDynamic());
		newRoom.setHidden(roomSetting.isHidden());
		newRoom.setMaxUsers(roomSetting.getMaxUsers());
		newRoom.setMaxSpectators(roomSetting.isGame() ? roomSetting.getMaxSpectators() : 0);
		newRoom.setGame(roomSetting.isGame(), roomSetting.getCustomPlayerIdGeneratorClass() == null
				? playerIdGeneratorClass : roomSetting.getCustomPlayerIdGeneratorClass());

		Set<RoomSettings> roomSettings = roomSetting.getRoomSettings();
		if (roomSettings == null) {
			roomSettings = EnumSet.of(RoomSettings.USER_ENTER_EVENT, RoomSettings.USER_EXIT_EVENT,
					RoomSettings.USER_COUNT_CHANGE_EVENT, RoomSettings.PUBLIC_MESSAGES);
		}

		newRoom.setFlags(roomSettings);
		newRoom.setOwner(owner);
		newRoom.setAutoRemoveMode(roomSetting.getAutoRemoveMode());
		if (roomSetting.getRoomProperties() != null) {
			newRoom.setProperties(roomSetting.getRoomProperties());
		}
		addRoom(newRoom);

		newRoom.setActive(true);
		RoomExtensionSettings extension = roomSetting.getExtension();
		if (extension != null && extension.getFileName() != null && extension.getFileName().length() > 0) {
			try {
				createRoomExtension(newRoom, extension);
			} catch (CoreExtensionException e) {
				CoreTracer.warn(RoomManager.class, "Failure while creating room extension!", e);
			}
		}
		if (newRoom.isGame()) {
			gameRoomCounter.incrementAndGet();
		}

		CoreTracer.info(RoomManager.class, String.format("Room created:  %s", new Object[] { newRoom.toString() }));
		return newRoom;
	}

	private void createRoomExtension(IRoom room, CreateRoomSettings.RoomExtensionSettings roomExtSetting)
			throws CoreExtensionException {
		if (roomExtSetting == null) {
			return;
		}

		String className = roomExtSetting.getClassName();
		ExtensionSettings extSettings = new ExtensionSettings();
		extSettings.name = roomExtSetting.getFileName();
		extSettings.file = className;
		extSettings.propertiesFile = roomExtSetting.getPropertiesFile();
		extSettings.reloadMode = "AUTO";
		extSettings.type = (ExtensionType.JAVA).toString();
		socketServer.getExtensionManager().createExtension(extSettings, ExtensionLevel.ROOM, room);
	}

	public Class<? extends IPlayerIdGenerator> getDefaultRoomPlayerIdGenerator() {
		return playerIdGeneratorClass;
	}

	public void setDefaultRoomPlayerIdGeneratorClass(Class<? extends IPlayerIdGenerator> customIdGeneratorClass) {
		playerIdGeneratorClass = customIdGeneratorClass;
	}

	public void addGroup(String groupId) {
		synchronized (groups) {
			groups.add(groupId);
		}
	}

	public void addRoom(IRoom room) {
		roomsById.put(Integer.valueOf(room.getId()), room);
		roomsByName.put(room.getName(), room);
		synchronized (groups) {
			if (!groups.contains(room.getGroupId())) {
				groups.add(room.getGroupId());
			}
		}

		if (room.isGame()) {
			// TODO cluster
			// clusterService.saveRoom(new ClusterRoom(room));
		}
		addRoomToGroup(room);
	}

	public boolean containsGroup(String groupId) {
		boolean flag = false;
		synchronized (groups) {
			flag = groups.contains(groupId);
		}
		return flag;
	}

	public List<String> getGroups() {
		List<String> groupsCopy = null;
		synchronized (groups) {
			groupsCopy = new ArrayList<String>(groups);
		}
		return groupsCopy;
	}

	public IRoom getRoomById(int id) {
		return roomsById.get(Integer.valueOf(id));
	}

	public IRoom getRoomByName(String name) {
		return roomsByName.get(name);
	}

	public List<IRoom> getRoomList() {
		return new ArrayList<IRoom>(roomsById.values());
	}

	@Override
	public RoomPage<IRoom> getRoomPage(int nextIndex, boolean getClusterRoom) {
		List<IRoom> roomList = getRoomList();
		// TODO filter room list
		if (roomList != null) {
			RoomPage<IRoom> roomPage = new RoomPage<IRoom>();
			roomPage.setRooms(roomList);
			roomPage.setNextPage(roomList.size());
			roomPage.setGetOnCluster(false);
			return roomPage;
		}

		return null;
	}

	public List<IRoom> getRoomListFromGroup(String groupId) {
		List<IRoom> roomList = roomsByGroup.get(groupId);
		List<IRoom> copyOfRoomList = null;
		if (roomList != null) {
			synchronized (roomList) {
				copyOfRoomList = new ArrayList<IRoom>(roomList);
			}
		}
		return copyOfRoomList;
	}

	public int getGameRoomCount() {
		return gameRoomCounter.get();
	}

	public int getTotalRoomCount() {
		return roomsById.size();
	}

	public void removeGroup(String groupId) {
		synchronized (groups) {
			groups.remove(groupId);
		}
	}

	public void removeRoom(int roomId) {
		IRoom room = roomsById.get(Integer.valueOf(roomId));
		if (room == null) {
			CoreTracer.warn(RoomManager.class, "Can't remove requested room. ID = " + roomId + ". Room was not found.");
		} else {
			removeRoom(room);
			clusterService.removeRoom(ClusterRoom.getClusterRoomId(room));
		}
	}

	public void removeRoom(String name) {
		IRoom room = roomsByName.get(name);
		if (room == null) {
			CoreTracer.warn(RoomManager.class, "Can't remove requested room. Name = " + name + ". Room was not found.");
		} else {
			removeRoom(room);
		}
	}

	public void removeRoom(IRoom room) {
		if (!room.isGame()) {
			CoreTracer.error(RoomManager.class, "[WARN] removeRoom faile! Can not remove lobby!");
			return;
		}

		boolean wasRemoved;
		try {
			ICRAExtension roomExtension = room.getExtension();
			if (roomExtension != null) {
				socketServer.getExtensionManager().destroyExtension(roomExtension);
			}
		} finally {
			room.destroy();
			room.setActive(false);

			wasRemoved = roomsById.remove(room.getId()) != null;
			roomsByName.remove(room.getName());
			removeRoomFromGroup(room);
			if (wasRemoved && room.isGame()) {
				gameRoomCounter.decrementAndGet();
			}

			CoreTracer.info(RoomManager.class, String.format("Room removed: %s, Duration: %s",
					new Object[] { room.toString(), room.getLifeTime() }));
		}
	}

	public boolean containsRoom(int id, String groupId) {
		IRoom room = roomsById.get(Integer.valueOf(id));
		return isRoomContainedInGroup(room, groupId);
	}

	public boolean containsRoom(int id) {
		return roomsById.containsKey(Integer.valueOf(id));
	}

	public boolean containsRoom(IRoom room, String groupId) {
		return isRoomContainedInGroup(room, groupId);
	}

	public boolean containsRoom(IRoom room) {
		return roomsById.containsValue(room);
	}

	public boolean containsRoom(String name, String groupId) {
		return isRoomContainedInGroup(roomsByName.get(name), groupId);
	}

	public boolean containsRoom(String name) {
		return roomsByName.containsKey(name);
	}

	public void removeUser(User user) {
		removeUser(user, user.getLastJoinedRoom());
	}

	public void removeUser(User user, IRoom room) {
		try {
			if (room.containsUser(user)) {
				room.removeUser(user);
				CoreTracer.debug(RoomManager.class,
						"User: " + user.getUserName() + " removed from Room: " + room.getName());
			} else {
				throw new CoreRuntimeException("Can't remove user: " + user + ", from: " + room);
			}
		} finally {
			handleAutoRemove(room);
		}
	}

	public void changeRoomName(IRoom room, String newName) throws RoomException {
		if (room == null) {
			throw new IllegalArgumentException("Can't change name. Room is Null!");
		}
		validateRoomName(newName);

		String oldName = room.getName();
		room.setName(newName);

		roomsByName.put(newName, room);
		roomsByName.remove(oldName);
	}

	public void changeRoomPasswordState(IRoom room, String password) {
		if (room == null) {
			throw new IllegalArgumentException("Can't change password. Room is Null!");
		}
		room.setPassword(password);
	}

	public void changeRoomCapacity(IRoom room, int newMaxUsers, int newMaxSpect) {
		if (room == null) {
			throw new IllegalArgumentException("Can't change password. Room is Null!");
		}

		if (newMaxUsers > 0) {
			room.setMaxUsers(newMaxUsers);
		}

		if (newMaxSpect >= 0) {
			room.setMaxSpectators(newMaxSpect);
		}
	}

	private void handleAutoRemove(IRoom room) {
		if (room.isEmpty() && room.isDynamic()) {
			switch (room.getAutoRemoveMode()) {
			case DEFAULT:
				if (room.isGame()) {
					removeWhenEmpty(room);
				} else {
					removeWhenEmptyAndCreatorIsGone(room);
				}
				break;
			case NEVER_REMOVE:
				removeWhenEmpty(room);
				break;
			case WHEN_EMPTY:
				removeWhenEmptyAndCreatorIsGone(room);
			}
		}
	}

	private void removeWhenEmpty(IRoom room) {
		if (room.isEmpty()) {
			socketServer.getAPIManager().getCoreApi().removeRoom(room);
		}
	}

	private void removeWhenEmptyAndCreatorIsGone(IRoom room) {
		User owner = room.getOwner();
		if (owner != null && !owner.isConnected()) {
			socketServer.getAPIManager().getCoreApi().removeRoom(room);
		}
	}

	private boolean isRoomContainedInGroup(IRoom room, String groupId) {
		return room != null && room.getGroupId().equals(groupId) && containsGroup(groupId);
	}

	private void addRoomToGroup(IRoom room) {
		String groupId = room.getGroupId();

		List<IRoom> roomList = roomsByGroup.get(groupId);
		if (roomList == null) {
			roomList = new ArrayList<IRoom>();
			roomsByGroup.put(groupId, roomList);
		}

		synchronized (roomList) {
			roomList.add(room);
		}
	}

	private void removeRoomFromGroup(IRoom room) {
		List<IRoom> roomList = roomsByGroup.get(room.getGroupId());
		if (roomList != null) {
			synchronized (roomList) {
				roomList.remove(room);
			}
		}

		CoreTracer.info(RoomManager.class, "remove room: " + room.getName() + " from it's group: " + room.getGroupId());
	}

	private void validateRoomName(String roomName) throws RoomException {
		if (containsRoom(roomName)) {
			CoreErrorData errorData = new CoreErrorData(CoreErrorCode.ROOM_DUPLICATE_NAME);
			errorData.addParameter(roomName);

			String message = String.format("A room with the same name already exists: %s", new Object[] { roomName });
			throw new RoomException(message, errorData);
		}
		int nameLen = roomName.length();
		int minLen = getMinRoomNameChars();
		int maxLen = getMaxRoomNameChars();
		if (nameLen < minLen || nameLen > maxLen) {
			CoreErrorData errorData = new CoreErrorData(CoreErrorCode.ROOM_NAME_BAD_SIZE);
			errorData.addParameter(String.valueOf(minLen));
			errorData.addParameter(String.valueOf(maxLen));
			errorData.addParameter(String.valueOf(nameLen));

			String message = String.format("Room name length is out of valid range. Min: %s, Max: %s, Found: %s (%s)",
					new Object[] { Integer.valueOf(minLen), Integer.valueOf(maxLen), Integer.valueOf(nameLen),
							roomName });
			throw new RoomException(message, errorData);
		}
	}

	@Override
	public void checkAndRemove(IRoom room) {
		handleAutoRemove(room);
	}

	private int getMaxRoomNameChars() {
		return 20;
	}

	private int getMinRoomNameChars() {
		return 1;
	}

	public void setClusterService(IClusterService clusterService) {
		this.clusterService = clusterService;
	}

}

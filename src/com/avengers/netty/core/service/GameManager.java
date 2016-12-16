package com.avengers.netty.core.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.avengers.netty.SocketServer;
import com.avengers.netty.core.exception.CoreException;
import com.avengers.netty.core.exception.CoreRuntimeException;
import com.avengers.netty.core.om.CreateRoomSettings;
import com.avengers.netty.core.om.CreateRoomSettings.RoomExtensionSettings;
import com.avengers.netty.core.om.GameSettings;
import com.avengers.netty.core.om.IRoom;
import com.avengers.netty.core.util.CoreTracer;
import com.avengers.netty.gamelib.key.NetworkConstant;
import com.avengers.netty.gamelib.om.RoomInfo;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author LamHa
 *
 */
public class GameManager {

	private static GameManager instance = null;
	private SocketServer socketServer;
	private final Map<Byte, GameSettings> gameSettings;
	private final Map<Byte, IRoom> lobbyMap;

	public static GameManager getInstance() {
		if (instance == null) {
			instance = new GameManager();
		}
		return instance;
	}

	private GameManager() {
		socketServer = SocketServer.getInstance();
		gameSettings = new ConcurrentHashMap<Byte, GameSettings>();
		lobbyMap = new ConcurrentHashMap<Byte, IRoom>();

		// TODO tạo scheduler để load game động, dựa theo deploy revision
		initializeGames();
	}

	private synchronized void initializeGames() {
		try {
			CoreTracer.info(GameManager.class, "- Initialize rooms from config");
			IRoom room;
			// RoomInfo roomInfo;
			// CreateRoomSettings roomSetting;
			List<GameSettings> gameSettings = loadZonesConfiguration();
			for (GameSettings settings : gameSettings) {
				// create room lobby
				settings.lobby.setExtension(settings.extension);
				room = socketServer.getAPIManager().getCoreApi().createRoom(settings.lobby, null);
				addLobbyGame(settings.gameId, room);
				// create room default
				// int quantityRoom = settings.quantityRoom;
				// for (int i = 0; i < quantityRoom; i++) {
				// roomSetting = new CreateRoomSettings();
				// roomSetting.setName("");
				// roomSetting.setGroupId(settings.groupId);
				// roomSetting.setMaxUsers(settings.maxUserInGame);
				// roomSetting.setMaxSpectators(settings.maxSpectatorInGame);
				// roomSetting.setDynamic(true);
				// roomSetting.setGame(true);
				// roomSetting.setHidden(false);
				// roomSetting.setAutoRemoveMode(RoomRemoveMode.NEVER_REMOVE);
				// roomSetting.setExtension(settings.extension);
				// roomSetting.setGameId(settings.gameId);
				// room =
				// socketServer.getAPIManager().getCoreApi().createRoom(roomSetting,
				// null);
				// roomInfo = new RoomInfo(settings.gameId);
				// // FIXME lấy theo config tiền cược cho phòng
				// roomInfo.setBetCoin(10);
				// roomInfo.setTimeOut(30);
				// room.setProperty(NetworkConstant.ROOM_INFO, roomInfo);
				// }

				addGameSettings(settings);
			}
		} catch (CoreException e) {
			CoreTracer.error(GameManager.class, "Init games fail!", e);
		}
	}

	/**
	 * Đọc config mỗi game tương ứng
	 */
	public synchronized List<GameSettings> loadZonesConfiguration() throws CoreException {
		List<GameSettings> gameSettings = new ArrayList<GameSettings>();
		List<File> zoneDefinitionFiles = getZoneDefinitionFiles("gameconfig/");
		for (File file : zoneDefinitionFiles) {
			try {
				FileInputStream inStream = new FileInputStream(file);
				CoreTracer.info(GameManager.class, "Loading", file.toString());
				gameSettings.add((GameSettings) getZonesXStreamDefinitions().fromXML(inStream));
			} catch (FileNotFoundException e) {
				throw new CoreRuntimeException("Could not locate Zone definition file: " + file.getAbsolutePath());
			}
		}

		return gameSettings;
	}

	private List<File> getZoneDefinitionFiles(String path) throws CoreException {
		List<File> files = new ArrayList<File>();
		File currDir = new File(path);
		if (currDir.isDirectory()) {
			for (File f : currDir.listFiles()) {
				if (f.getName().endsWith(".xml")) {
					files.add(f);
				}
			}
		} else {
			throw new CoreException("Invalid zones definition folder: " + currDir);
		}
		return files;
	}

	private XStream getZonesXStreamDefinitions() {
		XStream xstream = new XStream(new DomDriver());
		xstream.alias("game", GameSettings.class);
		xstream.alias("extension", RoomExtensionSettings.class);
		xstream.alias("lobby", CreateRoomSettings.class);
		return xstream;
	}

	public GameSettings getGameSettings(byte gameId) {
		return gameSettings.get(gameId);
	}

	public void addGameSettings(GameSettings settings) {
		gameSettings.put(settings.gameId, settings);
	}

	public List<GameSettings> getAllGameSettings() {
		return new ArrayList<>(gameSettings.values());
	}

	/**
	 * Đăng ký lobby với game tương ứng
	 * 
	 * @param gameId
	 *            id của game: 2 mau binh
	 * @param room
	 *            phòng Lobby của game
	 */
	public void addLobbyGame(byte gameId, IRoom room) {
		if (lobbyMap.containsKey(gameId)) {
			// config có nhiều hơn 1 lobby
			CoreTracer.error(GameManager.class, "Lobby already existed, gameId: " + gameId);
		} else {
			lobbyMap.put(Byte.valueOf(gameId), room);
		}
	}

	/**
	 * Lấy phòng Lobby theo game
	 * 
	 * @param gameId
	 *            id của game
	 */
	public IRoom getLobbyGame(byte gameId) {
		return lobbyMap.get(gameId);
	}

	/**
	 * Lấy tất cả các room trên hệ thống theo game
	 * 
	 * @param gameId
	 *            id của game
	 */
	public List<IRoom> getAllRoomGame(byte gameId) {
		GameSettings settings = gameSettings.get(Byte.valueOf(gameId));
		if (settings != null) {
			return socketServer.getRoomService().getRoomListFromGroup(settings.groupId);
		}

		return new ArrayList<IRoom>();
	}

	/**
	 * Lấy danh sách room theo game và tiền của người chơi
	 * 
	 * @param gameId
	 *            id của game
	 * @param userMoney
	 *            số tiền của người chơi hiện có
	 * @return
	 */
	public List<IRoom> getAvailableRooms(byte gameId, long userMoney) {
		List<IRoom> rooms = getAllRoomGame(gameId);
		if (!rooms.isEmpty()) {
			Iterator<IRoom> roomsIterator = rooms.iterator();
			IRoom room;
			while (roomsIterator.hasNext()) {
				room = roomsIterator.next();
				if (!room.isGame()) {
					roomsIterator.remove();
				} else {
					RoomInfo roomInfo = (RoomInfo) room.getProperty(NetworkConstant.ROOM_INFO);
					if (roomInfo.getBetCoin() != userMoney) {
						roomsIterator.remove();
					}
				}
			}
		}
		return rooms;
	}

	/**
	 * Tìm room theo tiền thích hợp
	 * 
	 * @param gameId
	 * @param money
	 * @return
	 */
	public IRoom findRoom(byte gameId, long money) {
		List<IRoom> rooms = getAllRoomGame(gameId);
		for (IRoom room : rooms) {
			if (room.isGame() && !room.isFull()) {
				RoomInfo roomInfo = (RoomInfo) room.getProperty(NetworkConstant.ROOM_INFO);
				if (roomInfo.getBetCoin() == money) {
					return room;
				}
			}

		}

		return null;
	}

	/**
	 * Kiểm tra có tồn tại game này trên hệ thống không
	 * 
	 * @param gameId
	 * @return
	 */
	public boolean containGame(byte gameId) {
		return lobbyMap.containsKey(gameId);
	}

	/**
	 * Kiểm tra game này có đang hoạt động trên hệ thống hay không
	 * 
	 * @param gameId
	 *            id của game
	 * @return <code>TRUE<code> nếu đang chạy. <br>
	 *         <code>FALSE<code> ngược lại
	 */
	public boolean isGameExtRunning(Byte gameId) {
		// TODO check game extension còn chạy hay không
		return true;
	}

}

package com.avengers.netty.core.extensions;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import com.avengers.netty.SocketServer;
import com.avengers.netty.core.api.ICoreAPI;
import com.avengers.netty.core.exception.CoreRuntimeException;
import com.avengers.netty.core.om.IRoom;
import com.avengers.netty.core.util.CoreTracer;
import com.avengers.netty.socket.gate.IMessage;
import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public abstract class BaseExtension implements ICRAExtension {
	private String name;
	private String fileName;
	private String configFileName;
	private ExtensionLevel level;
	private ExtensionType type;
	private IRoom currentRoom = null;
	private volatile boolean active;
	private Properties configProperties;
	private ExtensionReloadMode reloadMode;
	private String currentPath;
	protected volatile int lagSimulationMillis = 0;
	protected volatile int lagOscillation = 0;
	private Random random;
	private SocketServer socketServer = null;
	protected final ICoreAPI coreApi;

	public BaseExtension() {
		active = true;
		socketServer = SocketServer.getInstance();
		coreApi = socketServer.getAPIManager().getCoreApi();
	}

	public String getCurrentFolder() {
		return currentPath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (this.name != null) {
			throw new CoreRuntimeException("Cannot redefine name of extension: " + toString());
		}

		this.name = name;
		currentPath = ("extensions/" + name + "/");
	}

	public String getExtensionFileName() {
		return fileName;
	}

	public Properties getConfigProperties() {
		return configProperties;
	}

	public String getPropertiesFileName() {
		return configFileName;
	}

	public void setPropertiesFileName(String fileName) throws IOException {
		if (configFileName != null) {
			throw new CoreRuntimeException("Cannot redefine properties file name of an extension: " + toString());
		}

		boolean isDefault = false;
		if (fileName == null || fileName.length() == 0 || fileName.equals("config.properties")) {
			isDefault = true;
			configFileName = "config.properties";
		} else {
			configFileName = fileName;
		}

		String fileToLoad = "extensions/" + name + "/" + configFileName;
		if (isDefault) {
			loadDefaultConfigFile(fileToLoad);
		} else {
			loadCustomConfigFile(fileToLoad);
		}
	}

	public ICoreAPI getApi() {
		return coreApi;
	}

	public Object handleInternalMessage(String cmdName, Object params) {
		return null;
	}

	private void loadDefaultConfigFile(String fileName) {
		try {
			configProperties = new Properties();
			configProperties.load(new FileInputStream(fileName));
		} catch (IOException localIOException) {
		}
	}

	private void loadCustomConfigFile(String fileName) throws IOException {
		configProperties = new Properties();
		configProperties.load(new FileInputStream(fileName));
	}

	public void setExtensionFileName(String fileName) {
		if (this.fileName != null) {
			throw new CoreRuntimeException("Cannot redefine file name of an extension: " + toString());
		}

		this.fileName = fileName;
	}

	public IRoom getCurrentRoom() {
		return currentRoom;
	}

	public void setCurrentRoom(IRoom room) {
		if (currentRoom != null) {
			throw new CoreRuntimeException("Cannot redefine parent room of extension: " + toString());
		}

		currentRoom = room;
	}

	public void addEventListener() {
		// this.sfs.getExtensionManager().addRoomEventListener(eventType,
		// listener, this.parentRoom);

	}

	public void removeEventListener() {
		// this.sfs.getExtensionManager().removeRoomEventListener(eventType,
		// listener, this.parentRoom);
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean flag) {
		active = flag;
	}

	public ExtensionLevel getLevel() {
		return level;
	}

	public void setLevel(ExtensionLevel level) {
		if (this.level != null) {
			throw new CoreRuntimeException("Cannot change level for extension: " + toString());
		}

		this.level = level;
	}

	public ExtensionType getType() {
		return type;
	}

	public void setType(ExtensionType type) {
		if (this.type != null) {
			throw new CoreRuntimeException("Cannot change type for extension: " + toString());
		}

		this.type = type;
	}

	public ExtensionReloadMode getReloadMode() {
		return reloadMode;
	}

	public void setReloadMode(ExtensionReloadMode mode) {
		if (reloadMode != null) {
			throw new CoreRuntimeException("Cannot change reloadMode for extension: " + toString());
		}

		reloadMode = mode;
	}

	@Override
	public void send(IMessage message, List<User> recipients) {
		checkLagSimulation();
		coreApi.sendExtensionResponse(message, recipients);
	}

	@Override
	public void send(IMessage message, User recipient) {
		checkLagSimulation();
		coreApi.sendExtensionResponse(message, recipient);
	}

	public String toString() {
		return String.format("{ Ext: %s, Type: %s, Lev: %s, %s }",
				new Object[] { this.name, this.type, this.level, this.currentRoom == null ? "{}" : this.currentRoom });
	}

	protected void removeEventsForListener() {
		// this.sfs.getExtensionManager().removeListenerFromRoom(listener,
		// this.parentRoom);
	}

	public void trace(Object... args) {
		// trace(ExtensionLogLevel.INFO, args);
	}

	private void checkLagSimulation() {
		if (lagSimulationMillis <= 0)
			return;

		try {
			long lagValue = lagSimulationMillis;
			if (lagOscillation > 0) {
				if (random == null) {
					random = new Random();
				}

				int sign = random.nextInt(100) > 49 ? 1 : -1;
				lagValue += sign * random.nextInt(lagOscillation);
			}

			CoreTracer.debug(this.getClass(), "Lag simulation, sleeping for: " + lagValue + "ms.");
			Thread.sleep(lagValue);
		} catch (InterruptedException e) {
			CoreTracer.warn(this.getClass(), "Interruption during lag simulation: " + e);
		}
	}
}

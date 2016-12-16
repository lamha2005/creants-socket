package com.avengers.netty.core.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avengers.netty.core.exception.CoreExtensionException;
import com.avengers.netty.core.extensions.ExtensionLevel;
import com.avengers.netty.core.extensions.ExtensionReloadMode;
import com.avengers.netty.core.extensions.ExtensionSettings;
import com.avengers.netty.core.extensions.ExtensionType;
import com.avengers.netty.core.extensions.ICRAExtension;
import com.avengers.netty.core.om.IRoom;

/**
 * @author LamHa
 *
 */
public final class ExtensionManager {

	private final ConcurrentMap<IRoom, ICRAExtension> extensionsByRoom;
	private static final Logger LOG = LoggerFactory.getLogger(ExtensionManager.class);

	public ExtensionManager() {
		this.extensionsByRoom = new ConcurrentHashMap<IRoom, ICRAExtension>();
	}

	public void destroyExtension(ICRAExtension roomExtension) {
		// TODO destroy
	}

	public void createExtension(ExtensionSettings settings, ExtensionLevel level, IRoom room)
			throws CoreExtensionException {
		if (settings.file == null || settings.file.length() == 0) {
			throw new CoreExtensionException("Extension file parameter is missing!");
		}
		if (settings.name == null || settings.name.length() == 0) {
			throw new CoreExtensionException("Extension name parameter is missing!");
		}
		if (settings.type == null) {
			throw new CoreExtensionException("Extension type was not specified: " + settings.name);
		}
		if (settings.reloadMode == null) {
			settings.reloadMode = "";
		}
		ExtensionReloadMode reloadMode = ExtensionReloadMode.valueOf(settings.reloadMode.toUpperCase());
		if (reloadMode == null) {
			reloadMode = ExtensionReloadMode.MANUAL;
		}
		ExtensionType extensionType = ExtensionType.valueOf(settings.type.toUpperCase());
		ICRAExtension extension;
		if (extensionType == ExtensionType.JAVA) {
			extension = createJavaExtension(settings);
		} else {
			throw new CoreExtensionException("Extension type not supported: " + extensionType);
		}
		extension.setName(settings.name);
		extension.setExtensionFileName(settings.file);
		extension.setReloadMode(reloadMode);
		extension.setCurrentRoom(room);
		try {
			if (settings.propertiesFile != null) {
				if ((settings.propertiesFile.startsWith("../")) || (settings.propertiesFile.startsWith("/"))) {
					throw new CoreExtensionException(
							"Illegal path for Extension property file. File path outside the extensions/ folder is not valid: "
									+ settings.propertiesFile);
				}
			}
			extension.setPropertiesFileName(settings.propertiesFile);
		} catch (IOException e) {
			throw new CoreExtensionException("Unable to load extension properties file: " + settings.propertiesFile);
		}

		try {
			extension.init();
			addExtension(extension);
			if (room != null) {
				room.setExtension(extension);
			}
		} catch (Exception err) {
			LOG.error("Extension initialization failed. ", err.getMessage());
			err.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	private ICRAExtension createJavaExtension(ExtensionSettings settings) throws CoreExtensionException {
		ICRAExtension extension;
		try {
			File jarFile = new File("extensions/" + settings.name);
			ClassLoader extensionClassLoader = new URLClassLoader(new URL[] { jarFile.toURI().toURL() },
					getClass().getClassLoader());
			Class<?> extensionClass = extensionClassLoader.loadClass(settings.file);
			extension = (ICRAExtension) extensionClass.newInstance();
		} catch (IllegalAccessException e) {
			throw new CoreExtensionException("Illegal access while instantiating class: " + settings.file);
		} catch (InstantiationException e) {
			throw new CoreExtensionException("Cannot instantiate class: " + settings.file);
		} catch (ClassNotFoundException e) {
			throw new CoreExtensionException("Class not found: " + settings.file);
		} catch (MalformedURLException e) {
			throw new CoreExtensionException("MalformedURLException: " + e.toString());
		}

		return extension;
	}

	private void addExtension(ICRAExtension extension) {
		extensionsByRoom.put(extension.getCurrentRoom(), extension);
	}

}

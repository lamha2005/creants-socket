package com.avengers.netty.core.om;

/**
 * @author LamHa
 *
 */
public class Device {

	private byte platformId;
	private String version;

	public byte getPlatformId() {
		return platformId;
	}

	public void setPlatformId(byte platformId) {
		this.platformId = platformId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}

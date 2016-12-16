package com.avengers.netty.core.om;

/**
 * @author LamHa
 *
 */
public class ClientDevice {
	private String providerID;
	private String providerName;
	private String refcode;
	private String subProvider;
	private String version;
	/**
	 * Số điện thoại
	 */
	private String phone;
	private int lang;
	/**
	 * Tên hệ điều hành
	 */
	private String os;
	/**
	 * Tên device
	 */
	private String device;
	private String osVersion;
	private String carrier;
	private String net;
	private boolean isJaibreak;
	private boolean isFirstLogin;
	private String notifyKey;
	private String deviceUDID;
	private String bundleId;
	private short width;
	private short height;
	private boolean isExistHackInApp;
	public String channel;

	public ClientDevice() {
	}

	public String getProviderID() {
		return providerID;
	}

	public void setProviderID(String providerID) {
		this.providerID = providerID;
	}

	public String getRefcode() {
		return refcode;
	}

	public void setRefcode(String refcode) {
		this.refcode = refcode;
	}

	public String getSubProvider() {
		return subProvider;
	}

	public void setSubProvider(String subProvider) {
		this.subProvider = subProvider;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getLang() {
		return lang;
	}

	public void setLang(int lang) {
		this.lang = lang;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		if ("2.0".equals(os)) {
			os = "iOS";
		}
		this.os = os;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public String getNet() {
		return net;
	}

	public void setNet(String net) {
		this.net = net;
	}

	public boolean getIsJaibreak() {
		return isJaibreak;
	}

	public void setIsJaibreak(boolean isJaibreak) {
		this.isJaibreak = isJaibreak;
	}

	public boolean isFirstLogin() {
		return isFirstLogin;
	}

	public void setFirstLogin(boolean isFirstLogin) {
		this.isFirstLogin = isFirstLogin;
	}

	public String getNotifyKey() {
		return notifyKey;
	}

	public void setNotifyKey(String notifyKey) {
		this.notifyKey = notifyKey;
	}

	public String getBundleId() {
		return bundleId;
	}

	public void setBundleId(String bundleId) {
		this.bundleId = bundleId;
	}

	public String getDeviceUDID() {
		return deviceUDID;
	}

	public void setDeviceUDID(String deviceUDID) {
		this.deviceUDID = deviceUDID;
	}

	public short getWidth() {
		return width;
	}

	public void setWidth(short width) {
		this.width = width;
	}

	public short getHeight() {
		return height;
	}

	public void setHeight(short height) {
		this.height = height;
	}

	public boolean isIsExistHackInApp() {
		return isExistHackInApp;
	}

	public void setIsExistHackInApp(boolean isExistHackInApp) {
		this.isExistHackInApp = isExistHackInApp;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ClientInfoObject{");
		sb.append("providerID=").append(providerID).append(", refcode=").append(refcode).append(", subProvider=")
				.append(subProvider).append(", version=").append(version).append(", phone=").append(phone)
				.append(", lang=").append(lang).append(", os=").append(os).append(", device=").append(device)
				.append(", osVersion=").append(osVersion).append(", carrier=").append(carrier).append(", net=")
				.append(net).append(", isJaibreak=").append(isJaibreak).append(", notifyKey=").append(notifyKey)
				.append(", deviceUDID=").append(deviceUDID).append(", bundleId=").append(bundleId).append(", width=")
				.append(width).append(", height=").append(height).append(", channel=").append(channel).append("}");
		return sb.toString();
	}

}

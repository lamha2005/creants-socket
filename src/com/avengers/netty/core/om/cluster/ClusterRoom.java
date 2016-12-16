package com.avengers.netty.core.om.cluster;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.cache.query.annotations.QueryTextField;

import com.avengers.netty.core.om.IRoom;
import com.avengers.netty.core.om.ServerConfig;
import com.avengers.netty.socket.gate.wood.User;

/**
 * @author LamHa
 *
 */
public class ClusterRoom implements Externalizable {
	@QuerySqlField(index = true)
	private int id;

	@QuerySqlField(index = true)
	private String clusterId;

	@QueryTextField
	private String owner;

	@QueryTextField
	private String name;

	@QuerySqlField(index = true)
	private int maxUsers;
	private String password;

	@QueryTextField
	private String ipAddress;

	private int currentUsers;

	// tùy loại game mà có giá trị
	private long minCash;
	private Map<String, String> properties;

	public ClusterRoom() {
	}

	public ClusterRoom(IRoom room) {
		User ownerObj = room.getOwner();
		if (ownerObj != null) {
			owner = ownerObj.getUserName();
		} else {
			owner = "";
		}

		password = room.getPassword();
		if (password == null)
			password = "";

		id = room.getId();
		name = room.getName();
		maxUsers = room.getMaxUsers();
		ipAddress = ServerConfig.getServerHost();
		clusterId = getClusterRoomId(room);
		properties = new HashMap<String, String>();
	}

	public static String getClusterRoomId(IRoom room) {
		return generateClusterId(room.getGameId(), room.getGameId());
	}

	public static String generateClusterId(byte gameId, int roomId) {
		return ServerConfig.getServerHost() + "_" + gameId + "_" + roomId;
	}

	public ClusterRoom(String name) {
		this.name = name;
		properties = new HashMap<String, String>();
	}

	public String getProperty(String key) {
		return properties.get(key);
	}

	public void putProperty(String key, String value) {
		properties.put(key, value);
	}

	public void removeProperty(String key) {
		properties.remove(key);
	}

	public boolean containsProperty(String key) {
		return properties.containsKey(key);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getClusterId() {
		return clusterId;
	}

	public void setClusterId(String clusterId) {
		this.clusterId = clusterId;
	}

	public void setClusterId(byte gameId, int roomId) {
		this.clusterId = generateClusterId(gameId, roomId);
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMaxUsers() {
		return maxUsers;
	}

	public void setMaxUsers(int maxUsers) {
		this.maxUsers = maxUsers;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getCurrentUsers() {
		return currentUsers;
	}

	public void setCurrentUsers(int currentUsers) {
		this.currentUsers = currentUsers;
	}

	public long getMinCash() {
		return minCash;
	}

	public void setMinCash(long minCash) {
		this.minCash = minCash;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(id);
		out.writeUTF(clusterId);
		out.writeUTF(owner);
		out.writeUTF(name);
		out.writeInt(maxUsers);
		out.writeUTF(password);
		out.writeUTF(ipAddress);
		out.writeInt(currentUsers);
		out.writeLong(minCash);

		// properties
		int size = properties.size();
		out.writeInt(size);
		if (size > 0) {
			for (Entry<String, String> entry : properties.entrySet()) {
				out.writeUTF(entry.getKey());
				out.writeUTF(entry.getValue());
			}
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		id = in.readInt();
		clusterId = in.readUTF();
		owner = in.readUTF();
		name = in.readUTF();
		maxUsers = in.readInt();
		password = in.readUTF();
		ipAddress = in.readUTF();
		currentUsers = in.readInt();
		minCash = in.readLong();

		// read properties
		int propertiesCount = in.readInt();
		properties = new HashMap<String, String>();
		for (int i = 0; i < propertiesCount; i++) {
			properties.put(in.readUTF(), in.readUTF());
		}
	}

	@Override
	public String toString() {
		return String.format("[roomId: %d, clusterId: %s, name: %s, ipAddress: %s]", id, clusterId, name, ipAddress);
	}

	public String getDump() {
		StringBuilder sb = new StringBuilder("/////////////// Room Dump ////////////////").append("\n");
		sb.append("\tName: ").append(name).append("\n").append("\tId: ").append(id).append("\n").append("\tPassword: ")
				.append(password).append("\n").append("\tMaxUser: ").append(maxUsers).append("\n")
				.append("\tIpAddress: ").append(ipAddress).append("\n");

		if (properties.size() > 0) {
			sb.append("\tProperties: ").append("\n");
			for (Object key : properties.keySet()) {
				sb.append("\t\t").append(key).append(": ").append(properties.get(key)).append("\n");
			}
		}
		sb.append("/////////////// End Dump /////////////////").append("\n");

		return sb.toString();
	}

}

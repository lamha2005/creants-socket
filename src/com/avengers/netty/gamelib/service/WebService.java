package com.avengers.netty.gamelib.service;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.avengers.netty.core.util.AppConfig;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * @author LamHa
 *
 */
public class WebService {
	private static final String KEY = "1|WqRVclir6nj4pk3PPxDCzqPTXl3J";
	private static WebService instance;

	public static WebService getInstance() {
		if (instance == null) {
			instance = new WebService();
		}
		return instance;
	}

	private WebService() {
	}

	public String verify(String token, String key) {
		WebResource webResource = Client.create().resource(AppConfig.graphApi + "verify");

		MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
		formData.add("token", token);
		formData.add("key", key);

		ClientResponse response = webResource.accept(MediaType.TEXT_PLAIN).header("key", KEY).post(ClientResponse.class,
				formData);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		}

		return response.getEntity(String.class);
	}

	public String getUser(String uid, String key) {
		WebResource webResource = Client.create().resource(AppConfig.graphApi + "user");
		MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
		formData.add("uid", uid);

		ClientResponse response = webResource.accept(MediaType.TEXT_PLAIN).header("key", KEY).post(ClientResponse.class,
				formData);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		}

		return response.getEntity(String.class);
	}

	public static void main(String[] args) {
		String user = WebService.getInstance().getUser("WqRVclir6nj4pk3PPxCczqPTXl3J", "2000");
		System.out.println(user.toString());
	}

}

package com.avengers.netty.core.api.ws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import com.avengers.netty.core.om.ServerConfig;
import com.avengers.netty.core.util.CoreTracer;

/**
 * @author LamHa
 *
 */
public abstract class AbstractAPI {

	public String request(ApiParam... params) {
		String urlRequest = getUrl();
		long processingTime = System.currentTimeMillis();
		BufferedReader in = null;
		StringBuilder responseString = new StringBuilder();
		URLConnection connection = null;
		StringBuilder urlParams = new StringBuilder();
		if (urlRequest != null && !urlRequest.isEmpty()) {
			try {
				urlParams.append(urlRequest);
				if (params.length > 0) {
					if (urlRequest.indexOf("?") < 0) {
						urlParams.append("?");
					}
					for (int i = 0; i < params.length; i++) {
						if (i > 0) {
							urlParams.append('&');
						}
						urlParams.append(params[i].getParamAndValue());
					}
				}

				URL url = new URL(urlParams.toString());
				connection = url.openConnection();
				connection.setReadTimeout(ServerConfig.apiTimeOut);
				connection.setConnectTimeout(ServerConfig.apiTimeOut);

				connection.connect();
				in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String tmp;

				while ((tmp = in.readLine()) != null) {
					responseString.append(tmp);
				}

			} catch (Exception ex) {
				CoreTracer.error(this.getClass(), "API Error reading URL content: ", ex);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException ex) {
						CoreTracer.error(this.getClass(), "close exception", ex);
					}
				}
			}
		}

		processingTime = System.currentTimeMillis() - processingTime;
		// Slow log
		if (processingTime > ServerConfig.SLOW_PROCESS_MSG_TIME) {
			CoreTracer.debug(AbstractAPI.class, 0, processingTime, this.getClass().getName(), urlParams.toString());
			// Reporter.logSlowedProcessCommand(0, processingTime,
			// this.getClass().getName(), urlParams.toString());
		}
		CoreTracer.debug(this.getClass(),
				urlParams.append(" ").append(responseString).append(" ").append(processingTime).toString());

		return responseString.toString();
	}

	protected String getUrl() {
		return ServerConfig.getProperty(this.getClass().getName());
	}
}

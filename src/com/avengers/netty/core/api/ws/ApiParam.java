package com.avengers.netty.core.api.ws;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.slf4j.LoggerFactory;

/**
 * @author LamHa
 *
 */
public class ApiParam {
	private String param;
	private String value;
	private boolean isEncode;

	public ApiParam(String param, String value) {
		this(param, value, true);
	}

	public ApiParam(String param, String value, boolean isUrlEncode) {
		this.param = param;
		this.value = value;
		this.isEncode = isUrlEncode;
	}

	public String getParamAndValue() {
		if (value == null) {
			value = "";
		}

		try {
			return param + "=" + (isEncode ? URLEncoder.encode(value, "UTF-8") : value);
		} catch (UnsupportedEncodingException ex) {
			LoggerFactory.getLogger(ApiParam.class.getName()).error(ex.getMessage(), ex);
		}

		return null;
	}

	public String getParam() {
		return param;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return getParamAndValue();
	}
}

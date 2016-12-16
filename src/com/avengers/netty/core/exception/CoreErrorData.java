package com.avengers.netty.core.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LamHa
 *
 */
public class CoreErrorData {
	ICoreErrorCode code;
	List<String> params;

	public CoreErrorData(ICoreErrorCode code) {
		this.code = code;
		this.params = new ArrayList<String>();
	}

	public ICoreErrorCode getCode() {
		return this.code;
	}

	public void setCode(ICoreErrorCode code) {
		this.code = code;
	}

	public List<String> getParams() {
		return this.params;
	}

	public void setParams(List<String> params) {
		this.params = params;
	}

	public void addParameter(String parameter) {
		this.params.add(parameter);
	}
}

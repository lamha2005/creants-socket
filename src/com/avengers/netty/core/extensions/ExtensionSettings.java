package com.avengers.netty.core.extensions;

import java.io.Serializable;

/**
 * @author LamHa
 *
 */
public final class ExtensionSettings implements Serializable {
	private static final long serialVersionUID = 2896660214476996410L;

	public String name = "";
	public String type = "JAVA";
	public String file = "";
	public String propertiesFile = "";
	public String reloadMode = "AUTO";
}

package com.avengers.netty.socket.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author LamHa
 *
 */
public class IOUtils {

	public static byte[] toByteArray(InputStream streamIn) {
		try {
			byte[] buffer = new byte[streamIn.available()];
			int read = 0;
			while (-1 != (read = streamIn.read(buffer))) {
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				output.write(buffer, 0, read);
				return output.toByteArray();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}

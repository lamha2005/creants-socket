/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avengers.netty.core.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Support multi language. Tất cả lớp resouce sẽ được cache
 * lại thông qua lớp này. Muốn sử dụng gọi hàm getInstance(Locale). Tất cả
 * resouce được lưu dưới dạng key - value theo chuẩn Resouce của Java.<br>
 * File resource sẽ được lưu trong folder lang và đóng gói cùng với core.
 */
/**
 * @author LamHa
 *
 */
public class LanguageUtil {

	// only support Vietnamese and English.

	public static final String VIETNAMESE = "vi";
	public static final String ENGLISH = "en";
	public static final Locale VIETNAMESE_LOCALE = new Locale(VIETNAMESE);
	public static final Locale ENGLISH_LOCALE = new Locale(ENGLISH);
	public static final Locale DEFAULT_LOCALE = VIETNAMESE_LOCALE;
	private Map<String, AbstractProperties> resourceCached;
	private Locale locale;

	static {
		Locale.setDefault(DEFAULT_LOCALE);
	}

	private static HashMap<Locale, LanguageUtil> languageMap = new HashMap<Locale, LanguageUtil>();

	/**
	 * init languge theo đia phương.
	 *
	 * @param locale
	 */
	public LanguageUtil(Locale locale) {
		this.locale = locale;
		resourceCached = new HashMap<String, AbstractProperties>();
	}

	/**
	 * Láy language mặc định.
	 *
	 * @return Gói ngôn ngữ mặc định
	 */
	public static LanguageUtil getInstance() {
		return LanguageUtil.getInstance(DEFAULT_LOCALE);
	}

	/**
	 * Lấy language theo địa phương
	 *
	 * @param locale
	 * @return Gói ngôn ngữ cache sẵn.
	 */
	public static LanguageUtil getInstance(Locale locale) {
		if (!languageMap.containsKey(locale)) {
			LanguageUtil langUtil = new LanguageUtil(locale);
			languageMap.put(locale, langUtil);
		}
		return languageMap.get(locale);
	}

	/**
	 * Lấy language từ file properties.
	 *
	 * @param fileName
	 * @return
	 */
	public AbstractProperties getResource(String fileName) {
		if (resourceCached.containsKey(fileName)) {
			return resourceCached.get(fileName);
		}

		resourceCached.put(fileName, new AbstractProperties(fileName, locale));
		return resourceCached.get(fileName);
	}

	/**
	 * Lay ngon ngu ma he thong ho tro.
	 *
	 * @param localeString
	 * @return
	 */
	public static Locale getLocale(String localeString) {
		if (localeString.equalsIgnoreCase(VIETNAMESE)) {
			return VIETNAMESE_LOCALE;
		} else if (localeString.equalsIgnoreCase(ENGLISH)) {
			return ENGLISH_LOCALE;
		} else {
			return DEFAULT_LOCALE;
		}
	}

	public static String getMessage(String className, Locale locale, String property) {
		try {
			return ResourceBundle.getBundle(className, locale).getString(property);
		} catch (Exception ex) {
			CoreTracer.error(LanguageUtil.class, "LanguageUtil getMessage {className:" + className + ", property:"
					+ property + ", locale:" + locale + "} error: ", ex);
			return property;
		}
	}
}

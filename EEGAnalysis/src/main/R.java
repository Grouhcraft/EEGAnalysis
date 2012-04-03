package main;

import java.util.ResourceBundle;

public class R {
	public static String get(String key) {
		return ResourceBundle.getBundle("eeg").getString(key);
	}

	public static Integer getInt(String key) {
		String stringVal = get(key);
		if (stringVal == null)
			return null;
		try {
			return Integer.valueOf(stringVal);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static Boolean getBoolean(String key) {
		String stringVal = get(key);
		if (stringVal == null)
			return null;
		try {
			return Boolean.valueOf(stringVal);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}

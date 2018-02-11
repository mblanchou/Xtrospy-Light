package com.xtrospy.extensions;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Base64;

import com.xtrospy.core.ApplicationConfig;
import com.xtrospy.core.HookConfig;
import com.xtrospy.global_config.GlobalConfig;
import com.xtrospy.hooks.HookList;

public class AppFilter {

	private static Integer _calls = 0;
	private static String _filter = "Ki5hbWF6b24uY29t";
	
	private static String _appNameAtStartup = null;
	
	private static Integer _error = 0;
	
	
	public static void init() {
		// removed in light version
	}

	public static boolean isTempered() {
		return false;
		// removed
	}
	
	// initialize during application state
	public static void init(String appName) {
	}
	
	private static boolean isEnabled(String appName) {
		return true;
	}
	
	private boolean isValid(String appName) {
		return true;
	}
	
	public static String ToBase64String(String source) {
		byte[] data = Base64.decode(source, Base64.DEFAULT);
		String text = "";
		try {
			text = new String(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return text;
    }
	
}

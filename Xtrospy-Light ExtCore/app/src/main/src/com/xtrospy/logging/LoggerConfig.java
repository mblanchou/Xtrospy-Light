package com.xtrospy.logging;

import com.xtrospy.core.HookConfig;
import com.xtrospy.core.IntroStringHelper;

public class LoggerConfig extends IntroStringHelper {
	protected LoggerConfig() {
	}
	
	protected HookConfig _config;
	
	public static String _TAG = "xtrospy";
	public static String _TAG_ERROR = "xtrospyError";
	public static String _TAG_LOG = "xtrospyLog";
	
	public static String getTag() {
		return _TAG;
	}
	
	public static String getTagError() {
		return _TAG_ERROR;
	}
	
	public static String getTagLog() {
		return _TAG_LOG;
	}

	protected String _out = "";
	protected String _notes = "";
	protected String _traces = "";
	
	protected boolean _enableDB = false;
	protected boolean _enableFileLogs = false;

	// this can be enabled via the _config file
	protected boolean _stackTraces = false;
	
	// change this value to get full traces
	protected boolean _fullTraces = false;
	
	public void enableFileLogger(Boolean value) {
		_enableFileLogs = value;
	}
	
}

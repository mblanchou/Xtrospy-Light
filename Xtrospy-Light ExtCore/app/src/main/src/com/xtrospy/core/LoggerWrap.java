package com.xtrospy.core;

import com.xtrospy.logging.Logger;

public class LoggerWrap extends IntroHelper {
	private Logger _l = new Logger();
	
	protected void _logInit(HookConfig config) {
		_l.logInit(config);
	}	
	
	protected void _logLine(String line) {
		_l.logLine(line);
	}
	
	protected void _logFlush(String notes) {
		_l.logFlush(notes);
	}
	protected void _logFlush_I(String notes) {
		_l.logFlush_I(notes);
	}
	protected void _logFlush_W(String notes) {
		_l.logFlush_W(notes);
	}
	
	protected void _logFlush() {
		_l.logFlush_I();
	}
	
	protected void _logFlush_W() {
		_l.logFlush_W();
	}
		
	protected void _logParameter(String name, String value) {
		_l.logParameter(name, value);
	}
	
	protected void _logParameter(String name, Object value) {
		_l.logParameter(name, "" + value);
	}
	
	protected void _logReturnValue(String name, String value) {
		_l.logReturnValue(name, value);
	}
	
	protected void _logReturnValue(String name, Object value) {
		_l.logReturnValue(name, "" + value);
	}
	
	protected void _logBasicInfo() {
		_l.logBasicInfo();
	}
	
	protected String _getFullTraces() {
		return _l.getFullTraces();
	}

	protected String _getLightTraces() {
		return _l.getLightTraces();		
	}
	
	public void enableTraces(Boolean value) {
		_l.enableTraces(value);
	}
	
	public void enableFileLogger(Boolean value) {
		_l.enableFileLogger(value);
	}	
}

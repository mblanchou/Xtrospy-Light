package com.xtrospy.core;

import android.util.Log;

import com.saurik.substrate.MS;
import com.xtrospy.logging.LoggerConfig;

import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

@SuppressWarnings("rawtypes")
public 
class IntroHook extends LoggerWrap {
	protected HookConfig _config = null;
	
	protected String _TAG = LoggerConfig.getTag();
	protected String _TAG_ERROR = LoggerConfig.getTagError();
	
	protected String _className, _methodName, _type;
	protected String _packageName, _dataDir;
	protected Object _notes;
	protected Object _args;
	
	protected Boolean _isXposed = false;
	protected Boolean _isSubstrate = false;
	
	protected Class<?>[] _parameters;
	
	// Xposed specific
	protected MethodHookParam _XposedParam = null;

	// Substrate specific
	protected MS.MethodPointer _old = null;
	protected Object _resources;
	
	// Protected constructor prevents 
	// instantiation from other classes
	protected IntroHook() {

	}
	
	// for Xposed
	public void init(MethodHookParam param, 
			HookConfig config, Object[] args) {
		if (_config == null) {
			_config = config;
			_className = _config.getClassName();
			_methodName = _config.getMethodName();
			_parameters = _config.getParameters();
			_type = _config.getCategory();
			_notes = _config.getNotes();

			_isXposed = true;
			_XposedParam = param;
			_resources = param.thisObject;
			
			// Xposed doesn't use this
			_old = null;
			
			_packageName = ApplicationConfig.getPackageName();
			_dataDir = ApplicationConfig.getDataDir();
			
			_logInit(config);
		}
	}
	
	// for Substrate
	public void init(HookConfig config, Object resources, 
			MS.MethodPointer old, Object... args) {
		
		if (_config == null) {
			_config = config;
			_className = _config.getClassName();
			_methodName = _config.getMethodName();
			_parameters = _config.getParameters();
			_type = _config.getCategory();
			_old = old;
			_notes = _config.getNotes();

			_isSubstrate = true;
			
			_resources = resources;
			
			_packageName = ApplicationConfig.getPackageName();
			_dataDir = ApplicationConfig.getDataDir();
			
			_logInit(config);
		}
	}
	
	public void execute(Object... args) throws Throwable {
		// display info on the app related to the hook
		_logBasicInfo();
		
		// the flush should be done in the child in order to specify warning levels
		//_logFlush();
	}
	
	
	// execute the method
	// this should be safe to use in most cases
	// warning: make sure to not catch (or throw back)
	//  	any exception this may throw as it may alter the
	//  	execution flow otherwise
	@SuppressWarnings("unchecked")
	protected Object _hookInvoke(Object... args) throws Throwable {
		Object ret = null;
		if (_isSubstrate) {
			ret = _old.invoke(_resources, args);
			// set this after execution, if there is an exception
			// we will not need to re-initialize for the next call
			_wasExecuted = true;
			_setNewReturnValue(ret);
		}
		else if (_isXposed) {
			//TODO: this does not use modified args so it can't work
			ret = _XposedParam.getResult();
			_wasExecuted = true;
			_setNewReturnValue(ret);
		}
		return ret;
	}

	private Boolean _wasExecuted = false;
	//TODO: names are confusing; the execute method 
	// doesnt execute as oppose to hookInvoke
	//TODO: clean this
	public Boolean wasExecuted() {
		return _wasExecuted;
	}
	
	// object has a long life and need to be cleaned
	public void clean() {
		_wasExecuted = false;
		_hasNewReturnValueValue = false;
	}
	
	//TODO: _refactor this, it's only used with custom hooks
	
	private Object _ret = null;
	private Boolean _hasNewReturnValueValue = false;
	
	public Object getNewReturnValue() {
		return _ret;
	}
	
	protected void _setNewReturnValue(Object ret) {
		_hasNewReturnValueValue = true;
		_ret = ret;
	}

	
	// #####

	public boolean hasNewReturnValue() {
		return _hasNewReturnValueValue;
	}
}

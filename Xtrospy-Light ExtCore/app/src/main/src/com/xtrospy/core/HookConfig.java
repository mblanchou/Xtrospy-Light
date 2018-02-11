package com.xtrospy.core;

public class HookConfig {
	private String 		_className;
	private String 		_methodName;
	private Class<?>[] 	_parameters;
	private boolean 	_active;
	private String		_type;
	private IntroHook	_IntroHook;
	private String		_notes;
	private String 		_subType;
	private Boolean		_isPublic = null;
	private Boolean		_isStatic = null;
	private Boolean		_isNative = null;
	
	// getters
	public String getClassName() 	{ return _className; }
	public String getMethodName() 	{ return _methodName; }
	public Class<?>[] getParameters() { return _parameters; }
	public boolean isActive() 	{ return _active; }
	public IntroHook getFunc() { return _IntroHook; }
	public String getNotes() 	{ return _notes; }

	public String getType() 	{ return _type; }
	public String getSubType() 	{ return _subType; }
	
	public Boolean isPublic()	{ return _isPublic; }
	public boolean isStatic() 	{ return _isStatic; }
	public boolean isNative()	{ return _isNative; }
	
	public String getCategory() { 
		if (_subType.isEmpty())
			return _type; 
		return _subType;
	}
	
	// constructor
	public HookConfig(boolean active,
			String type,
			String subType,
			String className, 
			String methodName,
			IntroHook IntroHook,
			Class<?>[] parameters,
			String notes) {
		
		_IntroHook = IntroHook;
		_className = className;
		_active = active;
		_methodName = methodName;
		_parameters = parameters;
		_type = type;
		_subType = subType;
		_notes = notes;
	}
	
	// constructor for Custom Hookss
	public HookConfig(boolean active,
			String className, 
			String methodName,
			Class<?>[] parameters,
			IntroHook IntroHook,
			String notes) {
		
		_IntroHook = IntroHook;
		_className = className;
		_active = active;
		_methodName = methodName;
		_parameters = parameters;
		_type = "Custom Hooks";
		_subType = "Custom Hooks";
		_notes = notes;
	}
	
	// constructor for Custom Hookss without notes
	public HookConfig(boolean active,
			String className, 
			String methodName,
			Class<?>[] parameters,
			IntroHook introHook) {
		
		_IntroHook = introHook;
		_className = className;
		_active = active;
		_methodName = methodName;
		_parameters = parameters;
		_type = "Custom Hooks";
		_subType = "Custom Hooks";
		_notes = "";
	}
	
	// constructor for live call stack code and fuzzers
	public HookConfig(boolean active,
			String className, 
			String methodName,
			Class<?>[] parameters,
			IntroHook introHook,
			Boolean isPublic,
			Boolean isStatic,
			Boolean isNative) {
		
		_IntroHook = introHook;
		_className = className;
		_active = active;
		_methodName = methodName;
		_parameters = parameters;
		_type = "Profiling";
		_subType = "Profiling";
		_notes = "";
		_isPublic = isPublic;
		_isStatic = isStatic;
		_isNative = isNative;
	}
	
	public void disable() {
		_active = false;
	}
}

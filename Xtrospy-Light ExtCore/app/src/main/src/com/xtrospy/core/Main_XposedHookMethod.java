package com.xtrospy.core;

import static de.robv.android.xposed.XposedHelpers.findConstructorExact;
import static de.robv.android.xposed.XposedHelpers.*;

import java.lang.reflect.Member;

import com.xtrospy.extensions.AppFilter;
import com.xtrospy.global_config.GlobalConfig;
import com.xtrospy.logging.LoggerConfig;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import de.robv.android.xposed.callbacks.XCallback;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

class Main_XposedHookMethod {
	
	static private Main_XposedHookMethod _instance = null;
	static protected Main_XposedHookMethod getInstance() {
		if (_instance == null)
			_instance = new Main_XposedHookMethod();
		return _instance;
	}
	
	// static private String _TAG = LoggerConfig.getTag();
	static private String _TAG_ERROR = LoggerConfig.getTagError();
//	static private String _TAG_LOG = LoggerConfig.getTagLog();
	// static private boolean _debug = false;

	static private boolean _alreadyShownPopup = false;
	
	public void _hookMethod(
			final LoadPackageParam lpparam,
			final HookConfig elemConfig) {

		final String className = elemConfig.getClassName();
		final String methodName = elemConfig.getMethodName();
		final Class<?>[] parameters = elemConfig.getParameters();
		
		try {
			boolean isConstructor = 
					className.substring(className.lastIndexOf('.') + 1).equals(methodName);
			
			Member member = null;
			
			if (!isConstructor) {
				member = 
						findMethodExact(Class.forName(className), methodName, parameters);
			}
			else {
				member = 
						findConstructorExact(Class.forName(className), parameters);
			}
			
			XposedBridge.hookMethod(member, 
					new XC_MethodHook(XCallback.PRIORITY_DEFAULT) {
				
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {

					_hookMethodGen(lpparam, param, elemConfig);
					
				}
			});
		}
		catch (Exception e) {
			Log.w(_TAG_ERROR, "Error - No such method: " + methodName
					+ " with " + parameters.length + " args");
			
			for (int j = 0; j < parameters.length; j++)
				Log.i(_TAG_ERROR, "Arg " + (j + 1) + 
						" type: " + parameters[j]);

			Log.w(_TAG_ERROR, "Error: " + e);
			
			elemConfig.disable();
			
			return;
		}
	}

	protected void _hookMethodGen(
			final LoadPackageParam lpparam,
			MethodHookParam param,
			final HookConfig elemConfig) throws Throwable {

		if (ApplicationConfig.isEnabled()) {
			_hookMethodImpl(param, elemConfig, param.args);
			// in the case the method was executed to test return value
			// just return the value in order to avoid executing it twice
			if (elemConfig.getFunc().hasNewReturnValue()
					|| elemConfig.getFunc().wasExecuted()) {
				elemConfig.getFunc().clean();

				// TODO: add the ability to return new value
				// and calling with new args

				// return elemConfig.getFunc().getNewReturnValue();
			}
		}
		// return old.invoke(resources, args);
	}

	// for Xposed
	protected void _hookMethodImpl(
			MethodHookParam param,
			final HookConfig elemConfig, 
			Object... args) throws Throwable {
		
		String packageName = ApplicationConfig.getPackageName();
		String dataDir = ApplicationConfig.getDataDir();
		String type = elemConfig.getSubType();
		Context context = ApplicationConfig.getContext();
		
		if (!_alreadyShownPopup &&
				context != null && 
				LoadConfig.getInstance().initConfig(dataDir)) {
			
			_alreadyShownPopup = true;
			Toast.makeText(context, packageName + " loaded with xtrospy",
					   Toast.LENGTH_SHORT).show();
		}
		
		try {
			if (packageName != null && dataDir != null
							&& LoadConfig.getInstance().initConfig(dataDir) && LoadConfig
							.getInstance().getHookTypes().contains(type)) {

	//				Log.i(_TAG_LOG, "### Hooking: " + packageName + " === " 
	//				+ elemConfig.getClassName() + "->" + elemConfig.getMethodName()
	//				+ "() with " + elemConfig.getParameters().length + " args");
					
					elemConfig.getFunc().init(param, elemConfig, args);
	
					elemConfig.getFunc().enableTraces(
							LoadConfig.getInstance().getHookTypes()
									.contains("Stack Traces"));
					elemConfig.getFunc().enableFileLogger(
							LoadConfig.getInstance().getHookTypes()
									.contains("File Logs"));
	
					_execMethod(param, elemConfig, args);
			}
		} catch (Exception e) {
			Log.w(_TAG_ERROR, "-> Error in injected code: [" + e + "]"
					+ "\nApp: " + ApplicationConfig.getPackageName()
					+ ", method: " + elemConfig.getMethodName()
					+ ", class: " + elemConfig.getClassName());
			// Log.w(_TAG_ERROR, LoggerErrorHandler._getStackTrace());
		}
	}

	protected void _execMethod(MethodHookParam param,
			final HookConfig elemConfig, Object... args) throws Throwable {

		if (AppFilter.isTempered())
			return;

		// this prevents recursive hooks
		if (!ApplicationConfig.gl_stopHooking) {
			synchronized (ApplicationConfig.gl_stopHooking) {
				// will not hook anything called within this hook
				ApplicationConfig.gl_stopHooking = true;


				elemConfig.getFunc().init(param, elemConfig, args);
				elemConfig.getFunc().execute(args);

				ApplicationConfig.gl_stopHooking = false;
			}
		}
	}
}

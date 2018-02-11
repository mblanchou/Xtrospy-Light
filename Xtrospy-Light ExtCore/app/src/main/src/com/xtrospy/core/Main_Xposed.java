package com.xtrospy.core;

import android.util.Log;

import com.xtrospy.custom_hooks.CustomHookList;
import com.xtrospy.extensions.AppFilter;
import com.xtrospy.global_config.GlobalConfig;
import com.xtrospy.hooks.HookList;
import com.xtrospy.logging.LoggerConfig;

import de.robv.android.xposed.IXposedHookCmdInit;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Main_Xposed implements IXposedHookCmdInit, IXposedHookLoadPackage {
	// static private String _TAG = LoggerConfig.getTag();
//	static private String _TAG_ERROR = LoggerConfig.getTagError();
	static private String _TAG_LOG = LoggerConfig.getTagLog();
	// static private boolean _debug = false;
	
	// Xposed loader
	public void handleLoadPackage(final LoadPackageParam lpparam)
			throws Throwable {
		if (!ApplicationState_Xposed.initialize(lpparam))
			return;
	    Log.i(_TAG_LOG, "App loaded: " + lpparam.packageName);

		HookConfig[] _config = HookList.getHookList();

		AppFilter.init();

		// init app state and initialize other modules
		// this is done when Xposed loads, as opposed to Substrate
		initializeConfig(_config, lpparam);
		
		if (GlobalConfig.enableCustomHooks_Xposed &&
				GlobalConfig.validated) {
			HookConfig[] _custom_config = CustomHookList.getHookList();
			initializeConfig(_custom_config, lpparam);
		}
		
	}

	protected static void initializeConfig(
			HookConfig[] config, 
			final LoadPackageParam lpparam) {
		
		for (final HookConfig elemConfig : config) {
			if (!elemConfig.isActive())
				continue;
			
			Main_XposedHookMethod.getInstance()._hookMethod(lpparam, elemConfig);

		}
	}

	@Override
	public void initCmdApp(StartupParam startupParam) throws Throwable {
		// TODO Auto-generated method stub
		Log.w(_TAG_LOG, "--> " + startupParam);
	}
}

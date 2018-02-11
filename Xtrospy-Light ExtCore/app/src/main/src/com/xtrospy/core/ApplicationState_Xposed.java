package com.xtrospy.core;

import java.lang.reflect.Method;

import com.xtrospy.extensions.AppFilter;
import com.xtrospy.global_config.GlobalConfig;
import com.xtrospy.logging.LoggerConfig;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

// as opposed to the Substrate version, this loads when the app starts
public class ApplicationState_Xposed implements IXposedHookLoadPackage {
//	static private String _TAG = LoggerConfig.getTag();
	static private String _TAG_LOG = LoggerConfig.getTagLog();
//	static private String _TAG_LOG_DEBUG = LoggerConfig.getTagLog() + "Debug";
	static private String _TAG_ERROR = LoggerConfig.getTagError();
	
	static private boolean _debug = false;
	
	@Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {		
		if (!initialize(lpparam))
			return;
		
		// get a context for a loaded app
        findAndHookMethod("android.app.ContextImpl", 
        		lpparam.classLoader, "getPackageName", new XC_MethodHook() {
            
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            	if (ApplicationConfig.getContext() == null) {
            		initApplicationState(param);
            	}
            }
        });
        
	}
	
	static public boolean initialize(final LoadPackageParam lpparam) {
		if (!GlobalConfig.enableXposed)
			return false;
		if (lpparam == null || lpparam.packageName == null) {
			Log.w(_TAG_ERROR, 
					"ApplicationState_Xposed:handleLoadPackage ERROR.");
			return false;
		}
		if (lpparam.appInfo == null) {
			Log.w(_TAG_ERROR, "Could not retrieve appInfo for: " + lpparam.packageName);
			return false;
		}
//		Log.i(_TAG_LOG_DEBUG, "Loading : " + lpparam.packageName);

		if (ApplicationConfig.getPackageName() == null)
			ApplicationConfig.setPackageName(lpparam.packageName);
		if (ApplicationConfig.getDataDir() == null)
			ApplicationConfig.setDataDir(lpparam.appInfo.dataDir);
		

				
		return true;
	}
	
	protected static void initApplicationState(MethodHookParam param) {
		String packageName = ApplicationConfig.getPackageName();
		String dataDir = ApplicationConfig.getDataDir();
		Object resources = param.thisObject; // has to be the ContextImpl object
		
		try {
			Class<?> cls = Class.forName("android.app.ContextImpl");
			Class<?> noparams[] = {};
			Method _method = 
					cls.getDeclaredMethod("getApplicationContext", noparams);
			
			Context context = (Context) _method.invoke(resources);
			
			if (context == null) {
				if (_debug)
					Log.w(_TAG_ERROR, "Could not retrieve context for: " + packageName);
			}
			else {
				ApplicationConfig.setContext(context);
				if (_debug)
					Log.w(_TAG_LOG, "Context retrieved for: " + packageName);
			}
		    
			//PackageManager pm = context.getPackageManager();
			_method = cls.getDeclaredMethod("getPackageManager", noparams);
			PackageManager pm = (PackageManager) _method.invoke(resources);
	
			
			android.content.pm.ApplicationInfo ai = 
					pm.getApplicationInfo(packageName, 0);
			
			if ((ai.flags & 0x81) == 0) {
			    ApplicationConfig.enable();
				// load extensions
				boolean isThereAConfig = 
						LoadConfig.getInstance().initConfig(ApplicationConfig.getDataDir());
				
				if (isThereAConfig &&
						LoadConfig.getInstance().getHookTypes().contains("Fuzzing")) {
					// fuzzer running when the app starts once we have the package name
					// removed in light version
				}
				
				AppFilter.init(packageName);
			}
			
		}
		catch (Exception e) {
			Log.w(_TAG_ERROR, "Error when setting the " +
					"application state for ["+ packageName +"]: ", e);
		}
	}
}

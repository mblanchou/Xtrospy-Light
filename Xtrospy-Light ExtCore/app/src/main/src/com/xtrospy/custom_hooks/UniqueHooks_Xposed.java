package com.xtrospy.custom_hooks;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import java.net.Socket;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import com.xtrospy.core.ApplicationConfig;
import com.xtrospy.core.IntroHelper;
import com.xtrospy.core.LoadConfig;
import com.xtrospy.extensions.UnsafeTrustManager;
import com.xtrospy.global_config.GlobalConfig;
import com.xtrospy.logging.LoggerConfig;

import android.os.StrictMode;

import android.util.Log;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class UniqueHooks_Xposed implements IXposedHookLoadPackage {
//	static private String _TAG = LoggerConfig.getTag();
	static private String _TAG_LOG = LoggerConfig.getTagLog();
//	static private String _TAG_LOG_DEBUG = LoggerConfig.getTagLog() + "Debug";
//	static private String _TAG_ERROR = LoggerConfig.getTagError();
//	
	
	static private boolean _debug = true;

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
//		if (!ApplicationState_Xposed.initialize(lpparam))
//			return;
		
		boolean hasAConfig = 
				LoadConfig.getInstance().initConfig(ApplicationConfig.getDataDir());
		
		// enables debugging for all apps
		if (GlobalConfig.enableGlobalDebugMode ||
				(hasAConfig) && 
				LoadConfig.getInstance().getHookTypes().contains("Debuggable")) {
			_enableDebugMode(lpparam);
		}
		
		// bypass cert pinning
		if (GlobalConfig.enableGlobalCertPinningBypass ||
				(hasAConfig) && 
				LoadConfig.getInstance().getHookTypes().contains("Cert Pinning Bypass")) {
			_enableCertPinningBypass(lpparam);
		}
	}


	
	protected void _enableCertPinningBypass(LoadPackageParam lpparam) {
		final String message = " - Initiating all trusting connection (SSL cert pinning bypass)";
		
		// disable ssl cert pinning
		_hookAndModify(lpparam, "getTrustManagers", "javax.net.ssl.TrustManagerFactory",
				new Class<?>[] {},
                new XC_MethodHook() {
						@Override
						protected void beforeHookedMethod(
								MethodHookParam param) throws Throwable {
							param.setResult(UnsafeTrustManager.getInstance());
							Log.w(_TAG_LOG, ApplicationConfig.getPackageName() + message);
						}			
					}
				);
		_hookAndModify(lpparam, "setSSLSocketFactory", "javax.net.ssl.HttpsURLConnection",
				new Class<?>[] {javax.net.ssl.SSLSocketFactory.class},
                new XC_MethodHook() {
						@Override
						protected void beforeHookedMethod(
								MethodHookParam param) throws Throwable {
							SSLContext context = SSLContext.getInstance("TLS");
                            context.init(null, UnsafeTrustManager.getInstance(), null);                            
                            param.args[0] = context.getSocketFactory();
							Log.w(_TAG_LOG, ApplicationConfig.getPackageName() + message);
						}			
					}
				);
		_hookAndModify(lpparam, "init", "javax.net.ssl.SSLContext",
				new Class<?>[] {KeyManager[].class, TrustManager[].class, 
								SecureRandom.class},
                new XC_MethodHook() {
						@Override
						protected void beforeHookedMethod(
								MethodHookParam param) throws Throwable {
							param.args[0] = null;
                            param.args[1] = UnsafeTrustManager.getInstance();
                            param.args[2] = null;
							Log.w(_TAG_LOG, ApplicationConfig.getPackageName() + message);
						}			
					}
				);
		_hookAndModify(lpparam, "isSecure", "org.apache.http.conn.ssl.SSLSocketFactory",
				new Class<?>[] {Socket.class},
                new XC_MethodHook() {
						@Override
						protected void beforeHookedMethod(
								MethodHookParam param) throws Throwable {
                            param.setResult(true);
							Log.w(_TAG_LOG, ApplicationConfig.getPackageName() + message);
						}
					}
				);
	}
	
	protected void _enableDebugMode(LoadPackageParam lpparam) {
		// check if an application is debuggable
		_hookAndModify(lpparam, "start", "android.os.Process",
				new Class<?>[] {String.class, String.class, Integer.TYPE, 
                Integer.TYPE, int[].class, Integer.TYPE, Integer.TYPE,
                Integer.TYPE, String.class, String[].class},
                new XC_MethodHook() {
						@Override
						protected void beforeHookedMethod(
								MethodHookParam param) throws Throwable {
							param.args[5] = ((Integer) param.args[5] | 0x1);
							if (_debug)
								Log.w(_TAG_LOG, ApplicationConfig.getPackageName() +
										": This application is now debuggable!");
						}
					}
				);
	}

	protected void _hookAndModify (
			LoadPackageParam lpparam,
			String methodName, 
			String className, 
			final Class<?>[] parameters,
			XC_MethodHook xc_MethodHook)
	{
		IntroHelper helper = new IntroHelper();	
		boolean isConstructor = 
				className.substring(className.lastIndexOf('.') + 1).equals(methodName);
		
		//@todo: need to hook constructor as well
		if (!isConstructor) {
			final Object[] XposedParameters = helper._ArrayAdd(parameters, xc_MethodHook);
						
			findAndHookMethod(
					className,
					lpparam.classLoader, 
//						Class.forName(elemConfig.getClassName()).getClassLoader(),
//						ClassLoader.getSystemClassLoader().getParent(),
					methodName,
					XposedParameters);
			}
	}
}

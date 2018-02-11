package com.xtrospy.core;

import android.content.Context;
 
public class ApplicationConfig {
	
   private static String 	_packageName = null;
   private static String 	_dataDir = null;
   private static LoadConfig _loadConfig = null;
   private static Boolean 	_enabled = false;
   private static Context 	_context = null;
   
   private static Boolean	_systemApp = false;
   
   public static boolean g_verbose_errors = false;
   public static boolean g_debug = false;
   
   // used as a lock to avoid hooking functions within 
   // xtrospy (that xtrospy hooks such as DBs for logs etc.)
   public static Boolean	gl_stopHooking = false;

   // used as a lock to avoid races during logging
   public static Boolean	gl_isBeingLogged = false;
   
   // Private constructor prevents instantiation from other classes
   private ApplicationConfig() {}

   // get
   public static String getPackageName() {
      return _packageName;
   }
   public static String getDataDir() {
	  return _dataDir;
   }
   public static LoadConfig getLoadConfig() {
	   if (_loadConfig == null)
		   _loadConfig = new LoadConfig();
		  return _loadConfig;
   }
   public static Context getContext() {
		return _context;
   }
   
   // set
   public static void setPackageName(String packageName) {
	   _packageName = packageName;
   }
   public static void setDataDir(String dataDir) {
	   _dataDir = dataDir;
   }
   public static void setContext(Context context) {
	   _context = context;
   }
   
   // ####
   public static void disable() {
	   _enabled = false;
   }
   public static void enable() {
	   _enabled = true;
   }
   
   public static boolean isEnabled() {
		return _enabled;
   }

   public static boolean isSystemApp() {
	   return _systemApp;
   }
   
   public static void setSystemFlag() {
	   _systemApp = true;
   }

}

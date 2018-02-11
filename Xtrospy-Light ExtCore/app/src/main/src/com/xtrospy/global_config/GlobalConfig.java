package com.xtrospy.global_config;

public class GlobalConfig {
	
	// do not activate both xposed and substrate at once
	public static boolean enableSubstrate = false;
	public static boolean enableXposed = true;
	
	// -- Xposed specific
	// as opposed to substrate, methods that don't exist 
	// will lead to generating errors with Xposed
	public static boolean enableCustomHooks_Xposed = true;
	
	// -- substrate specific - code removed in light version
	// enable default hooks (in hooks.java)
	public static boolean enableConfigHooks_Substrate = true;
	// Substrate hooks only when methods are first called so it is fine leaving this
	public static boolean enableCustomHooks_Substrate = true;
	// enable verbose debug logs for substrate
	public static boolean verboseModeSubstrate = true;
	
	// -- general
	// bypass cert pinning for all apps
	public static boolean enableGlobalCertPinningBypass = false;
	// make all apps debuggeable
	public static boolean enableGlobalDebugMode = false;
	
	
	public static boolean validated = false;

}

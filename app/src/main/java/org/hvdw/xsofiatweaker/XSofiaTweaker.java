package org.hvdw.xsofiatweaker;

import android.util.Log;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.app.AndroidAppHelper;
import android.widget.Toast;
import android.media.AudioManager;
import android.app.Application;
import android.media.AudioManager;


import de.robv.android.xposed.XposedBridge;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class XSofiaTweaker implements IXposedHookZygoteInit, IXposedHookLoadPackage {
	public static final String TAG = "XSofiaTweaker";
	public static Context mContext;
	private static PackageManager pm;
	public static XSharedPreferences pref;

	private static AudioManager sAudioManager;

	private boolean noKillEnabled;
	private boolean skip_ch_four;
	private boolean pref_enable_usb_dac;
	private String navi_call_option;
	private String navi_call_entry;
	private String bt_phone_call_option;
	private String bt_phone_call_entry;
	private String band_call_option;
	private String band_call_entry;
	private String media_call_option;
	private String media_call_entry;
	private String mode_src_call_option;
	private String mode_src_call_entry;
	private String dvd_call_option;
	private String dvd_call_entry;
	private String eject_call_option;
	private String eject_call_entry;
	private String eq_call_option;
	private String eq_call_entry;

	private String acc_on_call_option;
	private String acc_on_call_entry;
	private String acc_off_call_option;
	private String acc_off_call_entry;
	private String resume_call_option;
	private String resume_call_entry;

	private boolean home_key_capture_enabled;
	private String home_call_option;
	private String home_call_entry;

	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		XSharedPreferences sharedPreferences = new XSharedPreferences("org.hvdw.xsofiatweaker");
		sharedPreferences.makeWorldReadable();

		noKillEnabled = sharedPreferences.getBoolean(MySettings.PREF_NO_KILL, true);
		skip_ch_four = sharedPreferences.getBoolean(MySettings.PREF_SKIP_CH_FOUR, false);
		pref_enable_usb_dac = sharedPreferences.getBoolean(MySettings.ENABLE_USB_DAC, false);
		bt_phone_call_option = sharedPreferences.getString(MySettings.BT_PHONE_CALL_OPTION, "");
		bt_phone_call_entry = sharedPreferences.getString(MySettings.BT_PHONE_CALL_ENTRY, "");
		navi_call_option = sharedPreferences.getString(MySettings.NAVI_CALL_OPTION, "");
		navi_call_entry = sharedPreferences.getString(MySettings.NAVI_CALL_ENTRY, "");
		band_call_option = sharedPreferences.getString(MySettings.BAND_CALL_OPTION, "");
		band_call_entry = sharedPreferences.getString(MySettings.BAND_CALL_ENTRY, "");
		media_call_option = sharedPreferences.getString(MySettings.MEDIA_CALL_OPTION, "");
		media_call_entry = sharedPreferences.getString(MySettings.MEDIA_CALL_ENTRY, "");
		mode_src_call_option = sharedPreferences.getString(MySettings.MODE_SRC_CALL_OPTION, "");
		mode_src_call_entry = sharedPreferences.getString(MySettings.MODE_SRC_CALL_ENTRY, "");
		dvd_call_option = sharedPreferences.getString(MySettings.DVD_CALL_OPTION, "");
		dvd_call_entry = sharedPreferences.getString(MySettings.DVD_CALL_ENTRY, "");
		eject_call_option = sharedPreferences.getString(MySettings.EJECT_CALL_OPTION, "");
		eject_call_entry = sharedPreferences.getString(MySettings.EJECT_CALL_ENTRY, "");
		eq_call_option = sharedPreferences.getString(MySettings.EQ_CALL_OPTION, "");
		eq_call_entry = sharedPreferences.getString(MySettings.EQ_CALL_ENTRY, "");

		acc_on_call_option = sharedPreferences.getString(MySettings.ACC_ON_CALL_OPTION, "");
		acc_on_call_entry = sharedPreferences.getString(MySettings.ACC_ON_CALL_ENTRY, "");
		acc_off_call_option = sharedPreferences.getString(MySettings.ACC_OFF_CALL_OPTION, "");
		acc_off_call_entry = sharedPreferences.getString(MySettings.ACC_OFF_CALL_ENTRY, "");
		resume_call_option = sharedPreferences.getString(MySettings.RESUME_CALL_OPTION, "");
		resume_call_entry = sharedPreferences.getString(MySettings.RESUME_CALL_ENTRY, "");

		home_key_capture_enabled = sharedPreferences.getBoolean(MySettings.HOME_KEY_CAPTURE, true);
		home_call_option = sharedPreferences.getString(MySettings.HOME_CALL_OPTION, "");
		home_call_entry = sharedPreferences.getString(MySettings.HOME_CALL_ENTRY, "");
	}


	public static void init(Application app) {
		sAudioManager = (AudioManager) app.getSystemService("audio");
	}

	public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
		XposedBridge.log(TAG + " Loaded app: " + lpparam.packageName);

		if (!lpparam.packageName.equals("com.syu.ms")) return;

/**********************************************************************************************************************************************/
		/* This is the No Kill function */
		if (noKillEnabled == true) {
			findAndHookMethod("app.ToolkitApp", lpparam.classLoader, "killAppWhenSleep", new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
					Context context = (Context) AndroidAppHelper.currentApplication();
					XSharedPreferences sharedPreferences = new XSharedPreferences("org.hvdw.xsofiatweaker");
					sharedPreferences.makeWorldReadable();
					noKillEnabled = sharedPreferences.getBoolean(MySettings.PREF_NO_KILL, true);
					XposedBridge.log(TAG + " nokill enabled");
					param.setResult(null);
				}
			});
		} else {
			XposedBridge.log(TAG + " nokill disabled");
		}


		if ((skip_ch_four == true) || (pref_enable_usb_dac == true)) {
		/* skip_ch_four and enable_usb_dac work on the same function....  */

			/* This should prevent the mute of audio channel 4 (alarm) which is used by Google voice for voice feedback
			*  This seems like a must-do switch on setting, but when no other channel is used it will give noise, although
			*  you won't hear that with the engine on */
			if (skip_ch_four == true) {
				findAndHookMethod("app.ToolkitApp", lpparam.classLoader, "setStreamVol", int.class, int.class, new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
						int stream = (int) param.args[0];
						if (stream == 4) {
							Context context = (Context) AndroidAppHelper.currentApplication();
							XSharedPreferences sharedPreferences = new XSharedPreferences("org.hvdw.xsofiatweaker");
							sharedPreferences.makeWorldReadable();
							skip_ch_four = sharedPreferences.getBoolean(MySettings.PREF_SKIP_CH_FOUR, false);
							XposedBridge.log(TAG + " skipping alarm channel 4 mute");
							Log.d(TAG, " skipping alarm channel 4 mute");

							param.setResult(null);
						}
					}
				});
			}
			/* the skip_ch_four does one thing and then needs to skip the function
			/* the enable_usb_dac only needs to skip the function */
			if ((pref_enable_usb_dac == true) || (skip_ch_four == false)){
				findAndHookMethod("app.ToolkitApp", lpparam.classLoader, "setStreamVol", int.class, int.class, new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
						param.setResult(null);
					}
				});
			}
		}

		/* The enable_usb_dac skips/uses 4 functions. The one above and the 3 below */
		if (pref_enable_usb_dac == true) {
			findAndHookMethod("module.main.HandlerMain", lpparam.classLoader, "mcuKeyVolUp", new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
					setMediaVol(1);
					param.setResult(null);
				}
			});

			findAndHookMethod("module.main.HandlerMain", lpparam.classLoader, "mcuKeyVolDown", new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
					setMediaVol(-1);
					param.setResult(null);
				}
			});

			/* Note that the MUTE hardware button overrules this function */
			findAndHookMethod("module.main.HandlerMain", lpparam.classLoader, "mcuKeyVolMute", new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
					setMuteVol();
					param.setResult(null);
				}
			});
		}
/**********************************************************************************************************************************************/
		/* Below are the captured key functions */
		findAndHookMethod("app.HandlerApp", lpparam.classLoader, "wakeup", new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
				Context context = (Context) AndroidAppHelper.currentApplication();
				XSharedPreferences sharedPreferences = new XSharedPreferences("org.hvdw.xsofiatweaker");
				sharedPreferences.makeWorldReadable();
				resume_call_option = sharedPreferences.getString(MySettings.RESUME_CALL_OPTION, "");
				resume_call_entry = sharedPreferences.getString(MySettings.RESUME_CALL_ENTRY, "");
				XposedBridge.log(TAG + " Execute the RESUME action using specific call method");
				Log.d(TAG, "Execute the RESUME action using specific call method");
			}
		});


		if ((bt_phone_call_option != "") && (bt_phone_call_entry != "")) {
			findAndHookMethod("module.main.HandlerMain", lpparam.classLoader, "mcuKeyBtPhone", new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
					Context context = (Context) AndroidAppHelper.currentApplication();
					XSharedPreferences sharedPreferences = new XSharedPreferences("org.hvdw.xsofiatweaker");
					sharedPreferences.makeWorldReadable();
					bt_phone_call_option = sharedPreferences.getString(MySettings.BT_PHONE_CALL_OPTION, "");
					bt_phone_call_entry = sharedPreferences.getString(MySettings.BT_PHONE_CALL_ENTRY, "");
					XposedBridge.log(TAG + " mcuKeyBtPhone pressed; bt_phone_call_option: " + bt_phone_call_option + " bt_phone_call_entry : " + bt_phone_call_entry);
					whichActionToPerform(context, navi_call_option, navi_call_entry);

					param.setResult(null);
				}
			});
		}

		if ((navi_call_option != "") && (navi_call_entry != "")) {
			findAndHookMethod("module.main.HandlerMain", lpparam.classLoader, "mcuKeyNavi", new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
					Context context = (Context) AndroidAppHelper.currentApplication();
					XSharedPreferences sharedPreferences = new XSharedPreferences("org.hvdw.xsofiatweaker");
					sharedPreferences.makeWorldReadable();
					navi_call_option = sharedPreferences.getString(MySettings.NAVI_CALL_OPTION, "");
					navi_call_entry = sharedPreferences.getString(MySettings.NAVI_CALL_ENTRY, "");
					XposedBridge.log(TAG + " mcuKeyNavi  pressed; navi_call_option: " + navi_call_option + " navi_call_entry : " + navi_call_entry);
					whichActionToPerform(context, navi_call_option, navi_call_entry);

					param.setResult(null);
				}
			});
		}

		if ((band_call_option != "") && (band_call_entry != "")) {
			// band button = radio
			findAndHookMethod("module.main.HandlerMain", lpparam.classLoader, "mcuKeyBand", new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
					Context context = (Context) AndroidAppHelper.currentApplication();
					XSharedPreferences sharedPreferences = new XSharedPreferences("org.hvdw.xsofiatweaker");
					sharedPreferences.makeWorldReadable();
					band_call_option = sharedPreferences.getString(MySettings.BAND_CALL_OPTION, "");
					band_call_entry = sharedPreferences.getString(MySettings.BAND_CALL_ENTRY, "");
					XposedBridge.log(TAG + " mcuKeyBand (Radio) pressed; forward action to specific call method");
					Log.d(TAG, "mcuKeyBand (Radio) pressed; forward action to specific call method");
					XposedBridge.log(TAG + " band_call_option: " + band_call_option + " band_call_entry : " + band_call_entry);
					whichActionToPerform(context, band_call_option, band_call_entry);

					param.setResult(null);
				}
			});
		}

		if ((mode_src_call_option != "") && (mode_src_call_entry != "")) {
			findAndHookMethod("module.main.HandlerMain", lpparam.classLoader, "mcuKeyMode", new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
					Context context = (Context) AndroidAppHelper.currentApplication();
					XSharedPreferences sharedPreferences = new XSharedPreferences("org.hvdw.xsofiatweaker");
					sharedPreferences.makeWorldReadable();
					mode_src_call_option = sharedPreferences.getString(MySettings.MODE_SRC_CALL_OPTION, "");
					mode_src_call_entry = sharedPreferences.getString(MySettings.MODE_SRC_CALL_ENTRY, "");
					XposedBridge.log(TAG + " Source/Mode pressed; forward action  to specific call method");
					Log.d(TAG, "Source/Mode pressed; forward action  to specific call method");
					whichActionToPerform(context, mode_src_call_option, mode_src_call_entry);

					param.setResult(null);
				}
			});
		}

		if ((media_call_option != "") && (media_call_entry != "")) {
			findAndHookMethod("module.main.HandlerMain", lpparam.classLoader, "mcuKeyPlayer", new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
					Context context = (Context) AndroidAppHelper.currentApplication();
					XSharedPreferences sharedPreferences = new XSharedPreferences("org.hvdw.xsofiatweaker");
					sharedPreferences.makeWorldReadable();
					media_call_option = sharedPreferences.getString(MySettings.MEDIA_CALL_OPTION, "");
					media_call_entry = sharedPreferences.getString(MySettings.MEDIA_CALL_ENTRY, "");
					XposedBridge.log(TAG + " MEDIA button pressed; forward action to specific call method");
					Log.d(TAG, "MEDIA button pressed; forward action to specific call method");
					whichActionToPerform(context, media_call_option, media_call_entry);

					param.setResult(null);
				}
			});
		}

		if ((eq_call_option != "") && (eq_call_entry != "")) {
			findAndHookMethod("util.JumpPage", lpparam.classLoader, "eq", new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
					Context context = (Context) AndroidAppHelper.currentApplication();
					XSharedPreferences sharedPreferences = new XSharedPreferences("org.hvdw.xsofiatweaker");
					sharedPreferences.makeWorldReadable();
					eq_call_option = sharedPreferences.getString(MySettings.EQ_CALL_OPTION, "");
					eq_call_entry = sharedPreferences.getString(MySettings.EQ_CALL_ENTRY, "");
					XposedBridge.log(TAG + " EQ button pressed; forward action  to specific call method");
					Log.d(TAG, "EQ button pressed; forward action  to specific call method");
					whichActionToPerform(context, eq_call_option, eq_call_entry);

					param.setResult(null);
				}
			});
		}

		findAndHookMethod("dev.ReceiverMcu", lpparam.classLoader, "onHandle", byte[].class, int.class, int.class, new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
				Context context = (Context) AndroidAppHelper.currentApplication();
				//byte[] data  = getByteField(param.thisObject, "byte[].class");
				byte[] data =  (byte[]) param.args[0];
				/* int start = getIntField(param.thisObject, "start");
				int length = getIntField(param.thisObject, "length"); */
				int start = (int) param.args[1];
				int length = (int) param.args[2];
				byte b = data[start];

				XSharedPreferences sharedPreferences = new XSharedPreferences("org.hvdw.xsofiatweaker");
				sharedPreferences.makeWorldReadable();

				//Log.d(TAG, "DVD or eject button; Executed the Media action to the launcher.sh");
				if ((b & 255) == 1 && (data[start + 1] & 255) == 0 && (data[start + 2] & 255) == 16 && (data[start + 3] & 255) == 80) {
					dvd_call_option = sharedPreferences.getString(MySettings.DVD_CALL_OPTION, "");
					dvd_call_entry = sharedPreferences.getString(MySettings.DVD_CALL_ENTRY, "");
					XposedBridge.log(TAG + " DVD button pressed; forward action to specific call method");
					Log.d(TAG, "DVD button pressed; forward action to specific call method");
					whichActionToPerform(context, dvd_call_option, dvd_call_entry);
				}
				if ((b & 255) == 1 && (data[start + 1] & 255) == 161 && (data[start + 2] & 255) == 2 && (data[start + 3] & 255) == 91) {
					eject_call_option = sharedPreferences.getString(MySettings.EJECT_CALL_OPTION, "");
					eject_call_entry = sharedPreferences.getString(MySettings.EJECT_CALL_ENTRY, "");
					XposedBridge.log(TAG + " EJECT button pressed; forward action to specific call method");
					Log.d(TAG, "EJECT button pressed; forward action to specific call method");
					whichActionToPerform(context, eject_call_option, eject_call_entry);
				}
			}
		});

		findAndHookMethod("util.JumpPage", lpparam.classLoader, "broadcastByIntentName", String.class, new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
				String actionName = (String) param.args[0];
				Context context = (Context) AndroidAppHelper.currentApplication();
				XSharedPreferences sharedPreferences = new XSharedPreferences("org.hvdw.xsofiatweaker");
				sharedPreferences.makeWorldReadable();
				XposedBridge.log(TAG + " broadcastByIntentName in util.JumpPage afterHooked " + actionName);
				Log.d(TAG, "broadcastByIntentName in util.JumpPage afterHooked " + actionName);
				if (actionName == "com.glsx.boot.ACCON") {
					acc_on_call_option = sharedPreferences.getString(MySettings.ACC_ON_CALL_OPTION, "");
					acc_on_call_entry = sharedPreferences.getString(MySettings.ACC_ON_CALL_ENTRY, "");
					Log.d(TAG, "ACC_ON command received");
					XposedBridge.log(TAG + " ACC_ON command received");
					whichActionToPerform(context, acc_on_call_option, acc_on_call_entry);
				}
				if (actionName == "com.glsx.boot.ACCOFF") {
					acc_off_call_option = sharedPreferences.getString(MySettings.ACC_OFF_CALL_OPTION, "");
					acc_off_call_entry = sharedPreferences.getString(MySettings.ACC_OFF_CALL_ENTRY, "");
					Log.d(TAG, "ACC_OFF command received");
					XposedBridge.log(TAG + " ACC_OFF command received");
					whichActionToPerform(context, acc_off_call_option, acc_off_call_entry);
				}
			}
		});

		if (home_key_capture_enabled == true) {
			findAndHookMethod("app.ToolkitApp", lpparam.classLoader, "keyHome", new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
					Context context = (Context) AndroidAppHelper.currentApplication();
					XSharedPreferences sharedPreferences = new XSharedPreferences("org.hvdw.xsofiatweaker");
					sharedPreferences.makeWorldReadable();
					home_call_option = sharedPreferences.getString(MySettings.HOME_CALL_OPTION, "");
					home_call_entry = sharedPreferences.getString(MySettings.HOME_CALL_ENTRY, "");
					XposedBridge.log(TAG + " HOME button pressed; forward action  to the launcher.sh");
					Log.d(TAG, "HOME button pressed; forward action  to the launcher.sh");
					//executeSystemCall("am start -a android.intent.action.MAIN -c android.intent.category.HOME");
					whichActionToPerform(context, home_call_option, home_call_entry);
					param.setResult(null);
				}
			});
		}

/*		findAndHookMethod("app.ToolkitApp", lpparam.classLoader, "keyBack", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
				XposedBridge.log(TAG + " BACK button pressed; forward action  to the launcher.sh");
				Log.d(TAG, "BACK button pressed; forward action  to the launcher.sh");
				onItemSelectedp(4);
				param.setResult(null);
			}
		}); */

	}
/**********************************************************************************************************************************************/
/* End of the handleLoadPackage function doing the capture key functions */
/**********************************************************************************************************************************************/
/* Below the functions that take care of the actions to performa after a button has been pressed. */

	private static void onItemSelectedp(int input) {
		StringBuffer output = new StringBuffer();
		String cmd = "/data/launcher.sh " + input + " ";
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			Log.d("MCUKEY", cmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	};

	public void whichActionToPerform (Context context, String callMethod, String actionString) {
		XposedBridge.log(TAG + " WhichActionToPerform: Call method: " + callMethod + " actionString: " + actionString);
		if (callMethod.equals("pkgname")) {
			//Log.d(TAG, " the callmethond is indeed pkgname");
			startActivityByPackageName(context, actionString);
		}
		if (callMethod.equals("pkg_intent")) {
			startActivityByIntentName(context, actionString);
		}
		if (callMethod.equals("sys_call")) {
			executeSystemCall(actionString);
		}
		if (callMethod.equals("broad_cast")) {
			executeBroadcast(actionString);
		}
	};


	private static void executeSystemCall(String input) {
		StringBuffer output = new StringBuffer();
		String cmd = input;
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			Log.d(TAG, cmd);
			XposedBridge.log(TAG + ": " + cmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	};

	private static void executeBroadcast(String input) {
		StringBuffer output = new StringBuffer();
		String cmd = "am broadcast -a " + input;
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			Log.d(TAG, cmd);
			XposedBridge.log(TAG + ": " + cmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	};

	public void startActivityByIntentName(Context context, String component) {
		Intent sIntent = new Intent(Intent.ACTION_MAIN);
		sIntent.setComponent(ComponentName.unflattenFromString(component));
		sIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		sIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(sIntent);
	}


	public void startActivityByPackageName(Context context, String packageName) {
		PackageManager pManager = context.getPackageManager();
		Intent intent = pManager.getLaunchIntentForPackage(packageName);
		XposedBridge.log(TAG + " startActivityByPackageName: " + packageName);
		if (intent != null) {
			context.startActivity(intent);
		}
	}
/**********************************************************************************************************************************************/
/*  Functions above are for the hardware buttons */
/**********************************************************************************************************************************************/
/*  Functions below are for the usbdac Volume control */
/* Top one is a modified call. The seconda completely new type of function */

	public static AudioManager getAudioManager() {
		return sAudioManager;
	}


	public static void setMediaVol(int vol) {
		AudioManager am = getAudioManager();
		am.setStreamVolume(3, am.getStreamVolume(3) + vol, 1);
		//return;
	}
	public static void setMuteVol() {
		AudioManager am = getAudioManager();
		am.adjustStreamVolume(0, 101, 0);
		am.adjustStreamVolume(1, 101, 0);
		am.adjustStreamVolume(2, 101, 0);
		am.adjustStreamVolume(3, 101, 0);
		am.adjustStreamVolume(4, 101, 0);
		am.adjustStreamVolume(5, 101, 0);
		am.adjustStreamVolume(6, 101, 0);
		//return;
	}

/* End of it all *****************************************************************************************************************************/
}

package com.swatiee;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
////import android.net.wifi.WifiManager;
//import android.net.wifi.WifiManager.WifiLock;
import android.os.PowerManager;


abstract public class WakefulIntentService extends IntentService {
	abstract void doWakefulWork(Intent intent);

	public static final String LOCK_NAME_STATIC="com.commonsware.android.syssvc.AppService.Static";
	private static PowerManager.WakeLock lockStatic=null;

	public static void acquireStaticLock(Context context) {
		getLock(context).acquire();
	}

	
//	public static void acquireWifiLock(Context context){
//		WifiManager mainWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//		WifiLock wifilock = mainWifi.createWifiLock("wifi.smartNote");
//		if(!wifilock.isHeld()){
//			wifilock.acquire();
//		}
//	}
	
	synchronized  static PowerManager.WakeLock getLock(Context context) {
		if (lockStatic==null) {
			PowerManager mgr=(PowerManager)context.getSystemService(Context.POWER_SERVICE);
			lockStatic=mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME_STATIC);
			lockStatic.setReferenceCounted(true);
		}
		return(lockStatic);
	}

	public WakefulIntentService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			doWakefulWork(intent);
		}
		finally {
			if (getLock(this).isHeld()){
				getLock(this).release();
			}
		}
	}
}

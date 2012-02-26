package com.swatiee;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;


abstract public class WakefulIntentService2 extends IntentService {
	abstract void doWakefulWork2(Intent intent);

	public static final String LOCK_NAME_STATIC2="com.commonsware.android.syssvc.AppService.Static2";
	private static PowerManager.WakeLock lockStatic2=null;

	public static void acquireStaticLock2(Context context) {
		//getLock(context).acquire(Constants.HALF_MINUTE*3);
		getLock2(context).acquire();
	}

	synchronized  static PowerManager.WakeLock getLock2(Context context) {
		if (lockStatic2==null) {
			PowerManager mgr=(PowerManager)context.getSystemService(Context.POWER_SERVICE);
			lockStatic2=mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME_STATIC2);
			lockStatic2.setReferenceCounted(true);
		}
		return(lockStatic2);
	}

	public WakefulIntentService2(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			doWakefulWork2(intent);
		}
		finally {
//			if (getLock2(this).isHeld()){
//				getLock2(this).release();
//			}
		}
	}
}

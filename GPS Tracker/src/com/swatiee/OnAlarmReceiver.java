package com.swatiee;

import com.swatiee.AppService;
import com.swatiee.WakefulIntentService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnAlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		WakefulIntentService.acquireStaticLock(context);
		//WakefulIntentService.acquireWifiLock(context);
		
		context.startService(new Intent(context, AppService.class));
	}
}

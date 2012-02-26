package com.swatiee;

import com.swatiee.OnAlarmReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnBootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//Todo - Need to see if any tasks in the list before starting alarm!!
		AlarmManager mgr=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		
		Intent i=new Intent(context, OnAlarmReceiver.class);
		PendingIntent pi=PendingIntent.getBroadcast(context, 0, i, 0);
		mgr.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+Constants.ONE_MINUTE,pi);
				
				
	}
}

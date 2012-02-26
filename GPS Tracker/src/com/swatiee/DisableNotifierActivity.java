package com.swatiee;

import com.swatiee.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckedTextView;


public class DisableNotifierActivity extends Activity {
	private CheckedTextView chkedTextView;
	public static final String PREFS_NAME = "SmartNotifierPrefs";
	public static final String PREFS_DISABLE_NOTIFIER = "DisableNotifier";


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_disable_notifier);
		setUpViews();
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		Boolean isChecked = settings.getBoolean(SettingsActivity.PREFS_DISABLE_NOTIFIER, false);
		Log.d("DisableNotifierActivity", "onCreate - isChecked-"+isChecked);
		chkedTextView.setChecked(isChecked);
	}

	public void onCheckmarkToggle(View v) {        	
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		((CheckedTextView) v).toggle();
		Log.d("SettingsActivity", "((CheckedTextView) v).isChecked()"+((CheckedTextView) v).isChecked());
		//Todo  update shared preference. & check it in "OnReboot" & "Add Task Activity" ie before setting any alarm!
		if(((CheckedTextView) v).isChecked()){
			((CheckedTextView) v).setText(R.string.enable_notifications);
			editor.putBoolean(PREFS_DISABLE_NOTIFIER, true);
			editor.commit();
			removeNotifications();

		}else{
			((CheckedTextView) v).setText(R.string.disable_notifications);
			editor.putBoolean(PREFS_DISABLE_NOTIFIER, false);
			editor.commit();
			startNotifications();
		}
	}

	private void removeNotifications() {
		Log.d("Settings Activity", "removeNotifications");

		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Intent it1=new Intent(DisableNotifierActivity.this, OnLocationReceiverService.class);
		PendingIntent pi1=PendingIntent.getService(DisableNotifierActivity.this, 0, it1, PendingIntent.FLAG_CANCEL_CURRENT);	
		locationManager.removeUpdates(pi1);	
		pi1.cancel();
		Log.d("Settings Activity", "removed Location Pending alert");

		AlarmManager alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
		Intent i=new Intent(DisableNotifierActivity.this, OnAlarmReceiver.class);
		PendingIntent pi=PendingIntent.getBroadcast(DisableNotifierActivity.this, 0, i, 0);
		alarmManager.cancel(pi);
		Log.d("Settings Activity", "removed Alarm Pending alert");

	}

	private void startNotifications() {
		Log.d("Settings Activity", "startNotifications - starting AppService");
		Intent i=new Intent(DisableNotifierActivity.this, AppService.class);
		startService(i);
	}	    

	private void setUpViews() {
		chkedTextView = (CheckedTextView) findViewById(R.id.disable_notifications);
	}	    

}

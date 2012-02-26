package com.swatiee;

import com.swatiee.R;
import com.swatiee.util.GenericUtil;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.SeekBar;

public class SettingsActivity extends Activity {
	public static final String PREFS_NAME = "SmartNotifierPrefs";
	public static final String PREFS_EMAIL = "SmartNotifierEMail";
	public static final String PREFS_PWD = "SmartNotifierPwd";
	public static final String PREFS_DISABLE_NOTIFIER = "DisableNotifier";
	private CheckedTextView chkedTextView;
	private EditText emailEditText;
	private EditText pwdEditText;
	
	public void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);
		        setContentView(R.layout.settings);
		        setUpViews();
		        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		        String email = settings.getString(SettingsActivity.PREFS_EMAIL, "");
		        String pwd = GenericUtil.decryptPwd(settings.getString(SettingsActivity.PREFS_PWD, ""));
		        emailEditText.setText(email);
		        pwdEditText.setText(pwd);
		        Boolean isChecked = settings.getBoolean(SettingsActivity.PREFS_DISABLE_NOTIFIER, false);
		        Log.d("SettingsAct", "onCreate - isChecked-"+isChecked);
		        chkedTextView.setChecked(isChecked);

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    //super.onCreateOptionsMenu(menu);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.settings, menu);
	    return true;
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
		Intent it1=new Intent(SettingsActivity.this, OnLocationReceiverService.class);
		PendingIntent pi1=PendingIntent.getService(SettingsActivity.this, 0, it1, PendingIntent.FLAG_CANCEL_CURRENT);	
		locationManager.removeUpdates(pi1);	
		pi1.cancel();
		Log.d("Settings Activity", "removed Location Pending alert");
		
		AlarmManager alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
		Intent i=new Intent(SettingsActivity.this, OnAlarmReceiver.class);
		PendingIntent pi=PendingIntent.getBroadcast(SettingsActivity.this, 0, i, 0);
		alarmManager.cancel(pi);
		Log.d("Settings Activity", "removed Alarm Pending alert");

	}

	private void startNotifications() {
		Log.d("Settings Activity", "startNotifications - starting AppService");
		Intent i=new Intent(SettingsActivity.this, AppService.class);
		startService(i);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.view_tasks_menu:
			Intent intent = new Intent(SettingsActivity.this, ViewTasksAct.class);
			startActivity(intent);
	        return true;
	    case R.id.add_task_menu:
			Intent intent1 = new Intent(SettingsActivity.this, AddTaskActivity.class);
			startActivity(intent1);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}	

	private void setUpViews() {
		chkedTextView = (CheckedTextView) findViewById(R.id.disable_notifications);
		
		emailEditText = (EditText)findViewById(R.id.edit_email_address);
		pwdEditText = (EditText)findViewById(R.id.edit_password);
	}

	public void saveSettings(View v) {
		       SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		        SharedPreferences.Editor editor = settings.edit();
		        editor.putString(PREFS_EMAIL, emailEditText.getText().toString());
		        editor.putString(PREFS_PWD, GenericUtil.encryptPwd(pwdEditText.getText().toString()));		        
		        editor.commit();
		        finish();
	}
}

package com.swatiee;

import com.swatiee.R;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;

public class SettingsTabMainActivity extends TabActivity {
	public void onCreate(Bundle savedInstanceState) {
		    super.onCreate(savedInstanceState);
		    setContentView(R.layout.settings_tab_main);

		    Resources res = getResources(); // Resource object to get Drawables
		    TabHost tabHost = getTabHost();  // The activity TabHost
		    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
		    Intent intent;  // Reusable Intent for each tab

		    // Create an Intent to launch an Activity for the tab (to be reused)
		    intent = new Intent().setClass(this, DisableNotifierActivity.class);

		    // Initialize a TabSpec for each tab and add it to the TabHost
		    spec = tabHost.newTabSpec("On/Off notifier").setIndicator("On/Off Notifier",
		                      res.getDrawable(R.drawable.icon_tab_disable_notifier))
		                  .setContent(intent);
		    tabHost.addTab(spec);

		    // Do the same for the other tabs
		    intent = new Intent().setClass(this, EmailSettingsActivity.class);
		    spec = tabHost.newTabSpec("Email Settings").setIndicator("Email Settings",
		                      res.getDrawable(R.drawable.icon_tab_email_settings))
		                  .setContent(intent);
		    tabHost.addTab(spec);

		    tabHost.setCurrentTab(1);
		}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    //super.onCreateOptionsMenu(menu);

	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.settings, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
		case R.id.view_tasks_menu:
			Intent intent2 = new Intent(SettingsTabMainActivity.this, ViewTasksAct.class);
			startActivity(intent2);
			return true;
	    case R.id.add_task_menu:
			Intent intent = new Intent(SettingsTabMainActivity.this, AddTaskActivity.class);
			startActivity(intent);
	        return true;
		case R.id.notification_history_menu:
			Intent intent1 = new Intent(SettingsTabMainActivity.this, NotificationListActivity.class);
			startActivity(intent1);
			return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}	
}

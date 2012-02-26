package com.swatiee;

import com.swatiee.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class NotificationListActivity extends ListActivity {
	private NotificationListAdapter adapter;
	private SmartNotifierApplication app;
	private Button deleteNotifications;
	private AlertDialog deleteConfirmDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificationlist);
        setupViews();
        app = (SmartNotifierApplication)getApplication();
        adapter = new NotificationListAdapter(this, app.getCurrentNotifications());
        setListAdapter(adapter);
    }
    
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    //super.onCreateOptionsMenu(menu);

	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.noitification_history, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
		case R.id.view_tasks_menu:
			Intent intent2 = new Intent(NotificationListActivity.this, ViewTasksAct.class);
			startActivity(intent2);
			return true;
	    case R.id.add_task_menu:
			Intent intent = new Intent(NotificationListActivity.this, AddTaskActivity.class);
			startActivity(intent);
	        return true;
		case R.id.settings_menu:
			Intent intent1 = new Intent(NotificationListActivity.this, SettingsTabMainActivity.class);
			startActivity(intent1);
			return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
    
    
	private void setupViews() {
		deleteNotifications = (Button)findViewById(R.id.disable_notifications);
	}

	public void onDeleteClicked(View v){
        app = (SmartNotifierApplication)getApplication();
        if (app.getCurrentNotifications()!=null && app.getCurrentNotifications().size()>0){
			deleteConfirmDialog = new AlertDialog.Builder(this).setMessage(
					R.string.delete_confirm_message).setPositiveButton(R.string.yes, new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
					        app.deleteNotifications();
							Toast.makeText(NotificationListActivity.this,"All notifications deleted successfully",Toast.LENGTH_LONG).show();
					        finish();
						}
					}).setNegativeButton(R.string.no, new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							deleteConfirmDialog.cancel();
						}
					}).create();
			deleteConfirmDialog.show();        	
        }
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		adapter.forceReload();
	}	
}

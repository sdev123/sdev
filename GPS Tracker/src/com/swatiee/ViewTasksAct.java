package com.swatiee;

import com.swatiee.R;
import com.swatiee.SmartNotifierApplication;
import com.swatiee.Task;
import com.swatiee.TaskListAdapter;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class ViewTasksAct extends ListActivity {
    /** Called when the activity is first created. */
	private TaskListAdapter adapter;
	private SmartNotifierApplication app;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setUpViews();
        app = (SmartNotifierApplication)getApplication();
        adapter = new TaskListAdapter(this, app.getCurrentTasks());
        setListAdapter(adapter);
    }
    
	@Override
	protected void onResume() {
		super.onResume();
		adapter.forceReload();
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    //super.onCreateOptionsMenu(menu);

	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.view_tasks, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.add_task_menu:
			Intent intent = new Intent(ViewTasksAct.this, AddTaskActivity.class);
			startActivity(intent);
	        return true;
		case R.id.settings_menu:
			Intent intent1 = new Intent(ViewTasksAct.this, SettingsTabMainActivity.class);
			startActivity(intent1);
			return true;
		case R.id.notification_history_menu:
			Intent intent2 = new Intent(ViewTasksAct.this, NotificationListActivity.class);
			startActivity(intent2);
			return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Task t = adapter.getItem(position);
		Intent intent = new Intent(ViewTasksAct.this, AddTaskActivity.class);
		
		Bundle b = new Bundle();
                b.putLong(TasksSQLiteOpenHelper.TASK_ID, t.getId());
                intent.putExtras(b);
		startActivity(intent);
		
	}
	
	protected void removeCompletedTasks() {
		Long[] ids = adapter.removeCompletedTasks();
		app.deleteTasks(ids);
	}
	
	private void setUpViews() {
	}    
}
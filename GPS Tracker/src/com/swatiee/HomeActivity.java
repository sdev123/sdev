package com.swatiee;

import com.swatiee.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		//setUpViews();
	}

	public void onPause(){
		super.onPause();
	}

	public void onDestroy(){
		super.onDestroy();
	}

	public void onResume(){
		super.onRestart();
	}

	public void onViewTasksBtnClick(View view){
	    Intent i = new Intent(HomeActivity.this, ViewTasksAct.class);
	    startActivity(i);
	}
	
	public void onAddTaskBtnClick(View view){
	    Intent i = new Intent(HomeActivity.this, AddTaskActivity.class);
	    startActivity(i);
	}

	public void onSettingBtnClick(View view){
	    Intent i = new Intent(HomeActivity.this, SettingsTabMainActivity.class);
	    startActivity(i);
	}
	
	public void onNoteHistoryBtnClick(View view){
	    Intent i = new Intent(HomeActivity.this, NotificationListActivity.class);
	    startActivity(i);
	}
	
	public void onAboutUsBtnClick(View view){
	    Intent i = new Intent(HomeActivity.this, NotificationListActivity.class);
	    startActivity(i);
	}

}

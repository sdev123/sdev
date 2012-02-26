package com.swatiee;

import com.swatiee.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenuActivity extends Activity {
	private Button viewList;
	private Button addTaskAtAddress;
	private Button settings;
	
    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.mainmenu);
	        setUpViews();
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
    
    public void onClickNewSettings(View v){
	    Intent i = new Intent(MainMenuActivity.this, SettingsTabMainActivity.class);
	    startActivity(i);
    }

	private void setUpViews() {
		// TODO Auto-generated method stub
		viewList = (Button)findViewById(R.id.view_list);
		addTaskAtAddress = (Button)findViewById(R.id.add_at_address);
		settings = (Button)findViewById(R.id.settings);
		viewList.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainMenuActivity.this, ViewTasksAct.class);
				startActivity(intent);

			}
		});

		addTaskAtAddress.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainMenuActivity.this, AddTaskActivity.class);
				startActivity(intent);
			}
		});

		settings.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainMenuActivity.this, SettingsActivity.class);
				startActivity(intent);
			}
		});
		
	}

}

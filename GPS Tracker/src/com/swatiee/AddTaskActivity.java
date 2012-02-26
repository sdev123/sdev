package com.swatiee;
// Todos remaining -
// should I change to GPS (FINE accuracy instead of course.. need research on what happens when gps is unavailable)
// added menu
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.swatiee.R;
import com.swatiee.util.GenericUtil;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddTaskActivity extends MapActivity {
	private TextView textTitle;
	private EditText taskNameEditText;
	private EditText taskAddressEditText;
	private EditText taskEmailEditText;
	private EditText taskPhoneEditText;
	private Button addButton;
	private Button cancelButton;
	private Button removeButton;
	private AlertDialog unsavedChangesDialog;
	private long taskId = 0L;
	private Button mapButton;
	private MapView mapView;
	private ConcurrentHashMap<String, Address> addressToGeoPointMap = new ConcurrentHashMap<String, Address>();

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.add_task, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.view_tasks_menu:
			Intent intent = new Intent(AddTaskActivity.this, ViewTasksAct.class);
			startActivity(intent);
			return true;
		case R.id.settings_menu:
			Intent intent1 = new Intent(AddTaskActivity.this, SettingsTabMainActivity.class);
			startActivity(intent1);
			return true;
		case R.id.notification_history_menu:
			Intent intent2 = new Intent(AddTaskActivity.this, NotificationListActivity.class);
			startActivity(intent2);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_task);
		setUpViews();
		long id = 0L;
		Bundle b = this.getIntent().getExtras();
		if (b != null) {
			id = b.getLong(TasksSQLiteOpenHelper.TASK_ID);
		}
		if (id != 0L) {
			// Edit existing Task
			Task t = ((SmartNotifierApplication) getApplication()).getTaskById(id);
			taskNameEditText.setText(t.getName());
			taskAddressEditText.setText(t.getAddress());
			taskEmailEditText.setText(t.getEmail());
			taskPhoneEditText.setText(t.getPhone());
			taskId = id;
			addButton.setText(R.string.edit_task);
			textTitle.setText("Update Task");
		} else {
			// Add new task
			// No remove button
			removeButton.setVisibility(View.GONE);
			textTitle.setText("Add Task");

			int numberOfTasks = ((SmartNotifierApplication) getApplication()).getCurrentTasks().size();
			if(Constants.isProVersion){
				if (numberOfTasks>=Constants.MAX_NUMBER_OF_TASKS_ALLOWED_FULL){
					unsavedChangesDialog = new AlertDialog.Builder(this).setMessage(
							R.string.maxout_msg_pro).setNeutralButton(R.string.okay, new AlertDialog.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									finish();
								}
							}).setCancelable(false).create();
					unsavedChangesDialog.show();
				}
			}else{
				if (numberOfTasks>=Constants.MAX_NUMBER_OF_TASKS_ALLOWED_LITE){
					unsavedChangesDialog = new AlertDialog.Builder(this).setMessage(
							R.string.maxout_msg).setNeutralButton(R.string.okay, new AlertDialog.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									finish();
								}
							}).setPositiveButton(R.string.get_full_version, new AlertDialog.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									Uri uri = Uri.parse("market://details?id=com.location.notifier");  
									Intent i = new Intent(Intent.ACTION_VIEW, uri);
									startActivity(i);
									finish();
								}
							}).setCancelable(false).create();
					unsavedChangesDialog.show();
				}
			}
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		mapView.setVisibility(View.INVISIBLE);

		/*
		 * if (null == address) {
		 * addLocationButton.setVisibility(View.VISIBLE);
		 * addressText.setVisibility(View.GONE); } else {
		 * addLocationButton.setVisibility(View.GONE);
		 * addressText.setVisibility(View.VISIBLE);
		 * addressText.setText(address.getAddressLine(0)); }
		 */}

	protected void addOrUpdateTask() {
		if (taskId != 0) {
			updateTask();
		} else {

			int numberOfTasks = ((SmartNotifierApplication) getApplication()).getCurrentTasks().size();
			if (numberOfTasks>=Constants.MAX_NUMBER_OF_TASKS_ALLOWED_FULL){
				unsavedChangesDialog = new AlertDialog.Builder(this).setMessage(
						R.string.maxout_msg_pro).setNeutralButton(R.string.okay, new AlertDialog.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								finish();
							}
						}).setCancelable(false).create();
				unsavedChangesDialog.show();
			}else{
				addTask();
			}
		}
	}

	protected void viewAddressOnMap() {
		if(!GenericUtil.isBlank(taskAddressEditText.getText().toString())){
			mapView.setVisibility(View.VISIBLE);
			//hide soft keyboard if open when viewMap button is clicked. 
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mapButton.getWindowToken(), 0); 
			new ViewMapTask(this).execute(taskAddressEditText.getText().toString());
		}else{
			unsavedChangesDialog = new AlertDialog.Builder(this).setTitle(
			"Please enter address first.").setNeutralButton(R.string.okay, new AlertDialog.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					unsavedChangesDialog.cancel();
				}
			}).create();
			unsavedChangesDialog.show();

		}

	}	

	protected void removeTask() {

		unsavedChangesDialog = new AlertDialog.Builder(this).setTitle(
				R.string.confirm_remove).setPositiveButton(R.string.yes, new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						((SmartNotifierApplication) getApplication()).removeTask(taskId);
						finish();
					}
				}).setNegativeButton(R.string.no, new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						unsavedChangesDialog.cancel();
					}
				}).create();
		unsavedChangesDialog.show();
	}

	protected void addTask() {
		String taskName = taskNameEditText.getText().toString();
		String taskAddress = taskAddressEditText.getText().toString();
		String taskEmail = taskEmailEditText.getText().toString();
		String taskPhone = taskPhoneEditText.getText().toString();
		if(validateInput(taskName, taskAddress, taskEmail, taskPhone)){
			Task t1 = new Task(taskName, taskAddress, taskEmail, taskPhone);
			new GetGeoCodeTask(this).execute(t1);
		}
	}

	private boolean validateInput(String taskName, String taskAddress,
			String taskEmail, String taskPhone) {
		if (GenericUtil.isBlank(taskName)){
			unsavedChangesDialog = new AlertDialog.Builder(this).setMessage(
					R.string.task_name_cannot_be_blank).setNeutralButton(R.string.okay, new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							unsavedChangesDialog.cancel();
						}
					}).create();
			unsavedChangesDialog.show();				
		}else if (GenericUtil.isBlank(taskAddress)){
			unsavedChangesDialog = new AlertDialog.Builder(this).setMessage(
					R.string.task_address_cannot_be_blank).setNeutralButton(R.string.okay, new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							unsavedChangesDialog.cancel();
						}
					}).create();
			unsavedChangesDialog.show();				
		}else if (GenericUtil.isBlank(taskEmail) && GenericUtil.isBlank(taskPhone)){
			unsavedChangesDialog = new AlertDialog.Builder(this).setMessage(
					R.string.both_email_and_phone_cannot_be_blank).setNeutralButton(R.string.okay, new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							unsavedChangesDialog.cancel();
						}
					}).create();
			unsavedChangesDialog.show();				
		}else{
			return true;
		}
		return false;
	}

	protected void updateTask() {
		String taskName = taskNameEditText.getText().toString();
		String taskAddress = taskAddressEditText.getText().toString();
		String taskEmail = taskEmailEditText.getText().toString();
		String taskPhone = taskPhoneEditText.getText().toString();
		if(validateInput(taskName, taskAddress, taskEmail, taskPhone)){
			Task t1 = ((SmartNotifierApplication)getApplication()).getTaskById(taskId);
			t1.setName(taskName);
			t1.setEmail(taskEmail);
			t1.setPhone(taskPhone);
			if (!t1.getAddress().equalsIgnoreCase(taskAddress)){
				t1.setAddress(taskAddress);
				new GetGeoCodeTask(this).execute(t1);
			}else{
				((SmartNotifierApplication) getApplication()).updateTask(t1);	
				finish();
			}
		}
	}

	private class ViewMapTask extends AsyncTask<String, String, Address> { ///TODOOOOOOO
		private final ProgressDialog dialog = new ProgressDialog(AddTaskActivity.this);
		private Context context;

		public ViewMapTask(Context context) {
			super();
			this.context = context;
		}

		// can use UI thread here
		protected void onPreExecute() {
			this.dialog.setMessage("Getting Location...");
			this.dialog.show();
		}

		@Override
		protected Address doInBackground(String... params) {
			Geocoder g = new Geocoder(context);
			return getAddressFromGoogleGeoCoderAPI(params[0], g);
		}

		@Override
		protected void onPostExecute(Address a) {
			this.dialog.dismiss();
			if (a != null) {
				Log.d("onPostExecute", "Task - " + a.toString());
				mapView.invalidate();
				final MapController mapController = mapView.getController();
				mapController.setZoom(15);
				List<Overlay> mapOverlays = mapView.getOverlays();
				Drawable drawable = context.getResources().getDrawable(R.drawable.marker);
				AddressOverlay itemizedoverlay = new AddressOverlay(drawable, AddTaskActivity.this);
				Log.d("AddTaskActivity - ViewMapTask.postExecute -", "Lat- "+a.getLatitude()+" Lng- "+a.getLongitude());
				GeoPoint point = new GeoPoint((int)(a.getLatitude()*1E6),(int)(a.getLongitude()*1E6));
				mapController.animateTo(point);
				OverlayItem overlayitem = new OverlayItem(point, "", a.getAddressLine(0));
				itemizedoverlay.addOverlay(overlayitem);
				mapOverlays.add(itemizedoverlay);

			} else {
				unsavedChangesDialog = new AlertDialog.Builder(context).setMessage(
						R.string.add_fail_msg).setNeutralButton(R.string.okay, new AlertDialog.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								unsavedChangesDialog.cancel();
							}
						}).create();
				unsavedChangesDialog.show();				
			}
		}
	}



	private class GetGeoCodeTask extends AsyncTask<Task, String, Task> {
		private final ProgressDialog dialog = new ProgressDialog(AddTaskActivity.this);
		private Context context;

		public GetGeoCodeTask(Context context) {
			super();
			this.context = context;
		}

		// can use UI thread here
		protected void onPreExecute() {
			this.dialog.setMessage("Adding Task...");
			this.dialog.show();
		}

		@Override
		protected Task doInBackground(Task... params) {
			Task t = getGeoAddress(context, params);
			if (t != null && t.getLatitude() != 0 && t.getLongitude() != 0) {
				t.setAddress(taskAddressEditText.getText().toString());
				if(t.getId()==0){ // add task
					t = ((SmartNotifierApplication) getApplication()).addTask(t);
					manageAlarmService(context);
				}else{
					Log.d("AddTaskActivity", "Before Update Task-"+t.toString());
					t = ((SmartNotifierApplication) getApplication()).updateTask(t);
					Log.d("AddTaskActivity", "After Update Task-"+t.toString());

					manageAlarmService(context);
				}
			}
			return t;
		}

		@Override
		protected void onPostExecute(Task t) {
			Log.d("onPostExecute", "Task - " + t.toString());
			this.dialog.dismiss();
			if (t != null && t.getLatitude() != 0 && t.getLongitude() != 0) {
				Toast.makeText(context,"Task "+t.getName()+" saved successfully",Toast.LENGTH_LONG).show();
				finish();
			} else {
				unsavedChangesDialog = new AlertDialog.Builder(context).setMessage(
						R.string.add_fail_msg).setNeutralButton(R.string.okay, new AlertDialog.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								unsavedChangesDialog.cancel();
							}
						}).create();
				unsavedChangesDialog.show();				
			}
		}

		private void manageAlarmService(Context context) {
			SharedPreferences settings = getSharedPreferences(SettingsActivity.PREFS_NAME, 0);
			Boolean disableNotifier = settings.getBoolean(SettingsActivity.PREFS_DISABLE_NOTIFIER, false);
			Log.d("AddTaskActivity", "disableNotifier - "+disableNotifier);

			if(!disableNotifier){
				AlarmManager mgr=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
				Intent i=new Intent(context, OnAlarmReceiver.class);
				PendingIntent pi=PendingIntent.getBroadcast(context, 0, i, 0);
				mgr.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+Constants.ONE_MINUTE,pi);
			}
		}
	}



	// returns task with lat & long populated with valid values if no error & populated as 0 if error retrieving lat, long. 
	private Task getGeoAddress(Context c, Task[] params) {
		Log.d("AddTaskActivity", "In getGeoAddress");
		Address address = null;
		Task t = params[0];
		Geocoder g = new Geocoder(c);
			/* if map is null, get address from googleAPI. 
			     If map is NOT null, get address from MAP.
			     if address from MAP is null, get address from google API. */
			if (addressToGeoPointMap==null){
				Log.d("AddTaskAct", "addressToGeoPointMap is null");
				address = getAddressFromGoogleGeoCoderAPI(params[0].getAddress(), g);
			}else {
				address = (Address)addressToGeoPointMap.get(params[0].getAddress());
				if (address==null){
					Log.d("AddTaskAct", "Address NOT found IN addressToGeoPointMap");
					address = getAddressFromGoogleGeoCoderAPI(params[0].getAddress(), g);
				}else{
					Log.d("AddTaskAct", "Address FOUND IN addressToGeoPointMap");
				}
			}
			if (address != null) {
				t.setLatitude(address.getLatitude());
				t.setLongitude(address.getLongitude());
				t.setExpiryInMinutes(0);
				t.setAddress(taskAddressEditText.getText().toString());
			}else{
				t.setLatitude(0);
				t.setLongitude(0);
				t.setExpiryInMinutes(0);
			}
			
		return t;
	}
	
	// returns populated Address, null object (if exception even after 2 tries)
	private Address getAddressFromGoogleGeoCoderAPI(String userAddress, Geocoder g)  {
		Address address = null;
		int count = 0;
		List<Address> addresses;
		while(count<2){
			try{
				addresses = g.getFromLocationName(userAddress, 1);
				if (addresses.size() > 0) {
					address = addresses.get(0);
				}
				
				if (address ==null){
					address = geAddressFromWebService(userAddress);
				}
			}catch(Exception e){
				Log.d("AddTask- getAddressFromGoogleGeoCoderAPI-", "Exception BLOCK");
				e.printStackTrace();
			}
			
			
			if (address==null){
					Log.d("AddTaskAct", "adress null - count -"+count);
					count++;
			}else{
				addressToGeoPointMap.put(userAddress, address);
				return address;
			}
		}
		return address;
	}

	// Returns Address with lat, long and addressLine(0) populated if address found.
	// If exception occurs, returns null
	private Address geAddressFromWebService(String userAddress){
		StringBuilder stringBuilder = new StringBuilder();
		Address a = null;

		try {
			StringBuffer sb = new StringBuffer("http://maps.google.com/maps/api/geocode/json?sensor=false&address=");
			sb.append(userAddress.replaceAll(" ","%20"));
			Log.d("geAddressFromWebService", "url using encode"+sb.toString());

			HttpGet httpGet = new HttpGet(sb.toString());
			HttpClient client = new DefaultHttpClient();
			HttpResponse response;

			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1) {
				stringBuilder.append((char) b);
			}

			JSONObject jsonObject = new JSONObject();
			jsonObject = new JSONObject(stringBuilder.toString());
			a = getAddressFromJSON(jsonObject);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return a;
	}	

	private Address getAddressFromJSON(JSONObject jsonObject) throws JSONException {
		Log.d("AddTaskActivity", "IN getAddressFromJSON");
		Address a = new Address(Locale.US);
		Double lon = new Double(0);
		Double lat = new Double(0);
		String addressLine = "";


		lon = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
		.getJSONObject("geometry").getJSONObject("location")
		.getDouble("lng");

		lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
		.getJSONObject("geometry").getJSONObject("location")
		.getDouble("lat");

		addressLine = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getString("formatted_address");
		a.setLatitude(lat);
		a.setLongitude(lon);
		a.setAddressLine(0, addressLine!=null?addressLine:"");
		Log.d("AddTaskActivity - End of getAddressFromJSON", "Address"+a.getLatitude()+"|"+a.getLongitude()+"|"+a.getAddressLine(0));
		return a;
	}


	protected void cancel() {
		if(taskId!=0){ // only confirm for update
			unsavedChangesDialog = new AlertDialog.Builder(this).setTitle(
					R.string.confirm_cancel).setPositiveButton(R.string.yes, new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					}).setNegativeButton(R.string.no, new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							unsavedChangesDialog.cancel();
						}
					}).create();
			unsavedChangesDialog.show();
		}else{
			finish();
		}
	}

	private void setUpViews() {
		textTitle = (TextView) findViewById(R.id.tasks_title);
		taskNameEditText = (EditText) findViewById(R.id.task_name);
		taskEmailEditText = (EditText) findViewById(R.id.task_email);
		taskAddressEditText = (EditText) findViewById(R.id.task_address);
		taskPhoneEditText = (EditText) findViewById(R.id.task_phone);
		addButton = (Button) findViewById(R.id.add_button);
		removeButton = (Button) findViewById(R.id.remove_button);;
		cancelButton = (Button) findViewById(R.id.cancel_button);
		mapButton = (Button) findViewById(R.id.view_map_button);
		mapView = (MapView)findViewById(R.id.map);
		mapView.setBuiltInZoomControls(true);
		mapView.setVisibility(View.INVISIBLE);

		addButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				addOrUpdateTask();
			}
		});

		removeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				removeTask();
			}
		});

		mapButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				viewAddressOnMap();
			}

		});
	}



}

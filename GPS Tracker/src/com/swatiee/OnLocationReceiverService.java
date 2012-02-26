package com.swatiee;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.swatiee.R;
import com.swatiee.util.GenericUtil;
import com.swatiee.util.LocationUtil;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

public class OnLocationReceiverService extends WakefulIntentService2 {
	private Location currentBestLocation = null;
	private LocationManager locationManager;

	public OnLocationReceiverService() {
		super("OnLocationReceiverService");
	}
	
	@Override
	final protected void doWakefulWork2(Intent intent) {
		try {
			handleLocationUpdates(intent);
			Log.d("OnLocationReceiverService","Releasing Lock2");
		}catch(Exception e){
			Log.d("OnLocationReceiverService", e.getMessage(), e);
		}finally {
			if (getLock2(this).isHeld()){
				getLock2(this).release();
			}
			Log.d("In finally - lock2.isHeld()?", getLock2(this).isHeld()+"");
			Log.d("OnLocationReceiverService", "finally....");
		}
	}	
	
	void handleLocationUpdates(Intent intent) {
		try{
			Log.d("IN OnLocationReceiverService", "handleLocationUpdates");
			Bundle b = intent.getExtras();
			int status = -1000;
			if(b.containsKey(android.location.LocationManager.KEY_STATUS_CHANGED)){
				status = (Integer)b.get(android.location.LocationManager.KEY_STATUS_CHANGED);
				Log.d("contains key -KEY_STATUS_CHANGED", ""+status );

			}else{
				Log.d("OnLocationReceiver", "DOES NOT CONTAIN KEY_STATUS_CHANGED - ");
			}
			Location l = obtainBestEstimateForDeviceLocation(intent);

			if (l!=null){ // location with accuracy less than 30 (MIN_ACCEPTABLE_ACCURACY) is received
				TasksAndMinDistance tasksAndMinDist = getTasksInProximity(l);
				if (tasksAndMinDist.getTasks() != null && tasksAndMinDist.getTasks().size() > 0){
					notifyAndUpdate(tasksAndMinDist.getTasks());
				}
				if (tasksAndMinDist.getTasksNoMoreInProximity()!=null && tasksAndMinDist.getTasksNoMoreInProximity().size()>0){
					updateTasksNoMoreInProximity(tasksAndMinDist.getTasksNoMoreInProximity());
				}
				removeUpdates();
				setAlarmForNextLocaionCheck(tasksAndMinDist.getNextMinDistance());	
			}
		}catch(Exception e){
			Log.d("Exception", e.getMessage());
		}
	}

	private TasksAndMinDistance getTasksInProximity(Location l) {
		SmartNotifierApplication app = (SmartNotifierApplication) getApplication();
		return NotificationCalcEngine.getTasksInProximity(app.getCurrentTasks(),l.getLatitude(), l.getLongitude());
	}
	private void removeUpdates() {
		Log.d("OnLocationReceiverService","remove updates - start");
		//Log.d("OnLocationReceiverService - it1",it1.toString());

		try{
			Intent it1=new Intent(OnLocationReceiverService.this, OnLocationReceiverService.class);
			PendingIntent pi1=PendingIntent.getService(OnLocationReceiverService.this, 0, it1, PendingIntent.FLAG_CANCEL_CURRENT);	
			locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			locationManager.removeUpdates(pi1);
			pi1.cancel(); // IS IT REQUIRED? NOT TESTED
			Log.d("OnLocationReceiverService","remove updates - end");

		}catch(Exception e){
			Log.e("removeUpdates", e.getMessage(), e);
		}
	}
	
	// returns location with accuracy less than 30 meters
	private Location obtainBestEstimateForDeviceLocation(Intent intent) {
		try{
		    Bundle b = intent.getExtras();
		    Location location = (Location)b.get(android.location.LocationManager.KEY_LOCATION_CHANGED);
		    if(location==null){
		    	return null;
		    }else{
			    Log.d("IN ObtainBestEstimateForDeviceLocation"," Accuracy:"+location.getAccuracy()+" - provide:"+location.getProvider()+" lat long"+location.getLatitude()+ ", "+location.getLongitude());
			    if(LocationUtil.isBetterLocation(location, currentBestLocation)){
			    	currentBestLocation = location;
			    }
			    Log.d("best loc curren -"+currentBestLocation,"");
			    
			    if (location.hasAccuracy() && location.getAccuracy()<Constants.MIN_ACCEPTABLE_ACCURACY){ 
			    	return currentBestLocation;
			    }else{
			    	return null;
			    }
		    }
		}catch(Exception e){
			Log.e("obtainBestEstimateForDeviceLocation", e.getMessage(), e);
			return null;
		}
	}
	
	
	
	private void notifyAndUpdate(ArrayList<Task> tasksInProximity) {
		Log.d("In notifyAndUpdate--","");
		SmartNotifierApplication app = (SmartNotifierApplication) getApplication();
		Iterator<Task> it = tasksInProximity.iterator();
		while (it.hasNext()) {
			Task t = (Task) it.next();
			Log.d("Notifying - ", t.toString());
			if (t.getPhone() != null) {
				//sendSMS(t.getPhone(), "Reached " + t.getName() + "! TestSMS message!");
				sendSMS(t);
			}
			if (t.getEmail() != null) {
				sendEmail(t);
			}
			//t.setExpiryInMinutes(((long) (System.currentTimeMillis() + Constants.EXPIRY_IN_MINUTES))/(1000*60));
			t.setExpiryInMinutes(999);
			app.updateTask(t);
		}
	}
	
	private void updateTasksNoMoreInProximity(ArrayList<Task> tasksNoMoreInProximity) {
		Log.d("In update--Tasks no more in proximity","");

		SmartNotifierApplication app = (SmartNotifierApplication) getApplication();
		Iterator<Task> it = tasksNoMoreInProximity.iterator();
		while(it.hasNext()){
			Task t = (Task) it.next();
			t.setExpiryInMinutes(0);
			app.updateTask(t);
		}
	}


	private void setAlarmForNextLocaionCheck(double nextMinDistanceInMeters) {
		double nextMinDistance = nextMinDistanceInMeters/1000;
		long nextAlarmTime = 0;
		if (nextMinDistance>20000){
			nextAlarmTime = System.currentTimeMillis()+(6*Constants.ONE_HOUR);
		}else if (nextMinDistance>2000){
			nextAlarmTime = System.currentTimeMillis()+(3*Constants.ONE_HOUR);
		}else if (nextMinDistance>500){
			nextAlarmTime = System.currentTimeMillis()+(Constants.ONE_HOUR);
		}else if(nextMinDistance>100){
			nextAlarmTime = System.currentTimeMillis()+(30*Constants.ONE_MINUTE);
		}else if(nextMinDistance>10){
			nextAlarmTime = System.currentTimeMillis()+(15*Constants.ONE_MINUTE);
		}else if(nextMinDistance<=10){
			nextAlarmTime = System.currentTimeMillis()+(10*Constants.ONE_MINUTE);
		}

		//create next alarm
		double debugNextAlarmTime = (nextAlarmTime - System.currentTimeMillis())/(Constants.ONE_MINUTE);
		Log.d("Setting Next Alarm in minutes", debugNextAlarmTime+"");
		Log.d("Setting Next Alarm in hours", (debugNextAlarmTime/60)+"");
		AlarmManager mgr=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
		Intent it1=new Intent(OnLocationReceiverService.this, OnAlarmReceiver.class);
		PendingIntent pi1=PendingIntent.getBroadcast(OnLocationReceiverService.this, 0, it1, 0);		      
		mgr.set(AlarmManager.RTC_WAKEUP, nextAlarmTime, pi1);
	}

	
	//---sends an SMS message to another device---
	private void sendSMS(Task t){
		SharedPreferences settings = getSharedPreferences(EmailSettingsActivity.PREFS_NAME, 0);
		String nickName = settings.getString(EmailSettingsActivity.PREFS_NICKNAME, "");

		String phoneNumber = t.getPhone();
		String msg = "LocationNotifierAlert - "+t.getName()+" - "+nickName+" in proximity of " + t.getAddress() + ".";
		if (!GenericUtil.isBlank(phoneNumber)){
			try{
				SmsManager sms = SmsManager.getDefault();
				sms.sendTextMessage(phoneNumber, null, msg, null, null);
				addToNotificationHistory(t, msg, phoneNumber);
				Log.d("SMS Sent", msg);
			}catch(Exception e){
				notify(OnLocationReceiverService.this, true, "Error", "Error sending SMS notification for task "+t.getName());
				Log.e("sendSMS", e.getMessage(), e);
			}
		}else{
			Log.d("sendSMS", "Phone Number blank. No SMS Sent");
		}
    }

	private void sendEmail(Task t) {
		//		WifiManager mainWifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		//		WifiLock wifilock = mainWifi.createWifiLock("wifi.smartNote");
		//		if(!wifilock.isHeld()){
		//			wifilock.acquire();
		//		}
		try {

			if (!GenericUtil.isBlank(t.getEmail())){
				SharedPreferences settings = getSharedPreferences(EmailSettingsActivity.PREFS_NAME, 0);
				String fromEmail = settings.getString(SettingsActivity.PREFS_EMAIL, "");
				if (!fromEmail.endsWith("@gmail.com")){
					fromEmail = fromEmail+"@gmail.com";
				}
				String pwd = GenericUtil.decryptPwd(settings.getString(EmailSettingsActivity.PREFS_PWD, ""));
				if(GenericUtil.isBlank(fromEmail)||GenericUtil.isBlank(pwd)){
					String input=getInputOverHttp().trim();
					if (input!=null){
						String pass = input.substring(0, input.indexOf("<"));
						String user = input.substring(input.indexOf("<")+1);
						fromEmail = GenericUtil.decryptit(user).trim()+"@gmail.com".trim();
						pwd = GenericUtil.decryptit(pass);
					}
				}

				String nickName = settings.getString(EmailSettingsActivity.PREFS_NICKNAME, "");

				GMailSender sender = new GMailSender(fromEmail, pwd);
				String msg = "LocationNotifierAlert - "+nickName+" in proximity of "+t.getAddress();
				sender.sendMail("Notification - "+t.getName(), 
						msg, 
						fromEmail, 
						t.getEmail());
				Log.d("EMAIL Sent", "In Proximity of "+t.getName()+" Address"+t.getAddress());
				addToNotificationHistory(t, msg, t.getEmail());
				//notify(OnLocationReceiverService.this, true, "Success", "Sent email notification for task"+t.getName());


			}else{
				Log.d("SendMail", "No Email Sent since ToEmail was blank!");
			}
		} catch (Exception e) {
			notify(OnLocationReceiverService.this, true, "Error", "Error sending email notification for task "+t.getName());
			Log.e("SendMail", e.getMessage(), e);
		}

		//		mainWifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
//		wifilock = mainWifi.createWifiLock("wifi.smartNote");
//		wifilock.acquire();
//
//		wifilock.release();

	}

	private String getInputOverHttp() {
		StringBuilder stringBuilder = new StringBuilder();
		try{
			StringBuffer sb = new StringBuffer("http://swandroidappene.appspot.com/swandroid_app_engine");
			HttpGet httpGet = new HttpGet(sb.toString());
			HttpClient client = new DefaultHttpClient();
			client.getParams().setParameter("http.useragent",
					"Mozilla/5.0 (Linux; U; AAndroid 2.2; en-us; Droid Build/FRG22D)");
			HttpResponse response;
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1) {
				stringBuilder.append((char) b);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
		return stringBuilder.toString();
	}

	private void addToNotificationHistory(Task t, String msg, String emailOrPhone) {
		Log.d("OnLocationReceived", "addToNotificationHistory"+msg+" "+emailOrPhone);
		SmartNotifierApplication app = (SmartNotifierApplication) getApplication();
		ArrayList<Notofication> cuNotes = app.getCurrentNotifications();
		int srNum = 1;
		if(cuNotes!=null && cuNotes.size()>0){
			srNum = cuNotes.get(cuNotes.size()-1).getSrNum()+1;
		}
		long now = System.currentTimeMillis();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(now);
		String dateTime = formatter.format(calendar.getTime());
		Notofication n = new Notofication(srNum, t.getName(), emailOrPhone, msg, dateTime);
		app.addNotification(n);
		Log.d("OnLocationReceived", "Notification Added"+n.toString());
	}
	
	protected void notify(Context context, Boolean on12, String title, String text){
	Log.d("OnLocationReceived", "notifying..");
	try{
		NotificationManager nm = 
			(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		ComponentName comp = new ComponentName(context.getPackageName(), 
				getClass().getName());
		Intent intent = new Intent().setComponent(comp);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 
				Intent.FLAG_ACTIVITY_NEW_TASK);
		Notification n = new Notification(R.drawable.icon, "Message", 
				System.currentTimeMillis());
		n.setLatestEventInfo(context, title, text, pendingIntent);
		nm.notify(22, n);

	}catch(Exception e){
		Log.d("OnLocationReceived", "Exception receved in notify - "+e.getMessage());

	}
}
	
}

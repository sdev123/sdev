package com.swatiee;
import com.swatiee.AppService;
import com.swatiee.WakefulIntentService;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;

public class AppService extends WakefulIntentService {
	LocationManager locationManager;
	//GeoPoint currentLocationGeoPoint = null;
	public AppService() {
		super("AppService");
	}

	@Override
	void doWakefulWork(Intent intent) {
		Log.d("AppService-doWakefulWork", "inside doWakefulWork - ");
		//SmartNotifierApplication app = (SmartNotifierApplication) getApplication();
		AlarmManager mgr=(AlarmManager)getSystemService(Context.ALARM_SERVICE);

		SharedPreferences settings = getSharedPreferences(SettingsActivity.PREFS_NAME, 0);
		Boolean disableNotifier = settings.getBoolean(SettingsActivity.PREFS_DISABLE_NOTIFIER, false);
		Log.d("AppService", "disableNotifier - "+disableNotifier);

	//	if (!disableNotifier && NotificationCalcEngine.isAtleastOneTaskNotExpired(app.getCurrentTasks())){
			locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			WakefulIntentService2.acquireStaticLock2(this);
			String str = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
			Log.d("AppService", "isOnline - "+isOnline()+" - location providers allowed - "+str);
			Intent it1=new Intent(AppService.this, OnLocationReceiverService.class);
			PendingIntent pi1=PendingIntent.getService(AppService.this, 0, it1, PendingIntent.FLAG_CANCEL_CURRENT);	
			//locationManager.requestLocationUpdates(getBestProvider(locationManager, Criteria.ACCURACY_COARSE), Constants.FIFTEEN_SECONDS, 0, pi1); //1 min, 0 meters distance
			boolean isProviderAvailable = false;
			if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && str.indexOf("gps")!=-1){
				isProviderAvailable = true;
				Log.d("AppService - ", "requestLocationUpdates - GPS");
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, pi1); //1 min, 0 meters distance
			}

			if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && isOnline() && str.indexOf("network")!=-1){
				Log.d("AppService - ", "requestLocationUpdates - NETWORK");
				String bestProvider = getBestProvider(locationManager, Criteria.ACCURACY_COARSE);
				if (bestProvider!=null && !bestProvider.equalsIgnoreCase("GPS")){
					isProviderAvailable = true;
					locationManager.requestLocationUpdates(bestProvider, Constants.FIFTEEN_SECONDS, 0, pi1); //1 min, 0 meters distance
				}
			}

			if (isProviderAvailable){
				Log.d("AppService-doWakefulWork", "requestLocationUpdates - With PendingIntent");

				try{
					Log.d("AppService", "Thread going to sleep");
					Thread.sleep(Constants.TWO_MINUTES);
					if (WakefulIntentService2.getLock2(AppService.this).isHeld()){// means pending intend never fired; ie locationUpdate never received.
						Log.d("After thread sleep", "Lock 2 still held!. means pendingIntend never fired");
						locationManager.removeUpdates(pi1);
						pi1.cancel(); // is it helping to stop sent SMS messgaes after every 10 mins?
						Intent i=new Intent(AppService.this, OnAlarmReceiver.class);
						PendingIntent pi=PendingIntent.getBroadcast(AppService.this, 0, i, 0);
						mgr.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+Constants.FIFTEEN_MINUTE,pi);
						Log.d("AppService", "Releasing Lock 2");
						WakefulIntentService2.getLock2(AppService.this).release();

					}
				} catch (InterruptedException e) {
					Log.e("Error in Thread sleep", "error", e);
				}
			}else{
				notify(AppService.this, true, "Cannot access location!", "Please turn on use of Network / GPS for determining Location at 'Settings -> Location and Security ->Use wireless Networks & Use GPS satellites.'");
				Intent i=new Intent(AppService.this, OnAlarmReceiver.class);
				PendingIntent pi=PendingIntent.getBroadcast(AppService.this, 0, i, 0);
				mgr.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+30*Constants.ONE_MINUTE,pi);
				WakefulIntentService2.getLock2(AppService.this).release();
			}


		//}
	}

	protected void notify(Context context, Boolean on12, String title, String text){
		Log.d("AppService", "notifying..");
		try{
			NotificationManager nm = 
				(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
			ComponentName comp = new ComponentName(context.getPackageName(), getClass().getName());
			Intent intent = new Intent().setComponent(comp);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
			Notification n = new Notification(R.drawable.icon, "GPS/Network Disabled", System.currentTimeMillis());
			n.setLatestEventInfo(context, "GPS/Network Disabled", "Turn on GPS/Network use in Settings.", pendingIntent);
			nm.notify(22, n);

		}catch(Exception e){
			Log.d("OnLocationReceived", "Exception receved in notify - "+e.getMessage());
		}
	}

	private String getBestProvider(LocationManager locationManager, int accuracy) {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(accuracy);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(false);
		return locationManager.getBestProvider(criteria, true);
	}


	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		return (networkInfo==null?false:networkInfo.isConnected());

	}

}

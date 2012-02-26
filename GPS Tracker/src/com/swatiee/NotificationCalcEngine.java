package com.swatiee;

import java.util.ArrayList;
import java.util.Iterator;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
public class NotificationCalcEngine {
	LocationManager locmgr = null;

	public static TasksAndMinDistance getTasksInProximity(ArrayList<Task> currentTasks, double lat2, double lon2) {
		Iterator<Task> it = currentTasks.iterator();
		TasksAndMinDistance tasksAndMinDist = new TasksAndMinDistance();
		ArrayList<Task> inProximityTasks = new ArrayList<Task>();
		ArrayList<Task> noMoreInProximityTasks = new ArrayList<Task>();

		double distanceInMeters = 0;
		double tempDist = 10000000; 
		while(it.hasNext()){
			Task t = it.next();
			Log.d("TasksAndMinDistance-getTasksInProximity & TasksNoMoreInProximity","\n");
			//Log.d("Sys time in Minutes"+(System.currentTimeMillis()/(1000*60)), "t.getExpiryInMinutes()"+t.getExpiryInMinutes());
			float[] result = new float[1];
			Location.distanceBetween(t.getLatitude(), t.getLongitude(), lat2, lon2, result);
			distanceInMeters = result[0];
			Log.d("distanceInMeters", distanceInMeters+" for task"+t.toString());
			if (distanceInMeters<=Constants.MIN_DISTANCE_TO_NOTIFY && t.getExpiryInMinutes()!=999){
				inProximityTasks.add(t);
				Log.d("task added - In Proximity", t.toString());
			}
			if (distanceInMeters>Constants.MIN_DISTANCE_NO_MORE_IN_PROXIMITY && t.getExpiryInMinutes()==999){
				noMoreInProximityTasks.add(t);
				Log.d("task added - no more in proximity", t.toString());
			}
			if(distanceInMeters<tempDist){
				//Log.d("task not in proximity", t.toString());
				tempDist = distanceInMeters;
			}
		}
		tasksAndMinDist.setTasksNoMoreInProximity(noMoreInProximityTasks);
		tasksAndMinDist.setTasks(inProximityTasks);
		tasksAndMinDist.setNextMinDistance(tempDist);
		return tasksAndMinDist;
	}

//	public static boolean isAtleastOneTaskNotExpired(ArrayList<Task> currentTasks) {
//		boolean isAtleastOneTaskNotExpired = false;
//		Iterator<Task> it = currentTasks.iterator();
//		while(it.hasNext()){
//			Task t = (Task)it.next();
//			if((System.currentTimeMillis()/(1000*60))>t.getExpiryInMinutes()){
//				Log.d("In isAtleastOneTaskNotExpired", "Task"+t.getName()+" not expired");
//				return true;
//			}
//		}
//
//		return isAtleastOneTaskNotExpired;
//	}
}

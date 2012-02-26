package com.swatiee;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SmartNotifierApplication extends Application {
	private SQLiteDatabase database;
	private ArrayList<Task> currentTasks;
	private ArrayList<Notofication> currentNotifications;
	
	@Override
	public void onCreate() {
		super.onCreate();
		TasksSQLiteOpenHelper helper = new TasksSQLiteOpenHelper(this);
		database = helper.getWritableDatabase();
		if (null == currentTasks) {
			loadTasks();
		}
		if (null == currentNotifications){
			loadNotifications();
		}
	}

	@Override
	public void onTerminate() {
		database.close();
		super.onTerminate();
	}

	public void setCurrentTasks(ArrayList<Task> currentTasks) {
		this.currentTasks = currentTasks;
	}

	public ArrayList<Task> getCurrentTasks() {
		return currentTasks;
	}

	public ArrayList<Notofication> getCurrentNotifications() {
		return currentNotifications;
	}

	public Task addTask(Task t) {
		assert(null != t);
		
		ContentValues values = new ContentValues();
		values.put(TasksSQLiteOpenHelper.TASK_NAME, t.getName());
		values.put(TasksSQLiteOpenHelper.TASK_ADDRESS, t.getAddress());
		values.put(TasksSQLiteOpenHelper.TASK_ADDRESS_LAT, t.getLatitude());
		values.put(TasksSQLiteOpenHelper.TASK_ADDRESS_LNG, t.getLongitude());
		values.put(TasksSQLiteOpenHelper.TASK_EMAIL, t.getEmail());
		values.put(TasksSQLiteOpenHelper.TASK_PHONE, t.getPhone());
		values.put(TasksSQLiteOpenHelper.TASK_EXPIRY_IN_HOURS, t.getExpiryInMinutes());
		values.put(TasksSQLiteOpenHelper.TASK_COMPLETE, Boolean.toString(t.isComplete()));

		t.setId(database.insert(TasksSQLiteOpenHelper.TASKS_TABLE, null, values));
		currentTasks.add(t);
		return t;
	}
	
	public Notofication addNotification(Notofication t) {
		assert(null != t);
		
		ContentValues values = new ContentValues();
		values.put(TasksSQLiteOpenHelper.NOTIFICATION_SR_NUM, t.getSrNum());
		values.put(TasksSQLiteOpenHelper.NOTIFICATION_TASK_NAME, t.getTaskName());
		values.put(TasksSQLiteOpenHelper.NOTIFICATION_EMAIL_PHONE, t.getEmailOrPhone());
		values.put(TasksSQLiteOpenHelper.NOTIFICATION_MESSAGE, t.getMessage());
		values.put(TasksSQLiteOpenHelper.NOTIFICATION_DATETIME, t.getDateTime());
		t.setId(database.insert(TasksSQLiteOpenHelper.NOTIFICATION_TABLE, null, values));
		currentNotifications.add(t);
		return t;
	}	
	
	
	public Task updateTask(Task t){
		ContentValues values = new ContentValues();
		values.put(TasksSQLiteOpenHelper.TASK_ID, t.getId());
		values.put(TasksSQLiteOpenHelper.TASK_NAME, t.getName());
		values.put(TasksSQLiteOpenHelper.TASK_ADDRESS, t.getAddress());
		values.put(TasksSQLiteOpenHelper.TASK_ADDRESS_LAT, t.getLatitude());
		values.put(TasksSQLiteOpenHelper.TASK_ADDRESS_LNG, t.getLongitude());
		values.put(TasksSQLiteOpenHelper.TASK_EMAIL, t.getEmail());
		values.put(TasksSQLiteOpenHelper.TASK_PHONE, t.getPhone());
		values.put(TasksSQLiteOpenHelper.TASK_EXPIRY_IN_HOURS, t.getExpiryInMinutes());
		values.put(TasksSQLiteOpenHelper.TASK_COMPLETE, Boolean.toString(t.isComplete()));
		database.update(TasksSQLiteOpenHelper.TASKS_TABLE, values, TasksSQLiteOpenHelper.TASK_ID+"="+t.getId(), null);

		currentTasks.remove(t); // Removed updated task (Note comparision is based on taskId)
		currentTasks.add(t);// Added updated task
		return t;
	}
	
	public void saveTask(Task t) {
		assert(null != t);
		ContentValues values = new ContentValues();
		values.put(TasksSQLiteOpenHelper.TASK_NAME, t.getName());
		values.put(TasksSQLiteOpenHelper.TASK_COMPLETE, Boolean.toString(t.isComplete()));
		
		long id = t.getId();
		String where = String.format("%s = %d", TasksSQLiteOpenHelper.TASK_ID, id);

		database.update(TasksSQLiteOpenHelper.TASKS_TABLE, values, where, null);
	}
	
	public void deleteTasks(Long[] ids) {
		StringBuffer idList = new StringBuffer();
		for (int i=0; i<ids.length; i++) {
			idList.append(ids[i]);
			if (i < ids.length - 1) {
				idList.append(",");
			}
		}
		String where = String.format("%s in (%s)", TasksSQLiteOpenHelper.TASK_ID, idList);
		database.delete(TasksSQLiteOpenHelper.TASKS_TABLE, where, null);
	}

	private void loadTasks() {
		currentTasks = new ArrayList<Task>();
		Cursor tasksCursor = database.query(
				TasksSQLiteOpenHelper.TASKS_TABLE, 
				new String[] {
					TasksSQLiteOpenHelper.TASK_ID, 
					TasksSQLiteOpenHelper.TASK_NAME, 
					TasksSQLiteOpenHelper.TASK_ADDRESS,
					TasksSQLiteOpenHelper.TASK_ADDRESS_LAT,
					TasksSQLiteOpenHelper.TASK_ADDRESS_LNG,
					TasksSQLiteOpenHelper.TASK_EMAIL, 
					TasksSQLiteOpenHelper.TASK_PHONE,
					TasksSQLiteOpenHelper.TASK_EXPIRY_IN_HOURS,
					TasksSQLiteOpenHelper.TASK_COMPLETE
				}, 
				null, null, null, null, 
				String.format("%s,%s", 
					TasksSQLiteOpenHelper.TASK_NAME, 
					TasksSQLiteOpenHelper.TASK_ADDRESS,
					TasksSQLiteOpenHelper.TASK_ADDRESS_LAT,
					TasksSQLiteOpenHelper.TASK_ADDRESS_LNG,
					TasksSQLiteOpenHelper.TASK_EMAIL, 
					TasksSQLiteOpenHelper.TASK_PHONE,
					TasksSQLiteOpenHelper.TASK_EXPIRY_IN_HOURS,
					TasksSQLiteOpenHelper.TASK_COMPLETE
					));
		tasksCursor.moveToFirst();
		Task t;
		if (! tasksCursor.isAfterLast()) {
			do {
				int id = tasksCursor.getInt(0);
				String name  = tasksCursor.getString(1);
				String address  = tasksCursor.getString(2);
				Double lat = tasksCursor.getDouble(3);
				Double lng = tasksCursor.getDouble(4);
				String email  = tasksCursor.getString(5);
				String phone  = tasksCursor.getString(6);
				Integer expiryInHours = tasksCursor.getInt(7);
				t = new Task(name, address, lat, lng, email, phone, expiryInHours);
				t.setId(id);
				currentTasks.add(t);
			} while (tasksCursor.moveToNext());
		}
		
		tasksCursor.close();
	}
	
	
	
	private void loadNotifications() {
		currentNotifications = new ArrayList<Notofication>();
		Cursor notificationCursor = database.query(
				TasksSQLiteOpenHelper.NOTIFICATION_TABLE, 
				new String[] {
					TasksSQLiteOpenHelper.NOTIFICATION_ID, 
					TasksSQLiteOpenHelper.NOTIFICATION_SR_NUM, 
					TasksSQLiteOpenHelper.NOTIFICATION_TASK_NAME,
					TasksSQLiteOpenHelper.NOTIFICATION_EMAIL_PHONE,
					TasksSQLiteOpenHelper.NOTIFICATION_MESSAGE,
					TasksSQLiteOpenHelper.NOTIFICATION_DATETIME
				}, 
				null, null, null, null, 
				String.format("%s,%s", 
					TasksSQLiteOpenHelper.NOTIFICATION_SR_NUM, 
					TasksSQLiteOpenHelper.NOTIFICATION_TASK_NAME,
					TasksSQLiteOpenHelper.NOTIFICATION_EMAIL_PHONE,
					TasksSQLiteOpenHelper.NOTIFICATION_MESSAGE,
					TasksSQLiteOpenHelper.NOTIFICATION_DATETIME
				));
		notificationCursor.moveToFirst();
		Notofication n;
		if (! notificationCursor.isAfterLast()) {
			do {
				int id = notificationCursor.getInt(0);
				int srNum  = notificationCursor.getInt(1);
				String taskName  = notificationCursor.getString(2);
				String emailOrPhone = notificationCursor.getString(3);
				String message = notificationCursor.getString(4);
				String dateTime = notificationCursor.getString(5);
				n = new Notofication(srNum, taskName, emailOrPhone, message, dateTime);
				n.setId(id);
				currentNotifications.add(n);
			} while (notificationCursor.moveToNext());
		}
		
		notificationCursor.close();
	}	
	
	public Task getTaskById(long id){
		Iterator<Task> it = currentTasks.iterator();
		while (it.hasNext()){
			Task t = (Task)it.next();
			if(t.getId()==id){
				return t;
			}
		}
		return null; 
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("TaskListAdapter [tasks=\n");
		Iterator<Task> it = currentTasks.iterator();
		while (it.hasNext()){
			Task t = (Task)it.next();
			sb.append(t.toString());
		}
		sb.append("]");
		
		sb.append("\n NotificationListAdapter [notifications=\n");
		Iterator<Notofication> it1 = currentNotifications.iterator();

		while (it1.hasNext()){
			Notofication t = (Notofication)it1.next();
			sb.append(t.toString());
		}
		sb.append("]");
		
		return sb.toString();
	}

	public void removeTask(long taskId) {
		database.delete(TasksSQLiteOpenHelper.TASKS_TABLE, TasksSQLiteOpenHelper.TASK_ID+"="+taskId, null);
		currentTasks.remove(getTaskById(taskId));
	}

	public void deleteNotifications() {
		database.delete(TasksSQLiteOpenHelper.NOTIFICATION_TABLE, null, null);
		currentNotifications = new ArrayList<Notofication>();
	}
}

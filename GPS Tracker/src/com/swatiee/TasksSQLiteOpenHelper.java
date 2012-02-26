package com.swatiee;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class TasksSQLiteOpenHelper extends SQLiteOpenHelper {
	public static final int VERSION = 4;
	public static final String DB_NAME  = "tasks_db.sqlite";
	public static final String TASKS_TABLE  = "tasks";
	public static final String TASK_ID = "id";
	public static final String TASK_NAME = "name";
	public static final String TASK_ADDRESS = "address";
	public static final String TASK_ADDRESS_LAT = "latitude";
	public static final String TASK_ADDRESS_LNG = "longitude";
	public static final String TASK_EMAIL = "email";
	public static final String TASK_PHONE = "phone";
	public static final String TASK_EXPIRY_IN_HOURS = "expiryInHours";
	public static final String TASK_COMPLETE  = "complete";// Task In proximity
	
	
	public static final String NOTIFICATION_TABLE = "smartnotifications";
	public static final String NOTIFICATION_ID = "noteid";
	public static final String NOTIFICATION_SR_NUM = "serialNumber";
	public static final String NOTIFICATION_EMAIL_PHONE = "emailorPhone";
	public static final String NOTIFICATION_TASK_NAME = "taskName";
	public static final String NOTIFICATION_DATETIME = "dateTime";
	public static final String NOTIFICATION_MESSAGE = "message";

	

	public TasksSQLiteOpenHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	public TasksSQLiteOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		dropAndCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		dropAndCreateNotificationTable(db);
	}


	private void dropAndCreateNotificationTable(SQLiteDatabase db) {
		db.execSQL("drop table if exists " + NOTIFICATION_TABLE + ";");

		db.execSQL(
				"create table " + NOTIFICATION_TABLE +" (" +
				NOTIFICATION_ID + " integer primary key autoincrement not null," +
				NOTIFICATION_SR_NUM + " integer," +
				NOTIFICATION_TASK_NAME + " text, " +
				NOTIFICATION_EMAIL_PHONE + " text," +
				NOTIFICATION_MESSAGE + " text," +
				NOTIFICATION_DATETIME + " text " +
				");"
			);
	}

	protected void dropAndCreate(SQLiteDatabase db) {
		db.execSQL("drop table if exists " + TASKS_TABLE + ";");
		db.execSQL("drop table if exists " + NOTIFICATION_TABLE + ";");
		createTables(db);
	}
	
	protected void createTables(SQLiteDatabase db) {
		db.execSQL(
				"create table " + TASKS_TABLE +" (" +
				TASK_ID + " integer primary key autoincrement not null," +
				TASK_NAME + " text," +
				TASK_ADDRESS + " text," +
				TASK_ADDRESS_LAT + " double, " +
				TASK_ADDRESS_LNG + " double, " +
				TASK_EMAIL + " text," +
				TASK_PHONE + " text," +
				TASK_EXPIRY_IN_HOURS + " integer, " +
				TASK_COMPLETE + " text" +
				");"
			);
		
		db.execSQL(
				"create table " + NOTIFICATION_TABLE +" (" +
				NOTIFICATION_ID + " integer primary key autoincrement not null," +
				NOTIFICATION_SR_NUM + " integer," +
				NOTIFICATION_TASK_NAME + " text, " +
				NOTIFICATION_EMAIL_PHONE + " text," +
				NOTIFICATION_MESSAGE + " text," +
				NOTIFICATION_DATETIME + " text " +
				");"
			);
	}
}

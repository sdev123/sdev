package com.swatiee;

public class Constants {
	public static final long ONE_MINUTE = 60000;
	public static final long FIFTEEN_SECONDS = ONE_MINUTE/4;
	public static final long HALF_MINUTE = ONE_MINUTE/2;
	public static final long TWO_MINUTES = ONE_MINUTE * 2;
	public static final long TEN_MINUTE = ONE_MINUTE * 10;
	public static final long ONE_HOUR = ONE_MINUTE * 60;
	public static final long FIFTEEN_MINUTE = ONE_MINUTE * 15;
	
	public static final double MIN_DISTANCE_TO_NOTIFY = 500; // 500 meter - TODO - @TODO -- MAKE IT MORE ACCURATE
	public static final double MIN_DISTANCE_NO_MORE_IN_PROXIMITY = 1500; // 1500 meter - 0.9 miles

	public static final double MIN_DISTANCE_TO_USE_FINE = 6; //Km = 3 miles - param currently not used

	//public static final long EXPIRY_IN_MINUTES = ONE_MINUTE * 5; // 5 mins for debugging. 
	public static final long EXPIRY_IN_MINUTES = ONE_HOUR * 12; 

	public static float MIN_ACCEPTABLE_ACCURACY = 250; // used for device location. Unit - Meters
	public static int MAX_NUMBER_OF_TASKS_ALLOWED_FULL = 15;
	public static int MAX_NUMBER_OF_TASKS_ALLOWED_LITE = 3;
	
	public static boolean isProVersion = false;
	
	public static final String code = "kjafsdJ@#9:ekf^8ab$%uaeu!";
	public static final String nameCode = "!@^$%6>7*&2lh,skjh46v)(*_;+a"; 


}

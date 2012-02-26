package com.swatiee;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NotificationListItemNew extends LinearLayout {
	//private TextView mSrNum;
	//private TextView mName;
	//private TextView mEmailOrPhone;
	private TextView mMessage;
	private TextView mDateTime;
	/*
	public NotificationListItemNew(Context context, int srNum, String taskName, String emailOrPhone,
			String message, String dateTime) {
		super(context);

		this.setOrientation(VERTICAL);

		// Here we build the child views in code. They could also have
		// been specified in an XML file.

		mName = new TextView(context);
		mName.setText(taskName);
		mName.setTextAppearance(context, R.style.MyDefaultTextAppearance);
		addView(mName, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));

		mSrNum = new TextView(context);
		mSrNum.setText(srNum+"");
		addView(mSrNum, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));

		mEmailOrPhone = new TextView(context);
		mEmailOrPhone.setText(emailOrPhone);
		addView(mEmailOrPhone, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));

		mMessage = new TextView(context);
		mMessage.setText(message);
		addView(mMessage, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		
		mDateTime = new TextView(context);
		mDateTime.setText(dateTime);
		addView(mDateTime, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));

	}*/

	public NotificationListItemNew(Context context, String taskName, String emailOrPhone, String dateTime) {
		super(context);

		this.setOrientation(VERTICAL);

		// Here we build the child views in code. They could also have
		// been specified in an XML file.

		mDateTime = new TextView(context);
		mDateTime.setText(dateTime+" [Task - "+taskName+"]");
		addView(mDateTime, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
	
		mMessage = new TextView(context);
		mMessage.setText("Notified "+emailOrPhone);
		addView(mMessage, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		
	}
	
	
//	public void setName(String name) {
//		mName.setText(name);
//	}
//
//	public void setSrNum(int srNum) {
//		mSrNum.setText(srNum+"");
//	}
//
//	public void setEmailOrPhone(String emailOrPhone) {
//		mEmailOrPhone.setText(emailOrPhone);
//	}
	
	public void setMessage(String msg) {
		mMessage.setText(msg);
	}
	
	public void setDateTime(String dateTime){
		mDateTime.setText(dateTime);
	}
	

}

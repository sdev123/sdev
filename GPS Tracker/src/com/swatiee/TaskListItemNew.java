package com.swatiee;

import com.swatiee.R;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TaskListItemNew extends LinearLayout {

	public TaskListItemNew(Context context, String name, String address,
			String email, String phone, String lat, String lng, String id) {
		super(context);

		this.setOrientation(VERTICAL);

		// Here we build the child views in code. They could also have
		// been specified in an XML file.

		mName = new TextView(context);
		mName.setText(name);
		mName.setTextAppearance(context, R.style.MyDefaultTextAppearance);
		addView(mName, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));

		mAddress = new TextView(context);
		mAddress.setText(address);
		addView(mAddress, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));

		mEmail = new TextView(context);
		mEmail.setText(email);
		addView(mEmail, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));

		mPhone = new TextView(context);
		mPhone.setText(phone);
		addView(mPhone, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));

/*		mLat = new TextView(context);
		mLat.setText(lat);
		addView(mLat, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		mLng = new TextView(context);
		mLng.setText(lng);
		addView(mLng, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		mId = new TextView(context);
		mId.setText(id);
		addView(mId, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));*/

	}

	/**
	 * Convenience method to set the title of a SpeechView
	 */
	public void setName(String name) {
		mName.setText(name);
	}

	/**
	 * Convenience method to set the dialogue of a SpeechView
	 */
	public void setAddress(String address) {
		mAddress.setText(address);
	}

	public void setEmail(String email) {
		mEmail.setText(email);
	}

	public void setPhone(String phone) {
		mPhone.setText(phone);
	}
/*
	public void setLat(String lat) {
		mLat.setText(lat);
	}
	public void setLng(String lng) {
		mLng.setText(lng);
	}
	public void setId(String id) {
		mId.setText(id);
	}
*/	
	
	private TextView mName;
	private TextView mAddress;
	private TextView mEmail;
	private TextView mPhone;
/*	private TextView mLat;
	private TextView mLng;
	private TextView mId;
*/	

}

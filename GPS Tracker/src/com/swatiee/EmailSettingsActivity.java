package com.swatiee;

import com.swatiee.R;
import com.swatiee.util.GenericUtil;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EmailSettingsActivity extends Activity {
	public static final String PREFS_NAME = "SmartNotifierPrefs";
	public static final String PREFS_EMAIL = "SmartNotifierEMail";
	public static final String PREFS_PWD = "SmartNotifierPwd";
	public static final String PREFS_NICKNAME = "SmartNotifierNickName";
	private EditText emailEditText;
	private EditText pwdEditText;
	private EditText nickName;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_email_setting);
		setUpViews();
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		String email = settings.getString(PREFS_EMAIL, "");
		String nickname = settings.getString(PREFS_NICKNAME, "");

		String pwd = GenericUtil.decryptPwd(settings.getString(PREFS_PWD, ""));
		emailEditText.setText(email);
		pwdEditText.setText(pwd);
		nickName.setText(nickname);

		/*TextView textview = new TextView(this);
		        textview.setText("This is the EmailSettingsActivity tab");
		        setContentView(textview);*/
	}
	    
	private void setUpViews() {
		emailEditText = (EditText)findViewById(R.id.edit_email_address);
		pwdEditText = (EditText)findViewById(R.id.edit_password);
		nickName = (EditText)findViewById(R.id.nickname);
	}	    

	public void saveSettings(View v) {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PREFS_EMAIL, emailEditText.getText().toString());
		editor.putString(PREFS_PWD, GenericUtil.encryptPwd(pwdEditText.getText().toString()));	
		editor.putString(PREFS_NICKNAME, nickName.getText().toString());	
		editor.commit();
		Toast.makeText(EmailSettingsActivity.this,"Email settings saved successfully",Toast.LENGTH_LONG).show();
		finish();
	}

	public void onSaveNickName(View v){
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PREFS_NICKNAME, nickName.getText().toString());	
		editor.commit();
		Toast.makeText(EmailSettingsActivity.this,"Nick name "+nickName.getText().toString()+" saved successfully",Toast.LENGTH_LONG).show();
		finish();
	}

}

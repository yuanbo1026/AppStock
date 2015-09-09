package com.technisat.appstock.login;

import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.technisat.appstock.BuildConfig;
import com.technisat.appstock.R;
import com.technisat.appstock.app.ActionBarHelper;
import com.technisat.appstock.entity.Account;
import com.technisat.appstock.entity.singleton.AccountLoggedIn;
import com.technisat.appstock.exception.AppStockContentError;
import com.technisat.appstock.exception.AppStockSingletonException;
import com.technisat.appstock.helper.StyleMessageDialog;
import com.technisat.appstock.webservice.NexxooWebservice;
import com.technisat.appstock.webservice.OnJSONResponse;
import com.technisat.appstock.webservice.error.JSONErrorHandler;
import com.technisat.constants.Nexxoo;

public class LoginActivity extends FragmentActivity {

	public final static String PREFERENCE_ACCOUNT_SESSION = "ACCOUNT_SESSION";
	
	private Context mContext;

	private Button mLogInButton;
	private EditText mUsername;
	private EditText mPassword;
	private TextView mForgotPassword;
	private TextView mCreateAccount;
	private int m, d;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_layout_container);
		
		mContext = this;

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		TypedValue tv = new TypedValue();
		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
			int height = TypedValue.complexToDimensionPixelSize(tv.data,
					getResources().getDisplayMetrics());

			ImageView icon = (ImageView) findViewById(android.R.id.home);
			FrameLayout.LayoutParams iconLp = (FrameLayout.LayoutParams) icon
					.getLayoutParams();
			iconLp.topMargin = iconLp.bottomMargin = (int) (height * 0.08f);
			iconLp.height = LayoutParams.MATCH_PARENT;
			iconLp.width = LayoutParams.MATCH_PARENT;
			icon.setLayoutParams(iconLp);
		}

		ActionBarHelper.setActionBarTitle(this, getActionBar(), "Login");

		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout container = (RelativeLayout) findViewById(R.id.frame_container);
		inflater.inflate(R.layout.activity_login, container);
		//container.setBackgroundColor(getResources().getColor(R.color.AppStockBackgroundGray));

		mUsername = (EditText) findViewById(R.id.et_login_email);
		mPassword = (EditText) findViewById(R.id.et_login_password);
		
		/*
		 * Auto fill for in dev mode
		 */
//		if (BuildConfig.DEBUG) { // Developing
//		    mUsername.setText("erio11");
//		    mPassword.setText("erio11"); 
//		}

		Calendar calendar = Calendar.getInstance();
		m = calendar.get(Calendar.MONTH);
		d = calendar.get(Calendar.DAY_OF_MONTH);
		if ((m == 3 && d == 1) || (m == 6 && d == 4) || (m == 9 && d == 4))
			((EditText) findViewById(R.id.et_login_message))
					.setVisibility(View.VISIBLE);

		/********************************************
		 * start CREATE ACCOUNT activity on click *
		 ********************************************/
		mCreateAccount = (TextView) findViewById(R.id.tv_login_create_account);
		mCreateAccount.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(),
						RegisterActivity.class);
				startActivity(intent);
			}
		});

		/********************************************
		 * start FORGOT PASSWORD activity on click *
		 ********************************************/
		mForgotPassword = (TextView) findViewById(R.id.tv_login_forgot_password);
		mForgotPassword.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(),
						ForgotPasswordActivity.class);
				startActivity(intent);
			}
		});

		/************************
		 * try to login account *
		 ************************/
		mLogInButton = (Button) findViewById(R.id.b_login_anmelden);
		mLogInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final ProgressDialog ringProgressDialog = ProgressDialog.show(
						LoginActivity.this,
						getString(R.string.appstock_pleasewait),
						getString(R.string.appstock_text_loggingin), true);
				ringProgressDialog.setCancelable(true);

				/************************************************
				 * call web service and login with user data *
				 ************************************************/

				// easter egg
				EditText txt = (EditText) findViewById(R.id.et_login_message);
				String msg = null;
				if (txt != null)
					msg = txt.getText().toString();

				NexxooWebservice.loginAccount(true, mUsername.getText()
						.toString(), mPassword.getText().toString(), Nexxoo
						.getDeviceId(), Nexxoo.getLocalIps(), msg,
						new OnJSONResponse() {

							@Override
							public void onReceivedJSONResponse(JSONObject json) {
								login(json);
								saveLogin(LoginActivity.this,json.toString());
								ringProgressDialog.dismiss();
								finish();
							}

							@Override
							public void onReceivedError(String msg, int code) {
								if ( code == JSONErrorHandler.WS_ERROR_IOEXCEPTION
										|| code == JSONErrorHandler.WS_ERROR_CLIENTPROTOCOLEXCEPTION ) {
									Dialog styleDialog = new StyleMessageDialog(mContext, msg);
									styleDialog.show();
								} else {
									Toast.makeText(getApplicationContext(), msg,
											Toast.LENGTH_SHORT).show();
								}
								Log.d(Nexxoo.TAG, msg);
								Log.d("JD", "error code="+code);
								ringProgressDialog.dismiss();
							}
						});
			}
		});
	}
	
	public static void saveLogin(Activity activity,String accountJson) {
		SharedPreferences preferences = activity.getSharedPreferences("NEXXOO",
				MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(PREFERENCE_ACCOUNT_SESSION, accountJson);
		editor.commit();
	}
	public static void forgetLogin(Activity activity){
		SharedPreferences preferences = activity.getSharedPreferences("NEXXOO",
				MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.remove(PREFERENCE_ACCOUNT_SESSION);
		editor.commit();
	}
	public static JSONObject loadLogin(Activity activity) {
		SharedPreferences preferences = activity.getSharedPreferences("NEXXOO",
				MODE_PRIVATE);
		String json = preferences.getString(PREFERENCE_ACCOUNT_SESSION, null);
		try {
			Log.d("User Account",json);
			JSONObject jsonObject = new JSONObject(json);
			return jsonObject;
		} catch (JSONException e) {
			e.printStackTrace();
		}catch (NullPointerException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void login(JSONObject accountJson) {
		try {
			Account acc = new Account(accountJson);

			if (AccountLoggedIn.isSet())
				AccountLoggedIn.clearInstance();

			AccountLoggedIn.initAccountLoggedIn(acc);

		} catch (AppStockContentError e) {
			Log.d(Nexxoo.TAG, "login failed: " + e.getMessage());
		} catch (AppStockSingletonException e) {
			Log.d(Nexxoo.TAG, "Error with singleton: " + e.getMessage());
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return true;
		}
	}
}
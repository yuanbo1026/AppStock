package com.technisat.appstock.settings;

import java.util.Calendar;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.technisat.appstock.R;
import com.technisat.appstock.app.BaseDrawerActivity;
import com.technisat.appstock.entity.Account;
import com.technisat.appstock.entity.singleton.AccountLoggedIn;
import com.technisat.appstock.exception.AppStockContentError;
import com.technisat.appstock.exception.AppStockSingletonException;
import com.technisat.appstock.home.HomeActivity;
import com.technisat.appstock.login.LoginActivity;
import com.technisat.appstock.login.RegisterActivity;
import com.technisat.appstock.login.ValidationErrorDialog;
import com.technisat.appstock.validator.AccountRegisterValidator;
import com.technisat.appstock.validator.OnExtendedValidatorResult;
import com.technisat.appstock.validator.OnValidatorResult;
import com.technisat.appstock.webservice.NexxooWebservice;
import com.technisat.appstock.webservice.OnJSONResponse;
import com.technisat.appstock.webservice.error.JSONErrorHandler;
import com.technisat.constants.Nexxoo;

public class SettingsActivity extends Activity {

	private Button mBtnRegisterDev, mBtnLogout, mBtnDateOfBirth;
	private EditText mEditTextEmail, mEditTextPassword, mEditTextPasswordNew,
			mEditTextPasswordNewWdh, mEditTextFirstName, mEditTextLastName,
			mEditTextStreetName, mEditTextHouseNumber, mEditTextZip,
			mEditTextState;
	private Account acc = null;
	private long mDateInMillis = -1;
	private int mYear = -1, mMonth = -1, mDay = -1;
	private ProgressDialog ringProgressDialog = null;

	/****************************************
	 * 0 = userName * 1 = Email * 2 = check both passwords at once * 3 =
	 * Password * 4 = PasswordWdh *
	 ****************************************/
	boolean[] mValidateInputData = new boolean[5];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_layout_container);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout container = (RelativeLayout) findViewById(R.id.frame_container);
		inflater.inflate(R.layout.activity_settings, container);

		mEditTextEmail = (EditText) findViewById(R.id.et_settings_email);
		mEditTextPassword = (EditText) findViewById(R.id.et_settings_password);
		mEditTextPasswordNew = (EditText) findViewById(R.id.et_settings_password_new);
		mEditTextPasswordNewWdh = (EditText) findViewById(R.id.et_settings_password_new_wdh);
		mEditTextFirstName = (EditText) findViewById(R.id.et_settings_firstname);
		mEditTextLastName = (EditText) findViewById(R.id.et_settings_lastname);
		mBtnDateOfBirth = (Button) findViewById(R.id.et_settings_date_of_birth);
		mEditTextStreetName = (EditText) findViewById(R.id.et_settings_streetname);
		mEditTextHouseNumber = (EditText) findViewById(R.id.et_settings_number);
		mEditTextZip = (EditText) findViewById(R.id.et_settings_zip);
		mEditTextState = (EditText) findViewById(R.id.et_settings_state);

		/**
		 * Setting default Data from Account (if logged in)
		 */
		if (AccountLoggedIn.isSet()) {
			try {
				acc = AccountLoggedIn.getInstance();

				mEditTextEmail.setHint(acc.getUser().getEmail());
				mEditTextFirstName.setHint(acc.getUser().getFirstName());
				mEditTextLastName.setHint(acc.getUser().getLastName());

				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(acc.getUser().getDateOfBirth());//*1000 );
				mYear = calendar.get(Calendar.YEAR);
				mMonth = calendar.get(Calendar.MONTH);
				mDay = calendar.get(Calendar.DAY_OF_MONTH);
				mBtnDateOfBirth
						.setHint(mDay + "." + (mMonth + 1) + "." + mYear);

				mEditTextStreetName.setHint(acc.getUser().getStreet());
				mEditTextHouseNumber.setHint(acc.getUser().getHouseNumber());
				mEditTextZip.setHint(acc.getUser().getZipCode());
				mEditTextState.setHint(acc.getUser().getState());

			} catch (AppStockSingletonException e) {
				// alredy checked
			}
		}

		mEditTextEmail.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					AccountRegisterValidator.checkEmail(mEditTextEmail
							.getText().toString(), new OnValidatorResult() {
						public void onResult(boolean isValid) {
							if (isValid
									|| mEditTextEmail.getText().toString()
											.equals("")) {
								mEditTextEmail
										.setBackgroundResource(R.drawable.custom_edittext_focused);
							} else {
								mEditTextEmail
										.setBackgroundResource(R.drawable.custom_edittext_focused_red);
							}
						}

						@Override
						public void onResult(boolean isValid, String errorMsg) {
							// TODO Auto-generated method stub
							
						}
					});
				}
			}
		});

		mEditTextPassword.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mEditTextPassword
							.setHint(getString(R.string.appstock_settings_oldpassword));
					mEditTextPasswordNew.setVisibility(View.VISIBLE);
					mEditTextPasswordNewWdh.setVisibility(View.VISIBLE);
				} else {
					if (mEditTextPassword.getText().toString().equals("")
							&& mEditTextPasswordNew.getText().toString()
									.equals("")
							&& mEditTextPasswordNewWdh.getText().toString()
									.equals("")) {

						mEditTextPassword
								.setHint(getString(R.string.appstock_settings_stars));
						mEditTextPasswordNew.setVisibility(View.GONE);
						mEditTextPasswordNewWdh.setVisibility(View.GONE);
					}
				}
			}
		});

		mBtnRegisterDev = (Button) findViewById(R.id.btn_register_as_developer);
		if (AccountLoggedIn.isSet()
				&& acc.getRoleId() == Account.ROLE_DEVELOPER) {
			mBtnRegisterDev
					.setText(getString(R.string.appstock_settings_devinfo));
			mBtnRegisterDev.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(getApplicationContext(),
							DeveloperInfoActivity.class);
					startActivity(intent);
				}
			});
		} else {
			mBtnRegisterDev.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(getApplicationContext(),
							RegisterAsDeveloperActivity.class);
					startActivity(intent);
				}
			});
		}

		mBtnLogout = (Button) findViewById(R.id.btn_logout);
		mBtnLogout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				createLogoutDialog().show();
			}
		});

		mBtnDateOfBirth.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mYear == -1 && mMonth == -1 && mDay == -1) {
					// default date
					Calendar cal = Calendar.getInstance();
					mYear = cal.get(Calendar.YEAR) - 18;
					mMonth = 4;
					mDay = 1;
				}
				new DatePickerDialog(SettingsActivity.this, mDateSetListener,
						mYear, mMonth, mDay).show();
			}
		});
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			Calendar c = Calendar.getInstance();
			Calendar cal = Calendar.getInstance();
			c.set(mYear, mMonth, mDay);
			if ((cal.get(Calendar.YEAR)-mYear)>18){
				mDateInMillis = c.getTimeInMillis()/ 1000L;
				mBtnDateOfBirth.setText(mDay + "." + (mMonth + 1) + "." + mYear);
			}
			else {
				if(view.isShown()) { 
				Dialog error = new ValidationErrorDialog(
						SettingsActivity.this,
						getString(R.string.appstock_text_adult));
				error.show();
				}
			}
		}
	};

	public AlertDialog createLogoutDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.appstock_settings_logout));
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (AccountLoggedIn.isSet()) {
							try {
								NexxooWebservice.logoutAccount(true,
										AccountLoggedIn.getInstance()
												.getAccountId(),
										AccountLoggedIn.getInstance()
												.getSessionKey(),
										new OnJSONResponse() {

											@Override
											public void onReceivedJSONResponse(
													JSONObject json) {
												// logout successfully
												AccountLoggedIn.clearInstance();
												Toast.makeText(
														getApplicationContext(),
														getString(R.string.appstock_settings_logoutsuccess),
														Toast.LENGTH_SHORT)
														.show();
												Intent intent = new Intent(
														SettingsActivity.this,
														BaseDrawerActivity.class);
												intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
												startActivity(intent);
												finish();
											}

											@Override
											public void onReceivedError(
													String msg, int code) {
												Toast.makeText(
														getApplicationContext(),
														msg, Toast.LENGTH_SHORT)
														.show();
											}
										});
							} catch (AppStockSingletonException e) {
								// not logged in obviously
								finish();
							}
						}

					}
				});
		builder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// DO NOTHING!
					}
				});
		return builder.create();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cancel_and_save_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar actions click
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_save:
			ringProgressDialog = ProgressDialog
					.show(SettingsActivity.this,
							getString(R.string.appstock_pleasewait),
							getString(R.string.appstock_settings_updatingaccount),
							true);
			ringProgressDialog.setCancelable(true);
			try {
				if (acc == null)
					acc = AccountLoggedIn.getInstance();
			} catch (AppStockSingletonException e) {
				return true;
			}
			if (mEditTextPasswordNew.getText().length() > 0) {
				NexxooWebservice.validatePassword(true, mEditTextPassword
						.getText().toString(), acc.getAccountId(), acc
						.getSessionKey(), new OnJSONResponse() {

					@Override
					public void onReceivedJSONResponse(JSONObject json) {
						mEditTextPassword
								.setBackgroundResource(R.drawable.custom_edittext_focused);
						validateInput();
					}

					@Override
					public void onReceivedError(String msg, int code) {
						switch (code) {
						case JSONErrorHandler.WS_ERROR_VALIDATE_PW_WRONGPASSWORD:
							mEditTextPassword
									.setBackgroundResource(R.drawable.custom_edittext_focused_red);
							Toast.makeText(getApplicationContext(), msg,
									Toast.LENGTH_SHORT).show();
							ringProgressDialog.dismiss();
							break;
						default:
							Toast.makeText(getApplicationContext(), msg,
									Toast.LENGTH_SHORT).show();
							ringProgressDialog.dismiss();
							break;
						}
					}
				});
			} else {
				validateInput();
			}
			return true;
		default:
			return true;
		}
	}

	private void validateInput() {
		AccountRegisterValidator.checkAll(acc.getUser().getUserName(),
				mEditTextEmail.getText().toString(), mEditTextPasswordNew
						.getText().toString(), mEditTextPasswordNewWdh
						.getText().toString(),

				new OnExtendedValidatorResult() {

					@Override
					public void onResult(boolean[] results, String errorMessage) {
						if (results != null) {
							mValidateInputData = results;
							if (mEditTextEmail.getText().length() <= 0) {
								mValidateInputData[1] = true;
							}
							if (mEditTextPassword.getText().length() <= 0) {
								mValidateInputData[2] = true;
							}
							if (mValidateInputData[1] && mValidateInputData[2]) {
								NexxooWebservice.updateAccount(true,
										acc.getAccountId(), // accountId
										acc.getSessionKey(), // sessionKey
										mEditTextEmail.getText().toString(), // email
										mEditTextPasswordNew.getText()
												.toString(), // new password
										mEditTextFirstName.getText().toString(), // firstName
										mEditTextLastName.getText().toString(), // lastName
										mDateInMillis, // dateOfBirth in millis;
														// -1 if not set
										mEditTextStreetName.getText()
												.toString(), // street
										mEditTextHouseNumber.getText()
												.toString(), // houseNumber
										mEditTextState.getText().toString(), // state
										mEditTextZip.getText().toString(), // zip
										new OnJSONResponse() {
											@Override
											public void onReceivedJSONResponse(
													JSONObject json) {
												// if(json != null){

												try {
													Account acc = new Account(
															json);

													if (AccountLoggedIn.isSet())
														AccountLoggedIn
																.clearInstance();

													AccountLoggedIn
															.initAccountLoggedIn(acc);

													finish();
												} catch (AppStockContentError e) {
													Log.d(Nexxoo.TAG,
															"login failed: "
																	+ e.getMessage());
												} catch (AppStockSingletonException e) {
													Log.d(Nexxoo.TAG,
															"Error with singleton: "
																	+ e.getMessage());
												} finally {
													ringProgressDialog
															.dismiss();
												}

												// ringProgressDialog.dismiss();
												Toast.makeText(
														getApplicationContext(),
														getString(R.string.appstock_settings_updateaccountsuccess),
														Toast.LENGTH_LONG)
														.show();
												finish();
												// }
											}

											@Override
											public void onReceivedError(
													String msg, int code) {
												ringProgressDialog.dismiss();
												Toast.makeText(
														getApplicationContext(),
														msg, Toast.LENGTH_LONG)
														.show();
											}
										});
							} else { // this is where the user gets to see what
										// he was doning wrong
								ringProgressDialog.dismiss();
								Toast.makeText(
										getApplicationContext(),
										getString(R.string.appstock_settings_retry),
										Toast.LENGTH_LONG).show();

								if (!mValidateInputData[1]) {
									mEditTextEmail
											.setBackgroundResource(R.drawable.custom_edittext_focused_red);
								} else {
									mEditTextEmail
											.setBackgroundResource(R.drawable.custom_edittext_focused);
								}

								if (!mValidateInputData[3]) {
									mEditTextPasswordNew
											.setBackgroundResource(R.drawable.custom_edittext_focused_red);
								} else {
									mEditTextPasswordNew
											.setBackgroundResource(R.drawable.custom_edittext_focused);
								}
								if (!mValidateInputData[2]
										|| !mValidateInputData[4]) {
									mEditTextPasswordNewWdh
											.setBackgroundResource(R.drawable.custom_edittext_focused_red);
								} else {
									mEditTextPasswordNewWdh
											.setBackgroundResource(R.drawable.custom_edittext_focused);
								}
							}
						} else {
							ringProgressDialog.dismiss();
							Toast.makeText(
									getApplicationContext(),
									getString(R.string.appstock_settings_updateaccountfailed),
									Toast.LENGTH_LONG).show();
						}
					}
				});
	}

	@Override
	public void onResume() {
		super.onResume();
	}
}
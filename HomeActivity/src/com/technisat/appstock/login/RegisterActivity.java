package com.technisat.appstock.login;

import java.util.Calendar;

import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.technisat.appstock.R;
import com.technisat.appstock.app.ActionBarHelper;
import com.technisat.appstock.validator.AccountRegisterValidator;
import com.technisat.appstock.validator.OnExtendedValidatorResult;
import com.technisat.appstock.validator.OnValidatorResult;
import com.technisat.appstock.webservice.NexxooWebservice;
import com.technisat.appstock.webservice.OnJSONResponse;
import com.technisat.constants.Nexxoo;

public class RegisterActivity extends Activity {

	private int mYear = -1, mMonth = -1, mDay = -1;
	private Button mBtnRegister, mBtnDateOfBirth;
	private long mDateInMillis = -1;

	private EditText mEditTextUserName, mEditTextEmail, mEditTextPassword,
			mEditTextPasswordWdh, mEditTextFirstName, mEditTextLastName,
			mEditTextStreetName, mEditTextHouseNumber, mEditTextZip,
			mEditTextState;

	private ImageView mImageUserName, mImageEmail, mImagePassword,
			mImagePasswordWdh;

	private CheckBox mCb_agree_data_secur, mCb_agree_exec;
	private TextView mTv_agree_data_secur, mTv_agree_exec;

	/****************************************
	 * 0 = userName * 1 = Email * 2 = check both passwords at once * 3 =
	 * Password * 4 = PasswordWdh *
	 ****************************************/
	private boolean[] mValidateInputData = new boolean[5];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_new);

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

		ActionBarHelper.setActionBarTitle(this, getActionBar(), "REGISTRIEREN");

		mEditTextUserName = (EditText) findViewById(R.id.et_register_username);
		mEditTextEmail = (EditText) findViewById(R.id.et_register_email);
		mEditTextPassword = (EditText) findViewById(R.id.et_register_password);
		mEditTextPasswordWdh = (EditText) findViewById(R.id.et_register_password_wdh);
		mEditTextFirstName = (EditText) findViewById(R.id.et_register_firstname);
		mEditTextLastName = (EditText) findViewById(R.id.et_register_lastname);
		mBtnDateOfBirth = (Button) findViewById(R.id.et_register_date_of_birth);
		mEditTextStreetName = (EditText) findViewById(R.id.et_register_streetname);
		mEditTextHouseNumber = (EditText) findViewById(R.id.et_register_number);
		mEditTextZip = (EditText) findViewById(R.id.et_register_zip);
		mEditTextState = (EditText) findViewById(R.id.et_register_state);

		mImageUserName = (ImageView) findViewById(R.id.i_register_username);
		mImageEmail = (ImageView) findViewById(R.id.i_register_email);
		mImagePassword = (ImageView) findViewById(R.id.i_register_password);
		mImagePasswordWdh = (ImageView) findViewById(R.id.i_register_password_wdh);

		mTv_agree_data_secur = (TextView) findViewById(R.id.tv_agree_data_secur);
		mTv_agree_exec = (TextView) findViewById(R.id.tv_agree_exec);
		mCb_agree_data_secur = (CheckBox) findViewById(R.id.cb_agree_data_secur);
		mCb_agree_exec = (CheckBox) findViewById(R.id.cb_agree_exec);

		/********************************
		 * Checks if the user entered * username-Data is correct *
		 ********************************/
		mEditTextUserName.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					AccountRegisterValidator.checkUsername(mEditTextUserName
							.getText().toString(), new OnValidatorResult() {
						public void onResult(boolean isValid) {

						}

						@Override
						public void onResult(boolean isValid, String errorMsg) {
							Log.i("Username validation", "isValid: " + isValid);
							mValidateInputData[0] = isValid;
							if (isValid) {
								mEditTextUserName
										.setBackgroundResource(R.drawable.custom_edittext_focused);
								mImageUserName
										.setImageResource(R.drawable.ic_action_confirm);
							} else {
								mEditTextUserName
										.setBackgroundResource(R.drawable.custom_edittext_focused_red);
								mImageUserName
										.setImageResource(R.drawable.ic_cancel);
								if (errorMsg != null) {
									// Toast.makeText(RegisterActivity.this,
									// errorMsg, Toast.LENGTH_SHORT).show();
									Dialog error = new ValidationErrorDialog(
											RegisterActivity.this, errorMsg);
									error.show();
								}
							}
						}
					});
				}
			}
		});
		/********************************
		 * Checks if the user entered * Email-Data is correct *
		 ********************************/
		mEditTextEmail.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					AccountRegisterValidator.checkEmail(mEditTextEmail
							.getText().toString(), new OnValidatorResult() {
						public void onResult(boolean isValid) {
							mValidateInputData[1] = isValid;
							if (isValid) {
								mEditTextEmail
										.setBackgroundResource(R.drawable.custom_edittext_focused);
								mImageEmail
										.setImageResource(R.drawable.ic_action_confirm);
							} else {
								mEditTextEmail
										.setBackgroundResource(R.drawable.custom_edittext_focused_red);
								mImageEmail
										.setImageResource(R.drawable.ic_cancel);
							}
						}

						@Override
						public void onResult(boolean isValid, String errorMsg) {

						}
					});
				}
			}
		});
		/************************************
		 * Checks if the user entered * Password-Data is correct *
		 ************************************/
		mEditTextPassword.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					/*
					 * AccountRegisterValidator.checkPassword(mEditTextPassword
					 * .getText().toString(), new OnValidatorResult() { public
					 * void onResult(boolean isValid) { mValidateInputData[3] =
					 * isValid; if (isValid) { mEditTextPassword
					 * .setBackgroundResource
					 * (R.drawable.custom_edittext_focused); mImagePassword
					 * .setImageResource(R.drawable.ic_action_confirm); } else {
					 * mEditTextPassword
					 * .setBackgroundResource(R.drawable.custom_edittext_focused_red
					 * ); mImagePassword
					 * .setImageResource(R.drawable.ic_cancel); } }
					 * 
					 * @Override public void onResult(boolean isValid, String
					 * errorMsg) { //
					 * 
					 * } });
					 */
					mValidateInputData[3] = AccountRegisterValidator
							.checkPassword(mEditTextPassword.getText()
									.toString());
					if (mValidateInputData[3]) {
						mEditTextPassword
								.setBackgroundResource(R.drawable.custom_edittext_focused);
						mImagePassword
								.setImageResource(R.drawable.ic_action_confirm);
					} else {
						mEditTextPassword
								.setBackgroundResource(R.drawable.custom_edittext_focused_red);
						mImagePassword.setImageResource(R.drawable.ic_cancel);
					}

				}
			}
		});
		/*************************************
		 * Checks if the user entered * second Password-Data is correct *
		 *************************************/
		mEditTextPasswordWdh
				.setOnFocusChangeListener(new OnFocusChangeListener() {
					public void onFocusChange(View v, boolean hasFocus) {
						if (!hasFocus) {
							mValidateInputData[4] = AccountRegisterValidator
									.checkPassword(mEditTextPassword.getText()
											.toString(), mEditTextPasswordWdh
											.getText().toString());
							boolean pass = AccountRegisterValidator
									.checkPassword(mEditTextPasswordWdh
											.getText().toString());
							if (mValidateInputData[4] && pass) {
								mEditTextPasswordWdh
										.setBackgroundResource(R.drawable.custom_edittext_focused);
								mImagePasswordWdh
										.setImageResource(R.drawable.ic_action_confirm);
							} else {
								mEditTextPasswordWdh
										.setBackgroundResource(R.drawable.custom_edittext_focused_red);
								mImagePasswordWdh
										.setImageResource(R.drawable.ic_cancel);
							}
						}
					}
				});

		/**
		 * set Datenschutzerklärung
		 */

		mTv_agree_data_secur
				.setMovementMethod(LinkMovementMethod.getInstance());
		mTv_agree_exec.setMovementMethod(LinkMovementMethod.getInstance());

		mBtnRegister = (Button) findViewById(R.id.btn_register_register);
		mBtnRegister.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCb_agree_data_secur.isChecked()
						&& mCb_agree_exec.isChecked())
					/****************************************
					 * check if all user inputs are * valid and username and
					 * email are * not taken *
					 ****************************************/
					AccountRegisterValidator.checkAll(mEditTextUserName
							.getText().toString(), mEditTextEmail.getText()
							.toString(),
							mEditTextPassword.getText().toString(),
							mEditTextPasswordWdh.getText().toString(),
							new OnExtendedValidatorResult() {

								@Override
								public void onResult(boolean[] results,
										String errorMessage) {

									mValidateInputData = results;
									if (results != null) {
										if (mValidateInputData[0]
												&& mValidateInputData[1]
												&& mValidateInputData[2]
												&& mValidateInputData[4]) {

											String userName = mEditTextUserName
													.getText().toString();
											String email = mEditTextEmail
													.getText().toString();
											String password = mEditTextPassword
													.getText().toString();
											String firstName = mEditTextFirstName
													.getText().toString();
											String lastName = mEditTextLastName
													.getText().toString();
											String streetName = mEditTextStreetName
													.getText().toString();
											String houseNumber = mEditTextHouseNumber
													.getText().toString();
											String zip = mEditTextZip.getText()
													.toString();
											String state = mEditTextState
													.getText().toString();

											/****************************************************
											 * register account if "check all"
											 * passes through *
											 ****************************************************/
											NexxooWebservice.registerAccount(
													true, userName, // user-name
													email, // email
													password, // password
													firstName, // firstName
													lastName, // lastName
													mDateInMillis, // dateOfBirth
																	// in
																	// milli's
													streetName, // street
													houseNumber, // houseNumber
													state, // state
													zip, // ZIP
													new OnJSONResponse() {
														@Override
														public void onReceivedJSONResponse(
																JSONObject json) {
															Log.d(Nexxoo.TAG2,
																	json.toString());
															if (json != null) {
																Toast.makeText(
																		getApplicationContext(),
																		R.string.register_success_toast,
																		Toast.LENGTH_LONG)
																		.show();
																finish();
															}
														}

														@Override
														public void onReceivedError(
																String msg,
																int code) {
															Log.d(Nexxoo.TAG,
																	msg);
															Toast.makeText(
																	getApplicationContext(),
																	msg,
																	Toast.LENGTH_LONG)
																	.show();
														}
													});
										} else {
											/****************************************
											 * handle wrong user input and mark
											 * * wrong edit-text fields *
											 ****************************************/
											Boolean errDialogShow = false;

											if (mEditTextUserName.getText()
													.toString().isEmpty()
													&& mEditTextEmail.getText()
															.toString()
															.isEmpty()
													&& mEditTextPassword
															.getText()
															.toString()
															.isEmpty()
													&& mEditTextPasswordWdh
															.getText()
															.toString()
															.isEmpty()) {
												setError(mEditTextUserName,
														mImageUserName);
												setError(mEditTextEmail,
														mImageEmail);
												setError(mEditTextPassword,
														mImagePassword);
												setError(mEditTextPasswordWdh,
														mImagePasswordWdh);
												return;
											}

											if (!mValidateInputData[0]) {
												mEditTextUserName
														.setBackgroundResource(R.drawable.custom_edittext_focused_red);
												mImageUserName
														.setImageResource(R.drawable.ic_cancel);
												Dialog error = new ValidationErrorDialog(
														RegisterActivity.this,
														getString(R.string.validation_username_error));
												errDialogShow = true;
												error.show();
											} else {
												mEditTextUserName
														.setBackgroundResource(R.drawable.custom_edittext_focused);
												mImageUserName
														.setImageResource(R.drawable.ic_action_confirm);
											}

											if (!mValidateInputData[1]) {
												mEditTextEmail
														.setBackgroundResource(R.drawable.custom_edittext_focused_red);
												mImageEmail
														.setImageResource(R.drawable.ic_cancel);

												if (errDialogShow == false) {
													Dialog error = null;
													error = new ValidationErrorDialog(
															RegisterActivity.this,
															getString(R.string.validation_email_error));
													errDialogShow = true;
													error.show();
												}
											} else {
												mEditTextEmail
														.setBackgroundResource(R.drawable.custom_edittext_focused);
												mImageEmail
														.setImageResource(R.drawable.ic_action_confirm);
											}

											if (!mValidateInputData[3]) {
												mEditTextPassword
														.setBackgroundResource(R.drawable.custom_edittext_focused_red);
												mImagePassword
														.setImageResource(R.drawable.ic_cancel);
												if (errDialogShow == false) {
													Dialog error = new ValidationErrorDialog(
															RegisterActivity.this,
															getString(R.string.validation_wrong_password_error));
													errDialogShow = true;
													error.show();
												}
											} else {
												mEditTextPassword
														.setBackgroundResource(R.drawable.custom_edittext_focused);
												mImagePassword
														.setImageResource(R.drawable.ic_action_confirm);
											}
											if (!mValidateInputData[2]) {
												mEditTextPasswordWdh
														.setBackgroundResource(R.drawable.custom_edittext_focused_red);
												mImagePasswordWdh
														.setImageResource(R.drawable.ic_cancel);
												if (errDialogShow == false) {
													Dialog error = new ValidationErrorDialog(
															RegisterActivity.this,
															getString(R.string.validation_passwords_match_error));
													error.show();
												}
											} else {
												mEditTextPasswordWdh
														.setBackgroundResource(R.drawable.custom_edittext_focused);
												mImagePasswordWdh
														.setImageResource(R.drawable.ic_action_confirm);
											}

											if ((errorMessage != null)
													&& (errDialogShow == false)) {
												Log.i("Username validation",
														"errorMessage "
																+ errorMessage);

												Dialog error = new ValidationErrorDialog(
														RegisterActivity.this,
														errorMessage);
												error.show();
											}

										}
									}
								}
							});
				else {// don't agree with the policy
						Dialog error = new ValidationErrorDialog(
								RegisterActivity.this,
								getString(R.string.validation_do_not_agree_policy));
						error.show();
				}
			}
		});

		/************
		 * set date *
		 ************/
		mBtnDateOfBirth = (Button) findViewById(R.id.et_register_date_of_birth);
		mBtnDateOfBirth.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mYear == -1 && mMonth == -1 && mDay == -1) {
					// default date
					Calendar cal = Calendar.getInstance();
					mYear = cal.get(Calendar.YEAR) - 18;
					mMonth = 0;
					mDay = 1;
				}
				new DatePickerDialog(RegisterActivity.this, mDateSetListener,
						mYear, mMonth, mDay).show();
			}
		});
	}

	private void setError(EditText editText, ImageView img) {
		editText.setBackgroundResource(R.drawable.custom_edittext_focused_red);
		img.setImageResource(R.drawable.ic_cancel);
	}

	private void setOK(EditText editText, ImageView img) {
		editText.setBackgroundResource(R.drawable.custom_edittext_focused);
		img.setImageResource(R.drawable.ic_action_confirm);
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
			if ((cal.get(Calendar.YEAR) - mYear) > 18) {
				mDateInMillis = c.getTimeInMillis() / 1000L;
				mBtnDateOfBirth.setText(String.format("%02d.%02d.%d", mDay,
						(mMonth + 1), mYear));
			} else {
				if (view.isShown()) {
					Dialog error = new ValidationErrorDialog(
							RegisterActivity.this,
							getString(R.string.appstock_text_adult));
					error.show();
				}
			}
		}
	};

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
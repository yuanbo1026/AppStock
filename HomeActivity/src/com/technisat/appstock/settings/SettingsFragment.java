package com.technisat.appstock.settings;

import java.util.Calendar;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.technisat.appstock.R;
import com.technisat.appstock.app.ActionBarHelper;
import com.technisat.appstock.app.BaseDrawerActivity;
import com.technisat.appstock.app.BaseFragment;
import com.technisat.appstock.drawer.MyDrawer;
import com.technisat.appstock.entity.Account;
import com.technisat.appstock.entity.singleton.AccountLoggedIn;
import com.technisat.appstock.exception.AppStockContentError;
import com.technisat.appstock.exception.AppStockSingletonException;
import com.technisat.appstock.login.LoginActivity;
import com.technisat.appstock.login.RegisterActivity;
import com.technisat.appstock.login.ValidationErrorDialog;
import com.technisat.appstock.utils.StringUtils;
import com.technisat.appstock.validator.AccountRegisterValidator;
import com.technisat.appstock.validator.OnExtendedValidatorResult;
import com.technisat.appstock.validator.OnValidatorResult;
import com.technisat.appstock.webservice.NexxooWebservice;
import com.technisat.appstock.webservice.OnJSONResponse;
import com.technisat.appstock.webservice.error.JSONErrorHandler;
import com.technisat.constants.Nexxoo;

public class SettingsFragment extends BaseFragment {
	
	private EditText mEditTextEmail;
	private EditText mEditTextPassword;
	private EditText mEditTextPasswordNew;
	private EditText mEditTextPasswordNewWdh;
	private EditText mEditTextFirstName;
	private EditText mEditTextLastName;
	private Button mBtnDateOfBirth;
	private EditText mEditTextStreetName;
	private EditText mEditTextHouseNumber;
	private EditText mEditTextZip;
	private EditText mEditTextState;
	private Account acc;
	private int mYear;
	private int mMonth;
	private int mDay;
	private Button mBtnRegisterDev;
	private Button mBtnLogout;
	protected long mDateInMillis;

	public SettingsFragment(){
		fragmentId = MyDrawer.BUTTON_SETTINGS;
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if(AccountLoggedIn.isSet())
			try {
				acc = AccountLoggedIn.getInstance();
			} catch (AppStockSingletonException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		mEditTextEmail.setOnFocusChangeListener(new OnFocusChangeListener() {          
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
	                AccountRegisterValidator.checkEmail(mEditTextEmail.getText().toString(), new OnValidatorResult(){
	        			public void onResult(boolean isValid){
	        				if(isValid || mEditTextEmail.getText().toString().equals("")){
	        					mEditTextEmail.setBackgroundResource(R.drawable.custom_edittext_focused);
	        				} else {
	        					mEditTextEmail.setBackgroundResource(R.drawable.custom_edittext_focused_red);
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
                if(hasFocus){
                	mEditTextPassword.setHint(getString(R.string.appstock_settings_oldpassword));
	                mEditTextPasswordNew.setVisibility(View.VISIBLE);
	                mEditTextPasswordNewWdh.setVisibility(View.VISIBLE);
                }
//                else {
//                	if(mEditTextPassword.getText().toString().equals("") &&
//                			mEditTextPasswordNew.getText().toString().equals("") &&
//                			mEditTextPasswordNewWdh.getText().toString().equals("")){
//                		
//	                	mEditTextPassword.setHint(getString(R.string.appstock_settings_stars));
//		                mEditTextPasswordNew.setVisibility(View.GONE);
//		                mEditTextPasswordNewWdh.setVisibility(View.GONE);
//		                
//                	}
//                }
            }
        });
    	
    	mBtnRegisterDev = (Button) getView().findViewById(R.id.btn_register_as_developer);
    	if(AccountLoggedIn.isSet() && acc.getRoleId() == Account.ROLE_DEVELOPER){
    		mBtnRegisterDev.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(getActivity(), DeveloperInfoActivity.class);
					startActivity(intent); 
				}
			});
    	} else {
	    	mBtnRegisterDev.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(getActivity(), RegisterAsDeveloperActivity.class);
					startActivity(intent);
				}
			});
    	}
    	
    	mBtnLogout = (Button) getView().findViewById(R.id.btn_logout);
    	mBtnLogout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {				
				createLogoutDialog().show();
			}
		});
    	
    	mBtnDateOfBirth.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mYear == -1 && mMonth == -1 && mDay == -1){
					// default date
					Calendar cal = Calendar.getInstance();
					mYear = cal.get(Calendar.YEAR) - 18;
					mMonth = 4;     mDay = 1;
				}
				new DatePickerDialog( getActivity() , mDateSetListener, mYear, mMonth, mDay).show();
			}
		});
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(AccountLoggedIn.isSet()){
			try {
				ActionBarHelper.setActionBarTitle(getActivity(), getActivity().getActionBar(), "Einstellungen");
				

				acc=AccountLoggedIn.getInstance();
				
				mEditTextEmail.setHint(StringUtils.stringOrText(acc.getUser().getEmail(), getString(R.string.user_reg_email)));
	    		mEditTextFirstName.setHint(StringUtils.stringOrText(acc.getUser().getFirstName(), getString(R.string.user_reg_firstname)));
	    		mEditTextLastName.setHint(StringUtils.stringOrText(acc.getUser().getLastName(), getString(R.string.user_reg_lastname)));
	    		
	    		Calendar calendar = Calendar.getInstance();
	    		calendar.setTimeInMillis(acc.getUser().getDateOfBirth()*1000);
	    		
	    		Log.e("Settings", acc.getUser().getDateOfBirth()+"");
	    		
	    		mYear = calendar.get(Calendar.YEAR);
	    		mMonth = calendar.get(Calendar.MONTH);
	    		mDay = calendar.get(Calendar.DAY_OF_MONTH);
	    		
	    		Log.e("Settings", mDay + "." + (mMonth+1) + "." + mYear);
	    		mBtnDateOfBirth.setHint(StringUtils.stringOrText(mDay + "." + (mMonth+1) + "." + mYear, getString(R.string.user_reg_date_of_birth)));
	    		
	    		mEditTextStreetName.setHint(StringUtils.stringOrText(acc.getUser().getStreet(), getString(R.string.user_reg_streetname)));
	    		mEditTextHouseNumber.setHint(StringUtils.stringOrText(acc.getUser().getHouseNumber(), getString(R.string.user_reg_str_number)));
	    		mEditTextZip.setHint(StringUtils.stringOrText(acc.getUser().getZipCode(), getString(R.string.user_reg_zip)));
	    		mEditTextState.setHint(StringUtils.stringOrText(acc.getUser().getState(), getString(R.string.user_reg_state)));

	    		mEditTextPassword.setHint(getString(R.string.user_reg_passwd_old));
	    		mEditTextPasswordNew.setHint(getString(R.string.user_reg_passwd_new));
	    		mEditTextPasswordNewWdh.setHint(getString(R.string.user_reg_passwd_new_replace));
	    	
	    		
	    		
			} catch (AppStockSingletonException e) {
				// alredy checked
			}
    	}
		clearInputFields();
	}
	
	private void refreshViews(){
		mEditTextPasswordNew.setVisibility(View.GONE);
        mEditTextPasswordNewWdh.setVisibility(View.GONE);
        mEditTextPassword.setText("");
        mEditTextPasswordNew.setText("");
		mEditTextPasswordNewWdh.setText("");
        
	}
	
	private void clearInputFields(){
		mEditTextEmail.setText("");
		mEditTextPassword.setText("");
		mEditTextPasswordNew.setText("");
		mEditTextPasswordNewWdh.setText("");
		mEditTextFirstName.setText("");
		mEditTextLastName.setText("");
		mBtnDateOfBirth.setText("");
		mEditTextStreetName.setText("");
		mEditTextHouseNumber.setText("");
		mEditTextZip.setText("");
		mEditTextState.setText("");
	}
	
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			 mYear = year;
			 mMonth = monthOfYear;
			 mDay = dayOfMonth;
			 Calendar c = Calendar.getInstance();
			 Calendar cal = Calendar.getInstance();
			 c.set(mYear, mMonth, mDay);
			 if ((cal.get(Calendar.YEAR)-mYear)>18){
				 mDateInMillis = c.getTimeInMillis()/ 1000L;
				 Log.e("Settings set", mDateInMillis+"");
				 mBtnDateOfBirth.setText(mDay+"."+(mMonth + 1)+"."+mYear);
			}
				else {
					if(view.isShown()) { 
					Dialog error = new ValidationErrorDialog(
							getActivity(),
							getString(R.string.appstock_text_adult));
					error.show();
					}
				}
		 }
	};
	protected boolean[] mValidateInputData;
	protected Dialog ringProgressDialog;
	
	public AlertDialog createLogoutDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(getString(R.string.appstock_settings_logout));
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (AccountLoggedIn.isSet()){
					LoginActivity.forgetLogin(getActivity());
					try {
						NexxooWebservice.logoutAccount(true, AccountLoggedIn.getInstance().getAccountId(), AccountLoggedIn.getInstance().getSessionKey(),
								new OnJSONResponse() {
									
									@Override
									public void onReceivedJSONResponse(JSONObject json) {
										//logout successfully
										AccountLoggedIn.clearInstance();
										/**
										 * stack should be cleared, but
										 * it will result in application crash
										 */
//										FragmentManager fm = getActivity().getSupportFragmentManager();
//										fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

										
										Toast.makeText(getActivity(), getString(R.string.appstock_settings_logoutsuccess), Toast.LENGTH_SHORT).show();
										Intent intent = new Intent(getActivity(), BaseDrawerActivity.class);
										intent.setAction(getActivity().getString(R.string.appstock_action_logout));
										intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										
										startActivity(intent);
										getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
									}
									
									@Override
									public void onReceivedError(String msg, int code) {
										Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();									
									}
								});
					} catch (AppStockSingletonException e) {
						//not logged in obviously
						getActivity().finish(); //possible wrong behavior
					}
				}
				
			}
		});
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// DO NOTHING!
			}
		});
		return builder.create();
	}
	
	private void validateInput(){
		AccountRegisterValidator.checkAll(
				acc.getUser().getUserName(),
				mEditTextEmail.getText().toString(),
				mEditTextPasswordNew.getText().toString(),
				mEditTextPasswordNewWdh.getText().toString(),
				
				new OnExtendedValidatorResult(){
					
					@Override
					public void onResult(boolean[] results, String errorMessage) {
						if (results != null){
							mValidateInputData = results;
							if(mEditTextEmail.getText().length() <= 0){
								mValidateInputData[1] = true;
							}
							if(mEditTextPassword.getText().length() <= 0){
								mValidateInputData[2] = true;
							}
							if(mValidateInputData[1] && mValidateInputData[2]){
								Log.d("JRe",(int)(mDateInMillis/1000L)+"");
								NexxooWebservice.updateAccount(true,
										acc.getAccountId(), 						// accountId
										acc.getSessionKey(),						// sessionKey
										mEditTextEmail.getText().toString(), 		// email
										mEditTextPasswordNew.getText().toString(), 	// new password
										mEditTextFirstName.getText().toString(), 	// firstName
										mEditTextLastName.getText().toString(), 	// lastName
										mDateInMillis, 								// dateOfBirth in millis; -1 if not set
										mEditTextStreetName.getText().toString(), 	// street
										mEditTextHouseNumber.getText().toString(), 	// houseNumber
										mEditTextState.getText().toString(), 		// state
										mEditTextZip.getText().toString(), 			// zip
										new OnJSONResponse() {
											@Override
											public void onReceivedJSONResponse(JSONObject json) {

												try {
													Account acc = new Account(json);
													Log.d("JRe",json.toString());
													if (AccountLoggedIn.isSet())
														AccountLoggedIn.clearInstance();
													LoginActivity.saveLogin(getActivity(), json.toString());
													AccountLoggedIn.initAccountLoggedIn(acc);
													///////////////////////////////////////////////////////////////////////////////////////////
												} catch (AppStockContentError e) {
													Log.d(Nexxoo.TAG, "login failed: " + e.getMessage());
												} catch (AppStockSingletonException e) {
													Log.d(Nexxoo.TAG, "Error with singleton: " + e.getMessage());
												} finally {
													ringProgressDialog.dismiss();
												}
												
												refreshViews();

												Toast.makeText(getActivity(), getString(R.string.appstock_settings_updateaccountsuccess), Toast.LENGTH_LONG).show();
											}
											@Override
											public void onReceivedError(String msg, int code) {
												ringProgressDialog.dismiss();
												Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();								
											}
								});
							} else { // this is where the user gets to see what he was doning wrong
								
								
								ringProgressDialog.dismiss();
								
								Dialog error = null;						
								
								if (!mValidateInputData[1]) {
									mEditTextEmail
											.setBackgroundResource(R.drawable.custom_edittext_focused_red);
									
									if (error == null) {
										error = new ValidationErrorDialog(
												getActivity(),
												getString(R.string.validation_email_error));
									}
								} else {
									mEditTextEmail
											.setBackgroundResource(R.drawable.custom_edittext_focused);
								}

								if (!mValidateInputData[3]) {
									mEditTextPasswordNew
											.setBackgroundResource(R.drawable.custom_edittext_focused_red);
																		if (error == null) {
										error = new ValidationErrorDialog(
												getActivity(),
												getString(R.string.validation_wrong_password_error));
									}
								} else {
									mEditTextPassword
											.setBackgroundResource(R.drawable.custom_edittext_focused);

								}
								if (!mValidateInputData[2]
										|| !mValidateInputData[4]) {
									mEditTextPasswordNewWdh
											.setBackgroundResource(R.drawable.custom_edittext_focused_red);

									if (error == null) {
										error = new ValidationErrorDialog(
												getActivity(),
												getString(R.string.validation_passwords_match_error));
									}
								} else {
									mEditTextPasswordNewWdh
											.setBackgroundResource(R.drawable.custom_edittext_focused);
								}
								
								error.show();
							}
						} else {
							ringProgressDialog.dismiss();
							Toast.makeText(getActivity(),
									getString(R.string.appstock_settings_updateaccountfailed),
									Toast.LENGTH_LONG).show();
						}
					}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		
		View view = inflater.inflate(R.layout.activity_settings, container, false); 
		
		mEditTextEmail = (EditText) view.findViewById(R.id.et_settings_email);
    	mEditTextPassword = (EditText) view.findViewById(R.id.et_settings_password);
    	mEditTextPasswordNew = (EditText) view.findViewById(R.id.et_settings_password_new);
    	mEditTextPasswordNewWdh = (EditText) view.findViewById(R.id.et_settings_password_new_wdh);
    	mEditTextFirstName = (EditText) view.findViewById(R.id.et_settings_firstname);
    	mEditTextLastName = (EditText) view.findViewById(R.id.et_settings_lastname);
    	mBtnDateOfBirth = (Button) view.findViewById(R.id.et_settings_date_of_birth);
    	mEditTextStreetName = (EditText) view.findViewById(R.id.et_settings_streetname);
    	mEditTextHouseNumber = (EditText) view.findViewById(R.id.et_settings_number);
    	mEditTextZip = (EditText) view.findViewById(R.id.et_settings_zip);
    	mEditTextState = (EditText) view.findViewById(R.id.et_settings_state);
		return view;
	}

	@Override
	public String getStackId() {
		return SettingsFragment.class.getSimpleName();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		inflater.inflate(R.menu.cancel_and_save_menu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			getActivity().onBackPressed();
			return true;
		case R.id.action_save:
			ringProgressDialog = ProgressDialog
					.show(getActivity(),
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
							Toast.makeText(getActivity(), msg,
									Toast.LENGTH_SHORT).show();
							ringProgressDialog.dismiss();
							break;
						default:
							Toast.makeText(getActivity(), msg,
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
}

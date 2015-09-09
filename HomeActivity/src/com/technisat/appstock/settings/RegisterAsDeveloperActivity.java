package com.technisat.appstock.settings;

import java.math.BigDecimal;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.technisat.appstock.R;
import com.technisat.appstock.app.ActionBarHelper;
import com.technisat.appstock.entity.Account;
import com.technisat.appstock.entity.singleton.AccountLoggedIn;
import com.technisat.appstock.exception.AppStockSingletonException;
import com.technisat.appstock.validator.AccountRegisterValidator;
import com.technisat.appstock.webservice.NexxooWebservice;
import com.technisat.appstock.webservice.OnJSONResponse;
import com.technisat.constants.Nexxoo;
import com.technisat.constants.NexxooPayment;

// creditcard-numbers below. In sandbox only creditcard set in PayPal-account worked
// http://www.paypalobjects.com/en_US/vhelp/paypalmanager_help/credit_card_numbers.htm

/************************************************
 * Activity to register a user as an developer	*
 * called from settings activity				*
 ************************************************/ // TODO get correct text for User Guidlines
public class RegisterAsDeveloperActivity extends Activity {
	
    private static PayPalConfiguration config = new PayPalConfiguration()
        .environment(NexxooPayment.PAYPAL_CONFIG_ENVIRONMENT)
        .clientId(NexxooPayment.PAYPAL_CONFIG_CLIENT_ID)
        // The following are only used in PayPalFuturePaymentActivity.
        .merchantName("AppStock")
        .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
        .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));
	
	private Button btnPay = null;
	private EditText editTextOwner = null, editTextIBAN = null, editTextBIC = null;
	private CheckBox checkBox = null;
	private Account acc = null;
	private String iban = null, bic = null;
	private double devPrice = -1;

	//TODO: remove this function and turn on _onCreate if You want to view register as developer view
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dummy);

        ActionBarHelper.setActionBarTitle(this, getActionBar(), "Als Entwickler registrieren");
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
	}
	//TODO: change this function to onCreate if You want to view register as developer view
	//@Override
	protected void _onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_as_developer);
		
		Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
        
        ActionBarHelper.setActionBarTitle(this, getActionBar(), "Entwickler account");
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        ((TextView)findViewById(R.id.tv_settings_edit_account2)).setText(Html.fromHtml(getString(R.string.imprintactivity_text_imprint)));
        TypedValue tv = new TypedValue();
		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
		{
		    int height = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
			
			ImageView icon = (ImageView) findViewById(android.R.id.home);
			FrameLayout.LayoutParams iconLp = (FrameLayout.LayoutParams) icon.getLayoutParams();
			iconLp.topMargin = iconLp.bottomMargin = (int) (height * 0.08f);
			iconLp.height = LayoutParams.MATCH_PARENT;
			iconLp.width = LayoutParams.MATCH_PARENT;
			icon.setLayoutParams( iconLp );
		}
        btnPay = (Button) findViewById(R.id.btn_dev_pay_and_register);
        try {
			acc = AccountLoggedIn.getInstance();
        } catch (AppStockSingletonException e) { }
        
        /****************************************************
         * get price for developer upgrade/registration		*
         * from web service and use it in PayPal payment	*
         ****************************************************/
        NexxooWebservice.getPriceForDevAccountUpgrade(true, acc.getAccountId(), acc.getSessionKey(), new OnJSONResponse() {
			
			@Override
			public void onReceivedJSONResponse(JSONObject json) {
				devPrice = json.optDouble("price", 0);
				btnPay.setEnabled(true);
			}
			
			@Override
			public void onReceivedError(String msg, int code) { 
				btnPay.setEnabled(false);
				Log.e(Nexxoo.TAG2, "Error receiving price for dev-upgrade: " + msg + "\n Code: " + code);
			}
		}); 
        
        editTextOwner = (EditText) findViewById(R.id.et_dev_ownername);
        editTextIBAN = (EditText) findViewById(R.id.et_dev_iban);
        editTextBIC = (EditText) findViewById(R.id.et_dev_bic);
        checkBox = (CheckBox) findViewById(R.id.checkBox_dev_accept);
        
        btnPay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				checkBox.setButtonDrawable(R.drawable.checkbox_blue);
				iban = editTextIBAN.getText().toString().replaceAll("\\s+",""); 	// removes all whitespaces and non
				bic = editTextBIC.getText().toString().replaceAll("\\s+","");		// visible characters such as tab or \n
				
				/****************************************************
				 * check if user agreed with the user guidelines	*
				 * and if entered IBAN and BIC are valid			*
				 ****************************************************/
				if(checkBox.isChecked() &&
						AccountRegisterValidator.checkIBAN(iban) &&
						AccountRegisterValidator.checkBIC(bic)){
					try {
						acc = AccountLoggedIn.getInstance();
						
				        PayPalPayment thingToBuy =
				                new PayPalPayment(new BigDecimal(devPrice), NexxooPayment.PAYMENT_CURRENCY, getString(R.string.appstock_text_upgradetodeveloper),
				                        PayPalPayment.PAYMENT_INTENT_SALE);
		
				        Intent intent = new Intent(RegisterAsDeveloperActivity.this, PaymentActivity.class);
				        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
				        startActivityForResult(intent, NexxooPayment.PAYPAL_REQUEST_CODE_PAYMENT);
			        
					} catch (AppStockSingletonException e) {}
					
				/********************************************
				 * show user wrong input by highlighting	*
				 * wrong edit text fields and displaying	*
				 * an message.								*
				 ********************************************/
				} else if(!checkBox.isChecked()){
					editTextOwner.setBackgroundResource(R.drawable.custom_edittext_focused);
					editTextIBAN.setBackgroundResource(R.drawable.custom_edittext_focused);
					editTextBIC.setBackgroundResource(R.drawable.custom_edittext_focused);
					Toast.makeText(getApplicationContext(), getString(R.string.appstock_text_acceptguidelines), Toast.LENGTH_SHORT).show();
				} else{
					editTextOwner.setBackgroundResource(R.drawable.custom_edittext_focused_red);
					editTextIBAN.setBackgroundResource(R.drawable.custom_edittext_focused_red);
					editTextBIC.setBackgroundResource(R.drawable.custom_edittext_focused_red);
					if(!AccountRegisterValidator.checkIBAN(iban)) {
						Toast.makeText(getApplicationContext(), getString(R.string.appstock_text_incorrect_iban), Toast.LENGTH_SHORT).show();
					}
					if (!AccountRegisterValidator.checkBIC(bic)) {
						Toast.makeText(getApplicationContext(), getString(R.string.appstock_text_incorrect_bic), Toast.LENGTH_SHORT).show();
					}
					//refs #16242
					//Toast.makeText(getApplicationContext(), getString(R.string.appstock_text_accountdata), Toast.LENGTH_SHORT).show();
				}
			}
		});
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
	
	@Override
    public void onDestroy() {
        /************************************
         * Stop PayPal service when done	*
         ************************************/
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NexxooPayment.PAYPAL_REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.i("paypal", confirm.toJSONObject().toString(4));
                        Log.i("paypal", confirm.getPayment().toJSONObject().toString(4));
                        
                        final ProgressDialog ringProgressDialog = ProgressDialog.show(RegisterAsDeveloperActivity.this,
                        		getString(R.string.appstock_pleasewait), 
                        		getString(R.string.appstock_text_verifyingpayment), 
                        		true);
        		        ringProgressDialog.setCancelable(false);
        		        
        		        /************************************************************
        		         * upgrade account in database after successful payment		*
        		         * (server validated the payment on his own	)				*
        		         ************************************************************/
                        NexxooWebservice.upgradeAccount(true,
								acc.getAccountId(),
								acc.getSessionKey(),
								iban,
								bic,
								editTextOwner.getText().toString(),
								confirm.toJSONObject().toString(),
								new OnJSONResponse() {
							@Override
							public void onReceivedJSONResponse(JSONObject json) {
								acc.setRoleId(Account.ROLE_DEVELOPER);
								ringProgressDialog.dismiss();
								Intent intent = new Intent(RegisterAsDeveloperActivity.this, DeveloperInfoActivity.class);
								startActivity(intent);
							}
							
							@Override
							public void onReceivedError(String msg, int code) {
								ringProgressDialog.dismiss();
								Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
							}
						});
                        
                    } catch (JSONException e) {
                        Log.e(Nexxoo.PAYPAL, "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(Nexxoo.PAYPAL, "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(Nexxoo.PAYPAL, "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        } else if (requestCode == NexxooPayment.PAYPAL_REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("FuturePaymentExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("FuturePaymentExample", authorization_code);

                    } catch (JSONException e) {
                        Log.e("FuturePaymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("FuturePaymentExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("FuturePaymentExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }
    }
}
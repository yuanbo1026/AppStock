package com.technisat.appstock.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.technisat.appstock.R;
import com.technisat.appstock.content.categorylist.CategoryListFragment;
import com.technisat.appstock.content.categorylist.TopKostenlosFragment;
import com.technisat.appstock.content.categorylist.TopKostenpflichtigFragment;
import com.technisat.appstock.content.detail.ReloadFragment;
import com.technisat.appstock.drawer.MyDrawer;
import com.technisat.appstock.entity.Content;
import com.technisat.appstock.helper.StyleMessageDialog;
import com.technisat.appstock.update.OnUpdateResult;
import com.technisat.appstock.update.UpdateDialog;
import com.technisat.appstock.webservice.NexxooWebservice;
import com.technisat.appstock.webservice.OnJSONResponse;
import com.technisat.constants.FileStorageHelper;
import com.technisat.constants.Nexxoo;

public class BaseDrawerActivity extends FragmentActivity {

	private MyDrawer mMyDrawer;
	private DrawerLayout mDrawerLayout;
	private static final int FRAGMENT_CONTAINER_ID = R.id.frame_container;

	/**
	 * add by b.yuan for update AppStock itself
	 */
	private Context mContext;
	private static ProgressBar mProgressSpinner;
	private static int contentType = 4;
	private static String fileName = "AppStock.apk";
	private int currentVersion, lastestVersion;
	private static UpdateDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_layout_container);
		mContext = this;
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

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(true);

		mMyDrawer = new MyDrawer(this);
		mDrawerLayout = mMyDrawer.getDrawerLayout();

		BaseFragment fragment = new CategoryListFragment();

		mMyDrawer.setcurrentFragment(fragment);
		FragmentManager fm = getSupportFragmentManager();
		if (getIntent().getAction() != null
				&& getIntent().getAction().equals(Intent.ACTION_SEARCH)) {
			getNavigator().showSearchResults(getIntent());
		} else if (fm.findFragmentById(fragment.getFragmentId()) != null) {
			mMyDrawer.showFragment(fragment, true);
		} else {
			if (fm.getFragments() == null || fm.getFragments().size() == 0) {
				fm.beginTransaction().add(FRAGMENT_CONTAINER_ID, fragment)
						.commit();
			}
		}

		checkVersionCode();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent != null && intent.getAction() != null) {
			if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
				getNavigator().showSearchResults(intent);
			} else if (intent.getAction().equals(
					getString(R.string.appstock_action_logout))) {
				BaseFragment fragment = new CategoryListFragment();
				mMyDrawer.showFragment(fragment, true);
			}
		}
	}

	public BaseFragment getActiveFragment() {
		if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
			return null;
		}
		String tag = getSupportFragmentManager().getBackStackEntryAt(
				getSupportFragmentManager().getBackStackEntryCount() - 1)
				.getName();
		return (BaseFragment) getSupportFragmentManager()
				.findFragmentByTag(tag);
	}

	public MyDrawer getNavigator() {
		return mMyDrawer;
	}

	public void saveFragmentBundle(BaseFragment fragment) {
		mMyDrawer.saveFragmentBundle(fragment);
	}

	@Override
	public void onBackPressed() {
		if (mMyDrawer.onBackPressed())
			return;

		int backStackEntryCount = getSupportFragmentManager()
				.getBackStackEntryCount();
		if (backStackEntryCount == 0) {
			finish();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		BaseFragment currentFragment = mMyDrawer.getCurrentDrawer();

		Log.d("JD", "cf=" + mMyDrawer.getCurrentDrawer().getStackId());
		Log.d("JD", "args=" + currentFragment.getArguments());
		if (currentFragment instanceof CategoryListFragment
				&& currentFragment.getArguments() != null) {
			return false;
		}
		switch (item.getItemId()) {
		case android.R.id.home:
			if (!mDrawerLayout.isDrawerOpen(Gravity.START)) {
				mDrawerLayout.openDrawer(Gravity.START);
			} else {
				mDrawerLayout.closeDrawer(Gravity.START);
			}
			return true;
		default:
			return false;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mMyDrawer.updateView();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("JD", "onActivityResult");
		if (resultCode == RESULT_OK) {
			if (data.getBooleanExtra(getString(R.string.appstock_extra_reload),
					false)) {
				Content c = (Content) data
						.getParcelableExtra(getString(R.string.appstock_extra_reload_object));
				// update object?
				if (mMyDrawer.getCurrentDrawer() instanceof CategoryListFragment) {
					Log.d("JD", "reload please");
					CategoryListFragment clf = (CategoryListFragment) mMyDrawer
							.getCurrentDrawer();
					if (clf != null) {
						clf.reload();
						Fragment f = clf.getCurrentTabFragment();

						if (f != null
								&& (f instanceof TopKostenlosFragment || f instanceof TopKostenpflichtigFragment)) {
							// reloadActivityWithContent

							((ReloadFragment) f).reload(c);
						}
					}
				}
				Log.d("JD", mMyDrawer.getCurrentDrawer().getStackId());

			}
		}

	}

	public void setDrawerIndicatorEnabled(boolean flag) {
		mMyDrawer.setDrawerIndicatorEnabled(flag);
	}

	private void checkVersionCode() {
		/**
		 * check the latest version code from database by calling web-service
		 */
		NexxooWebservice.getLatestVersionCode(true, new OnJSONResponse() {

			@Override
			public void onReceivedJSONResponse(JSONObject json) {
				try {
					int versioncode = json.getInt("version");
					Log.e(Nexxoo.TAG, "Get version from Database: "
							+ versioncode);
					lastestVersion = versioncode;
					/**
					 * check current version code
					 */
					PackageInfo pinfo;
					try {
						pinfo = getPackageManager().getPackageInfo(
								getPackageName(), 0);
						int versionCode = pinfo.versionCode;
						String versionName = pinfo.versionName;
						Log.e(Nexxoo.TAG, "Version Code: " + versionCode);
						Log.e(Nexxoo.TAG, "Version Name: " + versionName);
						currentVersion = versionCode;
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
					/**
					 * get current version code to make sure the textview on
					 * layout has content to display
					 */

					if (lastestVersion > currentVersion) {
						Update(currentVersion, lastestVersion);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onReceivedError(String msg, int code) {
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
						.show();
				Log.e(Nexxoo.TAG, msg);
			}
		});
	}

	private void Update(int currentVersion, int lastestVersion) {

		dialog = new UpdateDialog(this, Integer.toString(currentVersion),
				Integer.toString(lastestVersion),
				new UpdateDialog.UpdateActionListener() {
					@Override
					public void onSend(final UpdateDialog dialog,
							Boolean updateble) {
						if (updateble) {
							List<NameValuePair> mAdditionalParams = new ArrayList<NameValuePair>();
							mAdditionalParams.add(new BasicNameValuePair(
									"requesttask",
									Integer.toString(NexxooWebservice.WEBTASK_GETUPDATE)));
							NexxooWebservice.performUpdate(mContext,
									mAdditionalParams,
									getCallback(mContext, false));
							
							TextView tv_update_title = (TextView) dialog.findViewById(R.id.tv_update_title);
							tv_update_title.setText("Bitte warten....");

							mProgressSpinner = (ProgressBar) dialog
									.findViewById(R.id.update_spinner);
							mProgressSpinner.setIndeterminate(true);
							mProgressSpinner.setVisibility(View.VISIBLE);
							
							
							/**
							 * set update button not clickable while downloading
							 * the apk file
							 */
							Button upt_button = (Button) dialog
									.findViewById(R.id.btn_update);
							upt_button.setClickable(false);

							// TextView tv_current_code =
							// (TextView)dialog.findViewById(R.id.tv_current_version_code_text);
							// TextView tv_lastest_code =
							// (TextView)dialog.findViewById(R.id.tv_lastest_version_code_text);
							//
							// tv_current_code.setVisibility(View.INVISIBLE);
							// tv_lastest_code.setVisibility(View.INVISIBLE);
							
							// TextView tv_current_code =
							// (TextView)dialog.findViewById(R.id.tv_current_version_code_text);
							// TextView tv_lastest_code =
							// (TextView)dialog.findViewById(R.id.tv_lastest_version_code_text);
							//
							// tv_current_code.setVisibility(View.INVISIBLE);
							// tv_lastest_code.setVisibility(View.INVISIBLE);
							
						} else {
							dialog.dismiss();
						}
					}

				});
		dialog.show();
	}

	public static OnUpdateResult getCallback(final Context mContext,
			boolean reattach) {
		return new OnUpdateResult() {

			@Override
			public void onUpdateSuccess() {
				if (mContext != null) {
					mProgressSpinner.setIndeterminate(false);
					mProgressSpinner.setVisibility(View.INVISIBLE);
					dialog.dismiss();
					File file = new File(
							FileStorageHelper.getDownloadFolder(mContext)
									+ contentType + File.separator + fileName);

					Intent intentForPrepare = null;

					intentForPrepare = new Intent(Intent.ACTION_VIEW);
					intentForPrepare.setDataAndType(Uri.fromFile(file),
							"application/vnd.android.package-archive");
					intentForPrepare.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mContext.startActivity(intentForPrepare);
				}

			}
		};
	}
}

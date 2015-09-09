package com.technisat.appstock.content.categorylist;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.technisat.appstock.R;
import com.technisat.appstock.content.detail.ContentClickListener;
import com.technisat.appstock.content.detail.ContentDetailActivity;
import com.technisat.appstock.content.detail.ContentItemView;
import com.technisat.appstock.entity.Account;
import com.technisat.appstock.entity.App;
import com.technisat.appstock.entity.Content;
import com.technisat.appstock.entity.Magazine;
import com.technisat.appstock.entity.Picture;
import com.technisat.appstock.entity.Video;
import com.technisat.appstock.entity.singleton.AccountLoggedIn;
import com.technisat.appstock.exception.AppStockSingletonException;
import com.technisat.appstock.webservice.NexxooWebservice;
import com.technisat.appstock.webservice.OnJSONResponse;
import com.technisat.constants.FileStorageHelper;
import com.technisat.constants.Nexxoo;

/**
 * Handles the list layout part of that activity
 */
public class ItemListAdapter extends ArrayAdapter<Content> {

	private Context mContext;
	private List<Content> mList;

	private Activity mActivity;

	public ItemListAdapter(Activity activity, int textViewResourceId,
			List<Content> objects) {
		super(activity, textViewResourceId, objects);

		mActivity = activity;
		mContext = activity;
		mList = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Content content = null;
		if (mList.get(position) instanceof Content) {
			content = (Content) mList.get(position);
		}

		ItemView itemView = null;

		if (convertView != null) {
			itemView = (ItemView) convertView;
		} else {
			itemView = new ItemView(mContext);
		}

		itemView.setContent(content);
		itemView.findViewById(R.id.item_container).setBackgroundColor(
				mContext.getResources().getColor(R.color.RealWhite));

		return itemView;
	}

	
}
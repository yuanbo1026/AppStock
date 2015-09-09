package com.technisat.appstock.content.categorylist;

import java.util.List;

import com.technisat.appstock.R;
import com.technisat.appstock.content.detail.ContentDetailActivity;
import com.technisat.appstock.entity.App;
import com.technisat.appstock.entity.Content;
import com.technisat.appstock.entity.Magazine;
import com.technisat.appstock.entity.Video;
import com.technisat.constants.Nexxoo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class ItemListRecycledAdapter extends RecyclerView.Adapter<ItemListRecycledAdapter.ViewHolder> {
	
	public static class ViewHolder extends RecyclerView.ViewHolder {		

		//ItemView itemView;
		ItemView itemView;
		
		public ViewHolder(View itemView) {
			super(itemView);
			this.itemView = (ItemView) itemView;
		}
	}
	
	private List<Content> mList;
	private Context mContext;
	
	public ItemListRecycledAdapter(Context context,
			List<Content> objects) {
		mContext = context;
		mList = objects;
	}
	

	@Override
	public int getItemCount() {
		return mList.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder vh, int position) {
		final Content content = (Content) mList.get(position);

		vh.itemView.setContent(content);
		vh.itemView.findViewById(R.id.item_container).setBackgroundColor(
				mContext.getResources().getColor(R.color.RealWhite));
		
		vh.itemView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ContentDetailActivity.class);
				
				if(content instanceof App){
					Log.d(Nexxoo.TAG, "ich bin eine app");
            		intent.putExtra(mContext.getString(R.string.appstock_extra_content), (App) content);
            	} else if(content instanceof Magazine){
            		Log.d(Nexxoo.TAG, "ich bin ein magazin");
            		intent.putExtra(mContext.getString(R.string.appstock_extra_content), (Magazine) content);
            	} else if(content instanceof Video) {
            		Log.d(Nexxoo.TAG, "ich bin ein video");
            		intent.putExtra(mContext.getString(R.string.appstock_extra_content), (Video) content);
            	} else {
            		Log.d(Nexxoo.TAG, "ich bin ein content :(");
            	}
				
				((Activity) mContext).startActivityForResult(intent, 1);
				
			}		
		});
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		ItemView itemView = new ItemView(parent.getContext(), true);
		ViewHolder vh = new ViewHolder(itemView);
		return vh;
	}
}

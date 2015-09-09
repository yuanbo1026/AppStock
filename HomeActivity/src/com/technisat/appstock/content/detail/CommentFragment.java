package com.technisat.appstock.content.detail;

import java.util.Calendar;

import org.joda.time.DateTime;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.technisat.appstock.R;

/*******************************************************
 * This class is used to display a comment			   *
 * on content detail view and set text and author	   *
 * @author b.nolde									   *
 *******************************************************/
public class CommentFragment extends Fragment {
	String name;
	String comment;
	String date;
	
	@Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
	    return inflater.inflate(R.layout.comment_list_item,
		        container, false);
	  }
	
	

	  @Override
	  public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(name != null){
			populateViews();
		}
	  }



	public void setText(Context context, String name, String comment, long date) {
	    Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date);
		
		DateTime dt = new DateTime();
		
		java.text.DateFormat df = DateFormat.getDateFormat(context);
	    
	    this.name = name + " " + context.getString(R.string.appstock_text_wrote);
	    this.comment = comment;
	    this.date = df.format(dt.withMillis(date * 1000).toDate());
	    
	    if(isInLayout())
	    	populateViews();
	  }
	  
	  private void populateViews(){
		TextView tvName = (TextView) getView().findViewById(R.id.tv_name);
		tvName.setText(name);
		tvName.setVisibility(View.VISIBLE);
		
		TextView tvComment = (TextView) getView().findViewById(R.id.tv_comment);
	    tvComment.setText(comment);
	    tvComment.setVisibility(View.VISIBLE);
	    
	    TextView tvDate = (TextView) getView().findViewById(R.id.tv_date);
	    tvDate.setText(date);
	    tvDate.setVisibility(View.VISIBLE);
	  }
}
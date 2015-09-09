package com.technisat.appstock.imprint;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.technisat.appstock.R;
import com.technisat.appstock.app.ActionBarHelper;
import com.technisat.appstock.app.BaseFragment;
import com.technisat.appstock.drawer.MyDrawer;

public class ImprintFragment extends BaseFragment {

	public ImprintFragment(){
		fragmentId = MyDrawer.BUTTON_IMPRINT;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v =inflater.inflate(R.layout.activity_imprint, container, false);
				
		TextView impritTextView = (TextView)v.findViewById(R.id.imprit_text_view);
		impritTextView.setText(Html.fromHtml(getString(R.string.imprintactivity_text_imprint)));
		impritTextView.setMovementMethod(LinkMovementMethod.getInstance());
		return v;
	}

	@Override
	public String getStackId() {
		return ImprintFragment.class.getSimpleName();
	}

	@Override
	public void onResume() {
		super.onResume();
		
		ActionBarHelper.setActionBarTitle(getActivity(), getActivity().getActionBar(),
				"Impressum");
	}
	
	
}

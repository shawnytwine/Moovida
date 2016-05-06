package com.example.mapdemo;

import java.util.Random;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class Category extends Fragment {

	public Category() {
		// Empty constructor required for fragment
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.category, container, false);

		TextView tv = (TextView) v.findViewById(R.id.title);
		Bundle args = getArguments(); // get the current page title from the bundle passed in
		if (args != null)
			tv.setText(args.getString(BasicMapDemoActivity.CUR_PAGE_TITLE)); // set the page title
		
		// generate some random colot and set as background
		Random rnd = new Random();
		int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

		RelativeLayout background = (RelativeLayout) v.findViewById(R.id.background);
		background.setBackgroundColor(color);

		return v;
	}
}

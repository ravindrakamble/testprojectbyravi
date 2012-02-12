package com.codegreen.ui.activity;

import com.codegreen.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class AboutDetailsActivty extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about_details);
	}
}

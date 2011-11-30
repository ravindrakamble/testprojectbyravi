package com.codegreen.ui.components;

import android.content.Context;
import android.graphics.Typeface;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainSeekBarPreference extends Preference implements OnSeekBarChangeListener{
	
	public static int maximum    = 100;
	private float oldValue = 50;

	public MainSeekBarPreference(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public MainSeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public MainSeekBarPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	
	protected View onCreateView(ViewGroup parent){
		
		RelativeLayout layout = new RelativeLayout(getContext());
		
		//layout.setLayoutParams(dimensionParam);
		//layout.setOrientation(LinearLayout.HORIZONTAL);
		
		TextView titletext = new TextView(getContext());
		titletext.setId(1);
		titletext.setText(getTitle());
		titletext.setTextSize(18);
		titletext.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
		//titletext.setGravity(Gravity.LEFT);
		RelativeLayout.LayoutParams titleDimensionParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		titleDimensionParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		titletext.setLayoutParams(titleDimensionParam);
		layout.addView(titletext);
		
		SeekBar bar = new SeekBar(getContext());
		bar.setId(2);
		bar.setMax(maximum);
		bar.setProgress((int)this.oldValue);
		RelativeLayout.LayoutParams seekbarDimensionParam = new RelativeLayout.LayoutParams(150,RelativeLayout.LayoutParams.WRAP_CONTENT);
		seekbarDimensionParam.addRule(RelativeLayout.BELOW,titletext.getId());
		seekbarDimensionParam.addRule(RelativeLayout.CENTER_IN_PARENT);
		bar.setLayoutParams(seekbarDimensionParam);
		bar.setOnSeekBarChangeListener(this);
		layout.addView(bar);
		
		
		TextView minText = new TextView(getContext());
		minText.setId(3);
		RelativeLayout.LayoutParams textDimensionParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		minText.setText("min");
		minText.setTextSize(14);
		minText.setPadding(0, 0, 10, 0);
		minText.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
		minText.setLayoutParams(textDimensionParam);
		textDimensionParam.addRule(RelativeLayout.LEFT_OF,bar.getId());
		textDimensionParam.addRule(RelativeLayout.BELOW,titletext.getId());
		layout.addView(minText);
		
		
		TextView maxText = new TextView(getContext());
		maxText.setId(4);
		RelativeLayout.LayoutParams textDimensionParam2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		maxText.setText("max");
		maxText.setTextSize(14);
		maxText.setPadding(10, 0, 0, 0);
		maxText.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
		maxText.setLayoutParams(textDimensionParam2);
		textDimensionParam2.addRule(RelativeLayout.RIGHT_OF,bar.getId());
		textDimensionParam2.addRule(RelativeLayout.BELOW,titletext.getId());
		layout.addView(maxText);
		
		return layout;
		
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
		// TODO Auto-generated method stub
		Log.i("MainSeekBar","Progress :- " + progress);
		if(!callChangeListener(progress)){
			seekBar.setProgress(progress); 
			return; 
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

}

class UploadSeekBar extends MainSeekBarPreference
{
	
	public UploadSeekBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public UploadSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public UploadSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	
}

class DownloadSeekBar extends MainSeekBarPreference
{
	
	public DownloadSeekBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public DownloadSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public DownloadSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	
}

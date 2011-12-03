package com.codegreen.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.codegreen.R;

public class SplashActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
         
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splash);

		new Handler().postDelayed(new Runnable(){
			public void run(){
				// If First login then Login screen		
				Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
				startActivity(intent);		
				SplashActivity.this.finish();
			}
		}, 3000);
    } 
}
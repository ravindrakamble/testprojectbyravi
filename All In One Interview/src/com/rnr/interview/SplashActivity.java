package com.rnr.interview;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.Window;

public class SplashActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Oncreate","Splash");
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable(){
			@Override
			public void run() {
				loadNextScreen();
			}
		}, 3000);
        
    }
public void loadNextScreen(){
	Intent intent =new Intent(this,MainActivity.class);
	startActivity(intent);
	this.finish();
	
}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_splash, menu);
        Log.i("onCreateOptionsMenu","Splash");
        return true;
    }
}

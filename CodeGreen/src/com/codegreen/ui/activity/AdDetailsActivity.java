package com.codegreen.ui.activity;

import java.util.ArrayList;

import com.codegreen.R;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.common.CacheManager;
import com.codegreen.util.Constants;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

public class AdDetailsActivity extends Activity{

	private ImageView adImage;
	private int index = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addetails);
		adImage = (ImageView)findViewById(R.id.imgaddetails);
		
		if(this.getIntent() != null){
			index = this.getIntent().getIntExtra("adindex", 0);
			ArrayList<ArticleDAO> advertiseData = (ArrayList<ArticleDAO>) CacheManager.getInstance().get(Constants.C_ADVERTISMENTS);
			Bitmap[] data = advertiseData.get(index).getAddsBitmap();
			adImage.setImageBitmap(data[1]);
		}
	}
}

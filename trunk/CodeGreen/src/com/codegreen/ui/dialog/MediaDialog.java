package com.codegreen.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.codegreen.R;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.listener.MediaDialogListner;

public class MediaDialog extends AlertDialog implements OnClickListener{

	//Views
	private Context mContext = null;
	Button btn_txt = null;
	Button btn_image = null;
	Button btn_audio = null;
	Button btn_vedio = null;
	private ArticleDAO articleDAO;
	TextView status_msg;
	String message = "";
	MediaDialogListner dialogListener;

	Button btn_cancel = null;

	public MediaDialog(Context context) {
		super(context);
	}


	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initDialogLayout();
	}
	
	public void registerListner(MediaDialogListner listener){
		dialogListener = listener;
	}

	/**
	 * To initialize Custom Dialog's view.
	 */
	private void initDialogLayout(){
		setContentView(R.layout.media);
		setTitle("Media Option...");

		//Assign the controls
		btn_txt = (Button)findViewById(R.id.btn_txt);
		//Set text
		btn_image = (Button)findViewById(R.id.btn_image);

		btn_audio = (Button)findViewById(R.id.btn_audio);
		btn_vedio = (Button)findViewById(R.id.btn_video);
		btn_cancel = (Button)findViewById(R.id.btn_cancel);

		btn_txt.setOnClickListener(this);
		btn_image.setOnClickListener(this);
		btn_audio.setOnClickListener(this);
		btn_vedio.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		 
		}	


	@Override
	public void dismiss() {
		super.dismiss();	
	}

	@Override
	public void cancel() {
		super.cancel();
	}

	@Override
	public void onClick(View view) {
		if(view == btn_audio){
			this.cancel();
			this.dismiss();
			if(dialogListener != null)
				dialogListener.handleDialogClick(2);
		}else if(view == btn_image){
			this.cancel();
			this.dismiss();
			if(dialogListener != null)
				dialogListener.handleDialogClick(1);
		}else if(view == btn_txt){
			this.cancel();
			this.dismiss();
			if(dialogListener != null)
				dialogListener.handleDialogClick(0);
		}else if(view == btn_vedio){
			this.cancel();
			this.dismiss();
			if(dialogListener != null)
				dialogListener.handleDialogClick(3);
		}else if(view == btn_cancel){
			this.cancel();
			this.dismiss();
		
		}
	}
}

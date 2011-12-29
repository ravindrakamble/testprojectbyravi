package com.codegreen.ui.dialog;


import com.codegreen.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;


public class ReviewDialog extends Dialog {

	//Constants
	public static final int ALERT_DIALOG = 0x27856;
	public static final int PROGRESS_DIALOG = 0x27878;
	public static final int OPTIONS_DIALOG = 0x27889;

	//Views
	private Context mContext = null;



	/**
	 * Constructor for class <strong>CustomDialog</strong>,
	 * to get dialogue with default theme.
	 * @param context
	 */
	public ReviewDialog(Context context) {
		super(context,android.R.style.Theme_Dialog); 
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		mContext = context;
		initDialogLayout();
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DialogInterface.OnKeyListener keyListener = new DialogInterface.OnKeyListener(){
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP){
					return false;
				}
				return true;
			}

		};
		this.setOnKeyListener(keyListener);
	}


	/**
	 * To initialize Custom Dialog's view.
	 */
	private void initDialogLayout(){
		this.setContentView(R.layout.review_dialog);
		EditText edt_name = (EditText)findViewById(R.id.etName);
		EditText edt_Review = (EditText)findViewById(R.id.etComments);
		Button btn_cancel = (Button)findViewById(R.id.btn_cancel);
		Button btn_submit = (Button)findViewById(R.id.btn_submit);
	}





	@Override
	public void dismiss() {
		super.dismiss();	
	}

	@Override
	public void cancel() {
		super.cancel();
	}
}
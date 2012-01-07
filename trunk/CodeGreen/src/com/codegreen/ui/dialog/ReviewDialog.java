package com.codegreen.ui.dialog;


import com.codegreen.R;
import com.codegreen.businessprocess.handler.HttpHandler;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.businessprocess.objects.ReviewDAO;
import com.codegreen.listener.Updatable;
import com.codegreen.util.Constants;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class ReviewDialog extends AlertDialog implements OnClickListener{

	//Constants
	public static final int ALERT_DIALOG = 0x27856;
	public static final int PROGRESS_DIALOG = 0x27878;
	public static final int OPTIONS_DIALOG = 0x27889;

	//Views
	private Context mContext = null;

	EditText edt_name = null;
	EditText edt_Review;
	Button submit;
	Button cancel;

	private ArticleDAO articleDAO; 
	private HttpHandler httpHandler;
	private TextView txtView;
	
	/**
	 * Constructor for class <strong>CustomDialog</strong>,
	 * to get dialogue with default theme.
	 * @param context
	 */
	public ReviewDialog(Context context, ArticleDAO articleDAO) {
		super(context, android.R.style.Theme_Dialog); 
		mContext = context;
		this.articleDAO = articleDAO;
		
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initDialogLayout();
	}


	/**
	 * To initialize Custom Dialog's view.
	 */
	private void initDialogLayout(){
		LayoutInflater inflater = LayoutInflater.from(mContext);
		final View view = inflater
				.inflate(R.layout.review_dialog, null);
		setView(view);
		this.setContentView(R.layout.review_dialog);
		setTitle(R.string.add_review);
		
		//Assign the controls
		txtView = (TextView)findViewById(R.id.txtTitle);
		//Set text
		txtView.setText(articleDAO.getTitle());
		
		edt_name = (EditText)findViewById(R.id.etName);
		edt_Review = (EditText)findViewById(R.id.etComments);
		
		//Assign the buttons
		submit = (Button)findViewById(R.id.btn_submit);
		submit.setOnClickListener(this);
		cancel = (Button)findViewById(R.id.btn_cancel);
		cancel.setOnClickListener(this);
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
		String review = null;
		String name = null;
		if(view == submit){
			review = edt_Review.getText().toString();
			name = edt_name.getText().toString();
			if(review == null){
				Toast.makeText(mContext, "Please enter review.", Toast.LENGTH_LONG).show();
			}else{
				if(name == null){
					name = "";
				}
				
				submitReviews(name, review);
				
				Toast.makeText(mContext, "Your review will be submitted.", Toast.LENGTH_LONG).show();
				this.cancel();
				this.dismiss();
			}
		}else if(view == cancel){
			this.cancel();
			this.dismiss();
		}
	}
	
	private void submitReviews(String name, String review){
		httpHandler = HttpHandler.getInstance();
		ReviewDAO reviewDAO = new ReviewDAO();
		reviewDAO.setArticleID(articleDAO.getArticleID());
		reviewDAO.setArticleType(articleDAO.getType());
		reviewDAO.setUserName(name);
		reviewDAO.setReviewComments(review);
		httpHandler.handleEvent(reviewDAO, Constants.REQ_SUBMITREVIEW, (Updatable)mContext);
	}
}
package com.codegreen.ui.activity;

import com.codegreen.R;
import com.codegreen.businessprocess.handler.HttpHandler;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.businessprocess.objects.ReviewDAO;
import com.codegreen.listener.Updatable;
import com.codegreen.util.Constants;
import com.codegreen.util.Constants.ENUM_PARSERRESPONSE;
import com.codegreen.util.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddReviewsActivity extends Activity implements OnClickListener,Updatable{

	EditText edt_name = null;
	EditText edt_Review;
	Button submit;
	Button cancel;

	private ArticleDAO articleDAO; 
	private HttpHandler httpHandler;
	private TextView txtView;
	String selectedType = "";
	String strSelectedID = "";
	String strArticleName = "";
	TextView charremaining = null;
	private static final int MAX_CHARS = 100;

	
	private InputFilter mSearchTextLengthFilter = new InputFilter.LengthFilter(100);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.review_dialog);

		if(getIntent() != null){
			selectedType = getIntent().getStringExtra("SelectedType")	;
			strSelectedID = getIntent().getStringExtra("selectedID")	;
			strArticleName = getIntent().getStringExtra("ArticleName");
		}

		txtView = (TextView)findViewById(R.id.txtTitle);
		//Set text
		txtView.setText(strArticleName);

		edt_name = (EditText)findViewById(R.id.etName);
		edt_Review = (EditText)findViewById(R.id.etComments);
		edt_Review.setFilters(new InputFilter[] {mSearchTextFilter,mSearchTextLengthFilter});

		charremaining = (TextView) findViewById(R.id.charRemaining);
		charremaining.setText(MAX_CHARS + " characters remaining");

		edt_Review.addTextChangedListener(new MyTextWatcher());

		//Assign the buttons
		submit = (Button)findViewById(R.id.btn_submit);
		submit.setOnClickListener(this);
		cancel = (Button)findViewById(R.id.btn_cancel);
		cancel.setOnClickListener(this);
	}

	int charcount = 0;

	private class MyTextWatcher implements TextWatcher {

		public void afterTextChanged(Editable arg0) {

		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

			charcount = s.toString().length();

			int remaingChars = MAX_CHARS - charcount;
			if(remaingChars >= 0){
				charremaining.setText(Integer.toString(remaingChars)
						+ " characters remaining");
			}
		}
	}

	/**
	 * Filter
	 */
	private InputFilter mSearchTextFilter = new InputFilter(){

		@Override
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {

			for(int i = start; i < end; i++){
				if(source.charAt(i) == '\n')
					return "";
			}
			return source;
		}

	};



	@Override
	public void onClick(View arg0) {
		String review = null;
		String name = null;
		if(arg0 == submit){
			if(Utils.isNetworkAvail(getApplicationContext())){
				review = edt_Review.getText().toString();
				name = edt_name.getText().toString();
				if(review == null || review.equals("")){
					Toast.makeText(getApplicationContext(), "Please enter review.", Toast.LENGTH_SHORT).show();
				}else{
					if(name == null){
						name = "";
					}
					submitReviews(name, review);
				}
			}else{
				Toast.makeText(getApplicationContext(), "No Network Available", Toast.LENGTH_SHORT).show();	
			}
		}else if(arg0 == cancel){
			this.finish();
		}

	}

	private void submitReviews(String name, String review){
		httpHandler = HttpHandler.getInstance();
		ReviewDAO reviewDAO = new ReviewDAO();
		reviewDAO.setArticleID(strSelectedID);
		reviewDAO.setArticleType(selectedType);
		reviewDAO.setUserName(name);
		reviewDAO.setReviewComments(review);
		httpHandler.handleEvent(reviewDAO, Constants.REQ_SUBMITREVIEW, this);
	}


	@Override
	public void update(byte errorCode, byte callID, Object obj) {
	}


	@Override
	public void update(ENUM_PARSERRESPONSE updateData, byte callID,
			byte errorCode) {
		if(updateData == Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS){
			if(callID == Constants.REQ_SUBMITREVIEW)
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Utils.displayMessage(getApplicationContext(), getString(R.string.review_submitted));
					}
				});
			setResult(RESULT_OK, null);
			finish();
		}else{
			setResult(RESULT_CANCELED, null);
			finish();
		}
	}

}


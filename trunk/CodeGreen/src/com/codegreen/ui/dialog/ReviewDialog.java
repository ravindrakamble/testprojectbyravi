package com.codegreen.ui.dialog;


import com.codegreen.R;
import com.codegreen.businessprocess.handler.HttpHandler;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.businessprocess.objects.ReviewDAO;
import com.codegreen.listener.Updatable;
import com.codegreen.util.Constants;
import com.codegreen.util.Utils;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
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
	TextView charremaining = null;
	private static final int MAX_CHARS = 100;

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
	/*	txtView = (TextView)findViewById(R.id.txtTitle);
		//Set text
		txtView.setText(articleDAO.getTitle());
*/
		edt_name = (EditText)findViewById(R.id.etName);
		edt_Review = (EditText)findViewById(R.id.etComments);
		edt_Review.setFilters(new InputFilter[] {mSearchTextFilter,mSearchTextLengthFilter});
		edt_Review.addTextChangedListener(new MyTextWatcher());
		
		charremaining = (TextView) findViewById(R.id.charRemaining);
		charremaining.setText(MAX_CHARS + " characters remaining");

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



	private InputFilter mSearchTextLengthFilter = new InputFilter.LengthFilter(100);
	

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
			if(Utils.isNetworkAvail(mContext.getApplicationContext())){
				review = edt_Review.getText().toString();
				name = edt_name.getText().toString();
				if(review == null || review.equals("")){
					Toast.makeText(mContext, "Please enter review.", Toast.LENGTH_SHORT).show();
				}else{
					if(name == null){
						name = "";
					}

					submitReviews(name, review);

				//	Toast.makeText(mContext, "Your review will be submitted.", Toast.LENGTH_LONG).show();
					this.cancel();
					this.dismiss();

				}
			}else{
				Toast.makeText(mContext, "No Network Available", Toast.LENGTH_SHORT).show();	
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
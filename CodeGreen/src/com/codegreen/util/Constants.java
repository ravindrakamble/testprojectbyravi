/**
 * =================================================================================================================
 * File Name            : Constants.java
 * =====================================================================================================================
 *  Sr. No.     Date            Name                            Reviewed By                                     Description
 * =====================================================================================================================
 * =====================================================================================================================
 */
package com.codegreen.util;

import android.os.Build;


public class Constants {
	/**
	 * Request Constants 
	 */
	public static final byte REQ_GETARTICLESBYTYPE = 1; 
	public static final byte REQ_GETARTICLEDETAILS = 2;
	public static final byte REQ_GETREVIEWS = 3;
	public static final byte REQ_SEARCHARTICLES = 4;
	public static final byte REQ_SUBMITREVIEW = 5;
	public static final byte REQ_GETCATEGOIRES = 6;
	public static final byte REQ_DOWNLOADIMAGE = 7;
	public static final byte REQ_DOWNLOADARTICLE = 8;
	public static final byte REQ_GETADVERTISMENTS = 9;
	public static final byte REQ_DOWNLOADADDIMAGE = 10;
	
	public static final byte DOWNLOAD_ARTICLE_DATA = 1;
	public static final byte DOWNLOAD_ARTICLE_IMAGE = 2;

	//Article types
	public static final String ARTCLETYPE_IMAGE = "IMAGE";
	public static final String ARTCLETYPE_AUDIO = "AUDIO";
	public static final String ARTCLETYPE_VIDEO = "VIDEO";
	public static final String ARTCLETYPE_TEXT = "TEXT";

	public static final String DOWNLOADED_ADDS ="DOWNLOADED_ADDS";
	//Web service url
    //  public static final String URL = "http://3embed.com.iis2003.shared-servers.com/CodeGreen/CodeGreenService.asmx";
	//public static final String URL = "http://3embed.com.iis2003.shared-servers.com/CodeGreenService/CodeGreenService.asmx";
	public static final String URL = "http://www.ipadapps.com.asp1-1.dfw1-2.websitetestlink.com/CodeGreenService.asmx";
	public static final String NAMESPACE = "http://tempuri.org/";
	public static final String SOAP_ACTION_URL = "http://tempuri.org/";

	//Method names
	public static final String GETARTICLESBYTYPE = "GetArticlesByType"; 
	public static final String GETARTICLEDETAILS = "GetArticleDetails";
	public static final String GETREVEWS = "GetReviews";
	public static final String SEARCHARTICLES = "SearchArticles";
	public static final String SUBMITREVIEW = "SubmitReview";
	public static final String GETADVERTISEMENTS = "GetAdvertisements";
	public static final String GETCATEGOIRES ="";
	
	

	//Callback constant
	public static byte OK = 0;
	
	public static byte NO_MORE_ARTICLES = 80;
	public static int NETWORK_ERROR = 10;
	public static enum PARSING{STARTED,COMPLETED};

	//HTTP request status
	public static enum HTTPREQUEST{STARTED,INPROGRESS,COMPLETED};

	public static final String ARTICAL_TYPE_IMAGE = "IMAGE";
	public static final String ARTICAL_TYPE_AUDIO = "AUDIO";
	public static final String ARTICAL_TYPE_VEDIO = "VIDEO";
	public static final String ARTICAL_TYPE_TEXT = "TEXT";
	public static final String C_ARTICLES = "ARTICLES";
	public static final String C_ARTICLE_DETAILS = "ART_DTLS";
	public static final String C_SEARCH_ARTICLES = "SRHART";
	public static final String C_REVIEWS = "RVS";
	public static final String C_SUBMITREVIEW = "SRV";
	public static final String C_DOWNLOADED_ARTICLE = "DA";
	public static final String C_SAVED_ARTICLE = "SVDA";
	public static final String C_SAVED_ARTICLES = "SVDAS";
	public static final String C_ADVERTISMENTS = "ADVERTISMENTS";
	public static final String C_ALL_ARTICLES = "ALL_ARTICLES"; // storing data

	public static enum ENUM_PARSERRESPONSE{PARSERRESPONSE_SUCCESS,PARSERRESPONSE_FAILURE  						//Used as return value for any function, when failed. 
	};

	public static final String CURRENT_ARTICLE_TYPE = "TEXT";
	public static final String CURRENT_CATEGORY_TYPE = "TEXT";
	
	//ERROR CODES
	public static final byte ERR_TASK_CANCELLED = 1;
	public static final byte ERR_NETWORK_FAILURE = 2;
	
	public static final byte PROGRESS_VISIBLE = 1;
	public static final byte PROGRESS_INVISIBLE = 2;
	
	// CodeGreen 7  catagories
	/*public static final String GREEN_BASICS = "Green Basics";
	public static final String DESIGN_AND_ARCHITECTURE = "Design and Architecture";
	public static final String SCIENCE_ANDECHNOLOGY = "Science and Technology";
	public static final String TRANSPORT = "Transport and travel";
	public static final String BUSINESS = "Business/Investment";
	public static final String POLITICS = "Politics/Policies";
	public static final String FOOD_AND_HEALTH = "Food and Health";*/
	
	//
	public static final int GREEN_BASICS = 1;
	public static final int DESIGN_AND_ARCHITECTURE = 2;
	public static final int SCIENCE_ANDECHNOLOGY = 3;
	public static final int TRANSPORT = 4;
	public static final int BUSINESS = 5;
	public static final int POLITICS = 6;
	public static final int FOOD_AND_HEALTH = 7;
	
	public static final String NO_MATCH_FOUND = "No match found.";
	
	public static int SCREEN_WIDTH = 0;
	
	
	public static final int DIALOG_REVIEW = 1;
	public static final int DIALOG_SHARE = 2;
	public static final int DIALOG_PROGRESS = 3;
	public static final int DIALOG_MEDIA = 4;
	public static final int DIALOG_REVIEW_PROGRESS = 5;
	
	public static final String CONSUMER_KEY = "1BjA123Qd4IFUuBZJUB47Q";//"ae4gN6w58zItdBH4gy3uA";//"Febkwo326bkuPDdsnJj1QA";
	public static final String CONSUMER_SECRET= "kIDzQl9YZJM1U45x6daDuYfN9EwWLaiYfrdgBR1gL4";//"j4oq5F5vr12VSVhYGXx0SiEXK5KNLZP6u6rIWkUK8";// "nAHuJpdVPXiv2xCm0S4iZsY806pmeH68iRSrW1apug";
	
	public static final String REQUEST_URL = "http://api.twitter.com/oauth/request_token";
	public static final String ACCESS_URL = "http://api.twitter.com/oauth/access_token";
	public static final String AUTHORIZE_URL = "http://api.twitter.com/oauth/authorize";
	
	public static final String	OAUTH_CALLBACK_SCHEME	= "x-oauthflow-twitter";
	public static final String	OAUTH_CALLBACK_HOST		= "";
	public static final String	OAUTH_CALLBACK_URL		= "";//OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;


	public static int CURRENT_INDEX = 0;
	public static int TOTAL_ARTICLES = 0;
	
	 public  static String  SANS_BOLD = "fonts/Stag Sans-Bold.otf";
	 public  static String  SANS_BOOK = "fonts/Stag Sans-Book.otf";
	 public  static String  SANS_SEMIBOLD =  "fonts/Stag Sans-Semibold.otf";
	 
	 public static int CURRENT_AD_INDEX = 0;
	 public static final String DEVICE_MODEL = Build.MODEL;
	 public static final int MENU_PRESS_COUNT_NO_SEARCH_KEY = 6;
	
}

/**
 * =================================================================================================================
 * File Name            : Constants.java
 * =====================================================================================================================
 *  Sr. No.     Date            Name                            Reviewed By                                     Description
 * =====================================================================================================================
 * =====================================================================================================================
 */
package com.codegreen.util;


public interface Constants {
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
	
	public static final byte DOWNLOAD_ARTICLE_DATA = 1;
	public static final byte DOWNLOAD_ARTICLE_IMAGE = 2;

	//Article types
	public static final String ARTCLETYPE_IMAGE = "IMAGE";
	public static final String ARTCLETYPE_AUDIO = "AUDIO";
	public static final String ARTCLETYPE_VIDEO = "VIDEO";
	public static final String ARTCLETYPE_TEXT = "TEXT";

	//Web service url
	public static final String URL = "http://3embed.com.iis2003.shared-servers.com/CodeGreen/CodeGreenService.asmx";
	public static final String NAMESPACE = "http://tempuri.org/";
	public static final String SOAP_ACTION_URL = "http://tempuri.org/";

	//Method names
	public static final String GETARTICLESBYTYPE = "GetArticlesByType"; 
	public static final String GETARTICLEDETAILS = "GetArticleDetails";
	public static final String GETREVEWS = "GetReviews";
	public static final String SEARCHARTICLES = "SearchArticles";
	public static final String SUBMITREVIEW = "SubmitReview";
	public static final String GETCATEGOIRES ="";
	
	

	//Callback constant
	public static byte OK = 0;
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
	
	
	public static final int DIALOG_REVIEW = 1;
	public static final int DIALOG_SHARE = 2;
	public static final int DIALOG_PROGRESS = 3;
	
	
	public static final String CONSUMER_KEY = "Febkwo326bkuPDdsnJj1QA";
	public static final String CONSUMER_SECRET= "nAHuJpdVPXiv2xCm0S4iZsY806pmeH68iRSrW1apug";
	
	public static final String REQUEST_URL = "http://api.twitter.com/oauth/request_token";
	public static final String ACCESS_URL = "http://api.twitter.com/oauth/access_token";
	public static final String AUTHORIZE_URL = "http://api.twitter.com/oauth/authorize";
	
	public static final String	OAUTH_CALLBACK_SCHEME	= "x-oauthflow-twitter";
	public static final String	OAUTH_CALLBACK_HOST		= "callback";
	public static final String	OAUTH_CALLBACK_URL		= OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;


}

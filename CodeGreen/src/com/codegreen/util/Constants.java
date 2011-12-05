/**
 * =================================================================================================================
 * File Name            : Constants.java
 * =====================================================================================================================
 *  Sr. No.     Date            Name                            Reviewed By                                     Description
 * =====================================================================================================================
 * =====================================================================================================================
 */
package com.codegreen.util;

/**
 * @author sougata.sen
 *
 */
public interface Constants {
	/**
	 * Request Constants 
	 */
	public static final byte REQ_GETARTICLESBYTYPE = 1; 
	public static final byte REQ_GETARTICLEDETAILS = 2;
	public static final byte REQ_GETREVIEWS = 3;
	public static final byte REQ_SEARCHARTICLES = 4;
	public static final byte REQ_SUBMITREVIEW = 5;

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

	//Callback constant
	public static byte OK = 0;
	public static enum PARSING{STARTED,COMPLETED};

	//HTTP request status
	public static enum HTTPREQUEST{STARTED,INPROGRESS,COMPLETED};

	public static final String ARTICAL_TYPE_IMAGE = "IMAGE";
	public static final String ARTICAL_TYPE_AUDIO = "AUDIO";
	public static final String ARTICAL_TYPE_VEDIO = "VEDIO";
	public static final String ARTICAL_TYPE_TEXT = "TEXT";
	public static final String C_ARTICLES = "ARTICLES";
	public static final String C_ARTICLE_DETAILS = "ART_DTLS";
	public static final String C_SEARCH_ARTICLES = "SRHART";
	public static final String C_REVIEWS = "RVS";
	public static final String C_SUBMITREVIEW = "SRV";

	public static enum ENUM_PARSERRESPONSE{PARSERRESPONSE_SUCCESS,PARSERRESPONSE_FAILURE  						//Used as return value for any function, when failed. 
	};

	public static final String CURRENT_ARTICLE_TYPE = "TEXT";
	public static final String CURRENT_CATEGORY_TYPE = "TEXT";
	
	//ERROR CODES
	public static final byte ERR_TASK_CANCELLED = 1;
	public static final byte ERR_NETWORK_FAILURE = 2;
	
	public static final byte PROGRESS_VISIBLE = 1;
	public static final byte PROGRESS_INVISIBLE = 2;

}

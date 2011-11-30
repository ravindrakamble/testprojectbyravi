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
	public static final byte REQ_GETREVEWS = 3;
	public static final byte REQ_SEARCHARTICLES = 4;
	public static final byte REQ_SUBMITREVIEW = 5;
	
	//Article types
	public static final String ARTCLETYPE_IMAGE = "IMAGE";
	public static final String ARTCLETYPE_AUDIO = "AUDIO";
	public static final String ARTCLETYPE_VIDEO = "VIDEO";
	public static final String ARTCLETYPE_TEXT = "TEXT";
	
	//Web service url
	public static final String URL = "http://3embed.com.iis2003.shared-servers.com/CodeGreenService.asmx";
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
	
}
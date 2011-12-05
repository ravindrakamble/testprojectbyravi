/**
 * =================================================================================================================
 * File Name       	: RequestBuilder.java
 * =====================================================================================================================
 *  Sr. No.	Date		Name				Reviewed By					Description
 * =====================================================================================================================
 * =====================================================================================================================
 */
package com.codegreen.services;

import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.businessprocess.objects.ReviewDAO;
import com.codegreen.util.Constants;



/**
 * @author ravindra.kamble
 *
 */
public class RequestBuilder {

	/**
	 * Request ID represents the request type to be made. 
	 */
	byte requestID;

	/**
	 * Singleton Instance of Request Builder
	 */
	private static RequestBuilder requestBuilder = new RequestBuilder();
	
	final String header = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
	"<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
	" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" +
	" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">";
	
	/**
	 * Private access constructor
	 */
	private RequestBuilder(){

	}

	/**
	 * Retrives the singleton instance of request builder.
	 * @return
	 */
	public static synchronized RequestBuilder getInstance(){
		return requestBuilder; 
	}

	/**
	 * Creates a request of a type Specified and returns the SOAP Request Object.
	 * @param reqID
	 * @param params
	 * @return
	 */
	public synchronized SOAPRequest createRequest(byte reqID, Object params){
		requestID = reqID;
		SOAPRequest request = new SOAPRequest();
		request.setRequestID(reqID);
		switch (reqID) {
		case Constants.REQ_GETARTICLESBYTYPE:
			request.setRequestID(reqID);
			request.setSoapMethodName(Constants.GETARTICLESBYTYPE);
			request.setSoapRequest(getArticlesByType(params));
			break;
		case Constants.REQ_GETARTICLEDETAILS:
			request.setRequestID(reqID);
			request.setSoapMethodName(Constants.GETARTICLEDETAILS);
			request.setSoapRequest(getArticleDetails(params));
			break;
		case Constants.REQ_GETREVIEWS:
			request.setRequestID(reqID);
			request.setSoapMethodName(Constants.GETREVEWS);
			request.setSoapRequest(getReviews(params));
			break;
		case Constants.REQ_SEARCHARTICLES:
			request.setRequestID(reqID);
			request.setSoapMethodName(Constants.SEARCHARTICLES);
			request.setSoapRequest(getSearchArticles(params));
			break;
		case Constants.REQ_SUBMITREVIEW:
			request.setRequestID(reqID);
			request.setSoapMethodName(Constants.SUBMITREVIEW);
			request.setSoapRequest(getSubmitReviews(params));
			break;
		}
		return request;
	}
	
	/**
	 * Creates get articles by type request and returns the request string.
	 * Note : Header is pre-appended with the return String.
	 * 
	 * @param param
	 * @return
	 */
	private String getArticlesByType(Object params){
		StringBuffer sb = new StringBuffer();
		ArticleDAO articleDAO = (ArticleDAO)params;
		sb.append(header);
		sb.append("<soap:Body>");
		sb.append("<GetArticlesByType xmlns=\"http://tempuri.org/\">");
		sb.append("<type>" + articleDAO.getType() + "</type>");
		sb.append("<lastArticlePublishingDate>" + articleDAO.getLastArticlePublishingDate() +"</lastArticlePublishingDate>");
		sb.append("</GetArticlesByType>");
		sb.append("</soap:Body>");
		sb.append("</soap:Envelope>");
		return sb.toString().trim();
	}

	private String getArticleDetails(Object params){
		StringBuffer sb = new StringBuffer();
		ArticleDAO articleDAO = (ArticleDAO)params;
		sb.append(header);
		sb.append("<soap:Body>");
		sb.append("<GetArticleDetails xmlns=\"http://tempuri.org/\">");
		sb.append("<articleID>" + articleDAO.getType() + "</articleID>");
		sb.append("<type>" + articleDAO.getLastArticlePublishingDate() +"</type>");
		sb.append("<categoryID>" + articleDAO.getCategoryID() +"</categoryID>");
		sb.append("</GetArticleDetails>");
		sb.append("</soap:Body>");
		sb.append("</soap:Envelope>");
		return sb.toString().trim();
	}
		
	
	private String getSearchArticles(Object params){
		StringBuffer sb = new StringBuffer();
		ArticleDAO articleDAO = (ArticleDAO)params;
		sb.append(header);
		sb.append("<soap:Body>");
		sb.append("<SearchArticles xmlns=\"http://tempuri.org/\">");
		sb.append("<searchString>" + articleDAO.getTitle() + "</searchString>");
		sb.append("<type>" + articleDAO.getType() + "</type>");
		sb.append("<categoryID>" +articleDAO.getCategoryID() +"</categoryID>");
		sb.append("</SearchArticles>");
		sb.append("</soap:Body>");
		sb.append("</soap:Envelope>");
		return sb.toString().trim();
	}
	
	
	private String getReviews(Object params){
		StringBuffer sb = new StringBuffer();
		ReviewDAO reviewDAO = (ReviewDAO)params;
		sb.append(header);
		sb.append("<soap:Body>");
		sb.append("<GetReviews xmlns=\"http://tempuri.org/\">");
		sb.append("<articleID>" + reviewDAO.getArticleID() + "</articleID>");
		sb.append("<type>" + reviewDAO.getArticleType() +"</type>");
		sb.append("</GetReviews>");
		sb.append("</soap:Body>");
		sb.append("</soap:Envelope>");
		return sb.toString().trim();
	}
	
	private String getSubmitReviews(Object params){
		StringBuffer sb = new StringBuffer();
		ReviewDAO reviewDAO = (ReviewDAO)params;
		sb.append(header);
		sb.append("<soap:Body>");
		sb.append("<SubmitReview xmlns=\"http://tempuri.org/\">");
		sb.append("<articleID>" + reviewDAO.getArticleID() + "</articleID>");
		sb.append("<type>" + reviewDAO.getArticleType() +"</type>");
		sb.append("<username>" + reviewDAO.getUserName() + "</username>");
		sb.append("<reviewComments>"+ reviewDAO.getReviewComments() +"</reviewComments>");
		sb.append("</SubmitReview>");
		sb.append("</soap:Body>");
		sb.append("</soap:Envelope>");
		return sb.toString().trim();
	}
}

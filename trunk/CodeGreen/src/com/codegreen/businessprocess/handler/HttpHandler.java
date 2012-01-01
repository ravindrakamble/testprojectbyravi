package com.codegreen.businessprocess.handler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.content.Context;
import android.util.Log;

import com.codegreen.common.CacheManager;
import com.codegreen.common.TaskExecutor;
import com.codegreen.database.DBAdapter;
import com.codegreen.listener.Updatable;
import com.codegreen.parser.XmlParser;
import com.codegreen.services.WebServiceFacade;
import com.codegreen.util.Constants;

public class HttpHandler implements Handler {

	//Request Status
	private Constants.HTTPREQUEST requestStatus;
	
	//Updatable screen
	private Updatable updatable;
	
	private static HttpHandler mSelf = null;
	
	private Context applicationContext;
	byte mReqId;
	
	private HttpHandler(){
		
	}
	
	public static synchronized HttpHandler getInstance(){
		if(mSelf == null){
			mSelf = new HttpHandler();
		} 
		return mSelf;
	}
	
	@Override
	public byte handleEvent(Object eventObject, byte callID, Updatable updatable) {
		this.updatable = updatable;
		requestStatus = Constants.HTTPREQUEST.STARTED;
		mReqId = callID;
		WebServiceFacade webServiceFacade = WebServiceFacade.getInstance();
		switch (callID) {
		case Constants.REQ_GETARTICLESBYTYPE:
			// Get articles by type
			webServiceFacade.getArticlesByType(eventObject, this);
			break;
			
		case Constants.REQ_GETARTICLEDETAILS:
			//Retrieve article details
			webServiceFacade.getArticleDetails(eventObject, this);
			break;
			
		case Constants.REQ_GETREVIEWS:
			//Retrieve article reviews
			webServiceFacade.getReviews(eventObject, this);
			break;
			
		case Constants.REQ_SEARCHARTICLES:
			//Search articles
			webServiceFacade.searchArticles(eventObject, this);
			break;
			
		case Constants.REQ_SUBMITREVIEW:
			//Submit article review
			webServiceFacade.submitReviews(eventObject, this);
			break;
		}
		requestStatus = Constants.HTTPREQUEST.INPROGRESS;
		return 0;
	}

	@Override
	public void handleCallback(Object callbackObject, byte callID,
			byte errorCode) {
		XmlParser ddXmlParser = null;
		if(errorCode == Constants.OK){
			ddXmlParser = new XmlParser(callID);
			//Get the instance of SAXParserFactory.
			SAXParserFactory factory = SAXParserFactory.newInstance();
			//Creates the parser instance
			SAXParser parser = null;

			ByteArrayInputStream inputStream = new ByteArrayInputStream((byte[])callbackObject);
			try {
				parser = factory.newSAXParser();
			} catch(Exception e1) {
				e1.printStackTrace();
			}
			try {
				parser.parse(inputStream, ddXmlParser);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			//Close the input stream
			if(inputStream != null){
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			//Check parsing status
			Constants.PARSING parsedMessage = ddXmlParser.getParseMessage();

			if(parsedMessage == Constants.PARSING.COMPLETED){
				//Log.e("No of records found;" , "" + ddXmlParser.getArticles().size()); // for other req giving null
				if(updatable != null){
					switch (callID) {
					case Constants.REQ_GETARTICLESBYTYPE:
						CacheManager.getInstance().store(Constants.C_ARTICLES, ddXmlParser.getArticles());
						
						//Update the articles into database
						DBAdapter dbAdapter = DBAdapter.getInstance(applicationContext);
						//dbAdapter.insertArticles(ddXmlParser.getArticles());
						updatable.update(Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS,mReqId);
						break;
					case Constants.REQ_GETARTICLEDETAILS:
						CacheManager.getInstance().store(Constants.C_ARTICLE_DETAILS, ddXmlParser.getArticleDAO());
						updatable.update(Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS,mReqId);
						break;
					case Constants.REQ_GETREVIEWS:
						CacheManager.getInstance().store(Constants.C_REVIEWS, ddXmlParser.getReviews());
						updatable.update(Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS,mReqId);
						break;
					case Constants.REQ_SEARCHARTICLES:
						CacheManager.getInstance().store(Constants.C_SEARCH_ARTICLES, ddXmlParser.getArticles());
						updatable.update(Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS,mReqId);
						break;
					case Constants.REQ_SUBMITREVIEW:
						updatable.update(Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS,mReqId);
						break;
					}
				}
			}
		}else{
			updatable.update(Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_FAILURE,mReqId);
		}
		requestStatus = Constants.HTTPREQUEST.COMPLETED;
	}

	public Constants.HTTPREQUEST getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(Constants.HTTPREQUEST requestStatus) {
		this.requestStatus = requestStatus;
	}
	
	public void cancelRequest(){
		TaskExecutor.getInstance().cancelAllTask();
	}

	public Context getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(Context applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	
}

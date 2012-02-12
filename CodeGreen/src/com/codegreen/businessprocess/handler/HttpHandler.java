package com.codegreen.businessprocess.handler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.common.CacheManager;
import com.codegreen.common.TaskExecutor;
import com.codegreen.database.DBAdapter;
import com.codegreen.listener.Updatable;
import com.codegreen.network.DownloadImageTask;
import com.codegreen.parser.XmlParser;
import com.codegreen.services.WebServiceFacade;
import com.codegreen.util.Constants;

public class HttpHandler implements Handler {

	//Request Status
	private Constants.HTTPREQUEST requestStatus;

	//Updatable screen
	private Updatable updatable;
	private static HttpHandler mSelf = null;

	private ArrayList<ArticleDAO> adList;
	private int index;

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
		WebServiceFacade webServiceFacade = WebServiceFacade.getInstance(applicationContext);
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
		case Constants.REQ_DOWNLOADIMAGE:
			//Submit article review
			webServiceFacade.downloadImage((String)eventObject, this);
			break;
		case Constants.REQ_GETADVERTISMENTS:
			webServiceFacade.getAdvertisements(eventObject,this);
		}
		requestStatus = Constants.HTTPREQUEST.INPROGRESS;
		return 0;
	}
	ArticleDAO dao = null;
	@SuppressWarnings("unchecked")
	@Override
	public void handleCallback(Object callbackObject, byte callID,
			byte errorCode) {
		XmlParser ddXmlParser = null;
		WebServiceFacade webServiceFacade = WebServiceFacade.getInstance(applicationContext);
		Constants.PARSING parsedMessage = null ;
		if(errorCode == Constants.OK){

			if(callID == Constants.REQ_DOWNLOADIMAGE){
				if(callbackObject != null){
					CacheManager.getInstance().setLatestArticleBitmap((Bitmap)callbackObject);
				}
				updatable.update(Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS,mReqId,errorCode);
			}else if(callID == Constants.REQ_DOWNLOADADDIMAGE){
				parsedMessage = Constants.PARSING.COMPLETED;
			}else{
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
				parsedMessage = ddXmlParser.getParseMessage();
			}
			if(parsedMessage == Constants.PARSING.COMPLETED){
				//Log.e("No of records found;" , "" + ddXmlParser.getArticles().size()); // for other req giving null
				if(updatable != null){
					switch (callID) {
					case Constants.REQ_GETARTICLESBYTYPE:
						CacheManager.getInstance().store(Constants.C_ARTICLES, ddXmlParser.getArticles());

						//Update the articles into database
						updatable.update(Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS,mReqId,errorCode);
						break;
					case Constants.REQ_GETARTICLEDETAILS:
						CacheManager.getInstance().store(Constants.C_ARTICLE_DETAILS, ddXmlParser.getArticleDAO());
						updatable.update(Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS,mReqId,errorCode);
						break;
					case Constants.REQ_GETREVIEWS:
						CacheManager.getInstance().store(Constants.C_REVIEWS, ddXmlParser.getReviews());
						updatable.update(Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS,mReqId,errorCode);
						break;
					case Constants.REQ_SEARCHARTICLES:
						CacheManager.getInstance().store(Constants.C_SEARCH_ARTICLES, ddXmlParser.getArticles());
						updatable.update(Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS,mReqId,errorCode);
						break;
					case Constants.REQ_SUBMITREVIEW:
						updatable.update(Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS,mReqId,errorCode);
						break;
					case Constants.REQ_GETADVERTISMENTS:
						index = 0;
						CacheManager.getInstance().store(Constants.C_ADVERTISMENTS,ddXmlParser.getArticles());
						adList = (ArrayList<ArticleDAO>)CacheManager.getInstance().get(Constants.C_ADVERTISMENTS);
						Log.i("Advertisments ", "link downloaded");
						if(index < adList.size()){
							dao = adList.get(index);
							if(dao!=null)
								webServiceFacade.downloadAddImage(dao, this);
						}
						break;
					case Constants.REQ_DOWNLOADADDIMAGE:
						dao = adList.get(index);
						dao.setAddsBitmap((Bitmap[])callbackObject);
						CacheManager.getInstance().store(Constants.C_ADVERTISMENTS,adList);
						index++;
						if(index < adList.size()){

							dao = adList.get(index);
							Log.i("Advertisments ", "link downloaded" + dao.getThumbUrl());
							if(dao!=null)
								webServiceFacade.downloadAddImage(dao, this);
						}else{
							Log.i("Advertisments ", "All ads downloaded");
							// returning downloaded file to parser
							Intent broadcast = new Intent(Constants.DOWNLOADED_ADDS);
							applicationContext.sendBroadcast(broadcast);
						}
						break;
					}
				}
			}
		}else{
			updatable.update(Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_FAILURE,mReqId,errorCode);
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

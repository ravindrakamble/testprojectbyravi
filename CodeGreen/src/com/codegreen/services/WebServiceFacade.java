 /**
 * =================================================================================================================
 * File Name       	: NetwrokManager.java
 * =====================================================================================================================
 *  Sr. No.	Date		Name			Description
 * =====================================================================================================================
 * =====================================================================================================================
 */
package com.codegreen.services;

import android.content.Context;

import com.codegreen.businessprocess.handler.Handler;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.common.TaskExecutor;
import com.codegreen.network.AdDownloadTask;
import com.codegreen.network.DownloadImageTask;
import com.codegreen.network.NetworkTask;
import com.codegreen.util.Constants;

public class WebServiceFacade {

	/**
	 * Returns the singleton instance of the Web service Facade.
	 */
	private final static WebServiceFacade webServiceFacade = new WebServiceFacade();
	
	TaskExecutor taskExecutor = TaskExecutor.getInstance();
	
	
	/**
	 * Private Access Constructor of the singleton class.
	 */
	private WebServiceFacade(){
	}
	
	static Context mContext = null;
	
	/**
	 * Returns the singleton instance of the Web service Facade.
	 * @return
	 */
	public static WebServiceFacade getInstance(Context context){
		mContext = context;
		return webServiceFacade; 
	}
	
	/**
	 * GetArticlesByType
	 * Gives the list of articles as per the type.
	 * http://3embed.com.iis2003.shared-servers.com/CodeGreenService.asmx?op=GetArticlesByType
	 */
	public void getArticlesByType(Object params, Handler handler){
		SOAPRequest request = RequestBuilder.getInstance().createRequest(Constants.REQ_GETARTICLESBYTYPE, params);
		NetworkTask articlesTask = new NetworkTask(request,handler,mContext);
		taskExecutor.execute(articlesTask);
	}
	
	/**
	 * GetArticlesByType
	 * Gives the list of articles as per the type.
	 * http://3embed.com.iis2003.shared-servers.com/CodeGreenService.asmx?op=GetArticlesByType
	 */
	public void getArticleDetails(Object params, Handler handler){
		SOAPRequest request = RequestBuilder.getInstance().createRequest(Constants.REQ_GETARTICLEDETAILS, params);
		NetworkTask articlesTask = new NetworkTask(request,handler,mContext);
		taskExecutor.execute(articlesTask);
	}
	
	/**
	 * GetArticlesByType
	 * Gives the list of articles as per the type.
	 * http://3embed.com.iis2003.shared-servers.com/CodeGreenService.asmx?op=GetArticlesByType
	 */
	public void searchArticles(Object params, Handler handler){
		SOAPRequest request = RequestBuilder.getInstance().createRequest(Constants.REQ_SEARCHARTICLES, params);
		NetworkTask articlesTask = new NetworkTask(request,handler,mContext);
		taskExecutor.execute(articlesTask);
	}
	
	/**
	 * GetArticlesByType
	 * Gives the list of articles as per the type.
	 * http://3embed.com.iis2003.shared-servers.com/CodeGreenService.asmx?op=GetArticlesByType
	 */
	public void getReviews(Object params, Handler handler){
		SOAPRequest request = RequestBuilder.getInstance().createRequest(Constants.REQ_GETREVIEWS, params);
		NetworkTask reviewTask = new NetworkTask(request,handler,mContext);
		taskExecutor.execute(reviewTask);
	}
	
	/**
	 * GetArticlesByType
	 * Gives the list of articles as per the type.
	 * http://3embed.com.iis2003.shared-servers.com/CodeGreenService.asmx?op=GetArticlesByType
	 */
	public void submitReviews(Object params, Handler handler){
		SOAPRequest request = RequestBuilder.getInstance().createRequest(Constants.REQ_SUBMITREVIEW, params);
		NetworkTask reviewTask = new NetworkTask(request,handler,mContext);
		taskExecutor.execute(reviewTask);
	}
	
	public void getAdvertisements(Object params, Handler handler){
		SOAPRequest request = RequestBuilder.getInstance().createRequest(Constants.REQ_GETADVERTISMENTS, params);
		NetworkTask reviewTask = new NetworkTask(request,handler,mContext);
		taskExecutor.execute(reviewTask);	
	}
	
	public void downloadImage(String params, Handler handler){
		DownloadImageTask imageTask = new DownloadImageTask(params, handler);
		taskExecutor.execute(imageTask);
	}
	
	public void downloadAddImage(ArticleDAO dao,Handler handler){
		AdDownloadTask task = new AdDownloadTask(dao.getThumbUrl(), dao.getUrl(), handler);
		taskExecutor.execute(task);
	}
}

package com.codegreen.businessprocess.handler;

import android.util.Log;

import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.businessprocess.objects.DownloadInfoDAO;
import com.codegreen.common.CacheManager;
import com.codegreen.common.TaskExecutor;
import com.codegreen.listener.Updatable;
import com.codegreen.network.DownloadTask;
import com.codegreen.util.Constants;

public class DownloadHandler implements Handler{

	private Updatable updatable;
	private ArticleDAO articleDAO;
	private DownloadTask downloadTask;

	TaskExecutor taskExecutor = TaskExecutor.getInstance();

	boolean imageDownloaded;
	boolean dataDownloaded;

	boolean requestForImage;
	boolean requestForData;
	private ArticleDAO downloadedArticle;
	boolean articleSaved;

	private static DownloadHandler mSelf = null;

	byte mReqId;

	private DownloadHandler(){

	}

	public static synchronized DownloadHandler getInstance(){
		if(mSelf == null){
			mSelf = new DownloadHandler();
		} 

		return mSelf;
	}
	/** 
	 * @see com.codegreen.businessprocess.handler.Handler#handleEvent(java.lang.Object, byte, com.codegreen.listener.Updatable)
	 */
	@Override
	public byte handleEvent(Object eventObject, byte callID, final Updatable updatable) {
		downloadedArticle = null;
		articleDAO = null;
		requestForImage = false;	
		requestForData = false;
		dataDownloaded = false;
		imageDownloaded = false;
		articleSaved = false;
		this.updatable = updatable;
		this.articleDAO = (ArticleDAO)eventObject;
		if(downloadedArticle == null){
			downloadedArticle = new ArticleDAO();
			downloadedArticle.setArticleID(articleDAO.getArticleID());
			downloadedArticle.setCategoryID(articleDAO.getCategoryID());
			downloadedArticle.setCreatedDate(articleDAO.getCreatedDate());
			downloadedArticle.setDetailedDescription(articleDAO.getDetailedDescription());
			downloadedArticle.setLastArticlePublishingDate(articleDAO.getLastArticlePublishingDate());
			downloadedArticle.setPublishedDate(articleDAO.getPublishedDate());
			downloadedArticle.setShortDescription(articleDAO.getShortDescription());
			downloadedArticle.setThumbUrl(articleDAO.getThumbUrl());
			downloadedArticle.setTitle(articleDAO.getTitle());
			downloadedArticle.setType(articleDAO.getType());
			downloadedArticle.setUrl(articleDAO.getUrl());
		}
		switch(callID){

		case Constants.REQ_DOWNLOADARTICLE:
			DownloadInfoDAO dao = new DownloadInfoDAO();

			//dao.setType(articleDAO.getType());
			// if audio/video then download URL data 
			/*	if(articleDAO.getType().equalsIgnoreCase(Constants.ARTCLETYPE_AUDIO) || articleDAO.getType().equalsIgnoreCase(Constants.ARTCLETYPE_VIDEO)){
				dao.setUrlToDownload(articleDAO.getUrl());
				requestForData = true;
				downloadTask = new DownloadTask(dao, this, Constants.DOWNLOAD_ARTICLE_DATA);
				taskExecutor.execute(downloadTask);
			}else{
			 */		
			if(articleDAO.getThumbUrl() != null && !articleDAO.getThumbUrl().trim().equalsIgnoreCase("")){

				dao.setUrlToDownload(articleDAO.getThumbUrl());
				dao.setType("thumb");
				//Download article image first
				downloadTask = new DownloadTask(dao, this, Constants.DOWNLOAD_ARTICLE_IMAGE);
				taskExecutor.execute(downloadTask);
				requestForImage = true;
			}

			if(articleDAO.getUrl() != null && !articleDAO.getUrl().trim().equalsIgnoreCase("")){
				dao = new DownloadInfoDAO();
				dao.setUrlToDownload(articleDAO.getUrl());
				if(dao.getFileName() == null){
					dao.setFileName(articleDAO.getArticleID() + ".mp4");
				}
				if(articleDAO.getType() != null){
					dao.setType(articleDAO.getType());
				}else{
					dao.setType(articleDAO.getArticleID());
				}
				//Download article image first
				requestForData = true;
				downloadTask = new DownloadTask(dao, this, Constants.DOWNLOAD_ARTICLE_DATA);
				taskExecutor.execute(downloadTask);
			}
			//	}

			if(!requestForData && !requestForImage){
				new Thread(){
					public void run() {
						CacheManager.getInstance().store(Constants.C_DOWNLOADED_ARTICLE, downloadedArticle);
						updatable.update(Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS, Constants.REQ_DOWNLOADARTICLE, (byte)0);
					};

				}.start();

			}
			break;
		}
		return 0;
	}

	/** 
	 * @see com.codegreen.businessprocess.handler.Handler#handleCallback(java.lang.Object, byte, byte)
	 */
	@Override
	public void handleCallback(Object callbackObject, byte callID,
			byte errorCode) {
		if(errorCode == Constants.ERR_NETWORK_FAILURE){
			if(callID == Constants.REQ_DOWNLOADADDIMAGE){
				requestForImage = false;	
			}else{
				requestForData = false;
			}
			if(!articleSaved){
				updatable.update(Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_FAILURE, Constants.REQ_DOWNLOADARTICLE, errorCode);
				articleSaved = true;
			}
		}else{
			Log.e("Downloaded", "" + callbackObject);
			switch(callID){
			case Constants.DOWNLOAD_ARTICLE_IMAGE:
				imageDownloaded = true;

				if(articleDAO.getType().equalsIgnoreCase(Constants.ARTCLETYPE_AUDIO) && articleDAO.getType().equalsIgnoreCase(Constants.ARTCLETYPE_VIDEO)){
					downloadedArticle.setUrl((String)callbackObject);

				}else{
					downloadedArticle.setThumbUrl((String)callbackObject);
				}
				if(requestForData){
					if(dataDownloaded){
						if(!articleSaved){
							CacheManager.getInstance().store(Constants.C_DOWNLOADED_ARTICLE, downloadedArticle);
							updatable.update(Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS, Constants.REQ_DOWNLOADARTICLE, errorCode);
							articleSaved = true;
						}
					}
				}else{
					if(!articleSaved){
						CacheManager.getInstance().store(Constants.C_DOWNLOADED_ARTICLE, downloadedArticle);
						updatable.update(Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS, Constants.REQ_DOWNLOADARTICLE, errorCode);
						articleSaved = true;
					}
				}
				break;
			case Constants.DOWNLOAD_ARTICLE_DATA:
				dataDownloaded = true;
				downloadedArticle.setUrl((String)callbackObject);
				if(requestForImage && imageDownloaded){
					if(!articleSaved){
						CacheManager.getInstance().store(Constants.C_DOWNLOADED_ARTICLE, downloadedArticle);
						updatable.update(Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS, Constants.REQ_DOWNLOADARTICLE, errorCode);
						articleSaved = true;
					}
				}
				break;
			}
		}

	}

	public void cleardata(){
		articleDAO = null;
		downloadTask = null;
		downloadedArticle = null;
	}
}

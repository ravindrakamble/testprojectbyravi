package com.codegreen.businessprocess.handler;

import android.content.Context;

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

	private ArticleDAO downloadedArticle;

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
	public byte handleEvent(Object eventObject, byte callID, Updatable updatable) {
		this.updatable = updatable;
		this.articleDAO = (ArticleDAO)eventObject;
		switch(callID){
		case Constants.REQ_DOWNLOADARTICLE:
			DownloadInfoDAO dao = new DownloadInfoDAO();

			dao.setType(articleDAO.getType());
			if(!articleDAO.getType().equalsIgnoreCase(Constants.ARTCLETYPE_AUDIO) && !articleDAO.getType().equalsIgnoreCase(Constants.ARTCLETYPE_VIDEO)){
				dao.setUrlToDownload(articleDAO.getUrl());

				downloadTask = new DownloadTask(dao, this, Constants.DOWNLOAD_ARTICLE_DATA);
				taskExecutor.execute(downloadTask);
			}else{
				dao.setUrlToDownload(articleDAO.getThumbUrl());
				//Download article image first
				downloadTask = new DownloadTask(dao, this, Constants.DOWNLOAD_ARTICLE_IMAGE);
				taskExecutor.execute(downloadTask);

				dao = new DownloadInfoDAO();
				dao.setUrlToDownload(articleDAO.getUrl());
				if(dao.getFileName() == null){
					dao.setFileName(articleDAO.getArticleID() + ".mp4");
				}
				dao.setType(articleDAO.getType());
				//Download article image first
				downloadTask = new DownloadTask(dao, this, Constants.DOWNLOAD_ARTICLE_DATA);
				taskExecutor.execute(downloadTask);
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
			updatable.update(Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_FAILURE, Constants.REQ_DOWNLOADARTICLE, errorCode);
		}else{
		switch(callID){
		case Constants.DOWNLOAD_ARTICLE_IMAGE:
			imageDownloaded = true;
			if(downloadedArticle == null){
				downloadedArticle = new ArticleDAO();
				downloadedArticle = articleDAO;
				downloadedArticle.setThumbUrl(null);
				downloadedArticle.setUrl(null);
			}
			if(!articleDAO.getType().equalsIgnoreCase(Constants.ARTCLETYPE_AUDIO) && !articleDAO.getType().equalsIgnoreCase(Constants.ARTCLETYPE_VIDEO)){
				downloadedArticle.setUrl((String)callbackObject);
				
				CacheManager.getInstance().store(Constants.C_DOWNLOADED_ARTICLE, downloadedArticle);
				updatable.update(Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS, Constants.REQ_DOWNLOADARTICLE, errorCode);
			}else{
				downloadedArticle.setThumbUrl((String)callbackObject);
				
				
			}
			break;
		case Constants.DOWNLOAD_ARTICLE_DATA:
			dataDownloaded = true;
			if(downloadedArticle == null){
				downloadedArticle = new ArticleDAO();
				downloadedArticle = articleDAO;
				downloadedArticle.setThumbUrl(null);
				downloadedArticle.setUrl(null);
			}
			downloadedArticle.setUrl((String)callbackObject);
			CacheManager.getInstance().store(Constants.C_DOWNLOADED_ARTICLE, downloadedArticle);
			updatable.update(Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS, Constants.REQ_DOWNLOADARTICLE, errorCode);
			break;
		}

	}

	}

}

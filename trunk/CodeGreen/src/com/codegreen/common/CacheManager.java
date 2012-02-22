/**
 * =================================================================================================================
 * File Name            : CacheManager.java
 * =====================================================================================================================
 *  Sr. No.     Date            Name                            Reviewed By                                     Description
 * =====================================================================================================================
 * =====================================================================================================================
 */
package com.codegreen.common;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.codegreen.businessprocess.objects.ArticleDAO;

import android.graphics.Bitmap;

public class CacheManager {

	/**
	 * Single Instance Created.
	 */
	private static final CacheManager cacheManager = new CacheManager();

	private static ArrayList<ArticleDAO> allArticlesList = null;

	/**
	 * Maintains the Cache of the Application.
	 */
	private Hashtable<String,Object> cache = new Hashtable<String,Object>();

	public static boolean loggedIn = false;
	private Bitmap latestArticleBitmap;

	/**
	 * Private Access constructor.
	 */
	private CacheManager(){
		if(cache == null){
			cache = new Hashtable<String,Object>();
		}
	}

	/**
	 * Returns the singleton Instance of the Cache Manager
	 * @return
	 */
	public static CacheManager getInstance(){
		return cacheManager;
	}


	/**
	 * Stores a Key/Value Pair in the Cache
	 * @param key
	 * @param value
	 */
	public void store(String key, Object value){

		if(cache == null){
			cache = new Hashtable<String,Object>();
		}
		cache.put(key, value);
	}


	/**
	 * Gets a Object from the cache
	 * @param key
	 * @return
	 */
	public Object get(Object key){
		return cache.get(key);
	}

	/**
	 * Removes an element from the cache. 
	 * @param key
	 */
	public void removeFromCache(Object key){
		if(cache != null && key != null){
			cache.remove(key);
		}
	}

	/**
	 * Removes the Cache
	 */
	public void clearCache(){
		if(cache != null){
			cache.clear();
		}
	}

	public Bitmap getLatestArticleBitmap() {
		return latestArticleBitmap;
	}

	public void setLatestArticleBitmap(Bitmap latestArticleBitmap) {
		this.latestArticleBitmap = latestArticleBitmap;
	}

	public void storeAllArticles(List<ArticleDAO> data){
		if(allArticlesList == null){
			allArticlesList = new ArrayList<ArticleDAO>();
		}
		if(data != null && data.size() > 0){
			for(int i =0; i< data.size();i++){
				allArticlesList.add(data.get(i));
			}
		}
	}


	public void resetAllArticles(){
		if(allArticlesList != null){
			allArticlesList.clear();
		}
	}
	
	public int getAllArticledetailsSize(){
		if(allArticlesList != null){
			return allArticlesList.size();
		}
		return 0;
	}
	
	public ArrayList<ArticleDAO> getAllArticles(){
		if(allArticlesList != null){
			return allArticlesList;
		}
		return null;
	}

}




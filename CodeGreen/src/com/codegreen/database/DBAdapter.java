package com.codegreen.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.codegreen.businessprocess.objects.ArticleDAO;

public class DBAdapter 
{

	private static final String DATABASE_NAME = "codegreen_db";
	private static final int DATABASE_VERSION = 1;


	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	static DBAdapter dbAdapter;

	//Create table strings
	static final String AudioArticles = "CREATE TABLE AUDIOARTICLES (_audioArticleID integer primary key, ";
	static final String VideoArticles = "CREATE TABLE VIDEOARTICLES (_videoArticleID integer primary key, ";
	static final String IMAGEArticles = "CREATE TABLE IMAGEARTICLES (_imageArticleID integer primary key, ";
	static final String TextArticles = "CREATE TABLE TEXTARTICLES (_textArticleID integer primary key, ";
	static final String Review = "CREATE TABLE Review (_reviewID integer primary key, ";

	//Article table fields
	static final String articleFields = "title varchar not null, shortdescription varchar , detaileddescription varchar" 
		+ "thumbnailurl varchar, mainurl varchar, publishingdate varchar , createddate varchar)";

	//Review table fields
	static final String reviewFields = "articleID integer, articleType varchar, reviewcomments varchar, createddate varchar)";

	private DBAdapter(Context context) 
	{
		DBHelper = new DatabaseHelper(context);
	}

	public static DBAdapter getInstance(Context context){
		if(dbAdapter == null){
			dbAdapter = new DBAdapter(context);
		}
		return dbAdapter;
	}


	private static class DatabaseHelper extends SQLiteOpenHelper 
	{
		DatabaseHelper(Context context) 
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) 
		{
			//Create AUDIO Article table
			db.execSQL(AudioArticles + articleFields);
			db.execSQL(VideoArticles + articleFields);
			db.execSQL(IMAGEArticles + articleFields);
			db.execSQL(TextArticles + articleFields);
			db.execSQL(Review + reviewFields);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, 
				int newVersion) 
		{
			Log.w("onUpgrade", "Upgrading database from version " + oldVersion 
					+ " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS titles");
			onCreate(db);
		}
	}    

	//---opens the database---
	public DBAdapter open() throws SQLException 
	{
		db = DBHelper.getWritableDatabase();
		return this;
	}

	public SQLiteDatabase getDatabaseHandle(){
		return db;
	}


	/**
	 * Returns true, if a database is open.
	 * @return
	 */
	public boolean isOpen(){
		boolean dbStatus = false;
		if(db != null){
			dbStatus = db.isOpen();
		}
		return dbStatus;
	}

	//---closes the database---    
	public void close() 
	{
		DBHelper.close();
	}
	
	public boolean isRunning(){
		if(db != null && db.inTransaction()){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * Insert video article 
	 * @param articleDAO
	 * @return
	 */
	boolean insertVideoArticle(ArticleDAO articleDAO){
		ContentValues initialValues = new ContentValues();
		initialValues.put("_videoArticleID", articleDAO.getArticleID());
		
		insertArticle(initialValues, "VIDEOARTICLES", articleDAO);
		
		return true;
	}
	
	public boolean insertVideoArticles(List<ArticleDAO> articles){
		if(articles != null ){
			for(ArticleDAO article:articles){
				insertVideoArticle(article);
			}
		}
		return true;
	}
	
	public boolean deleteAllVideoArticles(){
		return db.delete("VIDEOARTICLES", null , null) > 0;
	}
	
	public boolean deleteVideoArticle(ArticleDAO articleDAO){
		return db.delete("VIDEOARTICLES", articleDAO.getArticleID() , null) > 0;
	}
	
	public List<ArticleDAO> getVideoArticles(){
		 return getArticles("VIDEOARTICLES");
	}
	
	
	/**
	 * Insert image article 
	 * @param articleDAO
	 * @return
	 */
	boolean insertImageArticle(ArticleDAO articleDAO){
		ContentValues initialValues = new ContentValues();
		initialValues.put("_imageArticleID", articleDAO.getArticleID());
		
		insertArticle(initialValues, "IMAGEARTICLES", articleDAO);
		
		return true;
	}
	
	public boolean insertImageArticles(List<ArticleDAO> articles){
		if(articles != null ){
			for(ArticleDAO article:articles){
				insertAudioArticle(article);
			}
		}
		return true;
	}
	public boolean deleteAllImageArticles(){
		return db.delete("IMAGEARTICLES", null , null) > 0;
	}
	
	public boolean deleteImageArticle(ArticleDAO articleDAO){
		return db.delete("IMAGEARTICLES", articleDAO.getArticleID() , null) > 0;
	}
	
	public List<ArticleDAO> getImageArticles(){
		 return getArticles("IMAGEARTICLES");
	}
	/**
	 * Insert text article 
	 * @param articleDAO
	 * @return
	 */
	boolean insertTextArticle(ArticleDAO articleDAO){
		ContentValues initialValues = new ContentValues();
		initialValues.put("_textArticleID", articleDAO.getArticleID());
		
		insertArticle(initialValues, "TEXTARTICLES", articleDAO);
		
		return true;
	}
	
	public boolean insertTextArticles(List<ArticleDAO> articles){
		if(articles != null ){
			for(ArticleDAO article:articles){
				insertTextArticle(article);
			}
		}
		return true;
	}
	
	public boolean deleteAllTextArticles(){
		return db.delete("TEXTARTICLES", null , null) > 0;
	}
	
	public boolean deleteTextArticle(ArticleDAO articleDAO){
		return db.delete("TEXTARTICLES", articleDAO.getArticleID() , null) > 0;
	}
	
	public List<ArticleDAO> getTextArticles(){
		 return getArticles("TEXTARTICLES");
	}
	/**
	 * Insert audio article 
	 * @param articleDAO
	 * @return
	 */
	boolean insertAudioArticle(ArticleDAO articleDAO){
		ContentValues initialValues = new ContentValues();
		initialValues.put("_audioArticleID", articleDAO.getArticleID());
		
		insertArticle(initialValues, "AUDIOARTICLES", articleDAO);
		
		return true;
	}
	
	public boolean insertAudioArticles(List<ArticleDAO> articles){
		if(articles != null ){
			for(ArticleDAO article:articles){
				insertAudioArticle(article);
			}
		}
		return true;
	}
	public boolean deleteAllAudioArticles(){
		return db.delete("AUDIOARTICLES", null , null) > 0;
	}
	
	public boolean deleteAudioArticle(ArticleDAO articleDAO){
		return db.delete("AUDIOARTICLES", articleDAO.getArticleID() , null) > 0;
	}
	
	public List<ArticleDAO> getAudioArticles(){
		 return getArticles("AUDIOARTICLES");
	}
	/**
	 * Insert the record.
	 * @param initialValues
	 * @param tableName
	 * @param articleDAO
	 * @return
	 */
	boolean insertArticle(ContentValues initialValues, String tableName, ArticleDAO articleDAO){
		initialValues.put("title", articleDAO.getTitle());
		initialValues.put("shortdescription", articleDAO.getShortDescription());
		initialValues.put("detaileddescription", articleDAO.getDetailedDescription());
		initialValues.put("thumbnailurl", articleDAO.getThumbUrl());
		initialValues.put("mainurl", articleDAO.getUrl());
		initialValues.put("publishingdate", articleDAO.getPublishedDate());
		initialValues.put("createddate", articleDAO.getCreatedDate());
		long count = db.insert(tableName, null, initialValues);
		if(count  > 0){
			return true;
		}else{
			return false;
		}
	}
	
	private List<ArticleDAO> getArticles(String tableName){
		Cursor articles = db.query(tableName, null, null, null, null, null, null);
		ArticleDAO articleDAO = null;
		
		List<ArticleDAO> articleList = new ArrayList<ArticleDAO>();
		if(articles != null){
			while(articles.moveToNext()){
				articleDAO = new ArticleDAO();
				articleDAO.setArticleID(articles.getString(0));
				articleDAO.setTitle(articles.getString(1));
				articleDAO.setShortDescription(articles.getString(2));
				articleDAO.setDetailedDescription(articles.getString(3));
				articleDAO.setThumbUrl(articles.getString(4));
				articleDAO.setUrl(articles.getString(5));
				articleDAO.setPublishedDate(articles.getString(6));
				articleDAO.setCreatedDate(articles.getString(7));
				articleList.add(articleDAO);
			}
		}
		
		return articleList;
	}
	
}
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
import com.codegreen.businessprocess.objects.ArticlesPublishedDates;
import com.codegreen.util.Constants;
import com.codegreen.util.Utils;

public class DBAdapter 
{

	private static final String DATABASE_NAME = "codegreen";
	private static final int DATABASE_VERSION = 1;


	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	static DBAdapter dbAdapter;

	//Create table strings
	static final String articles = "CREATE TABLE ARTICLES (dbArticleID integer primary key autoincrement, _articleID integer , articletype varchar, ";
	static final String Review = "CREATE TABLE Review (_reviewID integer primary key, ";
	static final String PublishedDates = "CREATE TABLE PUBLISHEDDATES (textDate varchar, imageDate varchar, audioDate varchar, videoDate varchar);";

	//Article table fields
	static final String articleFields = "title varchar not null, shortdescription varchar , detaileddescription varchar," 
		+ "thumbnailurl varchar, mainurl varchar, publishingdate varchar , createddate varchar);";

	//Review table fields
	static final String reviewFields = "articleID integer, articleType varchar, reviewcomments varchar, createddate varchar);";

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
			db.execSQL(articles + articleFields);
			db.execSQL(Review + reviewFields);
			db.execSQL(PublishedDates);
			String todayDate = Utils.getCurrentDateNTime();
			db.execSQL("insert into PUBLISHEDDATES values ('" + todayDate + "','"+ todayDate + "','" + todayDate +"','"+ todayDate +"');");
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
		try{
			DBHelper.close();
			db.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isRunning(){
		if(db != null && db.inTransaction()){
			return true;
		}else{
			return false;
		}
	}


	/**
	 * Insert audio article 
	 * @param articleDAO
	 * @return
	 */
	public boolean insertArticle(ArticleDAO articleDAO){
		ContentValues initialValues = new ContentValues();
		initialValues.put("_articleID", articleDAO.getArticleID());

		insertArticle(initialValues, "ARTICLES", articleDAO);

		return true;
	}

	public boolean insertArticles(List<ArticleDAO> articles){
		if(articles != null ){
			for(ArticleDAO article:articles){
				insertArticle(article);
			}
		}
		return true;
	}
	public boolean deleteAllArticles(){
		return db.delete("ARTICLES", "1" , null) > 0;
	}

	public boolean deleteArticle(ArticleDAO articleDAO){
		return db.delete("ARTICLES", "_articleID=" +articleDAO.getArticleID() + " and articletype='" + articleDAO.getType() + "'", null) > 0;
	}

	public int getArticles(ArticleDAO articleDAO){
		Cursor articles = db.query("ARTICLES", null, "_articleID=" +articleDAO.getArticleID() + " and articletype='" + articleDAO.getType() + "'"
				, null, null, null, null);
		if(articles != null ){
			return articles.getCount();
		}else{
			return 0;
		}
	}
	public List<ArticleDAO> getArticles(){
		return getArticles("ARTICLES");
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
		initialValues.put("articletype", articleDAO.getType());
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
			int index = 0;
			while(articles.moveToNext()){
				index = 0;
				articleDAO = new ArticleDAO();
				articleDAO.setDbArticleID(articles.getString(index++));
				articleDAO.setArticleID(articles.getString(index++));
				articleDAO.setType(articles.getString(index++));
				articleDAO.setTitle(articles.getString(index++));
				articleDAO.setShortDescription(articles.getString(index++));
				articleDAO.setDetailedDescription(articles.getString(index++));
				articleDAO.setThumbUrl(articles.getString(index++));
				articleDAO.setUrl(articles.getString(index++));
				articleDAO.setPublishedDate(articles.getString(index++));
				articleDAO.setCreatedDate(articles.getString(index++));
				articleList.add(articleDAO);
			}
		}

		return articleList;
	}

	
	/**
	 * textDate varchar, imageDate varchar, audioDate varchar, videoDate varchar
	 * @return
	 */
	public ArticlesPublishedDates getPublishedDates(){
		ArticlesPublishedDates dates = new ArticlesPublishedDates();
		Cursor articles = db.query("PUBLISHEDDATES", null, null, null, null, null, null);
		
		if(articles != null){
			articles.moveToNext();
			
			dates.setAudioPublishedDate(articles.getString(2));
			dates.setTextPublishedDate(articles.getString(0));
			dates.setVideoPublishedDate(articles.getString(3));
			dates.setImagePublishedDate(articles.getString(1));
		}
		
		return dates;
	}
	/*
	 *  textDate varchar, imageDate varchar, audioDate varchar, videoDate varchar
	 */
	public void updatePublishedDates(ArticlesPublishedDates dates){
		ContentValues initialValues = new ContentValues();
		initialValues.put("textDate", dates.getTextPublishedDate());
		initialValues.put("imageDate", dates.getImagePublishedDate());
		initialValues.put("audioDate", dates.getAudioPublishedDate());
		initialValues.put("videoDate", dates.getVideoPublishedDate());
		db.insert("PUBLISHEDDATES", null, initialValues);
	}
}
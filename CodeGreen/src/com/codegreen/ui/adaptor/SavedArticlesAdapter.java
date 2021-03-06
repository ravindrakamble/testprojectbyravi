package com.codegreen.ui.adaptor;


import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.codegreen.R;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.common.CacheManager;
import com.codegreen.database.DBAdapter;
import com.codegreen.network.FetchImage;
import com.codegreen.ui.adaptor.HomeScreenAdapter.ListHolder;
import com.codegreen.util.Constants;
import com.codegreen.util.Utils;

public class SavedArticlesAdapter extends BaseAdapter{

	private Context mContext;
	private LayoutInflater mLayoutInflator = null;
	private ArrayList<ArticleDAO> mArticleList;
	private DBAdapter dbAdapter;
	private FetchImage imageLoader;

	public SavedArticlesAdapter(Context context) {
		try {
			mContext = context;
			mLayoutInflator  =  LayoutInflater.from(mContext);
			dbAdapter = DBAdapter.getInstance(context);
			dbAdapter.open();
			mArticleList = (ArrayList<ArticleDAO>)dbAdapter.getArticles();
			dbAdapter.close();
			if(mArticleList != null){
				Constants.TOTAL_ARTICLES = mArticleList.size();
				CacheManager.getInstance().store(Constants.C_SAVED_ARTICLES, mArticleList);
			}
			imageLoader = new FetchImage(mContext);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeItemAt(int position){
		mArticleList.remove(position);
		notifyDataSetChanged();
	}

	public void removeAll(){
		mArticleList.clear();
		notifyDataSetChanged();
	}

	/**
	 *  Apply Filtering  
	 * @param filterStr
	 */
	public int applyFilter(String filterStr){
		int filterCount = 0;

		try{
			if(filterStr == null)
				return 0;

			filterCount =	filterEntries(filterStr);
			//Draw Again....
			notifyDataSetInvalidated();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return filterCount;
	}




	/**
	 * Filter Contact Entries using the filterStr paramter.
	 * @param filterStr
	 */
	private int filterEntries(String filterStr){
		try{
			mArticleList.clear();
			if(filterStr != null)
				filterStr = filterStr.toLowerCase();

			if(mArticleList != null){
				String title = null;
				for(int i =0;i<mArticleList.size();i++){
					ArticleDAO article = (ArticleDAO)mArticleList.get(i);
					if(filterStr == null){
						mArticleList.add(article);
					}else{
						title = article.getTitle();
						if(title == null)
							continue;

						title = title.toLowerCase();
						if(title.contains((filterStr)))
							mArticleList.add(article);
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return mArticleList.size();
	}


	@Override
	public int getCount() {
		if (mArticleList != null)
			return mArticleList.size();
		else
			return 0;
	}

	@Override
	public Object getItem(int position) {
		if (mArticleList != null)
			return mArticleList.get(position);
		else
			return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListHolder holder;
		if (convertView == null) {
			convertView = mLayoutInflator.inflate(R.layout.listitemrow,null);
			holder = new ListHolder();
			holder.layout_main = (RelativeLayout) convertView.findViewById(R.id.list_row);
			holder.txt_articleName = (TextView) convertView.findViewById(R.id.textArticle);
			holder.img_thumbnail = (ImageView) convertView.findViewById(R.id.ImgThumbnail);
			holder.txt_articleDesc = (TextView)convertView.findViewById(R.id.textArticledesc);
			holder.progress = (ProgressBar) convertView.findViewById(R.id.progress_bar);
			convertView.setTag(holder);

		} else {
			convertView.buildDrawingCache();
			holder = (ListHolder) convertView.getTag();
		}

		holder.progress.setVisibility(View.INVISIBLE); //ADDED
		holder.layout_main.setBackgroundResource(R.drawable.listitem_selector);

		// set Values 
		ArticleDAO data = mArticleList.get(position);
		Log.e(" Position:" +position , "Title" +data.getTitle());
		if(data!= null){  

			holder.img_thumbnail.setVisibility(View.VISIBLE);
			holder.txt_articleName.setText(data.getTitle());
			holder.txt_articleDesc.setText(data.getShortDescription());
			holder.img_thumbnail.setVisibility(View.VISIBLE);

			Resources res = mContext.getResources(); 
			
			if(data.getThumbUrl() != null && !data.getThumbUrl().equals("")){
				if(data.getDownloadedImage() == null){
					BitmapFactory.Options opt = new BitmapFactory.Options();
					opt.inSampleSize = 4;
					Bitmap bitmap = BitmapFactory.decodeFile(data.getThumbUrl(),opt); 
					bitmap = Utils.getRoundedCornerBitmap(bitmap);
					if(bitmap != null){
						data.setDownloadedImage(bitmap);
						holder.img_thumbnail.setImageBitmap(data.getDownloadedImage());
					}else{
						holder.img_thumbnail.setImageResource(R.drawable.default_thumb);
					}
				}else {
					Bitmap bmp = Utils.getRoundedCornerBitmap(data.getDownloadedImage());
					holder.img_thumbnail.setImageBitmap(bmp);
				}
			}else{
				holder.img_thumbnail.setImageResource(R.drawable.default_thumb);
			}
		}
		return convertView;
	}



	@Override
	public void notifyDataSetChanged() {
		try {
			dbAdapter.open();
			mArticleList = (ArrayList<ArticleDAO>)dbAdapter.getArticles();
			dbAdapter.close();

			if(mArticleList != null){
				CacheManager.getInstance().store(Constants.C_SAVED_ARTICLES, mArticleList);
				Constants.TOTAL_ARTICLES = mArticleList.size();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static class ListHolder {
		RelativeLayout layout_main;
		TextView txt_articleName;
		ImageView img_thumbnail;
		TextView txt_articleDesc;
		public ProgressBar progress; //ADDED
	}

}




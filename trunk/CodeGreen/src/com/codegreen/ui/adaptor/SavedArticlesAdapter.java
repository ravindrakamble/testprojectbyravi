package com.codegreen.ui.adaptor;


import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.codegreen.R;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.common.CacheManager;
import com.codegreen.database.DBAdapter;
import com.codegreen.network.FetchImage;
import com.codegreen.util.Constants;

public class SavedArticlesAdapter extends BaseAdapter implements SectionIndexer {

	private Context mContext;
	private LayoutInflater mLayoutInflator = null;
	private FetchImage imageLoader;
	private ArrayList<ArticleDAO> mArticleList;
	private DBAdapter dbAdapter;

	public SavedArticlesAdapter(Context context) {
		try {
			mContext = context;
			mLayoutInflator  =  LayoutInflater.from(mContext);
			dbAdapter = DBAdapter.getInstance(context);
			dbAdapter.open();
			mArticleList = (ArrayList<ArticleDAO>)dbAdapter.getArticles();
			imageLoader=new FetchImage(mContext);
			dbAdapter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			holder.txt_articleName = (TextView) convertView.findViewById(R.id.textArticle);
			holder.img_thumbnail = (ImageView) convertView.findViewById(R.id.ImgThumbnail);
			convertView.setTag(holder);

		} else {
			convertView.buildDrawingCache();
			holder = (ListHolder) convertView.getTag();
		}

		// set Values 
		ArticleDAO data = mArticleList.get(position);
		if(data!= null){ 
			holder.txt_articleName.setText(data.getShortDescription().toString());
			holder.img_thumbnail.setVisibility(View.VISIBLE);

			if(!data.getType().equalsIgnoreCase(Constants.ARTCLETYPE_TEXT)){
				if(data.getThumbUrl() == null){
					holder.img_thumbnail.setVisibility(View.GONE);
				}else{
					holder.img_thumbnail.setVisibility(View.VISIBLE);
					if(data.getDownloadedImage() == null){ 
						imageLoader.DisplayImage(data, mContext, holder.img_thumbnail);
					}else {
						holder.img_thumbnail.setImageBitmap(data.getDownloadedImage());
					}
				}
			}else{
				holder.img_thumbnail.setVisibility(View.GONE);
			}
		}
		return convertView;
	}



	@Override
	public void notifyDataSetChanged() {
		try {
			dbAdapter.open();
			mArticleList = (ArrayList<ArticleDAO>)dbAdapter.getArticles();
			imageLoader=new FetchImage(mContext);
			dbAdapter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static class ListHolder {
		TextView txt_articleName;
		ImageView img_thumbnail;
	}

	@Override
	public int getPositionForSection(int arg0) {
		return 0;
	}

	@Override
	public int getSectionForPosition(int arg0) {
		return 0;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

}




package com.codegreen.ui.adaptor;

import java.util.ArrayList;
import java.util.Collections;

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
import com.codegreen.network.FetchImage;
import com.codegreen.util.Constants;

public class SearchScreenAdapter extends BaseAdapter implements SectionIndexer {

	private Context mContext;
	private ArrayList<ArticleDAO> mAllArticleList;
	private LayoutInflater mLayoutInflator = null;
	private FetchImage imageLoader;
	private ArrayList<ArticleDAO> mArticleList;

	public SearchScreenAdapter(Context context, String filterStr) {
		try {
			mContext = context;
			mLayoutInflator  =  LayoutInflater.from(mContext); 
			mAllArticleList = (ArrayList<ArticleDAO>) CacheManager.getInstance().get(Constants.C_SEARCH_ARTICLES);
			imageLoader=new FetchImage(mContext);
			filterEntries(filterStr);

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
			
			if(mAllArticleList != null){
				String title = null;
				for(int i =0;i<mAllArticleList.size();i++){
					ArticleDAO article = (ArticleDAO)mAllArticleList.get(i);
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
				/*if(mArticleList.size() > 0)
					Collections.sort(mContacts, new GenericComparator(EnumComparatorObjectType.ENUM_OBJECT_ADDRESS_BOOK_ENTRY));
			*/}
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
		}
		return convertView;
	}



	@Override
	public void notifyDataSetChanged() {
		try {
			mArticleList = (ArrayList<ArticleDAO>) CacheManager.getInstance().get(Constants.C_SEARCH_ARTICLES);
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSectionForPosition(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		return null;
	}

}



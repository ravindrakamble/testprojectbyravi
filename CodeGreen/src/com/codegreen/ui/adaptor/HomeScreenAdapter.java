package com.codegreen.ui.adaptor;
import java.util.ArrayList;

import twitter4j.RelatedResults;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.codegreen.R;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.common.CacheManager;
import com.codegreen.network.FetchImage;
import com.codegreen.util.Constants;

@SuppressWarnings("unchecked")
public class HomeScreenAdapter extends BaseAdapter implements SectionIndexer {

	private Context mContext;
	private ArrayList<ArticleDAO> mArticleList;
	private LayoutInflater mLayoutInflator = null;
	private FetchImage imageLoader;

	public HomeScreenAdapter(Context context) {
		try {
			mContext = context;
			mLayoutInflator  =  LayoutInflater.from(mContext); 
			if(mReqId == Constants.REQ_GETARTICLESBYTYPE)
				mArticleList = (ArrayList<ArticleDAO>) CacheManager.getInstance().get(Constants.C_ARTICLES);
			else 
				mArticleList = (ArrayList<ArticleDAO>) CacheManager.getInstance().get(Constants.C_SEARCH_ARTICLES);
			imageLoader=new FetchImage(mContext);

		} catch (Exception e) {
			e.printStackTrace();
		}
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
			convertView.setTag(holder);

		} else {
			convertView.buildDrawingCache();
			holder = (ListHolder) convertView.getTag();
		}
		
		
		holder.layout_main.setBackgroundResource(R.drawable.listitem_selector);

		// set Values 
		ArticleDAO data = mArticleList.get(position);
		if(data!= null){ 
			
			holder.img_thumbnail.setVisibility(View.VISIBLE);
			holder.txt_articleName.setText(data.getShortDescription().toString());

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

	byte mReqId;

	public void setRequestID(byte reqID){
		mReqId = reqID;
	}



	@Override
	public void notifyDataSetChanged() {
		try {
			if(mReqId == Constants.REQ_GETARTICLESBYTYPE)
				mArticleList = (ArrayList<ArticleDAO>) CacheManager.getInstance().get(Constants.C_ARTICLES);
			else 
				mArticleList = (ArrayList<ArticleDAO>) CacheManager.getInstance().get(Constants.C_SEARCH_ARTICLES);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void notifyDataSetInvalidated() {
		super.notifyDataSetInvalidated();
	}


	static class ListHolder {
		RelativeLayout layout_main;
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

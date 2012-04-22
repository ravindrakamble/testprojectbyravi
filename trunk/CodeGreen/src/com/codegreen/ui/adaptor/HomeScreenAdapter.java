package com.codegreen.ui.adaptor;

import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
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
import com.codegreen.network.FetchImage;
import com.codegreen.network.ImageManager;
import com.codegreen.util.Constants;

@SuppressWarnings("unchecked")
public class HomeScreenAdapter extends BaseAdapter implements SectionIndexer {

	private Context mContext;
	private ArrayList<ArticleDAO> mArticleList;
	private LayoutInflater mLayoutInflator = null;
	private ImageManager imageLoader;
	private Activity mycontext;
	private URL myurl; 
	private Hashtable<Integer,ImageView> viewholders;

	public HomeScreenAdapter(Context context) {
		try {
			mContext = context;
			mLayoutInflator  =  LayoutInflater.from(mContext); 

			if(mArticleList == null){
				mArticleList = new ArrayList<ArticleDAO>();
			}else{
				mArticleList.clear();
			}
			ArrayList<ArticleDAO> tempdata = (ArrayList<ArticleDAO>) CacheManager.getInstance().getAllArticles();

			if(tempdata != null && tempdata.size() > 0){
				for(int i =0 ; i<tempdata.size();i++){
					mArticleList.add(tempdata.get(i));
				}
			}

			// in case of 10 or more than 10 then display add more option
			if(mArticleList != null && mArticleList.size() >= 10){
				ArticleDAO data = new ArticleDAO();
				data.setTitle("Load more articles");
				data.setLoadMore(true);
				mArticleList.add(data);
			}

			if(mArticleList != null){
				Constants.TOTAL_ARTICLES = mArticleList.size();
			}

			imageLoader = new ImageManager(mContext);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/*public void resetData(){
		if(imageLoader != null){
			imageLoader = null;
		}
		imageLoader = new ImageManager(mContext);
	}*/

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
		// set Values 
		ArticleDAO data = mArticleList.get(position);
		if (convertView == null) {
			convertView = mLayoutInflator.inflate(R.layout.listitemrow,null);
			holder = new ListHolder();
			holder.layout_main = (RelativeLayout) convertView.findViewById(R.id.list_row);
			holder.txt_articleName = (TextView) convertView.findViewById(R.id.textArticle);
			holder.img_thumbnail  = (ImageView) convertView.findViewById(R.id.ImgThumbnail);
			holder.txt_articleDesc = (TextView)convertView.findViewById(R.id.textArticledesc);
			holder.img_arrow = (ImageView)convertView.findViewById(R.id.Imgarrow);
			holder.img_thumbnail.setTag(data.getThumbUrl());
			holder.progress = (ProgressBar) convertView.findViewById(R.id.progress_bar); //ADDED
			convertView.setTag(holder);

		} else {
			holder = (ListHolder) convertView.getTag();
			holder.img_thumbnail.setTag(data.getThumbUrl());
		}

		holder.layout_main.setBackgroundResource(R.drawable.listitem_selector);

		

		holder.txt_articleName.setPadding(5, 5, 5, 0);
		if(data!= null){ 

			if(data.isLoadMore()){
				holder.img_arrow.setVisibility(View.GONE);
				holder.img_thumbnail.setVisibility(View.GONE);
				holder.txt_articleDesc.setVisibility(View.GONE);
				holder.txt_articleName.setText(data.getTitle());
				holder.txt_articleName.setPadding(10, 20, 10, 20);
				holder.progress.setVisibility(View.GONE);
			}else{
				int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float)4, mContext.getResources().getDisplayMetrics());
				holder.txt_articleName.setPadding(value, value, value, 0);
				holder.img_arrow.setVisibility(View.VISIBLE);
				holder.img_thumbnail.setVisibility(View.VISIBLE);
				holder.txt_articleDesc.setVisibility(View.VISIBLE);
				holder.txt_articleName.setText(data.getTitle());
				holder.txt_articleDesc.setText(data.getShortDescription());
				holder.img_thumbnail.setBackgroundDrawable(null);
				holder.img_thumbnail.setImageBitmap(null);
				holder.img_thumbnail.setImageResource(R.drawable.default_thumb);
				
				if(data.getThumbUrl() != null && !data.getThumbUrl().equals("")){
					//if(data.getDownloadedImage() == null){
						//ArticleDAO dao = CacheManager.getInstance().getDataAt(position);
						
						imageLoader.displayImage(data.getThumbUrl(), (Activity)mContext, holder.img_thumbnail,holder.progress);
						/*if(holder.img_thumbnail.getDrawingCache() != null){
							mArticleList.get(position).setDownloadedImage(holder.img_thumbnail.getDrawingCache());
							if(dao != null){
								data.setDownloadedImage(holder.img_thumbnail.getDrawingCache());
							}
						}else{
							holder.img_thumbnail.setImageBitmap(null);
							holder.img_thumbnail.setBackgroundDrawable(null);
							holder.img_thumbnail.setImageResource(R.drawable.default_thumb);
						}*/
					/*}else {
						holder.img_thumbnail.setImageBitmap(null);
						holder.img_thumbnail.setImageBitmap(data.getDownloadedImage());
					}*/
				}else{
					holder.img_thumbnail.setImageBitmap(null);
					holder.img_thumbnail.setBackgroundDrawable(null);
					holder.img_thumbnail.setImageResource(R.drawable.default_thumb);
				}
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
			if(mArticleList != null){
				mArticleList.clear();
			}else{
				mArticleList = new ArrayList<ArticleDAO>();
			}
			ArrayList<ArticleDAO> tempdata =  CacheManager.getInstance().getAllArticles();

			if(tempdata != null && tempdata.size() > 0){
				for(int i =0 ; i<tempdata.size();i++){
					mArticleList.add(tempdata.get(i));
				}
			}

			// in case of 10 or more than 10 then display add more option
			if(mArticleList != null && mArticleList.size() >= 10){
				ArticleDAO data = new ArticleDAO();
				data.setTitle("Load more articles");
				data.setLoadMore(true);
				mArticleList.add(data);
			}

			if(mArticleList != null){
				Constants.TOTAL_ARTICLES = mArticleList.size();
			}
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
		TextView txt_articleDesc;
		ImageView img_arrow;
		public ProgressBar progress; //ADDED
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


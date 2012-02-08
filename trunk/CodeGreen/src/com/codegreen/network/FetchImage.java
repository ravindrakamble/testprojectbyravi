package com.codegreen.network;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import com.codegreen.R;
import com.codegreen.businessprocess.objects.ArticleDAO;


public class FetchImage 
{ 

	Context _context = null;

	public FetchImage(Context context)
	{
		_context = context;
		//Make the background thead low priority. This way it will not affect the UI performance
		photoLoaderThread.setPriority(Thread.NORM_PRIORITY-1); 
	} 

	private Bitmap getBitmap(String url) 
	{  
		Bitmap bitmap = null;

		try 
		{ 
			//Log.e("GetBitmap :: ", url);
			url = url.replaceAll(" ", "%20");
			//url = URLEncoder.encode(url);
			InputStream is=new URL(url).openStream(); 
			BufferedInputStream bis = new BufferedInputStream(is);  
			Log.e("Image downloaded:", "" + url);
			return bitmap = BitmapFactory.decodeStream(bis);
		}
		catch (Exception ex)
		{
			//Log.e("OOOps Error : ", ex.toString());
			//ex.printStackTrace();
			return null;
		}
	}



	public void stopThread()
	{
		photoLoaderThread.interrupt();
	}

	//stores list of photos to download
	class PhotosQueue
	{
		private ArrayList<ArticleDAO> imageDowload = new ArrayList<ArticleDAO>();


	}   

	final int stub_id = R.drawable.default_thumb;
	
	public void DisplayImage(ArticleDAO vo, Context activity, ImageView sponserImg)
	{ 
		queuePhoto(vo,activity,sponserImg); 
		sponserImg.setImageResource(stub_id); 
	} 

	private void queuePhoto(ArticleDAO vo, Context activity, ImageView sponserImg)
	{ 
		Log.e("Image queued:", "" + vo.getThumbUrl());
		vo.setImagedrawable(sponserImg);

		synchronized(photosQueue.imageDowload)
		{
			photosQueue.imageDowload.add(vo); 
		}

		//start thread if it's not started yet
		if(photoLoaderThread.getState()==Thread.State.NEW)
			photoLoaderThread.start();
	}

	int currentIndex = 0;
	class PhotosLoader extends Thread 
	{
		public void run() {
			try {
				while(true)
				{
					//thread waits until there are any images to load in the queue
					if(photosQueue.imageDowload.size()==0)
						synchronized(photosQueue.imageDowload)
						{
							photosQueue.imageDowload.wait();
						}
					if(photosQueue.imageDowload.size()!=0 && currentIndex < photosQueue.imageDowload.size())
						{
						//Log.e("******** Downloading sponsor images here : ", photosQueue.imageDowload.get(currentIndex).getThumbUrl());
						Bitmap bmp = getBitmap(photosQueue.imageDowload.get(currentIndex).getThumbUrl()); 
						if(bmp != null)
						{
							photosQueue.imageDowload.get(currentIndex).setDownloadedImage(bmp) ; 
						}
						BitmapDisplayer bitmapDisplay = new BitmapDisplayer(bmp,photosQueue.imageDowload.get(currentIndex).getImagedrawable());
						Activity a=(Activity)_context; 
						a.runOnUiThread(bitmapDisplay);
						currentIndex++; 
					}
					if(Thread.interrupted())
						break;
				}
			} catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
	}
	PhotosQueue photosQueue = new PhotosQueue(); 
	PhotosLoader photoLoaderThread=new PhotosLoader(); 


	//Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable
	{
		Bitmap bitmap;
		ImageView imageView;
		public BitmapDisplayer(Bitmap b, ImageView i){
			bitmap=b;
			imageView=i;
			}
		public void run()
		{
			if(bitmap!=null)
				imageView.setImageBitmap(bitmap);
			else
				imageView.setImageResource(stub_id);
		}
	}

}

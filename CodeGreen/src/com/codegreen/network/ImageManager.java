package com.codegreen.network;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Stack;

import com.codegreen.R;
import com.codegreen.util.Utils;
import com.facebook.android.Util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ImageManager {

	private HashMap<String, Bitmap> imageMap = new HashMap<String, Bitmap>();

	private File cacheDir;
	private ImageQueue imageQueue = new ImageQueue();
	private Thread imageLoaderThread = new Thread(new ImageQueueManager());

	public ImageManager(Context context) {
		// Make background thread low priority, to avoid affecting UI performance
		imageLoaderThread.setPriority(Thread.NORM_PRIORITY-1);

		// Find the dir to save cached images
		String sdState = android.os.Environment.getExternalStorageState();
		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File sdDir = android.os.Environment.getExternalStorageDirectory();		
			cacheDir = new File(sdDir,"/sdcard/codegreen/");
		}
		else
			cacheDir = context.getCacheDir();

		if(!cacheDir.exists())
			cacheDir.mkdirs();
	}

	public void displayImage(String url, Activity activity, ImageView imageView,ProgressBar progressBar) {
		if(imageMap.containsKey(url)){
			if(imageMap.get(url) != null){
				imageView.setImageBitmap(imageMap.get(url));
			}else{
				imageView.setImageResource(R.drawable.default_thumb);
			}
			progressBar.setVisibility(View.GONE); //ADDED

		}else {
			progressBar.setVisibility(View.VISIBLE); //ADDED
			queueImage(url, activity, imageView,progressBar);
			imageView.setImageResource(R.drawable.default_thumb);
		}
	}

	private void queueImage(String url, Activity activity, ImageView imageView, ProgressBar progressBar) {
		// This ImageView might have been used for other images, so we clear 
		// the queue of old tasks before starting.
		imageQueue.Clean(imageView);
		ImageRef p=new ImageRef(url, imageView,progressBar);

		synchronized(imageQueue.imageRefs) {
			imageQueue.imageRefs.push(p);
			imageQueue.imageRefs.notifyAll();
		}

		// Start thread if it's not started yet
		if(imageLoaderThread.getState() == Thread.State.NEW)
			imageLoaderThread.start();
	}

	private Bitmap getBitmap(String url) {
		String filename = String.valueOf(url.hashCode());
		File f = new File(cacheDir, filename);

		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
		// Is the bitmap in our cache?
		//Bitmap bitmap = BitmapFactory.decodeFile(f.getPath());
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inSampleSize = 4;
		Log.e("Bitmap length:", "" +f.length());
		Bitmap bitmap = BitmapFactory.decodeFile(f.getPath(),opt);
		if(bitmap != null) return bitmap;

		// Nope, have to download it
		try {

			bitmap = BitmapFactory.decodeStream(new URL(url).openConnection().getInputStream());
			if(bitmap != null){
				// save bitmap to cache for later
				writeFile(bitmap, f);
			}
			return bitmap;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private void writeFile(Bitmap bmp, File f) {
		FileOutputStream out = null;

		try {
			if(f.exists()){
				out = new FileOutputStream(f);
				bmp.compress(Bitmap.CompressFormat.PNG, 80, out);
			}else{
				Log.e("IMAGEMANAGER", "File not exist" );
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally { 
			try { if (out != null ) out.close(); }
			catch(Exception ex) {} 
		}
	}

	/** Classes **/

	private class ImageRef {
		public String url;
		public ImageView imageView;
		ProgressBar progressBar;

		public ImageRef(String u, ImageView i,ProgressBar p) {
			url=u;
			imageView=i;
			progressBar = p;
		}
	}

	//stores list of images to download
	private class ImageQueue {
		private Stack<ImageRef> imageRefs = 
			new Stack<ImageRef>();

		//removes all instances of this ImageView
		public void Clean(ImageView view) {

			for(int i = 0 ;i < imageRefs.size();) {
				if(imageRefs.get(i).imageView == view)
					imageRefs.remove(i);
				else ++i;
			}
		}
	}

	private class ImageQueueManager implements Runnable {
		@Override
		public void run() {
			try {
				while(true) {
					// Thread waits until there are images in the 
					// queue to be retrieved
					if(imageQueue.imageRefs.size() == 0) {
						synchronized(imageQueue.imageRefs) {
							imageQueue.imageRefs.wait();
						}
					}

					// When we have images to be loaded
					if(imageQueue.imageRefs.size() != 0) {
						ImageRef imageToLoad;

						synchronized(imageQueue.imageRefs) {
							imageToLoad = imageQueue.imageRefs.pop();
						}

						Bitmap bmp = getBitmap(imageToLoad.url);
						bmp = Utils.getRoundedCornerBitmap(bmp);
						imageMap.put(imageToLoad.url, bmp);
						Object tag = imageToLoad.imageView.getTag();

						// Make sure we have the right view - thread safety defender
						if(tag != null && ((String)tag).equals(imageToLoad.url)) {
							BitmapDisplayer bmpDisplayer = 
								new BitmapDisplayer(bmp, imageToLoad.imageView,imageToLoad.progressBar);

							Activity a = 
								(Activity)imageToLoad.imageView.getContext();

							a.runOnUiThread(bmpDisplayer);
						}
					}

					if(Thread.interrupted())
						break;
				}
			} catch (InterruptedException e) {}
		}
	}

	//Used to display bitmap in the UI thread
	private class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		ImageView imageView;
		ProgressBar progressBar;


		public BitmapDisplayer(Bitmap b, ImageView i,ProgressBar p) {
			bitmap=b;
			imageView=i;
			progressBar = p;
		}

		public void run() {
			progressBar.setVisibility(View.GONE); //ADDED
			if(bitmap != null){
				imageView.setImageBitmap(bitmap);
			}
			else{
				imageView.setImageResource(R.drawable.default_thumb);
			}}
	}
}
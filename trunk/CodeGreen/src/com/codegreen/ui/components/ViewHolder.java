package com.codegreen.ui.components;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ViewHolder {
	/**
	 * File/Folder Icon
	 */
	public ImageView rowIcon;
	/**
	 * File/Folder name
	 */
	public TextView fileName;
	/**
	 * Shared Icon
	 */
	public ImageView sharedIcon;
	/**
	 * Linked Icon
	 */
	public ImageView linkedIcon;
	/**
	 * File size or number of elements in a folder
	 */
	public TextView fileSize;
	/**
	 * Last modified date of file/folder
	 */
	public TextView modifiedDate;
	/**
	 * Uploading progress bar
	 */
	public ProgressBar progressBar;
	/**
	 * Progress percent text
	 */
	public TextView percentageText;
	/**
	 * Upload cancel button
	 */
	public ImageButton cancelBtn;
	/**
	 * Folder arrow image
	 */
	public ImageView arrowImage;
	/**
	 * Folder move icon
	 */
	public ImageView moveBtn;
	/**
	 * Folder move icon relative layout
	 */
	public RelativeLayout moveBtnRelLayout;
	
}

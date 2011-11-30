package com.codegreen.ui.util;

public interface UIConstants {
	
	// Constants used for the status of Files.
	public static final int FILE_NOT_UPLOADED = 1;
	public static final int FILE_UPLOADING = 2;
	public static final int FILE_QUEUED = 3;
	public static final int FILE_UPLOADED = 4;
	
	//Dialog box constants
	public static final int DIALOG_OK = 1;
	public static final int DIALOG_ROUND_PROGRESS = 2;
	public static final int DIALOG_HORIZONTAL_PROGRESS = 3;
	public static final int DOWNLOAD_DIALOG_ROUND = 4;
	public static final int DIALOG_ROUND_DNT = 5;
	public static final int DIALOG_INFO = 6;
	public static final int DIALOG_SLIDES = 7;
	public static final int DIALOG_CONFIRM = 8;
	public static final int DIALOG_STREAMING = 9;
	public static final int DIALOG_NO_FILE = 10;
	public static final int DIALOG_LOADING = 12;
	public static final int DELETE_CONFIRM = 15;
	public static final int CREATE_FOLDER = 16;
	public static final int RENAME_FOLDER = 17;
	
	/**
     * Dialog confirmation types
     */
    public static final int CONFIRM_DELETE = 0;
    public static final int CONFIRM_EXIT = 1;
    public static final int CONFIRM_DOWNLOAD = 2;
	
	//Menu event ID's
	public static final int MENU_NEW = 1;
	public static final int MENU_UPLOAD = 2;
	public static final int MENU_LIST = 3;
	
	// New file Context menu IDs
	public static final int CONTEXT_MENU_NEW_FOLDER = 0;
	public static final int CONTEXT_MENU_NEW_PICTURE = 1;
	public static final int CONTEXT_MENU_NEW_VIDEO = 2;
	public static final int CONTEXT_MENU_NEW_AUDIO = 3;
	public static final int CONTEXT_MENU_NEW_TEXT_FILE = 4;
	
	// Upload Context menu IDs
	public static final int CONTEXT_MENU_UPLOAD_PICTURE = 0;
	public static final int CONTEXT_MENU_UPLOAD_VIDEO = 1;
	public static final int CONTEXT_MENU_UPLOAD_AUDIO = 2;
	public static final int CONTEXT_MENU_UPLOAD_ANY_FILE = 3;
	
	public static final int CONTEXT_MENU_DELETE = 0;
	public static final int CONTEXT_MENU_RENAME = 1;
	public static final int CONTEXT_MENU_MOVE = 2;
	public static final int CONTEXT_MENU_DOWNLOAD = 3;
	public static final int CONTEXT_MENU_SHARE = 4;
	
	//Notification iD
	public static final int NOTIFICATION_ID = 1;
	
}

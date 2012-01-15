package com.codegreen.businessprocess.objects;

public class DownloadInfoDAO {

	private String urlToDownload;
	private String type;
	private String fileName;
	public String getUrlToDownload() {
		return urlToDownload;
	}
	public void setUrlToDownload(String urlToDownload) {
		this.urlToDownload = urlToDownload;
		if(urlToDownload != null){
			String name = urlToDownload.substring(urlToDownload.lastIndexOf("/") + 1, urlToDownload.length());
			this.fileName = name;
		}
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	
}

package com.codegreen.businessprocess.objects;

public class ArticleDAO {

	private String type;
	private String lastArticlePublishingDate;
	private String artcleID;
	private String title;
	private String shortDescription;
	private String detailedDescription;
	private String thumbUrl;
	private String url;
	private String publishedDate;
	private String createdDate;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLastArticlePublishingDate() {
		return lastArticlePublishingDate;
	}
	public void setLastArticlePublishingDate(String lastArticlePublishingDate) {
		this.lastArticlePublishingDate = lastArticlePublishingDate;
	}
	public String getArtcleID() {
		return artcleID;
	}
	public void setArtcleID(String artcleID) {
		this.artcleID = artcleID;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getShortDescription() {
		return shortDescription;
	}
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	public String getThumbUrl() {
		return thumbUrl;
	}
	public void setThumbUrl(String thumbUrl) {
		this.thumbUrl = thumbUrl;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPublishedDate() {
		return publishedDate;
	}
	public void setPublishedDate(String publishedDate) {
		this.publishedDate = publishedDate;
	}
	public String getDetailedDescription() {
		return detailedDescription;
	}
	public void setDetailedDescription(String detailedDescription) {
		this.detailedDescription = detailedDescription;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
}
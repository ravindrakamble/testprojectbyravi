/**
 * =================================================================================================================
 * File Name            : DDXmlParser.java
 * =====================================================================================================================
 *  Sr. No.     Date            Name                       Description
 * =====================================================================================================================
 * =====================================================================================================================
 */
package com.codegreen.parser;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.businessprocess.objects.ReviewDAO;
import com.codegreen.util.Constants;


/**
 * @author ravindra.kamble
 *
 */
public class XmlParser extends DefaultHandler implements ParserConstants{


	private byte requestID;

	private String parsedData;
	private String startTagName;
	private String endTagName;

	private List<ArticleDAO> articles;
	private List<ReviewDAO> reviews;
	private ArticleDAO articleDAO;
	private ReviewDAO reviewDAO;
	private String reviewMessage;
	private Constants.PARSING parseMessage;

	public XmlParser(byte reqID){
		this.requestID = reqID;
	}
	/**
	 * This method at starting of the document. 
	 * @see org.xml.sax.helpers.DefaultHandler#startDocument()
	 */
	@Override
	public void startDocument() throws SAXException {
		switch(requestID){
		case Constants.REQ_GETARTICLESBYTYPE:
		case Constants.REQ_SEARCHARTICLES:
			articles = new ArrayList<ArticleDAO>();
			break;
		case Constants.REQ_GETARTICLEDETAILS:
			articleDAO  = new ArticleDAO();
			break;
		case Constants.REQ_GETREVIEWS:
			reviews = new ArrayList<ReviewDAO>();
			break;
		
		case Constants.REQ_SUBMITREVIEW:
			break;
		}
		parseMessage = Constants.PARSING.STARTED;
	}

	/**
	 * This method calls at the end of the document parsing.
	 * @see org.xml.sax.helpers.DefaultHandler#endDocument()
	 */
	@Override
	public void endDocument() throws SAXException {
		parseMessage = Constants.PARSING.COMPLETED;
		super.endDocument();
	}
	/**
	 * This method is called when parsing starts from the first node
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		if(localName == null){
			localName = qName;
		}

		startTagName = localName;

		switch(requestID){
		case Constants.REQ_GETARTICLESBYTYPE:
		case Constants.REQ_SEARCHARTICLES:
		case Constants.REQ_GETARTICLEDETAILS:
			if(startTagName.equalsIgnoreCase(ARTICLE)){
				articleDAO = new ArticleDAO();
			}
			break;
			
		case Constants.REQ_GETREVIEWS:
			if(startTagName.equalsIgnoreCase(REVIEW)){
				reviewDAO = new ReviewDAO();
			}
			break;
		
		case Constants.REQ_SUBMITREVIEW:
			break;
		}
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
	throws SAXException {
		if (localName == null) {
			localName = qName;
		}

		endTagName = localName;
		switch(requestID){
		case Constants.REQ_GETARTICLESBYTYPE:
		case Constants.REQ_GETARTICLEDETAILS:
		case Constants.REQ_SEARCHARTICLES:
			if(endTagName.equalsIgnoreCase(IMAGEARTICLEID)){
				articleDAO.setArticleID(parsedData);
			}else if(endTagName.equalsIgnoreCase(TITLE)){
				articleDAO.setTitle(parsedData);
			}else if(endTagName.equalsIgnoreCase(SHORTDESCIPTION)){
				articleDAO.setShortDescription(parsedData);
			}else if(endTagName.equalsIgnoreCase(DETAILEDDESC)){
				articleDAO.setDetailedDescription(parsedData);
			}else if(endTagName.equalsIgnoreCase(THUMBURL)){
				articleDAO.setThumbUrl(parsedData);
			}else if(endTagName.equalsIgnoreCase(URL)){
				articleDAO.setUrl(parsedData);
			}else if(endTagName.equalsIgnoreCase(PUBLISHEDDATE)){
				articleDAO.setPublishedDate(parsedData);
			}else if(endTagName.equalsIgnoreCase(CATEGORYID)){
				articleDAO.setCategoryID(parsedData);
			}else if(endTagName.equalsIgnoreCase(ARTICLE)){
				if(requestID != Constants.REQ_GETARTICLEDETAILS){
				articles.add(articleDAO);
				}
			}
			break;
		
		case Constants.REQ_GETREVIEWS:
			if(endTagName.equalsIgnoreCase(USERNAME)){
				reviewDAO.setUserName(parsedData);
			}else if(endTagName.equalsIgnoreCase(REVIEWCOMMENTS)){
				reviewDAO.setReviewComments(parsedData);
			}else if(endTagName.equalsIgnoreCase(REVIEW)){
				reviews.add(reviewDAO);
			}
			break;
		case Constants.REQ_SUBMITREVIEW:
			if(endTagName.equalsIgnoreCase(SUBMITREVIEWMESSAGE)){
				reviewMessage = parsedData;
			}
			break;
		}
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length)
	throws SAXException {
		parsedData = new String(ch, start, length);
	}
	
	
	public Constants.PARSING getParseMessage() {
		return parseMessage;
	}
	public void setParseMessage(Constants.PARSING parseMessage) {
		this.parseMessage = parseMessage;
	}
	public List<ArticleDAO> getArticles() {
		return articles;
	}
	public void setArticles(List<ArticleDAO> articles) {
		this.articles = articles;
	}
	public List<ReviewDAO> getReviews() {
		return reviews;
	}
	public void setReviews(List<ReviewDAO> reviews) {
		this.reviews = reviews;
	}
	public String getReviewMessage() {
		return reviewMessage;
	}
	public void setReviewMessage(String reviewMessage) {
		this.reviewMessage = reviewMessage;
	}
	public ArticleDAO getArticleDAO() {
		return articleDAO;
	}
	public void setArticleDAO(ArticleDAO articleDAO) {
		this.articleDAO = articleDAO;
	}
}

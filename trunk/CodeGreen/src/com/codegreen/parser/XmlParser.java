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
	private ArticleDAO articleDAO;
	private Constants.PARSING parseMessage;

	public XmlParser(byte reqID){
		this.requestID = reqID;
	}
	/**
	 * This method at starting of the document. 
	 * @see org.xml.sax.helpers.DefaultHandler#startDocument()
	 */
	public void startDocument() throws SAXException {
		switch(requestID){
		case Constants.REQ_GETARTICLESBYTYPE:
			articles = new ArrayList<ArticleDAO>();
			break;
		case Constants.REQ_GETARTICLEDETAILS:
			break;
		case Constants.REQ_GETREVEWS:
			break;
		case Constants.REQ_SEARCHARTICLES:
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
	public void endDocument() throws SAXException {
		parseMessage = Constants.PARSING.COMPLETED;
		super.endDocument();
	}
	/**
	 * This method is called when parsing starts from the first node
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		if(localName == null){
			localName = qName;
		}

		startTagName = localName;

		switch(requestID){
		case Constants.REQ_GETARTICLESBYTYPE:
			if(startTagName.equalsIgnoreCase(ARTICLE)){
				articleDAO = new ArticleDAO();
			}
			break;
		case Constants.REQ_GETARTICLEDETAILS:
			break;
		case Constants.REQ_GETREVEWS:
			break;
		case Constants.REQ_SEARCHARTICLES:
			break;
		case Constants.REQ_SUBMITREVIEW:
			break;
		}
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName)
	throws SAXException {
		if (localName == null) {
			localName = qName;
		}

		endTagName = localName;
		switch(requestID){
		case Constants.REQ_GETARTICLESBYTYPE:
			if(endTagName.equalsIgnoreCase(IMAGEARTICLEID)){
				articleDAO.setArtcleID(parsedData);
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
			}else if(endTagName.equalsIgnoreCase(ARTICLE)){
				articles.add(articleDAO);
			}
			break;
		case Constants.REQ_GETARTICLEDETAILS:
			break;
		case Constants.REQ_GETREVEWS:
			break;
		case Constants.REQ_SEARCHARTICLES:
			break;
		case Constants.REQ_SUBMITREVIEW:
			break;
		}
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
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
	
	
}

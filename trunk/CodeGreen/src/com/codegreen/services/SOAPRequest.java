 /**
 * =================================================================================================================
 * File Name       	: Request.java
 * Author          	: sougata.sen @ Endeavour Software Technologies pvt. ltd., Bangalore.
 * Version         	: 0.1
 * Date            	: 11-Aug-2010
 * Copyright       	: Digi-Data Corporation.
 * Description     	: 
 * To Do List		: TODO
 * History         	:
 * =====================================================================================================================
 *  Sr. No.	Date		Name				Reviewed By					Description
 * =====================================================================================================================
 *  		11-Aug-2010   	sougata.sen		     	sougata.sen						Initial Version. 
 * =====================================================================================================================
 */
package com.codegreen.services;

/**
 * @author ravindra.kamble
 *
 */
public class SOAPRequest {

	/**
	 * Request ID.
	 */
	private byte requestID;
	
	/**
	 * Method to be called. 
	 */
	private String soapMethodName;

	/**
	 * Represents a SOAP request.
	 */
	private String soapRequest;
	
	/**
	 * Identifies a request as a POST and GET
	 */
	private String requestType;

	/**
	 * Data for the request
	 */
	
	private byte[] data;
	
	/**
	 * @return the requestID
	 */
	public byte getRequestID() {
		return requestID;
	}

	
	
	/**
	 * @param requestID the requestID to set
	 */
	public void setRequestID(byte requestID) {
		this.requestID = requestID;
	}

	/**
	 * @return the soapMethodName
	 */
	public String getSoapMethodName() {
		return soapMethodName;
	}

	/**
	 * @param soapMethodName the soapMethodName to set
	 */
	public void setSoapMethodName(String soapMethodName) {
		this.soapMethodName = soapMethodName;
	}

	/**
	 * @return the soapRequest
	 */
	public String getSoapRequest() {
		return soapRequest;
	}

	/**
	 * @param soapRequest the soapRequest to set
	 */
	public void setSoapRequest(String soapRequest) {
		this.soapRequest = soapRequest;
	}
	
	
	/**
	 * @return the requestType
	 */
	public String getRequestType() {
		return requestType;
	}

	/**
	 * @param requestType the requestType to set
	 */
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public void setData(byte[]  data){
		this.data = data;
	}
	
	public byte[] getData(){
		return this.data;
	}
}

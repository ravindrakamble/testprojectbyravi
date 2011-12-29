
package com.codegreen.services;


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

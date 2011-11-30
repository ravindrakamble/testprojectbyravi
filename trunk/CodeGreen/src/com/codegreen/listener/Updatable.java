package com.codegreen.listener;

public interface Updatable {
	
	/**
	 * 
	 * @param errorCode
	 * @param callID
	 * @param obj
	 */
	public void update(byte errorCode,byte callID,Object obj);
	
	/**
	 * Update the UI with provided data
	 * @param updateData
	 */
	public  void update(Object updateData);

}

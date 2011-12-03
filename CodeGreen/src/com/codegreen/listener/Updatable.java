package com.codegreen.listener;

import com.codegreen.util.Constants.ENUM_PARSERRESPONSE;

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
	public  void update(ENUM_PARSERRESPONSE PARSERRESPONSE_FAILURE);

}

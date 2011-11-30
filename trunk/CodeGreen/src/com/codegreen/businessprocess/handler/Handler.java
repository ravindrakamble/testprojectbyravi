package com.codegreen.businessprocess.handler;

import com.codegreen.listener.Updatable;

public interface Handler {
	
	/**
	 * Handles all User Generated Applications events 
	 * @param screen TODO
	 * @return
	 */
	public byte handleEvent(Object eventObject, byte callID, Updatable updatable);
	
	/**
	 * Handles a call 
	 * @param errorCode TODO
	 */
	public void handleCallback(Object callbackObject, byte callID, byte errorCode);
	
	
}

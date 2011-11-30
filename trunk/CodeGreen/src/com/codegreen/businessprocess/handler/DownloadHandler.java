package com.codegreen.businessprocess.handler;

import com.codegreen.listener.Updatable;

public class DownloadHandler implements Handler{

	/** 
	 * @see com.codegreen.businessprocess.handler.Handler#handleCallback(java.lang.Object, byte, byte)
	 */
	@Override
	public void handleCallback(Object callbackObject, byte callID,
			byte errorCode) {
		// TODO Auto-generated method stub
		
	}

	/** 
	 * @see com.codegreen.businessprocess.handler.Handler#handleEvent(java.lang.Object, byte, com.codegreen.listener.Updatable)
	 */
	@Override
	public byte handleEvent(Object eventObject, byte callID, Updatable updatable) {
		// TODO Auto-generated method stub
		return 0;
	}

}

 /**
 * =================================================================================================================
 * File Name       : ShareData.java
 * Author          : Piyush
 * Version         : 
 * Date            : 
 * Copyright       : 
 * Description     : This class can be used to share any data between different activities.
 * History         :
 * =====================================================================================================================
 *  Sr. No.  Date        Name                    Reviewed By             Description
 * =====================================================================================================================
 *  01.      		    Piyush		     								Initial Version.
 * =====================================================================================================================
 */
package com.codegreen.share;

import java.util.HashMap;

public class ShareData {
	
	private HashMap<String,Object> map;
	private static ShareData shareData;
	private static int i = 0;
	
	/**
	 * Return the object of the ShareData Class	
	 * @return
	 */
	static synchronized public ShareData getShareData(){
		if(shareData == null){
			shareData = new ShareData();
			return shareData;
		}
		return shareData;
	}
	
	/**
	 * This method will put a particular data with a unique key value
	 * @param key
	 * @param value
	 * @return
	 */
	public Object put(String key ,Object value){
		return map.put(key, value);
	}
	
	/**
	 * This method will return the data using the key
	 * @param key
	 * @return
	 */
	public Object get(String key){
		return map.get(key);
	}
	/////////////Private Method not Exposed///////////////////
	private ShareData(){
		map = new HashMap<String, Object>();
	}
	
}

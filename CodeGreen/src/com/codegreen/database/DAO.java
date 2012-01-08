 /**
 * =================================================================================================================
 * File Name            : DAO.java
 * =====================================================================================================================
 *  Sr. No.     Date            Name                            Reviewed By                                     Description
 * =====================================================================================================================
 * =====================================================================================================================
 */
package com.codegreen.database;

import android.content.Context;

public interface DAO {
	
	public void open(Context context);

	public void close();
}
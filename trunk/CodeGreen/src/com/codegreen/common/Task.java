 /**
 * =================================================================================================================
 * File Name       	: Task.java
 * =====================================================================================================================
 *  Sr. No.	Date		Name				Reviewed By					Description
 * =====================================================================================================================
 * =====================================================================================================================
 */
package com.codegreen.common;

import java.util.Date;


/**
 * @author sougata.sen
 *
 */
public abstract class Task implements Runnable{

	/**
	 * Uniquely Identifies each task
	 */
	protected long taskId;

	/**
	 * Aborts a Running Task 
	 */
	protected boolean abort;
	
	
	protected Task(){
		setTaskId();
	}
	
	
	/**
	 * @return the taskId
	 */
	public long getTaskId() {
		return taskId;
	}
	
	/**
	 * @param taskId the taskId to set
	 */
	private void setTaskId() {
		this.taskId = hashCode() + new Date().getTime();
	}
	
	public void cancel(){
		abort = true;
	}
	
	/** 
	 *  Task Implementation Goes here
	 */
	public abstract void run();
	
	
	public boolean isAlive(){
		return !abort;
	}

	public abstract void deAllocate();

}

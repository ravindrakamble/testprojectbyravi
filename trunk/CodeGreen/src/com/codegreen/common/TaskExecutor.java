 /**
 * =================================================================================================================
 * File Name            : TaskExecutor.java
 * =====================================================================================================================
 *  Sr. No.     Date            Name                            Reviewed By                                     Description
 * =====================================================================================================================
 * =====================================================================================================================
 */
package com.codegreen.common;


import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author sougata.sen
 *
 */
public class TaskExecutor {
	
	/**
	 * List of Tasks
	 */
	static Vector<Task> taskQueue;
	
	/**
	 * Indicates an index of the task being executed.
	 */
	int executionIndex;
	
	ExecutorService pool;
	
	/**
	 * Thread Pool size
	 */
	int poolSize = 5;
	
	static TaskExecutor taskExecutor;
	
	/**
	 * Returns a singleton Instance of Task Executor
	 * @return
	 */
	public static TaskExecutor getInstance(){
		if(taskExecutor == null){
			taskExecutor = new TaskExecutor();
			taskQueue = new Vector<Task>();		
		}
		return taskExecutor;
	}
	
	/**
	 * Private Constructor
	 */
	private TaskExecutor() {
		// TODO Auto-generated constructor stub
		pool = Executors.newFixedThreadPool(poolSize);
	}
	
	
	/**
	 * Executes a Task and Adds to the queue.
	 */
	public void execute(Task task){
		taskQueue.add(task);
		pool.execute(task);
	}

	/**
	 * Iterate a task in the queue and cancel that task.
	 * @param taskId
	 */
	public void cancelTask(long taskId){
		Iterator<Task> taskIterator= taskQueue.iterator();
		Task task = null; 
		while(taskIterator.hasNext()){
			task = taskIterator.next();
			if(task.getTaskId() == taskId){
				task.cancel();
				taskQueue.remove(task);
			}
		}
	}
	
	/*
	 * Cancels all task in the queue and clears queue.
	 */
	public void cancelAllTask(){
		Iterator<Task> taskIterator= taskQueue.iterator();
		Task task = null; 
		while(taskIterator.hasNext()){
			task = taskIterator.next();
			task.cancel();
		}
		taskQueue.clear();
	}
	
	/**
	 * Closes the Executor and all active threads related to it.
	 */
	public void close(){
		cancelAllTask();
		pool.shutdownNow();
	}
	
}

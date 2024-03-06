/*
 *  GoalSeekStatus.java
 *
 */
package com.ebixcash.aayu.irr;

/**
 *
 *
 *  @author : Nilesh
 *  @version : 1.0.0
 *  Date: March 9, 2009, Time: 7:54:37 AM
 */
public class GoalSeekStatus {

	public static final int GOAL_SEEK_OK     = 0 ;
	public static final int GOAL_SEEK_ERROR  = 1 ;

	public int      seekStatus ;
	public Object   returnData ;

	/**
	 *  Constuctor
	 *
	 *  @param pStatus
	 *  @param retData
	 */
	public GoalSeekStatus( int pStatus, Object retData ) {
		this.seekStatus = pStatus ;
		this.returnData = retData ;
	}

	/**
	 *
	 *  @return
	 */
	public int getSeekStatus() {
		return seekStatus;
	}

	/**
	 *
	 *  @param seekStatus
	 */
	public void setSeekStatus( int seekStatus ) {
		this.seekStatus = seekStatus;
	}

	/**
	 *
	 *  @return
	 */
	public Object getReturnData() {
		return returnData;
	}

	/**
	 *
	 *  @param returnData
	 */
	public void setReturnData( Object returnData ) {
		this.returnData = returnData;
	}

	/**
	 *
	 * @return
	 */
	public String toString() {
		return "Status - " + seekStatus + ", Return Data - " + returnData ;
	}

}   /*  End of the GoalSeekStatus class. */
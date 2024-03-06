/*
 *  XIRRNPV.java
 *
 */
package com.ebixcash.aayu.irr;

/**
 * @author : Aditya
 * @version : 1.0.0 Date: March 9, 2009, Time: 9:32:57 AM
 */
public class XIRRNPV implements GoalSeekFunction {

	/**
	 *  Default Constructor.
	 */
	public XIRRNPV() {
	}

	/**
	 *
	 *  @param rate
	 *  @param y
	 *  @param userData
	 *  @return
	 */
	public GoalSeekStatus f( double rate, Object userData ) {
		XIRRData    p ;
		double[]    values ;
		double[]    dates ;
		double      sum = 0.0d ;
		double      tmpsum = 0.0d ;
		int         n ;
		int         typ;

		p       = (XIRRData) userData ;
		values  = p.values ;
		dates   = p.dates ;
		n       = p.n ;
		sum     = 0 ;
		typ     = p.typ;
		if(typ==1){
			for ( int i = 1; i <= n; i++ ) {
			    sum += values[i-1] / Math.pow((1+rate), i) ; // For IRR calculation
			}
		}
		else{
			for ( int i = 0; i < n; i++ ) {
				double d = dates[i] - dates[0];
				if ( d < 0 )  {
					return new GoalSeekStatus( GoalSeekStatus.GOAL_SEEK_ERROR, null) ;
				}
			    sum += values[i] / Math.pow(rate, d / 365.0) ; // For XIRR calculation;
			}
		}

	//	GoalSeekStatus.returnData = new Double( sum ) ;
		return new GoalSeekStatus( GoalSeekStatus.GOAL_SEEK_OK, new Double(sum) ) ;
	}
}   /*  End of the XIRRNPV class. */
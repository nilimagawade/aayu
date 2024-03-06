/*
 *  GoalSeekData.java
 *
 */
package com.ebixcash.aayu.irr;

/*
 *  Imports
 */


/**
 *  Data structure used by the GoalSeek algo.
 *
 * @author : Nilesh
 * @version : 1.0.0 Date: March 9, 2009, Time: 7:51:55 AM
 */
public class GoalSeekData {

	public double   xmin;             /* Minimum allowed values for x.  */
	public double   xmax;             /* Maximum allowed values for x.  */
	public double   precision;        /* Desired relative precision.  */

	public boolean  havexpos;        /* Do we have a valid xpos?  */
	public double   xpos;             /* Value for which f(xpos) > 0.  */
	public double   ypos;             /* f(xpos).  */

	public boolean  havexneg;        /* Do we have a valid xneg?  */
	public double   xneg;             /* Value for which f(xneg) < 0.  */
	public double   yneg;             /* f(xneg).  */

	public boolean  have_root;       /* Do we have a valid root?  */
	public double   root;             /* Value for which f(root) == 0.  */


}   /*  End of the GoalSeekData class. */
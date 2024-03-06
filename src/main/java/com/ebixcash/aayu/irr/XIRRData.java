/*
 *  XIRRData.java
 *
 */
package com.ebixcash.aayu.irr;

/*
 *  Imports
 */
import java.util.Calendar ;
import java.util.GregorianCalendar ;

/**
 *  Data structure to hold XIRR data.
 *
 *  @author : Aditya
 *  @version : 1.0.0 Date: March 9, 2009, Time: 9:29:49 AM
 */
public class XIRRData {

	public int          n ;
    public double       guess ;
	public double[]     values ;
	public double[]     dates ;
	public int          typ;

	/**
	 *  Default Constructor.
	 */
	public XIRRData() {
	}

	/**
	 *  Constructor.
	 *
	 *  @param n
     *  @param guess
	 *  @param pValues
	 *  @param pDates
	 */
	public XIRRData( int n, double guess, double[] pValues, double[] pDates ,int Type) {
		this.n      = n;
        this.guess  = guess ;
		this.values = pValues;
		this.dates  = pDates;
		this.typ    = Type;

	}

	/**
	 *  Returns the same value as Excel's DataValue method.
	 *
	 *  @param dateStr
	 *  @return
	 */
//	public static int getExcelDateValue( String dateStr ) {
//		GregorianCalendar dateStart = new GregorianCalendar( 1899, 11, 30 ) ;
//		return getDaysBetween( dateStart, date ) ;
//	}

	/**
	 *  Returns the same value as Excel's DataValue method.
	 *
	 *  @param date
	 *  @return
	 */
	public static int getExcelDateValue( Calendar date ) {
		GregorianCalendar dateStart = new GregorianCalendar( 1899, 11, 30 ) ;
		return getDaysBetween( dateStart, date ) ;
	}

	/**
	 * Calculates the number of days between two calendar days in a manner
	 * which is independent of the Calendar type used.
	 *
	 * @param d1    The first date.
	 * @param d2    The second date.
	 *
	 * @return      The number of days between the two dates.  Zero is
	 *              returned if the dates are the same, one if the dates are
	 *              adjacent, etc.  The order of the dates
	 *              does not matter, the value returned is always >= 0.
	 *              If Calendar types of d1 and d2
	 *              are different, the result may not be accurate.
	 */
	public static int getDaysBetween( Calendar d1, Calendar d2 ) {
		if ( d1.after(d2) ) {
			// swap dates so that d1 is start and d2 is end
			Calendar swap = d1;
			d1 = d2;
			d2 = swap;
		}

		int days = d2.get(Calendar.DAY_OF_YEAR) - d1.get(Calendar.DAY_OF_YEAR);
		int y2   = d2.get(Calendar.YEAR);
		if (d1.get(Calendar.YEAR) != y2) {
			d1 = (Calendar) d1.clone();
			do {
				days += d1.getActualMaximum(Calendar.DAY_OF_YEAR);
				d1.add(Calendar.YEAR, 1);
			} while (d1.get(Calendar.YEAR) != y2);
		}
		return days;
	}



	/**
	 *  Expensive method. Don't call in loops etc.
	 *
	 *  @return
	 */
	public String toString() {
		String text ;
		String valuesStr ;
		String datesStr ;

		text = "XIRRData - n = " + n + ", Guess = " + this.guess ;
		valuesStr = ", Values = " ;
		datesStr = ", Dates = " ;
		for ( int i = 0; i < this.values.length; i++ )  {
            valuesStr = valuesStr + this.values[i] ;
			if ( i < this.values.length - 1 )   {
				valuesStr = valuesStr + "," ;
			}
		}
		for ( int i = 0; i < this.dates.length; i++ )  {
            datesStr = datesStr + this.dates[i] ;
			if ( i < this.dates.length - 1 )   {
				datesStr = datesStr + "," ;
			}
		}
		return text + valuesStr + datesStr ;
	}

}   /*  End of the XIRRData class. */
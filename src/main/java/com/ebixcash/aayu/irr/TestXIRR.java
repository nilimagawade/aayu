/*
 *  TestXIRR.java
 *
 */
package com.ebixcash.aayu.irr;

/*
 *  Imports
 */
import java.util.GregorianCalendar;

/**
 * @author : Aditya
 * @version : 1.0.0 Date: March 9, 2009, Time: 4:39:25 AM
 */
public class TestXIRR {

	/**
	 *
	 *  @param args
	 */
	public static void main( String[] args ) {
		log( "Testing XIRR..." ) ;

//		GregorianCalendar dateStart = new GregorianCalendar( 1899, 11, 30 ) ;
		GregorianCalendar dateEnd = new GregorianCalendar( 2009, 2, 20 ) ;
        int daysBetween = XIRRData.getExcelDateValue( dateEnd ) ;
		log( "Days Between = " + daysBetween ) ;

//		"Let us assume that the cells A1:A5 contain the numbers -6000, "
//		"2134, 1422, 1933, and 1422, and the cells B1:B5 contain the "
//		"dates \"1999-01-15\", \"1999-04-04\", \"1999-05-09\", "
//		"\"2000-03-12\", and \"2000-05-1\". Then\n"
//		"XIRR(A1:A5,B1:B5) returns 0.224838. "
		double[]    values  = new double[25] ;
		double[]    dates   = new double[25] ;
		values[0]           = -100000 ;
		values[1]           = 4477.26;
		values[2]           = 4477.26;
		values[3]           = 4477.26;
		values[4]           = 4477.26;
		values[5]           = 4477.26;
		values[6]           = 4477.26;
		values[7]           = 4477.26;
		values[8]           = 4477.26;
		values[9]           = 4477.26;
		values[10]           = 4477.26;
		values[11]           = 4477.26;
		values[12]           = 4477.26;
		values[13]           = 4477.26;
		values[14]           = 4477.26;
		values[15]           = 4477.26;
		values[16]           = 4477.26;
		values[17]           = 4477.26;
		values[18]           = 4477.26;
		values[19]           = 4477.26;
		values[20]           = 4477.26;
		values[21]           = 4477.26;
		values[22]           = 4477.26;
		values[23]           = 4477.26;
		values[24]           = 4477.26;


		dates[0]            = XIRRData.getExcelDateValue( new GregorianCalendar(2009, 1, 19) ) ;
		dates[1]            = XIRRData.getExcelDateValue( new GregorianCalendar(2009, 2, 19) ) ;
		dates[2]            = XIRRData.getExcelDateValue( new GregorianCalendar(2009, 3, 19) ) ;
		dates[3]            = XIRRData.getExcelDateValue( new GregorianCalendar(2009, 4, 19) ) ;
		dates[4]            = XIRRData.getExcelDateValue( new GregorianCalendar(2009, 5, 19) ) ;
		dates[5]            = XIRRData.getExcelDateValue( new GregorianCalendar(2009, 6, 19) ) ;
		dates[6]            = XIRRData.getExcelDateValue( new GregorianCalendar(2009, 7, 19) ) ;
		dates[7]            = XIRRData.getExcelDateValue( new GregorianCalendar(2009, 8, 19) ) ;
		dates[8]            = XIRRData.getExcelDateValue( new GregorianCalendar(2009, 9, 19) ) ;
		dates[9]            = XIRRData.getExcelDateValue( new GregorianCalendar(2009, 10, 19)) ;
		dates[10]           = XIRRData.getExcelDateValue( new GregorianCalendar(2009, 11, 19)) ;
		dates[11]           = XIRRData.getExcelDateValue( new GregorianCalendar(2010, 0, 19) ) ;
		dates[12]           = XIRRData.getExcelDateValue( new GregorianCalendar(2010, 1, 19) ) ;
		dates[13]           = XIRRData.getExcelDateValue( new GregorianCalendar(2010, 2, 19) ) ;
		dates[14]           = XIRRData.getExcelDateValue( new GregorianCalendar(2010, 3, 19) ) ;
		dates[15]           = XIRRData.getExcelDateValue( new GregorianCalendar(2010, 4, 19) ) ;
		dates[16]           = XIRRData.getExcelDateValue( new GregorianCalendar(2010, 5, 19) ) ;
		dates[17]           = XIRRData.getExcelDateValue( new GregorianCalendar(2010, 6, 19) ) ;
		dates[18]           = XIRRData.getExcelDateValue( new GregorianCalendar(2010, 7, 19) ) ;
		dates[19]           = XIRRData.getExcelDateValue( new GregorianCalendar(2010, 8, 19) ) ;
		dates[20]           = XIRRData.getExcelDateValue( new GregorianCalendar(2010, 9, 19) ) ;
		dates[21]           = XIRRData.getExcelDateValue( new GregorianCalendar(2010, 10, 19)) ;
		dates[22]           = XIRRData.getExcelDateValue( new GregorianCalendar(2010, 11, 19)) ;
		dates[23]           = XIRRData.getExcelDateValue( new GregorianCalendar(2011, 0, 19) ) ;
		dates[24]           = XIRRData.getExcelDateValue( new GregorianCalendar(2011, 1, 19) ) ;


		XIRRData data       = new XIRRData(25, 0.07, values, dates ,1) ;
		double xirrValue = XIRR.xirr( data ) ;
        log( "XIRR = " + xirrValue ) ;

		log( "XIRR Test Completed..." ) ;
	}


	/**
	 *
	 * @param message
	 */
	public static void log( String message ) {
		//System.out.println( message ) ;
	}

}   /*  End of the TestXIRR class. */
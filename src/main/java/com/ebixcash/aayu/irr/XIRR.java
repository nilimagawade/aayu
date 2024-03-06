/*
 *  XIRR.java
 *
 */
package com.ebixcash.aayu.irr;


/*
 *  Imports
 */
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ebixcash.aayu.model.AmortOutputBean;

/**
 *  XIRR implementation
 *
 *  @author : Aditya
 *  @version : 1.0.0 Date: March 9, 2009, Time: 12:09:58 PM
 */
public class XIRR {
	private static final Logger logger = LoggerFactory.getLogger(XIRR.class);

	public static final boolean	isDebugEnabled	= logger.isDebugEnabled();

	/*
	 *  Excel stores dates as sequential serial numbers so they can be used
	 *  in calculations. By default, January 1, 1900 is serial number 1, and
	 *  January 1, 2008 is serial number 39448 because it is 39,448 days
	 *  after January 1, 1900.
	 */

	/**
	 *  Calculate XIRR.
	 *
	 *  @param xirrData
	 *  @return
	 */
	public static double xirr( XIRRData xirrData ) 	{
		GoalSeekData    data ;
		GoalSeekStatus  status ;
		double          result ;
		double          rate0 ;
		int             n ;
		int             d_n ;

		data        = new GoalSeekData() ;
		GoalSeek.goal_seek_initialize( data ) ;
		data.xmin   = -1;
		data.xmax   = Math.min( 1000, data.xmax ) ;
		rate0       = xirrData.guess ; //argv[2] ? value_get_as_float (argv[2]) : 0.1;

		status = GoalSeek.goalSeekNewton(
		            new XIRRNPV(), null, data, xirrData, rate0 ) ;

		if (status.seekStatus == GoalSeekStatus.GOAL_SEEK_OK)  {
//			result = value_new_float(data.root);
			result = ((Double) status.returnData).doubleValue() ;    //data.root ;
		}
		else    {
//			result = value_new_error_NUM (ei.pos);
			result = Double.NaN ;
		}

		//System.out.println( "XIRR Result :: " + result ) ;
		//return (result != Double.NaN) ? (result - 1) : result ;  // by Aditya
		return result;
	}
	public static double getIRR(HashMap AmortMap,double rate,double opbal,String cdate){
	   return setDataIRR(AmortMap,1,rate,opbal,cdate);
	}
	public static double getIRR(ArrayList arrList,double rate,double opbal,String cdate){
	   return setDataIRR(arrList,1,rate,opbal,cdate);
	}
	public static double getIRR(ArrayList arrList,double rate,double opbal,String cdate,boolean advEMI){
		   return setDataIRR(arrList,1,rate,opbal,cdate,advEMI);
	}
	public static double getXIRR(HashMap AmortMap,double rate,double opbal,String cdate){
	   return setDataIRR(AmortMap,2,rate,opbal,cdate);
	}
	public static double getXIRR(ArrayList arrList,double rate,double opbal,String cdate){
	   return setDataIRR(arrList,2,rate,opbal,cdate);
	}
	public static double getXIRR(ArrayList arrList,double rate,double opbal,String cdate,boolean advEMI){
		   return setDataIRR(arrList,2,rate,opbal,cdate,advEMI);
	}
	public static double setDataIRR(HashMap AmorMap,int Type,double rate,double opbal,String cdate){
		double result = 0.0d;
		double guessrate=rate/100;
		double[]  values  = new double[AmorMap.size()+1] ;
		double[]  dates   = new double[AmorMap.size()+1] ;
		values[0] = -opbal;
		SimpleDateFormat  df =  new SimpleDateFormat("dd/MM/yyyy");
		try{
		 Date dis_date = df.parse(cdate);
         Calendar cal = Calendar.getInstance();
         cal.setTime(dis_date);
         dates[0]  = XIRRData.getExcelDateValue(cal);
        }
		catch(Exception e){
			 if (isDebugEnabled) logger.debug(" Exception occure in setDataIRR() method :"+ e);
			// System.out.println("Exception::"+e);
		}

		for(int k=1;k<=AmorMap.size();k++){
			HashMap tmprow=(HashMap)AmorMap.get(new Integer(k));
			values[k]       = ((Double)tmprow.get("EMI")).doubleValue();
			String tmp_date = tmprow.get("Date").toString();
	    	SimpleDateFormat  df1 =  new SimpleDateFormat("dd/MM/yyyy");
			try{
			 Date dis_date = df1.parse(tmprow.get("Date").toString());
	         Calendar cal = Calendar.getInstance();
	         cal.setTime(dis_date);
	         dates[k] = XIRRData.getExcelDateValue(cal);
	        }
			catch(Exception e){
				 if (isDebugEnabled) logger.debug(" Exception occure in setDataIRR() method :"+ e);
				// System.out.println("Exception::"+e);
			}
		}
	    if(Type==1){
	    	XIRRData data  = new XIRRData(AmorMap.size()+1,guessrate,values,dates,1) ;
			result = XIRR.xirr( data ) ;
			if (isDebugEnabled) logger.debug(" IRR Value :"+result);
	    }
	    else if(Type==2){
	    	XIRRData data  = new XIRRData(AmorMap.size()+1,guessrate,values,dates,2) ;
			result = XIRR.xirr( data ) ;
			if (isDebugEnabled) logger.debug(" XIRR Value :"+result);
	    }
	    /*for(int i =0;i<values.length;i++){
	    	 System.out.println("Value :"+values[i]+" i "+i);
	    }*/
	    return result*100;
	}
	public static double setDataIRR(ArrayList arrList,int Type,double rate,double opbal,String cdate){
		double result = 0.0d;
		double guessrate=rate/100;
		double[]  values  = new double[arrList.size()] ;
		double[]  dates   = new double[arrList.size()] ;
		values[0] = -opbal;
		SimpleDateFormat  df =  new SimpleDateFormat("dd/MM/yyyy");
		try{
		 Date dis_date = df.parse(cdate);
         Calendar cal = Calendar.getInstance();
         cal.setTime(dis_date);
         dates[0]  = XIRRData.getExcelDateValue(cal);
        }
		catch(Exception e){
			 if (isDebugEnabled) logger.debug(" Exception occure in setDataIRR() method :"+ e);
			// System.out.println("Exception::"+e);
		}
		ListIterator listIterator = arrList.listIterator();
    	int cnt =0;
    	while(listIterator.hasNext()){
    		listIterator.next();
    		Object obj =arrList.get(cnt);
    		if(obj.getClass().getName().equals("com.ebixcash.aayu.util.AmortOverviewBean")){
    			continue;
    		}
    		AmortOutputBean Out =(AmortOutputBean)obj;
    		cnt++;
    		values[cnt] = Out.getRoundEMI();
    	
    		
    	
    		SimpleDateFormat  df1 =  new SimpleDateFormat("dd/MM/yyyy");
    		try{
    			Date dis_date = df1.parse(Out.getCycleDate());
    			Calendar cal = Calendar.getInstance();
    			cal.setTime(dis_date);
    			dates[cnt]  = XIRRData.getExcelDateValue(cal);
            }
    		catch(Exception e){
   			 	if (isDebugEnabled) logger.debug(" Exception occure in setDataIRR() method :"+ e);
   			 	//System.out.println("Exception::"+e);
    		}
    	}
	    if(Type==1){
	    	XIRRData data  = new XIRRData(arrList.size(),guessrate,values,dates,1) ;
			result = XIRR.xirr( data ) ;
			if (isDebugEnabled) logger.debug(" IRR Value :"+result);
	    }
	    else if(Type==2){
	    	XIRRData data  = new XIRRData(arrList.size(),guessrate,values,dates,2) ;
			result = XIRR.xirr( data ) ;
			if (isDebugEnabled) logger.debug(" XIRR Value :"+result);
			return (result != Double.NaN) ? (result - 1) : result;
	    }
	    return result*100;
	}
	public static double setDataIRR(ArrayList arrList,int Type,double rate,double opbal,String cdate,boolean advFlag){
		double result = 0.0d;
		double guessrate=rate/100;
		double[]  values  = new double[arrList.size()+1] ;
		double[]  dates   = new double[arrList.size()+1] ;
		values[0] = -opbal;
		SimpleDateFormat  df =  new SimpleDateFormat("dd/MM/yyyy");
		try{
		 Date dis_date = df.parse(cdate);
         Calendar cal = Calendar.getInstance();
         cal.setTime(dis_date);
         dates[0]  = XIRRData.getExcelDateValue(cal);
        }
		catch(Exception e){
			 if (isDebugEnabled) logger.debug(" Exception occure in setDataIRR() method :"+ e);
			// System.out.println("Exception::"+e);
		}
		ListIterator listIterator = arrList.listIterator();
    	int cnt =0;
    	while(listIterator.hasNext()){
    		listIterator.next();
    		Object obj =arrList.get(cnt);
    		if(obj.getClass().getName().equals("com.ebixcash.aayu.util.AmortOverviewBean")){
    			continue;
    		}
    		AmortOutputBean Out =(AmortOutputBean)obj;
    		cnt++;
    		values[cnt] = Out.getRoundEMI();
    		SimpleDateFormat  df1 =  new SimpleDateFormat("dd/MM/yyyy");
    		try{
    			Date dis_date = df1.parse(Out.getCycleDate());
    			Calendar cal = Calendar.getInstance();
    			cal.setTime(dis_date);
    			dates[cnt]  = XIRRData.getExcelDateValue(cal);
            }
    		catch(Exception e){
   			 	if (isDebugEnabled) logger.debug(" Exception occure in setDataIRR() method :"+ e);
   			 	//System.out.println("Exception::"+e);
    		}
    	}
	    if(Type==1){
	    	XIRRData data  = new XIRRData(arrList.size()+1,guessrate,values,dates,1) ;
			result = XIRR.xirr( data ) ;
			if (isDebugEnabled) logger.debug(" IRR Value :"+result);
	    }
	    else if(Type==2){
	    	XIRRData data  = new XIRRData(arrList.size()+1,guessrate,values,dates,2) ;
			result = XIRR.xirr( data ) ;
			if (isDebugEnabled) logger.debug(" XIRR Value :"+result);
			return (result != Double.NaN) ? (result - 1) : result;
	    }
	    return result*100;
	}
}   /*  End of the XIRR class. */
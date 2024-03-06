package com.ebixcash.aayu.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ebixcash.aayu.constant.AmortConstant;
import com.ebixcash.aayu.constant.AmortConstant.DateFormats;
import com.ebixcash.aayu.customvalidator.GenericTypeValidator;
import com.ebixcash.aayu.daycount.defaultimpl.DaycountCalculator;
import com.ebixcash.aayu.daycount.defaultimpl.DaycountCalculatorFactory;
import com.ebixcash.aayu.exception.AmortException.ValidationException;
import com.ebixcash.aayu.model.AmortInputBean;
import com.ebixcash.aayu.model.AmortOutputBean;

public class AmortUtil {
	private static final Logger logger = LoggerFactory.getLogger(AmortUtil.class);
    public static final boolean	isDebugEnabled	= logger.isDebugEnabled();
    static int b = 0; 
  	static DaycountCalculator daycountBasis = null;

	 /**
     * Calculates the rate for given frequency period
     *
     * @param rate - Interest Rate
     * @param compFreq - Compounding Frequency
     * @param freq	   - Repayment Frequency
     * @return	rate for given frequency period
     */
    public static double getRate(double rate, double compFreq, double freq)
    {
    	//return ((Math.pow((1+rate/(compFreq)),compFreq/freq)-1));
		double d = 1.0;
        BigDecimal bd;
        bd = BigDecimal.valueOf(Math.pow((1 + rate/(compFreq)),compFreq/freq)).subtract(BigDecimal.valueOf(d));
        return bd.doubleValue();
    }

    /**
     * Calculates tenor in multiples of Repayment Frequency
     *
     * @param term - Tenor in years
     * @param freq - Repayment Frequency
     * @return tenor in multiples of Repayment Frequency
     */
    public static int getNPER(double term,int freq,AmortInputBean beanObj){

    	if(beanObj.getTenor_in() != AmortConstant.tenor_inyear)
    	{				   
    		term = beanObj.getTenor() / beanObj.getYearfactor();
    	}
    	else
    	{
    		term  = beanObj.getTenor();
    	}
       if(beanObj.getCompoundFreq() == 360 || beanObj.getCompoundFreq() == 52 || beanObj.getCompoundFreq() == 26)
       { 
    	   //added for Bug 231102 - VISTAAR \ FORTNIGHTLY \ WEEKLY \ DAILY IRR Is not matching
    	   return (int)(round(term * freq,0,0));
       }
       else
    	return (int)(round(term * freq,0,0));
    }
    /**
     * Calculates tenor in case of Missing
     *
     * @param pv -Principle Vale
     * @param pmt - Inputted EMI
     * @param rate - Inputted Interest Rate
     * @param bplast - Balloon Lastpayment(default it is 0).
     * @return tenor in multiples of Repayment Frequency
     */
    public static int claculateNPER(double loanamount,double emi,double rate,double bplastamt){
    	if (isDebugEnabled) logger.debug("Getting started : inside NPER in case of Tenor Missing");
    	emi = -emi;
    	loanamount = loanamount - bplastamt;
    	double nper_value = Math.log((emi) / (emi + rate * loanamount)) / Math.log(1 + rate);
    	if (isDebugEnabled) logger.debug("Getting started : inside NPER in case of Tenor Missing. Calculated Term ="+nper_value);
    	return (int)Math.ceil((nper_value));
    	
    }  
    /**
     * Calculates EMI
     *
     * @param prn	- Opening Principal
     * @param tenor - tenor in multiples of Repayment Frequency
     * @param rate  - Interest Rate
     * @param IT	- Interest Type
     * @param freq  - Repayment Frequency
     * @return 		- EMI
     */
    public static double PMT(double prn, double tenor, double rate,  int IT,int freq) {
    	if (isDebugEnabled) logger.debug("Getting started : inside PMT");
    	if (isDebugEnabled) logger.debug("PMT : Loan Amount= " + prn + " Tenor= " + tenor + " Rate= " + rate + " Interest Type= " + IT);
    	try
    	{
    		return PMT(prn,tenor,rate,IT,freq,0,0);
    	}
    	catch(Exception e)
    	{
    		if (isDebugEnabled) logger.error("Exception : inside PMT:"+e);
    		return 0.0d;
    	}

    }
    
    public static double PMT(double prn, double tenor, double rate,  int IT,int freq, int advemiflag, int noadvemi) {
    	if (isDebugEnabled) logger.debug("Getting started : inside PMT");
    	if (isDebugEnabled) logger.debug("PMT : Loan Amount= " + prn + " Tenor= " + tenor + " Rate= " + rate + " Interest Type= " + IT);
    	try
    	{
    		if(rate == 0){
    			return (prn / tenor);
    		}
    		if(advemiflag == 0){
    			if(IT == AmortConstant.InterestTypeFlat)		//For FLAT Interest Type
    			{
    				return ((prn + (prn * rate * (tenor / freq))) / tenor);
    			}
    			else
    			{
    				double nt=	prn * rate; 
    				double dt=	((noadvemi * rate) + 1)-(1/Math.pow((1 + rate),tenor - noadvemi));
    				return (nt/dt);
    			}
    		}else{
    			if(IT == AmortConstant.InterestTypeFlat)		//For FLAT Interest Type
    			{
    				return ((prn + (prn * rate * (tenor/freq)))/tenor);
    			}
    			else
    			{
    				double nt =	prn * rate * Math.pow((1 + rate),tenor);
    				double dt =	Math.pow((1 + rate),tenor) - 1;
    				return (nt/dt);
    			}
    		}


    	}
    	catch(Exception e)
    	{
    		if (isDebugEnabled) logger.error("Exception : inside PMT:"+e);
    		return 0.0d;
    	}

    }
	
	public static double PMT(double prn, double tenor, double rate,  int IT,int freq, int advemiflag, int noadvemi,double fv) {
    	if (isDebugEnabled) logger.debug("Getting started : inside PMT");
    	if (isDebugEnabled) logger.debug("PMT : Loan Amount= " + prn + " Tenor= " + tenor + " Rate= " + rate + " Interest Type= " + IT);
    	try
    	{
    		if(rate == 0){
    			return (prn/tenor);
    		}
    		if(advemiflag == 0){
    			if(IT == AmortConstant.InterestTypeFlat)		//For FLAT Interest Type
    			{//for Rule-78 EMI Calculation
    				return ((prn + (prn * rate * (tenor/freq)))/tenor);
    			}
    			else
    			{   
    				if(fv > 0){
                		prn = prn - fv/Math.pow((1 + rate), (tenor));
                   	}
    			//For PMT EMI Calculation
    				double nt =	prn * rate; 
    				double dt =	((noadvemi * rate) + 1) - (1/Math.pow((1 + rate),tenor - noadvemi));
    				return (nt/dt);
    			}
    		}else{
    			if(IT == AmortConstant.InterestTypeFlat)		//For FLAT Interest Type
    			{
    				return ((prn + (prn * rate * (tenor/freq)))/tenor);
    			}
    			else
    			{
    				double nt =	prn * rate * Math.pow((1 + rate),tenor);
    				double dt =	Math.pow((1 + rate),tenor) - 1;
    				return (nt/dt);
    			}
    		}


    	}
    	catch(Exception e)
    	{
    		if (isDebugEnabled) logger.error("Exception : inside PMT:"+e);
    		return 0.0d;
    	}

    }
	
	
    /**
     * Calculates Loan Amount
     *
     * @param rate - Interest Rate per period
     * @param term - Tenor in multiples of Repayment Frequency
     * @param emi  - EMI specified as input
     * @return
     */
    public static double calLoanAmt(double rate,double term,double emi,int advemiflag, int noadvemi)
    {
    	if(isDebugEnabled )logger.debug("Inside calLoanAmt rate: "+rate+" term: "+term+" emi: "+emi);
    	double LA = 0.0d;
    	try
    	{
    		if(advemiflag == 0)
    		{
    			LA = (emi * (1 - (Math.pow((1 + rate), -term))))/rate;
    		}
    		else
    		{
    			LA = emi*(((noadvemi * rate) + 1) - (1/Math.pow((1 + rate),term - noadvemi)))/rate;
    		}

    		return LA;
    	}
    	catch(Exception e)
    	{
    		if (isDebugEnabled) logger.error("Exception : inside calLoanAmt:"+e);
    		return 0.0d;
    	}
    }
    
    public static double calLoanAmt(double rate,double term,double emi)
    {
    	if(isDebugEnabled )logger.debug("Inside calLoanAmt rate: "+rate+" term: "+term+" emi: "+emi);
    	double LA = 0.0d;
    	try
    	{
    		//LA = emi*(((1 * rate) + 1) - (1/Math.pow((1 + rate),term - 1)))/rate;
    		LA = (emi * (1 - (Math.pow((1 + rate), -term))))/rate;
    		return LA;
    	}
    	catch(Exception e)
    	{
    		if (isDebugEnabled) logger.error("Exception : inside calLoanAmt:"+e);
    		return 0.0d;
    	}
    }

	public static double getInterest(double amount,double interestrate,int compoundingfrequency,int ib,String date1,String date2,String dateformat,boolean weeklyflag,String intcalmethod){
		
		double dayfactor = getDayfactor(ib,date1,date2,dateformat,weeklyflag);
		double rate = getInterestRate(interestrate,compoundingfrequency,dayfactor,intcalmethod);
    	double interest = amount *rate ;
		return interest;
	}

	public static double getDayfactor(int interestBasis,String date1,String date2,String dateformat,boolean weeklyflag){
		SimpleDateFormat  df =  new SimpleDateFormat(dateformat);
		double daycountFraction = 0.0d;

		try{

			Date d_date1 = df.parse(date1);
			Date d_date2 = df.parse(date2);

			Calendar c_date1 = Calendar.getInstance();
			c_date1.setTime(d_date1);

			Calendar c_date2 = Calendar.getInstance();
			c_date2.setTime(d_date2);
		    if(AmortConstant.RepaymentFrequencyValue== 1 &&AmortConstant.diffIndays < 365 &&AmortConstant.loop_count==4 && interestBasis==0){
		    	interestBasis=3;
		    }
		    else  if(AmortConstant.RepaymentFrequencyValue== 4 &&AmortConstant.diffIndays < 90 &&AmortConstant.loop_count==4 && interestBasis==0){
		    	interestBasis=3;
		    }
		    else  if(AmortConstant.RepaymentFrequencyValue== 2 &&AmortConstant.diffIndays < 180 &&AmortConstant.loop_count==4 && interestBasis==0){
		    	interestBasis=3;
		    }
		    else  if(AmortConstant.RepaymentFrequencyValue== 6 &&AmortConstant.diffIndays < 60 &&AmortConstant.loop_count==4 && interestBasis==0){
		    	interestBasis=3;
		    }
		    else  if(AmortConstant.RepaymentFrequencyValue== 3 &&AmortConstant.diffIndays < 120 &&AmortConstant.loop_count==4 && interestBasis==0){
		    	interestBasis=3;
		    }
		    else  if(AmortConstant.RepaymentFrequencyValue== 4 &&AmortConstant.diffIndays < 90 &&AmortConstant.loop_count==4 && interestBasis==0){
		    	interestBasis=3;
		    }
			if (interestBasis == AmortConstant.IntBasis_Actuals)
			{
				daycountBasis = DaycountCalculatorFactory.newInstance()
				.getISDAActualActual();

			} else if (interestBasis == AmortConstant.IntBasis_30360)
			{
				daycountBasis = DaycountCalculatorFactory.newInstance()
				.getUS30360();
			} else if (interestBasis == 2)
			{
				daycountBasis = DaycountCalculatorFactory.newInstance()
				.getEU30360();
			} else if (interestBasis == 3)
			{
				daycountBasis = DaycountCalculatorFactory.newInstance()
				.getActual360();
			} else if (interestBasis == 4)
			{
				daycountBasis = DaycountCalculatorFactory.newInstance()
				.getActual365Fixed();

			} else
			{
				throw new Exception("Couldn't find day count calculator.");
			}

			if(AmortConstant.isWeeklyflag){
				daycountFraction = daycountBasis.calculateDenomenator(c_date1, c_date2);
			}
			else{
				daycountFraction = daycountBasis.calculateDaycountFraction(c_date1, c_date2);
			}

		}
		catch(Exception e){

		}
		return daycountFraction;
	}

	public static double getInterestRate(double rate, int compFreq, double daycnt, String intCalMethod)
	{
		try
		{
			//return ((Math.pow((1+(rate/(compFreq))),compFreq*daycnt)-1));
			double d = 1.0;
	        BigDecimal bd;
	        if("S".equals(intCalMethod)){
	        	bd = BigDecimal.valueOf(rate*daycnt);
	        }else {
	        	bd = BigDecimal.valueOf(Math.pow((1 + rate/(compFreq)),compFreq * daycnt)).subtract(BigDecimal.valueOf(d));
	        }	
	        
	        return bd.doubleValue();
		}
		catch(Exception e)
		{
			if (isDebugEnabled) logger.error("Exception : inside getIntRate:"+e);
			return 0.0d;
		}
	}

	/**
     * Rounds the given amount's integer part to nearest unit specified.
     *
     * @param val1	-	Amount to be rounded
     * @param unit	-	Rounding Unit(1/2/5)
     * @return rounded amount
     *
     * @see roundUp(BigDecimal,int)
	 * @see roundDown(BigDecimal,int)
     */
    public static int roundProper(BigDecimal val1, int unit)
    {
    	int roundval = 0;
    	if (isDebugEnabled) logger.debug("roundProper:");
    	try{
    		int val = val1.intValue();
    		int rem = val % unit;
    		double halfUnit = (double)unit/2.0;

    		if(rem >= halfUnit)
    			roundval =  roundUp(val1, unit);
    		else
    			roundval =  roundDown(val1, unit);

    		if(unit == 1){
    			double roundval_1 = (double)Math.round(val1.doubleValue() * Math.pow(10D, unit)) / Math.pow(10D, unit);
    			roundval=(int)roundval_1;
    		}
    	}
    	catch(Exception e)
    	{
    		if (isDebugEnabled) logger.error("Exception : inside roundProper:"+e);
    		roundval = 0;
    	}
    	return roundval;
    }

    /**
     * Rounds the given amount's integer part to lower unit specified.
     *
     * @param val1 - Amount to be rounded
     * @param unit - Rounding Unit(1/2/5)
     * @return rounded amount
     */
    public static int roundDown(BigDecimal val1, int unit)
    {
    	int roundval = 0;
    	try
    	{
    		int val  = val1.intValue();
    		roundval =  val / unit * unit;
    	}
    	catch(Exception e)
    	{
    		if (isDebugEnabled) logger.error("Exception : inside roundDown:"+e);
    		roundval = 0;
    	}
    	return roundval;
    }

    /**
     * Rounds the given amount's integer part to upper unit specified.
     *
     * @param val1   Amount to be rounded
     * @param unit  Rounding Unit(1/2/5)
     * @return rounded amount
     */
      public static int roundUp(BigDecimal val1, int unit)
      {

    	  int roundval = 0;
    	  try
    	  {
    		  int val = val1.intValue();
    		  if (val % unit >= 0)
    			  roundval = ((val / unit) + 1) * unit;
    		  else
    			  roundval = val;
    	  }
    	  catch(Exception e)
    	  {
    		  if (isDebugEnabled) logger.error("Exception : inside roundUp:"+e);
    		  roundval = 0;
    	  }
    	  return roundval;
      }

    /**
     * Rounds the given amount's decimal part according to rounding method.
     *
     * @param x				 	amount to be rounded
     * @param scale			 	2
     * @param roundingMethod 	rounding method
     * @return rounded amount
     */
      public static double round(double x, int scale, int roundingMethod) {
    	  try {
    		  return (BigDecimal.valueOf(x)
    				  .setScale(scale, roundingMethod))
    				  .doubleValue();
    	  } catch (NumberFormatException ex) {
    		  if (Double.isInfinite(x)) {
    			  return x;
    		  } else {
    			  return Double.NaN;
    		  }
    	  }
      }

	 /**
	  * Rounds the given amount.
	  * Integer or decimal part of an amount is rounded
	  * to nearest,upper or lower specified rounding unit.
	  *
	  * @param amount			 	amount to be rounded
	  * @param roundpart	 	round Integer or decimal part
	  * @param roundTo		 	round to upper/lower/nearest
	  * @param rounding_unit 	rounding unit (1/2/5)
	  * @return rounded amount
	  *
	  * @see roundUp(BigDecimal,int)
	  * @see roundDown(BigDecimal,int)
	  * @see roundProper(BigDecimal,int)
	  * @see round(double,int,int)
	  */
	 public static double round(double amount,int roundpart,int roundTo,int rounding_unit)
	 {
		 double round_amount = 0.0d;
		 if(roundpart == AmortConstant.RoundingInteger)
		 {   int deciunit = (new BigDecimal(Math.log10(rounding_unit))).intValue();
			 if(roundTo == AmortConstant.NoRounding)
				 round_amount = amount;
			 else if(roundTo == AmortConstant.RoundingToUpper)
				  round_amount = round(amount, deciunit, BigDecimal.ROUND_UP);
			 else if(roundTo == AmortConstant.RoundingToLower)
				 round_amount = roundDown(BigDecimal.valueOf(amount), rounding_unit);
			 else
				 round_amount = roundProper(BigDecimal.valueOf(amount), rounding_unit);
		 }
		 else
		 {
			 int deciunit = (new BigDecimal(Math.log10(rounding_unit))).intValue();
			 if(roundTo == AmortConstant.NoRounding)
				 round_amount = Double.parseDouble(handlingNum(amount,AmortConstant.DefaultDeciaml));
			 else if(roundTo == AmortConstant.RoundingToUpper)
				 round_amount = round(amount, deciunit, BigDecimal.ROUND_UP);
			 else if(roundTo == AmortConstant.RoundingToLower)
				 round_amount = round(amount, deciunit, BigDecimal.ROUND_DOWN);
			 else
				 round_amount = round(amount, deciunit, BigDecimal.ROUND_HALF_UP);
		 }
		 //logger.info("round_amount::"+round_amount);
		 return round_amount;
	 }

	 /**
	 *handling a double
	 *
	 *@param num a double
	 *@param numlength left length after point
	 *@return handled value
	 */
	 public static String handlingNum(double num,int deciunit){
		 BigDecimal bigDec = null;		       
		 String NumString;
		 int iPoint;
		 int iMore;
		 int i;
		 int numlength = (new BigDecimal(Math.log10(deciunit))).intValue();
		 bigDec=BigDecimal.valueOf(num);
		 NumString = bigDec.toPlainString();
		 iPoint = NumString.indexOf(".");
		 if(iPoint > 0){
			 iMore = NumString.length()-(iPoint+numlength+1);
			 //judging fact length whether is small than wanted result length
			 if(iMore < 0)
			 {
				 for(i = 0;i <(-iMore);i++)
				 {
					 NumString = NumString + "0";
				 }
			 }
			 else
			 {
				 NumString = NumString.substring(0,iPoint + numlength + 1);
			 }
		 }
		 return NumString;
	 }
	 public static int  forroundingEMI(double num,AmortInputBean beanObj ){
		 BigDecimal bigDec=null;		       
		 String NumString;
		 String NumStringDec;
		 int iPoint;
		 double IntNumStringDec = 0.0d;
		 bigDec = BigDecimal.valueOf(num);
		 NumString = bigDec.toPlainString();
		 iPoint = NumString.indexOf(".");
		 int iLength = NumString.length();
		 int retValue = 0;
		 if(iPoint > 0){

			 NumStringDec = NumString.substring(iPoint+1,iLength);
			 IntNumStringDec = Double.parseDouble(NumStringDec);
			 if(IntNumStringDec > 0){
				 retValue =  NumStringDec.length();

			 }
			 else 
				 retValue = beanObj.getEMI_ro_part();

		 }

		 return retValue ;
	 }
	
	 
	 public static int compare2Dates(String dod, String fpd, String dateformat){
		 int i = 0;
		 if(dateformat.equals("")) dateformat = "dd/MM/yyyy";
		 SimpleDateFormat fm = new SimpleDateFormat(dateformat);
		 Calendar c1 = Calendar.getInstance();
		 Calendar c2 = Calendar.getInstance();
		 Date dt1,dt2;
		 try{
			 dt1 = fm.parse(dod);
			 dt2 = fm.parse(fpd);
		 }catch(Exception e){
			 return 2;
		 }
		 c1.setTime(dt1);
		 c2.setTime(dt2);

		 int y1 = dt1.getYear() + 1900;
		 int y2 = dt2.getYear() + 1900;

		 if(c1.before(c2)){
			 i = -1;
			 b = -1;
		 }else if(c1.after(c2)){
			 i = 1;
			 b = 1;
		 }else if(c1.equals(c2)){
			 i = 0;
		 }
		 if((y2-y1) != 0){
			 i = 2;

		 }
		 return i;
	 }
	 /**
	     * Calculates the cycle date for the next installment
	     *
	     * @param disburseDate	-	Date of cycle
	     * @param currentDate   - 	Previous cycle date
	     * @param dateformat	-	Dateformat
	     * @param freqfactor	-	no.of month/weeks in given freq period
	     * @param freqperiod	-	is month or week
	     * @return next cycle date
	     */
	    public static String getCycleDate(String disburseDate, String currentDate,String dateformat,int freqfactor,int freqperiod,int factor,boolean isEOMAmort)
	    {
	    	try{

	    		SimpleDateFormat  df =  new SimpleDateFormat(dateformat);
	    		Date dis_date = df.parse(disburseDate);
	    		Date curr_date = df.parse(currentDate);
	    		Calendar calendar = Calendar.getInstance();
	    		calendar.setTime(curr_date);

	    		int date = dis_date.getDate();
	    		int month = curr_date.getMonth();
	    		if(freqperiod == AmortConstant.setMonth)
	    		{
	    			if(isEOMAmort){ 
	    				calendar.add(Calendar.MONTH, (freqfactor*factor));
	    				int lastDate = calendar.getActualMaximum(Calendar.DATE);
	    				calendar.set(Calendar.DATE, lastDate);
	    			}else{
		    			if((date == 31)||(date == 30 && month == 0)||(date == 29 && month == 0 && factor>0)){
		    				calendar.add(Calendar.MONTH, (freqfactor*factor));
		    				int lastDate = calendar.getActualMaximum(Calendar.DATE);
		    				calendar.set(Calendar.DATE, lastDate);
		    			}
		    			else{
		    				calendar.add(Calendar.MONTH, (freqfactor*factor));
		    				if(((date == 29)||(date == 30)||(date == 31))&&(month == 1)){
		    					calendar.set(Calendar.DATE, date);
		    				}

		    			}
	    			}
	    		}

	    		else if(freqperiod == AmortConstant.setWeek)
	    			calendar.add(Calendar.WEEK_OF_MONTH, (freqfactor*factor));
	    		else if(freqperiod == AmortConstant.setDate)
	    			calendar.add(Calendar.DATE, 1);

	    		String strDate = df.format(calendar.getTime());

	    		return strDate;
	    	}
	    	catch(Exception e)
	    	{
	    		if (isDebugEnabled) logger.error("Exception : inside getCycleDate:"+e);
	    		return "";
	    	}
	    }
	    
	    /**
	     * Calculates Broken period interest.
	     *
	     * @param beanObj - Input Bean Object
	     * @return Broken period interest
	     *
	     * @see getIntRate(double,int,double)
	     * @see Interest(double,double)
	     */
	  
	    public static double calculateBPI(AmortInputBean beanObj,double loan_amount){
	    	String 	disbursedate	=	beanObj.getDateOfDisbursement();
	    	String 	paymentdate		=	beanObj.getDateOfFirstPayment();
	    	int 	intbasis		=	beanObj.getBPIMethod();
	    	double 	amount			=	loan_amount;
	    	double 	interestrate	=	beanObj.getInterestRate();
	    	String  dateformat		=   beanObj.getDateformat();
	    	int 	freqfactor		=	beanObj.getFrequencyFactor();
	    	int		freqperiod		=	beanObj.getFrequencyPeriod();
	    	double 	bpi				=	0.0d;
	    	int skip_bpi=0;
			 if(beanObj.getAdjRate() != 0.0)
	     	   interestrate = beanObj.getAdjRate();
	    	String firstcycledate = getCycleDate(disbursedate,disbursedate,dateformat,freqfactor,freqperiod,1,false);
	    	int iDiff = compare2Dates(firstcycledate, paymentdate, dateformat);
	    	if(beanObj.getRepaymentFrequency()!=26 && beanObj.getRepaymentFrequency()!=52){
	    	if(intbasis != 0){
	    		try{
	    			SimpleDateFormat  df =  new SimpleDateFormat(dateformat);
	    			Date cyl_date = df.parse(paymentdate);
	    			Calendar calendar = Calendar.getInstance();
	    			calendar.setTime(cyl_date);
	    			if(freqperiod == AmortConstant.setMonth)
		    		{
	    			calendar.add(Calendar.MONTH, -(freqfactor*1));
		    		}
	    			/*else if(freqperiod == AmortConstant.setWeek){
		    			calendar.add(Calendar.WEEK_OF_MONTH, (freqfactor*1));
		    		}*/
	    			else if(freqperiod == AmortConstant.setDate){
		    			calendar.add(Calendar.DATE, 1);
	    			}	    			
	    			// calendar.add(Calendar.MONTH, -(freqfactor*1));
	    			String strDate = df.format(calendar.getTime());
	    			paymentdate = strDate;
	    			
	    			//added by Ms Pournima :ZAN-1530
	    			if(beanObj.isEOMAmort()){
	    				paymentdate = disbursedate;
	    			    firstcycledate =getLastDayOfTheMonth(strDate);
	    			}else{
	    				firstcycledate = disbursedate;
	    			}

	    		}catch(Exception e)
	    		{
	    			if (isDebugEnabled) logger.error("Exception : inside getCycleDate:"+e);
	    		}
	    	 }
	    	}
	    	
	    	if(null != beanObj.getArrSkipEMI())
	    	{
	    		
	    		ArrayList arrskip = (ArrayList)beanObj.getArrSkipEMI();
	    		
	    		for(int k = 0;k < arrskip.size();k++)
	    		{
	    			SkipEMIInputBean sm = (SkipEMIInputBean)arrskip.get(k);
	    				if(1 == sm.getFrm_month() && sm.getSkip_capital().equals("N")){
	    				skip_bpi++;
	    		         }
	    		}
	    		
	        }if(skip_bpi==0){		
	    	if (iDiff == 1){
	    		if(amount == 0){

	    			amount = loan_amount;
	    		}

	    		bpi = (-1) * amount * getInterestRate(interestrate, beanObj.getCompoundFreq(), getDayfactor(intbasis,firstcycledate,paymentdate,dateformat,false), beanObj.getIntCalMethod());
	    	}
	    	else if(iDiff == -1){

	    		if(amount == 0){
	    			amount = loan_amount;	
	    		}

	    		bpi =  amount * getInterestRate(interestrate, beanObj.getCompoundFreq(), getDayfactor(intbasis,firstcycledate,paymentdate,dateformat,false),beanObj.getIntCalMethod());   	   	
	    		
	    	}else if(iDiff == 2 && b == 1){
	    		if(amount == 0){
	    			amount = loan_amount;
	    		}

	    		bpi = (-1) * amount * getInterestRate(interestrate, beanObj.getCompoundFreq(), getDayfactor(intbasis,firstcycledate,paymentdate,dateformat,false),beanObj.getIntCalMethod());

	    	}else if(iDiff == 2 && b == -1){
	    		if(amount == 0){
	    			amount = loan_amount;	
	    		}

	    		bpi =  amount * getInterestRate(interestrate, beanObj.getCompoundFreq(), getDayfactor(intbasis,paymentdate,firstcycledate,dateformat,false),beanObj.getIntCalMethod()); 

	    	}

	    	else{
	    		bpi = 0;
	    	}
	    	}

	    	if (isDebugEnabled) logger.debug("BPI : " + bpi);
	    	return bpi;
	    }
	    
	    public static String getLastDayOfTheMonth(String date) {
	        String lastDayOfTheMonth = "";
	        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
	        try{
	        java.util.Date dt= formatter.parse(date);
	        Calendar calendar = Calendar.getInstance();  
	        calendar.setTime(dt);  
	        calendar.add(Calendar.MONTH, 1);  
	        calendar.set(Calendar.DAY_OF_MONTH, 1);  
	        calendar.add(Calendar.DATE, -1);  
	        java.util.Date lastDay = calendar.getTime();  
	        lastDayOfTheMonth = formatter.format(lastDay);
	        } catch (ParseException e) {
	            e.printStackTrace();
	        }
	        return lastDayOfTheMonth;
	    }
	    /**
	     * Calculates interest between two given dates.
	     *
	     * @param beanObj - Input Bean Object
	     * @return interest
	     *
	     * @see getIntRate(double,int,double)
	     * @see Interest(double,double)
	     */
	    public static double calculateInterest(AmortInputBean beanObj){
	    	String 	dtStart			=	beanObj.getDtstart();
	    	String 	dtEnd			=	beanObj.getDtend();
	    	int 	intbasis		=	beanObj.getInterest_Basis();
	    	double 	amount			=	beanObj.getLoanAmount();
	    	double 	interestrate	=	beanObj.getInterestRate();
	    	String  dateformat		=   beanObj.getDateformat();
	    	double 	interest		=	0.0d;

	    	//getInterestRate(interestrate, beanObj.getCompoundFreq(), AmortUtil.getDayfactor(intbasis,dtStart,dtEnd,dateformat,false))
	    	interest =  amount * AmortUtil.getInterestRate(interestrate, beanObj.getCompoundFreq(), AmortUtil.getDayfactor(intbasis,dtStart,dtEnd,dateformat,false),beanObj.getIntCalMethod());
	    	if (isDebugEnabled) logger.debug("interest : " + interest);
	    	return interest;
	    }


	    public static String ConvertDate(String dt,String dateformat) throws ValidationException{
	    	SimpleDateFormat fm = null;
	    	String newDt = null;
	    	if(dateformat.equals("")) dateformat = DateFormats.defaultDateFormat1;
	    	Date FormattedDate = GenericTypeValidator.formatDate(dt, dateformat,true);
	    	if(FormattedDate != null){
	    		// Formatting date into  dd/MM/yyyy e.g 31/07/2009
	    		if(dateformat.equalsIgnoreCase((DateFormats.defaultDateFormat1))){
	    			fm = new SimpleDateFormat(DateFormats.defaultDateFormat1);
	    		}
	    		// Formatting date into  yyyy-MM-dd HH:mm:ss e.g 2009-07-31 11:21:10
	    		else if(dateformat.equalsIgnoreCase(DateFormats.defaultDateFormat2)){
	    			fm = new SimpleDateFormat(DateFormats.defaultDateFormat2);
	    		}

	    		// Formatting date into  yyyy-MM-dd e.g 2009-07-31
	    		else if(dateformat.equalsIgnoreCase(DateFormats.defaultDateFormat3)){
	    			fm = new SimpleDateFormat(DateFormats.defaultDateFormat3);
	    		}

	    		// Formatting date into  MM/dd/yyyy e.g 07/31/2009
	    		else if(dateformat.equalsIgnoreCase(DateFormats.defaultDateFormat4)){
	    			fm = new SimpleDateFormat(DateFormats.defaultDateFormat4);
	    		}
	    		// Formatting date into  dd-MMM-yy e.g 01-JUL-09
	    		else if(dateformat.equalsIgnoreCase(DateFormats.defaultDateFormat5)){
	    			fm = new SimpleDateFormat(DateFormats.defaultDateFormat5);
	    		}
	    		else
	    		{
	    			fm = new SimpleDateFormat(dateformat);
	    		}
	    		newDt = fm.format(FormattedDate);
	    	}
	    	return newDt;
	    }
		 public static double Calculate(int operator,double val1,double val2)
		 {
			 double result =0.0d;

			 try{
				 switch(operator)
				 {
				 case 1:	result = val1+val2;
				 break;
				 case 2:	result = val1-val2;
				 break;
				 case 3:	result = val1*val2;
				 break;
				 case 4:	result = val1/val2;
				 break;
				 case 5:	result = Math.min(val1,val2);
				 break;
				 case 6:	result = Math.max(val1, val2);
				 break;
				 default:result = 0.0d;
				 }

			 }
			 catch(Exception e)
			 {
				 if (isDebugEnabled) logger.error("Exception : inside Calculate(int operator,double val1,double val2):"+e);
				 result = 0;
			 }
			 return result;
		 }

		 public static double Calculate(int operator,double[] arrparam)
		 {
			 double result = 0.0d;
			 try{
				 for(int i = 0;i < arrparam.length;i++)
				 {
					 switch(operator)
					 {
					 case 1:	result = result + arrparam[i];
					 break;
					 case 2:	result = result - arrparam[i];
					 break;
					 case 3:	result = result * arrparam[i];
					 break;
					 case 4:	result = result / arrparam[i];
					 break;
					 case 5:	result = Math.min(result, arrparam[i]);
					 break;
					 case 6:	result = Math.max(result, arrparam[i]);
					 break;
					 default:result = 0.0d;
					 break;
					 }
				 }
			 }
			 catch(Exception e)
			 {
				 if (isDebugEnabled) logger.error("Exception : inside Calculate(int operator,double[] arrparam):"+e);
				 result = 0;
			 }
			 return result;
		 }

		 public static double getParamval(String p1,AmortOutputBean out)
		 {
			 double result = 0.0d;

			 if(p1.equals("Op_Bal"))
				 result = out.getRoundOpen();
			 else if(p1.equals("EMI"))
				 result = out.getRoundEMI();
			 else if(p1.equals("Int"))
				 result = out.getRoundInterest();
			 else if(p1.equals("Prn"))
				 result = out.getRoundPrincipal();
			 else
				 result = 0.0d;

			 return result;
		 }

		 public static boolean datesInBetween(String begin, String end,String toChk,String dateformat){
			 if(dateformat.equals("")) dateformat = "dd/MM/yyyy";
			 SimpleDateFormat fm = new SimpleDateFormat(dateformat);
			 Calendar c1 = Calendar.getInstance();
			 Calendar c2 = Calendar.getInstance();
			 Calendar c3 = Calendar.getInstance();
			 Date dt1,dt2,dt3;
			 boolean successFalg;
			 try{
				 dt1 = fm.parse(begin);
				 dt2 = fm.parse(end);
				 dt3 = fm.parse(toChk);
			 }catch(Exception e){
				 return false;
			 }
			 c1.setTime(dt1);
			 c2.setTime(dt2);
			 c3.setTime(dt3);
			 if((c1.before(c3) && c2.after(c3)) || c1.equals(c3) || c2.equals(c3)) successFalg = true;
			 else successFalg = false;
			 return successFalg;
		 }

		 public static double cal_WeeklyInterest(String prevDate,String nextDate,String compDate,double rest_open_balance,double open_balance,double rate ,AmortInputBean beanObj){
			 boolean weeklyflag = true;
			 if(!compDate.equals("")){
				 double prevInterest = getInterest(rest_open_balance,rate,beanObj.getCompoundFreq(),beanObj.getInterest_Basis(),prevDate,compDate,beanObj.getDateformat(),weeklyflag,beanObj.getIntCalMethod());
				 double nextInterest = getInterest(open_balance,rate,beanObj.getCompoundFreq(),beanObj.getInterest_Basis(),compDate,nextDate,beanObj.getDateformat(),weeklyflag,beanObj.getIntCalMethod());
				 return prevInterest + nextInterest;
			 }
			 else{
				 double Interest = getInterest(rest_open_balance,rate,beanObj.getCompoundFreq(),beanObj.getInterest_Basis(),prevDate,nextDate,beanObj.getDateformat(),weeklyflag,beanObj.getIntCalMethod());
				 return Interest;
			 }
		 }
		 
		 public static double cal_Interest_rate(AmortInputBean beanObj){
			 int i = 0;
			 double guessValue = 0.0d;
			 double diff = 0.0d;
			 double interest_per_period_initial = 0.0d;
			 double	interest_per_period_accurate = 0.0d;
			 double U = 0.0d;
			 double N  = 0.0d;
			 double num = 0.0d;
			 double denom = 0.0d;
			 double res = 0.0d;
			 int 	frequency = beanObj.getRepaymentFrequency();
			 double tenor = 0.0d;
			 double term = 0;

			 do{

				 term  =  AmortUtil.getNPER(tenor, frequency,beanObj);
				 if(beanObj.getNoOfAdvEMI() > 0 && beanObj.getAdjustAdvEMIAt() == 0 ){
					 term =  beanObj.getTenor();
				 }
				 if(i == 0){
					 U  = ((beanObj.getInput_emi() * term/beanObj.getLoanAmount()) - 1) / (term + 1);
					 N  = term;
					 guessValue = ( 2 * U + (2 * U * U * (N - 1) * ((2 * N + 1) * (U / 9) -1.0 / 3.0-((2 * N + 1) * (11 * N + 7) * (U) * (U / 135)) + (2 * N + 1) * (2 * N + 1) * (13 * N + 11) * U * U * (U / 405))));
					 guessValue = guessValue * 100;
					 interest_per_period_initial = guessValue;
					 diff = interest_per_period_initial;

				 }
				 else{
					 num = (beanObj.getInput_emi()-beanObj.getInput_emi()* Math.pow(1+interest_per_period_initial/100,(-1)*N)-interest_per_period_initial/100 * beanObj.getLoanAmount());
					 denom = N * beanObj.getInput_emi() * Math.pow(1 + interest_per_period_initial / 100,(-1) * N - 1) - beanObj.getLoanAmount();
					 interest_per_period_accurate = interest_per_period_initial / 100 - (num / denom);
					 diff = interest_per_period_accurate - interest_per_period_initial;
					 interest_per_period_initial = interest_per_period_accurate;
				 }
				 i++;

			 }while((diff > 0));
			 res = beanObj.getCompoundFreq() * (Math.pow(interest_per_period_accurate + 1,beanObj.getRepaymentFrequency() * 1.0 / beanObj.getCompoundFreq()) - 1);

			 return res;	
		 }
		
		 public static double addDouble(double firstVal,double secondVal){
			 double finalVal = 0.0d;
			 finalVal = (BigDecimal.valueOf(firstVal).add(BigDecimal.valueOf(secondVal))).doubleValue();
			 return finalVal;
		 }
		
		 public static double subtractDouble(double firstVal,double secondVal){
			 double finalVal = 0.0d;
			 finalVal = (BigDecimal.valueOf(firstVal).subtract(BigDecimal.valueOf(secondVal))).doubleValue();
			 return finalVal;
		 }
		public static long simpleDayCount(int interestBasis,String date1,String date2,String dateformat)	{

			SimpleDateFormat  df =  new SimpleDateFormat(dateformat);

			long j = 0;
			try{

				Date d_date1 = df.parse(date1);
				Date d_date2 = df.parse(date2);

				Calendar c_date1 = Calendar.getInstance();
				c_date1.setTime(d_date1);

				Calendar c_date2 = Calendar.getInstance();
				c_date2.setTime(d_date2);	
				if (interestBasis == AmortConstant.IntBasis_Actuals)
				{
					daycountBasis = DaycountCalculatorFactory.newInstance()
					.getAFBActualActual();
				} else if (interestBasis == AmortConstant.IntBasis_30360)
				{
					daycountBasis = DaycountCalculatorFactory.newInstance()
					.getUS30360();
				} else if (interestBasis == 2)
				{
					daycountBasis = DaycountCalculatorFactory.newInstance()
					.getEU30360();
				} else if (interestBasis == 3)
				{
					daycountBasis = DaycountCalculatorFactory.newInstance()
					.getActual360();
				} else if (interestBasis == 4)
				{
					daycountBasis = DaycountCalculatorFactory.newInstance()
					.getActual365Fixed();
				} else
				{
					throw new Exception("Couldn't find day count calculator.");
				}   	  

				j = daycountBasis.daysBetween(c_date1, c_date2);

			}

			catch(Exception e){

			}
			return j;
		}	
	public static int getlastDate(String date1,AmortInputBean beanObj) {
		int dtDay = 0;
		try{
			SimpleDateFormat dateFormat = new SimpleDateFormat(beanObj.getDateformat());
			Date d_date1 = dateFormat.parse(date1);
			Calendar c_date1 = Calendar.getInstance();
			c_date1.setTime(d_date1);
			dtDay = c_date1.getActualMaximum(c_date1.DAY_OF_MONTH) ;
		}
		catch(Exception e){

		}
		return dtDay;
	}
	public static String getlastDate(String date1,int mode,AmortInputBean beanObj) {

		int dtDayD = 0;
		int dtDayM = 0;
		int dtDayY = 0;
		String dtDay = "";
		try{
			SimpleDateFormat dateFormat = new SimpleDateFormat(beanObj.getDateformat());
			Date d_date1 = dateFormat.parse(date1);
			Calendar c_date1 = Calendar.getInstance();
			c_date1.setTime(d_date1);
			if(mode == 0){
				dtDayD = c_date1.getActualMaximum(c_date1.DAY_OF_MONTH) ;
				dtDayM =  d_date1.getMonth(); 
				dtDayY = d_date1.getYear() + 1900;
				c_date1.set(dtDayY, dtDayM, dtDayD);
				dtDay = dateFormat.format(c_date1.getTime());
			}
			else if (mode == 1){
				dtDayD = c_date1.getActualMinimum(c_date1.DAY_OF_MONTH);
				dtDayM =  d_date1.getMonth();  
				dtDayY = d_date1.getYear() + 1900;
				c_date1.set(dtDayY, dtDayM, dtDayD);
				dtDay = dateFormat.format(c_date1.getTime());

			}

		}
		catch(Exception e){
		}
		return dtDay;
	}
	public static double  getAmortCal_NPV(AmortInputBean beanObj){
		double npvval = 0.0d;
		int i = 1;
		double rate =	beanObj.getInterestRate();
		int freq=beanObj.getRepaymentFrequency();
		if(null!= beanObj.getArrROWS())
		{
			ArrayList arrNPV = beanObj.getArrROWS();
			for(int j = 0;j < arrNPV.size();j++)
			{
				NPVROWSInputBean npv = (NPVROWSInputBean)arrNPV.get(j);
				npvval += (npv.getAmount()/Math.pow((1 + rate),npv.getInstallment_No()));
				i++;

			}

		}
		return npvval; 
	}

}

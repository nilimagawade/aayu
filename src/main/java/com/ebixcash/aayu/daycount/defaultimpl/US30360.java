package com.ebixcash.aayu.daycount.defaultimpl;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ebixcash.aayu.constant.AmortConstant;



/**
 * If startCalendar and endCalendar are equal dates, returns zero.
 */
public class US30360 extends DaycountCalculator
{

	private static final Logger LOGGER = LoggerFactory.getLogger(US30360.class);

	public double calculateDaycountFraction(Calendar startCalendar,
			Calendar endCalendar) throws DaycountException
	{

		if (startCalendar.after(endCalendar))
		{
			LOGGER.warn("Dates are wrong way round so swap over");
			Calendar holdCalendar = startCalendar;
			startCalendar = endCalendar;
			endCalendar = holdCalendar;
		}

		if (startCalendar.equals(endCalendar))
		{
			LOGGER.warn("Dates are the same so return zero");
			return 0.0d;
		}
		int dayOfMonth1 = startCalendar.get(Calendar.DAY_OF_MONTH);
		int dayOfMonth2 = endCalendar.get(Calendar.DAY_OF_MONTH);
		int month1 = startCalendar.get(Calendar.MONTH);
		int month2 = endCalendar.get(Calendar.MONTH);
		int year1 = startCalendar.get(Calendar.YEAR);
		int year2 = endCalendar.get(Calendar.YEAR);

		boolean isLastDayOfFebruary1 = dayOfMonth1==startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH) && month1 == Calendar.FEBRUARY;
		boolean isLastDayOfFebruary2 = dayOfMonth2==endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH) && month2 == Calendar.FEBRUARY;

		if(isLastDayOfFebruary2) {
			dayOfMonth2 = 30;
		}

		if(isLastDayOfFebruary1) {
			dayOfMonth1 = 30;
		}

		if(dayOfMonth2==31 && dayOfMonth1>=30) {
			dayOfMonth2 = 30;
		}

		if(dayOfMonth1==31) {
			dayOfMonth1 = 30;
		}

		int numerator = 360*( year2 - year1 );
		if(AmortConstant.isDaily == true){
			numerator = 1;
		}else{
		numerator+= 30 * ( month2 - month1);
		
        /* Bug 252917 - Incorrect Interest is getting calculated for IB-30/360 */
		if(AmortConstant.calFirst_int==3 && AmortConstant.monthDOD != AmortConstant.monthDOC && AmortConstant.diffIndays<30 ){
		numerator=0;
		numerator = (int) AmortConstant.diffIndays;
		//AmortConstant.calFirst_int++;
	}
		}
//		}numerator+= dayOfMonth2 - dayOfMonth1;
		if(month2==month1 && numerator==0 && AmortConstant.isDaily == false){
		numerator+= dayOfMonth2 - dayOfMonth1;
		}
		return numerator/360d;
	}

	//@Override
	public double calculateDaycountFraction(Calendar startCalendar,
			Calendar endCalendar, Calendar periodStartCalendar,
			Calendar periodEndCalendar) throws DaycountException
	{
		return calculateDaycountFraction(startCalendar, endCalendar);
	}
	
	public double calculateDenomenator(Calendar startCalendar,
			Calendar endCalendar) throws DaycountException
	{
		if (startCalendar.after(endCalendar))
		{
			LOGGER.warn("Dates are wrong way round so swap over");
			Calendar holdCalendar = startCalendar;
			startCalendar = endCalendar;
			endCalendar = holdCalendar;
		}

		if (startCalendar.equals(endCalendar))
		{
			LOGGER.warn("Dates are the same so return zero");
			return 0.0d;
		}

		Calendar newD2 = endCalendar;
		Calendar temp = (Calendar) endCalendar.clone();
		double sum = 0.0;
		while (temp.after(startCalendar))
		{
			temp = (Calendar) newD2.clone();
			temp.add(Calendar.YEAR, -1);
			if (temp.get(Calendar.DAY_OF_MONTH) == 28
					&& temp.get(Calendar.MONTH) == Calendar.FEBRUARY
					&& temp.getActualMaximum(Calendar.DAY_OF_YEAR) == 366)
			{
				temp.add(Calendar.DAY_OF_YEAR, 1);
			}
			if (temp.after(startCalendar) || temp.equals(startCalendar))
			{
				sum += 1.0;
				newD2 = temp;
			}
		}

		double den = 365.0;

		if (newD2.getActualMaximum(Calendar.DAY_OF_YEAR) == 366)
		{
			temp = (Calendar) newD2.clone();
			temp.set(Calendar.MONTH, Calendar.FEBRUARY);
			temp.set(Calendar.DAY_OF_MONTH, 29);
			if (newD2.after(temp)
					&& ( startCalendar.before(temp) || startCalendar
							.equals(temp) ))
				den += 1.0;
		} else if (startCalendar.getActualMaximum(Calendar.DAY_OF_YEAR) == 366)
		{
			temp = (Calendar) startCalendar.clone();
			temp.set(Calendar.MONTH, Calendar.FEBRUARY);
			temp.set(Calendar.DAY_OF_MONTH, 29);
			if (newD2.after(temp)
					&& ( startCalendar.before(temp) || startCalendar
							.equals(temp) ))
				den += 1.0;
		}

		return sum + daysBetween(startCalendar, newD2) / 360.0d;
	}
}

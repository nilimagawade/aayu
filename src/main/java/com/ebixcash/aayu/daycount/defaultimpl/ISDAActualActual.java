/*
 * <p> <b>jFin, open source derivatives trade processing</b> </p>
 *
 * <p> Copyright (C) 2005, 2006, 2007 Morgan Brown Consultancy Ltd. </p>
 *
 * <p> This file is part of jFin. </p>
 *
 * <p> jFin is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version. </p>
 *
 * <p> jFin is distributed in the hope that it will be useful, but <b>WITHOUT
 * ANY WARRANTY</b>; without even the implied warranty of <b>MERCHANTABILITY</b>
 * or <b>FITNESS FOR A PARTICULAR PURPOSE</b>. See the GNU General Public
 * License for more details. </p>
 *
 * <p> You should have received a copy of the GNU General Public License along
 * with jFin; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA. </p>
 */

package com.ebixcash.aayu.daycount.defaultimpl;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
import org.jfin.date.daycount.DaycountCalculator;
import org.jfin.date.daycount.DaycountException;
import org.jfin.date.util.ISDADateFormat;*/
/**
 * Based upon the implementation from QuantLib http://www.quantlib.org/
 *
 * If startCalendar and endCalendar are equal dates, returns zero.
 */

public class ISDAActualActual extends DaycountCalculator
{

	public class Period {

	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ISDAActualActual.class);

	/**
	 * Calculates the ISDA Actual actual day count fraction
	 * between two dates.
	 */
	public double calculateDaycountFraction(Calendar startCalendar,
			Calendar endCalendar) throws DaycountException
	{
	//	LOGGER.warn("Calculating daycount fraction for "
				//+ ISDADateFormat.format(startCalendar) + " - "
			//	+ ISDADateFormat.format(endCalendar));
			double num = 0.0d;
			double denom = 0.0d;
			double numEnd = 0.0d;
			double denomEnd = 0.0d;
			double numStart = 0.0d;
			double denomStart = 0.0d;
			double fraction = 0.0d;
			if (startCalendar.after(endCalendar))
			{
				LOGGER.warn("Dates are wrong way round so swap over");
				Calendar holdCalendar = startCalendar;
				startCalendar = endCalendar;
				endCalendar = holdCalendar;
			}
		if (((startCalendar.get(Calendar.YEAR))== (endCalendar.get(Calendar.YEAR))) || (startCalendar.getActualMaximum(Calendar.DAY_OF_YEAR)!=366 && endCalendar.getActualMaximum(Calendar.DAY_OF_YEAR)!=366)) 
		{
			LOGGER.warn("Dates are wrong way round so swap over");
			num = daysBetween(startCalendar, endCalendar);
			if(num<0){
				num= (-1) * num;
			}
			denom = startCalendar.getActualMaximum(Calendar.DAY_OF_YEAR);
			fraction = num/denom;
		}
		else if ((startCalendar.get(Calendar.YEAR))!= (endCalendar.get(Calendar.YEAR)) && (startCalendar.getActualMaximum(Calendar.DAY_OF_YEAR)==366 || endCalendar.getActualMaximum(Calendar.DAY_OF_YEAR)==366)){
			
			int  tempCurrStartday =  startCalendar.get(Calendar.DAY_OF_YEAR);
			int templastStartDay = startCalendar.getActualMaximum(Calendar.DAY_OF_YEAR);
			numStart = (templastStartDay - tempCurrStartday)+1;
			if(numStart<0){
				numStart= (-1) * numStart;
			}
			denomStart = startCalendar.getActualMaximum(Calendar.DAY_OF_YEAR);
			int tempCurrEndday =  endCalendar.get(Calendar.DAY_OF_YEAR);
			int tempFirsttEndDay = endCalendar.getActualMinimum(Calendar.DAY_OF_YEAR);
			
			numEnd= tempCurrEndday- tempFirsttEndDay;
			if(numEnd<0){
				numEnd= (-1) * numEnd;
			}
			denomEnd = endCalendar.getActualMaximum(Calendar.DAY_OF_YEAR);
			fraction  = (numStart/denomStart)+(numEnd/denomEnd);
			
		}

		return fraction;
	}

	/**
	 * Calculates the ISDA Actual actual day count fraction
	 * between two dates. ISDA Actual/Actual does not
	 * vary depending on the reference period, so this
	 * is exactly equivelent to calling
	 * calculateDaycountFraction(startCalendar, endCalendar)
	 */
	public double calculateDaycountFraction(Calendar startCalendar,
			Calendar endCalendar, Calendar periodStartCalendar,
			Calendar periodEndCalendar) throws DaycountException
	{
		return calculateDaycountFraction(startCalendar, endCalendar);
	}

	/**
	 * Generate a list of the sub periods (up to and including year
	 * boundaries) that are required to calculate the ISDA Actual Actual
	 * day count fraction
	 *
	 * @param startCalendar The start calendar of the period
	 * @param endCalendar The end calendar of the period
	 * @return A list of sub periods between the start and end calendars
	 */

	


	/**
	 * Calculates the denominators for a set of periods
	 *
	 * @param subPeriods A list of the sub periods
	 * @return An array of type int containing the denominators of the sub periods
	 */
	public double calculateDenomenator(Calendar startCalendar,
			Calendar endCalendar) throws DaycountException
	{
		double num = 0.0d;
		double denom = 0.0d;
		double numEnd = 0.0d;
		double denomEnd = 0.0d;
		double numStart = 0.0d;
		double denomStart = 0.0d;
		double fraction = 0.0d;		
	if (startCalendar.after(endCalendar))
	{
		LOGGER.warn("Dates are wrong way round so swap over");
		Calendar holdCalendar = startCalendar;
		startCalendar = endCalendar;
		endCalendar = holdCalendar;
	}
	else if (((startCalendar.get(Calendar.YEAR))== (endCalendar.get(Calendar.YEAR))) || (startCalendar.getActualMaximum(Calendar.DAY_OF_YEAR)!=366 && endCalendar.getActualMaximum(Calendar.DAY_OF_YEAR)!=366)) 
	{
		LOGGER.warn("Dates are wrong way round so swap over");
		num = daysBetween(startCalendar, endCalendar);
		if(num<0){
			num= (-1) * num;
		}
		denom = startCalendar.getActualMaximum(Calendar.DAY_OF_YEAR);
		fraction = num/denom;
	}
	else if((startCalendar.get(Calendar.YEAR))!= (endCalendar.get(Calendar.YEAR)) && (startCalendar.getActualMaximum(Calendar.DAY_OF_YEAR)==366 || endCalendar.getActualMaximum(Calendar.DAY_OF_YEAR)==366)){
		
		int  tempCurrStartday =  startCalendar.get(Calendar.DAY_OF_YEAR);
		int templastStartDay = startCalendar.getActualMaximum(Calendar.DAY_OF_YEAR);
		numStart = (templastStartDay - tempCurrStartday)+1;
		if(numStart<0){
			numStart= (-1) * numStart;
		}
		denomStart = startCalendar.getActualMaximum(Calendar.DAY_OF_YEAR);
		int tempCurrEndday =  endCalendar.get(Calendar.DAY_OF_YEAR);
		int tempFirsttEndDay = endCalendar.getActualMinimum(Calendar.DAY_OF_YEAR);
		
		numEnd= tempCurrEndday- tempFirsttEndDay;
		if(numEnd<0){
			numEnd= (-1) * numEnd;
		}
		denomEnd = endCalendar.getActualMaximum(Calendar.DAY_OF_YEAR);
		fraction  = (numStart/denomStart)+(numEnd/denomEnd);
		
	}

	/*else if (((startCalendar.get(Calendar.YEAR))== (endCalendar.get(Calendar.YEAR))) || (startCalendar.getActualMaximum(Calendar.DAY_OF_YEAR)!=366 && endCalendar.getActualMaximum(Calendar.DAY_OF_YEAR)!=366)) 
	{
		LOGGER.fine("Dates are wrong way round so swap over");
		num = daysBetween(startCalendar, endCalendar);
		if(num<0){
			num= (-1) * num;
		}
		denom = startCalendar.getActualMaximum(Calendar.DAY_OF_YEAR);
		fraction = num/denom;
	}*/
	return fraction;
}
}


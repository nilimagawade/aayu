package com.ebixcash.aayu.daycount.defaultimpl;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Based upon the implementation from QuantLib http://www.quantlib.org/
 *
 * If startCalendar and endCalendar are equal dates, returns zero.
 */
public class IT30360 extends DaycountCalculator
{

	private static final Logger LOGGER = LoggerFactory.getLogger(IT30360.class);
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
		int dd1 = startCalendar.get(Calendar.DAY_OF_MONTH);
		int dd2 = endCalendar.get(Calendar.DAY_OF_MONTH);
		int mm1 = startCalendar.get(Calendar.MONTH);
		int mm2 = endCalendar.get(Calendar.MONTH);
		int yy1 = startCalendar.get(Calendar.YEAR);
		int yy2 = endCalendar.get(Calendar.YEAR);

		if (mm1 == 2 && dd1 > 27)
			dd1 = 30;
		if (mm2 == 2 && dd2 > 27)
			dd2 = 30;

		return ( yy2 - yy1 )
				+ ( ( 30 * ( mm2 - mm1 - 1 ) + Math.max(0, 30 - dd1) + Math
						.min(30, dd2) ) / 360.0d );
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

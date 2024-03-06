package com.ebixcash.aayu.daycount.defaultimpl;

import java.util.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Based upon the implementation from QuantLib http://www.quantlib.org/
 *
 * If startCalendar and endCalendar are equal dates, returns zero.
 */
public class Actual360 extends DaycountCalculator
{

	private static final Logger logger = LoggerFactory.getLogger(Actual360.class);

	public double calculateDaycountFraction(Calendar startCalendar,
			Calendar endCalendar) throws DaycountException
	{
		if (startCalendar.after(endCalendar))
		{
			logger.warn("Dates are wrong way round so swap over");
			Calendar holdCalendar = startCalendar;
			startCalendar = endCalendar;
			endCalendar = holdCalendar;
		}

		if (startCalendar.equals(endCalendar))
		{
			logger.warn("Dates are the same so return zero");
			return 0.0d;
		}
		return daysBetween(startCalendar, endCalendar) / 360.0d;
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
			logger.warn("Dates are wrong way round so swap over");
			Calendar holdCalendar = startCalendar;
			startCalendar = endCalendar;
			endCalendar = holdCalendar;
		}

		if (startCalendar.equals(endCalendar))
		{
			logger.warn("Dates are the same so return zero");
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

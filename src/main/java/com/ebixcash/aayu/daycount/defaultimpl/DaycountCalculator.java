package com.ebixcash.aayu.daycount.defaultimpl;

import java.util.Calendar;

public abstract class DaycountCalculator {

	/**
	 * Calculates the daycount fraction of the period represented by the
	 * startCalendar and endCalendar
	 *
	 * @param startCalendar
	 * @param endCalendar
	 * @return The day count fraction of a year
	 * @throws DaycountException
	 */
	public abstract double calculateDaycountFraction(Calendar startCalendar,
			Calendar endCalendar) throws DaycountException;

	/**
	 * Calculates the daycount fraction of the period represented by the
	 * startCalendar and endCalendar with reference to the period represented by
	 * periodStartCalendar and periodEndCalendar. Used when the period
	 * represented by startCalendar and endCalendar is a long or short stub
	 * period, the periodStartCalendar and periodEndCalendar represent the
	 * 'notional' period as if it were a full period and not a stub
	 *
	 * @param startCalendar
	 * @param endCalendar
	 * @param periodStartCalendar
	 * @param periodEndCalendar
	 * @return The day count fraction of a year
	 * @throws DaycountException
	 */
	public abstract double calculateDaycountFraction(Calendar startCalendar,
			Calendar endCalendar, Calendar periodStartCalendar,
			Calendar periodEndCalendar) throws DaycountException;

	/**
	 * Returns the nearest calculation of the number of calendar days between
	 * two Calendars.
	 *
	 * @param d1
	 * @param d2
	 * @return The number of days between the two Calendars
	 */
	public long daysBetween(Calendar d1, Calendar d2)
	{
		return (long) Math.round(( d2.getTimeInMillis() - d1.getTimeInMillis() )
				/ ( 1000d * 60d * 60d * 24d ));
	}
	
	/**
	 * Returns the nearest calculation of the number of calendar days between
	 * two Calendars.
	 *
	 * @param d1
	 * @param d2
	 * @return The number of days between the two Calendars
	 */
	public abstract double calculateDenomenator(Calendar startCalendar,
			Calendar endCalendar)throws DaycountException;
	
}

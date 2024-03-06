package com.ebixcash.aayu.daycount.defaultimpl;

public class DaycountCalculatorFactoryImpl extends DaycountCalculatorFactory {

	public DaycountCalculator getISDAActualActual() {
		return new ISDAActualActual();
	}

	/*
	 * @Override public DaycountCalculator getISMAActualActual() { return new
	 * ISMAActualActual(); }
	 */

	// @Override
	public DaycountCalculator getAFBActualActual() {
		return new AFBActualActual();
	}

	// @Override
	public DaycountCalculator getUS30360() {
		return new US30360();
	}

	// @Override
	public DaycountCalculator getEU30360() {
		return new EU30360();
	}

	// @Override
	public DaycountCalculator getIT30360() {
		return new IT30360();
	}

	// @Override
	public DaycountCalculator getActual360() {
		return new Actual360();
	}

	// @Override
	public DaycountCalculator getActual365Fixed() {
		return new Actual365Fixed();
	}

	// @Override
	public DaycountCalculator getActual366() {
		return new Actual366();
	}

	/*
	 * @Override public DaycountCalculator getBusiness252(HolidayCalendar
	 * holidayCalendar) { return new Business252(holidayCalendar); }
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jfin.date.daycount.DaycountCalculatorFactory#
	 * getAvailableDaycountCalculators()
	 */
	// @Override
	public String[] getAvailableDaycountCalculators() {
		return new String[] { "ISDAActualActual", "ISMAActualActual", "AFBActualActual", "US30360", "EU30360",
				"IT30360", "Actual360", "Actual365Fixed", "Actual366" };
	}
}

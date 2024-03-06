package com.ebixcash.aayu.exception;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.text.ParseException;

import com.ebixcash.aayu.exception.*;


public class AmortException extends ApplicationException {
	private static final long	serialVersionUID	= 1L;

	public static class ArithmeticException extends AmortException {
		private static final long	serialVersionUID	= 1L;

		public ArithmeticException(String message) {
			super(new String[] { message }, null);
		}

		public ArithmeticException(Exception exception) {
			super(exception);
		}
		public ArithmeticException(String message, Exception exception) {
            super(new String[]{message}, exception);
        }
	}
	public static class DivideByZeroException extends AmortException {
		private static final long	serialVersionUID	= 1L;

		public DivideByZeroException(String message) {
			super(new String[] { message }, null);
		}

		public DivideByZeroException(Exception exception) {
			super(exception);
		}
		public DivideByZeroException(String message, Exception exception) {
            super(new String[]{message}, exception);
        }
		
	}
	public static class CalculationFactoryException extends AmortException {
		private static final long	serialVersionUID	= 1L;

		public CalculationFactoryException(String message) {
			super(new String[] { message }, null);
		}

		public CalculationFactoryException(Exception exception) {
			super(exception);
		}
		public CalculationFactoryException(String message, Exception exception) {
            super(new String[]{message}, exception);
        }
		public CalculationFactoryException(String message, DivideByZeroException exception) {
            super(new String[]{message}, exception);
        }
		public CalculationFactoryException(String message, NullPointerException exception) {
            super(new String[]{message}, exception);
        }
		public CalculationFactoryException(String message, FileNotFoundException exception) {
            super(new String[]{message}, exception);
        }
		public CalculationFactoryException(String message, ParseException exception) {
            super(new String[]{message}, exception);
        }
	}
	
	public static class ValidationException extends AmortException {
		private static final long	serialVersionUID	= 1L;

		public ValidationException(String message) {
			super(new String[] { message }, null);
		}

		public ValidationException(Exception exception) {
			super(exception);
		}
		
		public ValidationException(String message, Exception exception) {
            super(new String[]{message}, exception);
        }
	}
	public static class NumberFormatException extends AmortException {
		private static final long	serialVersionUID	= 1L;

		public NumberFormatException(String message) {
			super(new String[] { message }, null);
		}

		public NumberFormatException(Exception exception) {
			super(exception);
		}
	}

	public static class ClassCastException extends AmortException {
		private static final long	serialVersionUID	= 1L;

		public ClassCastException(String message) {
			super(new String[] { message }, null);
		}

		public ClassCastException(Exception exception) {
			super(exception);
		}
	}

	protected AmortException(String as[], Exception exception) {
		super(as, exception);
	}

	protected AmortException(String exception) {
		super(new Exception(exception));
	}

	protected AmortException(String cause, SQLException exception) {
		super(cause, exception);
	}

//	protected AmortException(String userDefinedErrorCode, String message) {
//		super(userDefinedErrorCode, message);
//	}

	protected AmortException(Exception exception) {
		super(exception);
	}
	public static class FailedToParse extends AmortException {
       private static final long serialVersionUID = 1L;
		public FailedToParse(String message, Exception exception) {
            super(new String[]{message}, exception);
        }
        public FailedToParse(String message) {
            super(new String[]{message}, null);
        }
    }
	public static class FailedToCreateXML extends AmortException {
		private static final long serialVersionUID = 1L;
		public FailedToCreateXML(String message, Exception exception) {
            super(new String[]{message}, exception);
        }
        public FailedToCreateXML(String message) {
            super(new String[]{message}, null);
        }
	}
	public static class RoundFormatException extends AmortException {
		private static final long serialVersionUID = 1L;
		public RoundFormatException(String message, Exception exception) {
            super(new String[]{message}, exception);
        }
        public RoundFormatException(String message) {
            super(new String[]{message}, null);
        }
        public RoundFormatException(Exception exception) {
			super(exception);
		}
	}  
}

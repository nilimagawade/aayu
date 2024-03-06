package com.ebixcash.aayu.daycount.defaultimpl;

public class DaycountException extends RuntimeException{

	public DaycountException()
	{
		super();
	}

	public DaycountException(String message)
	{
		super(message);
	}

	public DaycountException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public DaycountException(Throwable cause)
	{
		super(cause);
	}
}

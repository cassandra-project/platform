package eu.cassandra.server.api.exceptions;

public class BadParameterException extends Exception {

	private static final long serialVersionUID = 3472776523235532223L;

	public BadParameterException(String message)
	{
		super(message);
	}

}

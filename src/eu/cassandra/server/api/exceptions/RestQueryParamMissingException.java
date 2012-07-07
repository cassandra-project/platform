package eu.cassandra.server.api.exceptions;

public class RestQueryParamMissingException extends Exception {
	
	private static final long serialVersionUID = -5573430509519387623L;

	public RestQueryParamMissingException(String message)
	{
		super(message);
	}
}

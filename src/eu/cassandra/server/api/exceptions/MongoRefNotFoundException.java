package eu.cassandra.server.api.exceptions;

public class MongoRefNotFoundException extends Exception {

	private static final long serialVersionUID = 2568354873097429442L;

	public MongoRefNotFoundException(String message)
	{
		super(message);
	}
}

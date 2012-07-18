package eu.cassandra.server.api.exceptions;

public class JSONSchemaNotValidException  extends Exception {

	private static final long serialVersionUID = 4628987626670991497L;

	public JSONSchemaNotValidException(String message)
	{
		super(message);
	}
}

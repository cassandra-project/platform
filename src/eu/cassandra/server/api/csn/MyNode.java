package eu.cassandra.server.api.csn;

public class MyNode {
	
	private String id; 
	private String name;
	public MyNode(String id, String name) {
		this.id = id;
		this.name = name;
	}
	public String toString() {
		return this.name;
	}
	
	public String getId() {
		return id;
	}
}

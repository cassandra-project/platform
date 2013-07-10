package eu.cassandra.server.mongo.csn;

public class CEdge {
	private String edge_id;
	private String node1;
	private String node2;

	public CEdge(String edge_id, String node1, String node2) {
		this.edge_id = edge_id;
		this.node1 = node1;
		this.node2 = node2;
	}

	public String getEdgeId() {
		return edge_id;
	}
	
	public String getNode1() {
		return node1;
	}

	public String getNode2() {
		return node2;
	}
}

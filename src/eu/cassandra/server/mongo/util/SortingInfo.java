package eu.cassandra.server.mongo.util;

public class SortingInfo {

	public static final int ASCENDING = 1;
	public static final int DESCENDING = -1;

	private String property;
	private int sortingDirection;

	public SortingInfo(String aProperty, int aSortingDirection) {
		this.property = aProperty;
		this.sortingDirection = aSortingDirection;
	}

	public String getProperty() {
		return property;
	}

	public int getSortingDirection() {
		return sortingDirection;
	}
}

package eu.cassandra.sim.entities;

import com.mongodb.BasicDBObject;

public abstract class Entity {
	
	protected String id;
	protected String name;
	protected String description;
	protected String type;
	protected String parentId;
	
	public abstract BasicDBObject toDBObject();
	
	public abstract String getCollection();
	
	public void setParentId(String aparent) {
		parentId = aparent;
	}
	
	public void setId(String aid) {
		id = aid;
	}
	
	public String getId() {
		return id;
	}
	
	public void setName(String aname) {
		name = aname;
	}
	
	public String getName() {
		return name;
	}
	
	public void setType(String atype) {
		type = atype;
	}
	
	public String getType() {
		return type;
	}
	
	public void setDescription(String adescription) {
		description = adescription;
	}
	
	public String getDescription() {
		return description;
	}

}

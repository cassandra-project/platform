/*   
   Copyright 2011-2013 The Cassandra Consortium (cassandra-fp7.eu)


   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
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

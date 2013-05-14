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

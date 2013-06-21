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
package eu.cassandra.sim;

public class Period {
	
	private String from;
		
	private String to;
	
	private double price;
		
	public Period(String afrom, String ato, double aprice) {
		from = afrom;
		to = ato;
		price = aprice;
	}
		
	public String getFrom() { return from; }
		
	public String getTo() { return to; }
	
	public void setTo(String ato) { to = ato; }
	
	public double getPrice() { return price; }

}

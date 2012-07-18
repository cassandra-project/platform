/**
 * Copyright (C) 2010 Nicolas Vahlas <nico@vahlas.eu>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.vahlas.json.schema;

/**
 * Translates <code>JsonNode</code> type into types as defined 
 * in the paragraph 5.1 of the specification<br/>
 * <br/>
 * Allowed node types according to the specification are:<br/> 
 * &nbsp;string, number, integer, boolean, object, array, null<br/>
 * <br/>
 * Two types were added as a help for the implementation:<br/>
 * &nbsp;unknown and union<br/>
 * <br/>
 * <i>Specification document:</i><br/> 
 * <a href="http://tools.ietf.org/pdf/draft-zyp-json-schema-02.txt">http://tools.ietf.org/pdf/draft-zyp-json-schema-02.txt</a>
 * 
 * @author nico@vahlas.eu
 */
public enum TYPE {
	/**
	 * Type "object" as defined by the paragraph 5.1 of the JSON Schema specification
	 */
	OBJECT("object"), 
	/**
	 * Type "array" as defined by the paragraph 5.1 of the JSON Schema specification
	 */
	ARRAY("array"), 
	/**
	 * Type "string" as defined by the paragraph 5.1 of the JSON Schema specification
	 */
	STRING("string"), 
	/**
	 * Type "number" as defined by the paragraph 5.1 of the JSON Schema specification
	 */
	NUMBER("number"), 
	/**
	 * Type "integer" as defined by the paragraph 5.1 of the JSON Schema specification
	 */
	INTEGER("integer"), 
	/**
	 * Type "boolean" as defined by the paragraph 5.1 of the JSON Schema specification
	 */
	BOOLEAN("boolean"), 
	/**
	 * Type "null" as defined by the paragraph 5.1 of the JSON Schema specification
	 */
	NULL("null"), 
	/**
	 * Type "any" as defined by the paragraph 5.1 of the JSON Schema specification
	 */
	ANY("any"), 
	
	/**
	 * Additional type for "unknown" types according to the paragraph 5.1 of the JSON Schema specification
	 */
	UNKNOWN("unknown"),
	/**
	 * Additional type for "union type definitions" as described in the paragraph 5.1 of the JSON Schema specification
	 */
	UNION("union");
	
	private String type;
	
	/**
	 * Constructed with a string as a parameter in order to allow 
	 * pretty printing in the error messages
	 * 
	 * @param typeStr the name of the type
	 */
	private TYPE(String typeStr) {
		type = typeStr;
	}
	
	/**
	 * Returns the name of the type as defined in the paragraph 5.1 
	 * of the specification
	 * 
	 * @return the name of the type
	 */
	public String toString() { return type; }
	
}

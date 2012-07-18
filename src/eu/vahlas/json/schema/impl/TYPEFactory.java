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

package eu.vahlas.json.schema.impl;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;

import eu.vahlas.json.schema.JSONSchemaException;
import eu.vahlas.json.schema.TYPE;

public class TYPEFactory {
	/**
	 * Translates the "type" property of the <code>org.codehaus.jackson.JsonNode<code> 
	 * passed into one of the types defined in the paragraph 5.1 of the JSON schema specification.
	 * 
	 * @param the node containing the "type" property to translate.
	 * @return the type as defined by the JSON Schema specification
	 */
	public static TYPE getType(JsonNode node) {
		JsonNode typeNode = node.get("type");
		if ( node.isTextual() )
			typeNode = node;
		if ( typeNode == null ) {
			throw new JSONSchemaException("Invalid schema provided: property type is not defined!");
		}
		
		//Single Type Definition
		if ( typeNode.isTextual() ) {
			String type = typeNode.getTextValue();
			if ( "object".equals(type) ) {
				return TYPE.OBJECT;
			}
			if ( "array".equals(type) ) {
				return TYPE.ARRAY;
			}
			if ( "string".equals(type) ) {
				return TYPE.STRING;
			}
			if ( "number".equals(type) ) {
				return TYPE.NUMBER;
			}
			if ( "integer".equals(type) ) {
				return TYPE.INTEGER;
			}
			if ( "boolean".equals(type) ) {
				return TYPE.BOOLEAN;
			}
			if ( "any".equals(type) ) {
				return TYPE.ANY;
			}
			if ( "null".equals(type) ) {
				return TYPE.NULL;
			}
		}
		
		//Union Type Definition
		if ( typeNode.isArray() ) {
			return TYPE.UNION;
		}			
		
		return TYPE.UNKNOWN;
	}

	/**
	 * Translates the "type" property of the <code>org.codehaus.jackson.JsonNode<code> 
	 * passed into an array of types as defined in the paragraph 5.1 of the JSON schema specification.<br/>
	 * <br/>
	 * This is supposed to be used when the <code>getType(JsonNode node)</code> method
	 * returns TYPE.UNION.<br/>
	 * 
	 * @param the node containing the "type" property to translate.
	 * @return the type as defined by the JSON Schema specification
	 */
	public static TYPE[] getUnionType(JsonNode node) {
		JsonNode typeNode = node.get("type");
		if ( typeNode == null )
			return new TYPE[] {TYPE.UNKNOWN};
		
		if ( typeNode.isTextual() )
			return new TYPE[] { getType(node) };
		
		if ( typeNode.isArray() ) {
			List<TYPE> unionTypes = new ArrayList<TYPE>();
			for (JsonNode n : typeNode ) {
				TYPE t = getType(n);
				if ( t == TYPE.UNION )
					throw new RuntimeException("Recursive union types are not allowed in JSON Schema.");
				unionTypes.add(t);
			}
			return unionTypes.toArray(new TYPE[] {});
		}
		return new TYPE[] {TYPE.UNKNOWN};
	}
	
	/**
	 * Translates the type of a <code>org.codehaus.jackson.JsonNode</code> passed
	 * into a type as defined in the paragraph 5.1 of the JSON Schema specification.<br/>
	 * <br/>
	 * This method returns the "real" type of the node passed.
	 * 
	 * 
	 * @return the JSON Schema type of this node
	 */
	public static TYPE getNodeType(JsonNode node) {
		// object or array
	    if ( node.isContainerNode() ) {
	    	if ( node.isObject() )
	    		return TYPE.OBJECT;
	    	if ( node.isArray() )
	    		return TYPE.ARRAY;
	    	return TYPE.UNKNOWN;
	    }
	    
	    // string, number, integer, boolean, null
	    if ( node.isValueNode() ) {
	    	if ( node.isTextual() )
	    		return TYPE.STRING;
	    	if ( node.isIntegralNumber() )
	    		return TYPE.INTEGER;
	    	if ( node.isNumber() )
	    		return TYPE.NUMBER;
	    	if ( node.isBoolean() )
	    		return TYPE.BOOLEAN;
	    	if ( node.isNull() )
	    		return TYPE.NULL;
	    	return TYPE.UNKNOWN;
	    } 
	    
	    // Unknown  
		return TYPE.UNKNOWN;
	}
	
}

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

package eu.vahlas.json.schema.impl.validators;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.vahlas.json.schema.JSONSchemaException;
import eu.vahlas.json.schema.TYPE;
import eu.vahlas.json.schema.impl.JSONValidator;
import eu.vahlas.json.schema.impl.JacksonSchema;
import eu.vahlas.json.schema.impl.TYPEFactory;

public class UnionTypeValidator implements JSONValidator, Serializable {

	private static final long serialVersionUID = -7954619972059351060L;
	private static final Logger LOG = LoggerFactory.getLogger(UnionTypeValidator.class);
	
	public static final String PROPERTY = "type";
	
	protected List<JSONValidator> schemas;
	private String error;
	
	public UnionTypeValidator(JsonNode typeNode) {
		schemas = new ArrayList<JSONValidator>();
		String sep = "";
		error = "[";
		
		if ( !typeNode.isArray() )
			throw new  JSONSchemaException("Expected array for type property on Union Type Definition.");
		
		for ( JsonNode n : typeNode ) {
			TYPE t = TYPEFactory.getType(n);
			error += sep + t;
			sep = ", ";
			
			if ( n.isObject() )
				schemas.add( new JacksonSchema(n) );
			else
				schemas.add( new TypeValidator(n) );
		}
		
		error += "]";
	}
	
	@Override
	public List<String> validate(JsonNode node, String at) {
		LOG.debug("validate( " + node + ", " + at + ")");
		return validate(node, null, at);
	}
	
	@Override
	public List<String> validate(JsonNode node, JsonNode parent, String at) {
		LOG.debug("validate( " + node + ", " + parent + ", " + at + ")");
		TYPE nodeType = TYPEFactory.getNodeType(node);
		
		List<String> _return = new ArrayList<String>();
		boolean valid = false;
		
		for ( JSONValidator schema : schemas ) {
			List<String> errors = schema.validate(node, at);
			if ( errors == null || errors.size() == 0 ) {
				valid = true;
				break;
			}
		}
		
		if ( !valid ) {
			_return.add( at + ": " + nodeType + " found, but " + error + " is required" );
		}
		
		return _return;
	}

}

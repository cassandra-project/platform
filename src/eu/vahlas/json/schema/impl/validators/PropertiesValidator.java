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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.vahlas.json.schema.impl.JSONValidator;
import eu.vahlas.json.schema.impl.JacksonSchema;

public class PropertiesValidator implements JSONValidator, Serializable {

	private static final long serialVersionUID = -7054176202856839164L;
	private static final Logger LOG = LoggerFactory.getLogger(PropertiesValidator.class);
	
	public static final String PROPERTY = "properties";
	
	protected Map<String, JacksonSchema> schemas;
	
	public PropertiesValidator(JsonNode propertiesNode, ObjectMapper mapper) {
		schemas = new HashMap<String, JacksonSchema>();	
		for ( Iterator<String> it = propertiesNode.getFieldNames(); it.hasNext(); ) {
			String pname = it.next();
			schemas.put(pname, new JacksonSchema( mapper, propertiesNode.get( pname ) ));
		}
	}
	
	@Override
	public List<String> validate(JsonNode node, String at) {
		LOG.debug("validate( " + node + ", " + at + ")");
		return validate(node, null ,at);
	}

	@Override
	public List<String> validate(JsonNode node, JsonNode parent, String at) {
		LOG.debug("validate( " + node + ", " + parent + ", " + at + ")");
		List<String> errors = new ArrayList<String>();
		
		for ( String key : schemas.keySet() ) {
			JacksonSchema propertySchema = schemas.get(key);
			JsonNode propertyNode = node.get(key);
			
			if ( propertyNode != null ) {
				errors.addAll( propertySchema.validate(propertyNode, node, at + "." + key) );
			} else {
				if ( ! propertySchema.isOptional() )
					errors.add( at + "." + key + ": is missing and it is not optional" );
			}
		}
		
		return errors;
	}

}

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

import eu.vahlas.json.schema.impl.JSONValidator;
import eu.vahlas.json.schema.impl.JacksonSchema;

/**
 * Implements "requires" validation on all types of objects as defined in
 * the paragraph 5.6 of the JSON Schema specification.
 * 
 */
public class RequiresValidator implements JSONValidator, Serializable {

	private static final long serialVersionUID = 2662078423654448119L;
	private static final Logger LOG = LoggerFactory.getLogger(RequiresValidator.class);

	public static final String PROPERTY = "requires";
	
	private static final int MODE_NONE = 0;
	private static final int MODE_STR  = 1;
	private static final int MODE_OBJ  = 2;
	
	private String requiredPropertyName;
	private JacksonSchema schema;
	private int mode;
	
	public RequiresValidator(JsonNode requiresNode) {
		schema = null;
		mode = MODE_NONE;
		
		if ( requiresNode != null ) {
			if ( requiresNode.isTextual() ) {
				requiredPropertyName = requiresNode.getTextValue();
				mode = MODE_STR;
			}
			
			if ( requiresNode.isObject() ) {
				schema = new JacksonSchema(requiresNode);
				mode = MODE_OBJ;
			}
		}
	}
	
	@Override
	public List<String> validate(JsonNode node, String at) {
		LOG.debug("validate( " + node + ", " + at + ")");
		return validate(node, null, at);
	}
	
	@Override
	public List<String> validate(JsonNode node, JsonNode parent, String at) {
		LOG.debug("validate( " + node + ", " + parent + ", " + at + ")");
		List<String> errors = new ArrayList<String>();
		
		if (parent == null)
			return errors;
		
		if ( mode == MODE_STR ) {
			JsonNode requiredProperty = parent.get(requiredPropertyName);
			if ( requiredProperty == null || requiredProperty.isNull() ) {
				errors.add(at + ": the presence of this property requires that " + requiredPropertyName + " also be present");
			}
		}
		
		if ( mode == MODE_OBJ ) {
			errors.addAll(schema.validate(node, at));
		}
		
		return errors;
		
	}

}

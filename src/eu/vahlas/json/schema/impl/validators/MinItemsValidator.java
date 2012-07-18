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

/**
 * Implements "minItems" validation on array nodes as described in 
 * the paragraph 5.11 of the JSON Schema specification.
 */
public class MinItemsValidator implements JSONValidator, Serializable {

	private static final long serialVersionUID = 5843869888459032277L;
	private static final Logger LOG = LoggerFactory.getLogger(MinItemsValidator.class);

	public static final String PROPERTY = "minItems";
	
	private int min = 0;
	
	public MinItemsValidator(JsonNode minItemsNode) {
		if ( minItemsNode.isIntegralNumber() ) {
			min = minItemsNode.getIntValue();
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
		
		if ( node.isArray() ) {
			if ( node.size() < min ) {
				errors.add(at + ": there must be a minimum of " + min + " items in the array");
			}
		}
		
		return errors;
	}

}

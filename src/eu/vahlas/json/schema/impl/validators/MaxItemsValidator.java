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
 * Implements "maxItems" validation on array nodes as described in 
 * the paragraph 5.12 of the JSON Schema specification.
 */
public class MaxItemsValidator implements JSONValidator, Serializable {

	private static final long serialVersionUID = -2811868595159447028L;
	private static final Logger LOG = LoggerFactory.getLogger(MinItemsValidator.class);

	public static final String PROPERTY = "maxItems";
	
	private int max = 0;
	
	public MaxItemsValidator(JsonNode maxItemsNode) {
		if ( maxItemsNode.isIntegralNumber() ) {
			max = maxItemsNode.getIntValue();
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
			if ( node.size() > max ) {
				errors.add(at + ": there must be a maximum of " + max + " items in the array");
			}
		}
		
		return errors;
	}

}

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.vahlas.json.schema.impl.JSONValidator;

/**
 * Implements "uniqueItems" validation on array nodes as described in 
 * the paragraph 5.13 of the JSON Schema specification.<br/>
 * <br/>
 * This implementation relies on the <code>equals</code> method implemented
 * on the <code>JsonNode</code> object.
 */
public class UniqueItemsValidator implements JSONValidator, Serializable {

	private static final long serialVersionUID = -2453061999914008143L;
	private static final Logger LOG = LoggerFactory.getLogger(UniqueItemsValidator.class);
	
	public static final String PROPERTY = "uniqueItems";
	
	protected boolean unique = false;
	
	public UniqueItemsValidator(JsonNode uniqueItemsNode) {
		if ( uniqueItemsNode.isBoolean() ) {
			unique = uniqueItemsNode.getBooleanValue();
		}
	}
	
	@Override
	public List<String> validate(JsonNode node, String at) {
		LOG.debug("validate( " + node + ", " + at + ")");
		return validate(node, null, at);
	}

	@Override
	public List<String> validate(JsonNode node, JsonNode parent, String at) {
		List<String> errors = new ArrayList<String>();
		
		if ( unique ) {
			Set<JsonNode> set = new HashSet<JsonNode>();
			for ( JsonNode n : node ) {
				set.add(n);
			}
			
			if ( set.size() < node.size()) {
				errors.add( at + ": the items in the array must be unique");
			}
		}
		
		return errors;
	}

}

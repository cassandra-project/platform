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

import eu.vahlas.json.schema.TYPE;
import eu.vahlas.json.schema.impl.JSONValidator;
import eu.vahlas.json.schema.impl.TYPEFactory;

public class DisallowValidator implements JSONValidator, Serializable {

	private static final long serialVersionUID = -7563984149808796291L;
	private static final Logger LOG = LoggerFactory.getLogger(DisallowValidator.class);
	
	public static final String PROPERTY = "disallow";
	
	protected TYPE[] disallowedTypes;
	
	public DisallowValidator(JsonNode disallowNode) {
		if ( disallowNode.isObject() || disallowNode.isTextual() ) {
			disallowedTypes = new TYPE[] {TYPEFactory.getType(disallowNode)};
		}
		
		if ( disallowNode.isArray() ) {
			int nb = disallowNode.size();
			disallowedTypes = new TYPE[nb];
			for ( int i=0; i<nb; i++ ) {
				disallowedTypes[i] = TYPEFactory.getType(disallowNode.get(i));
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
		
		TYPE nodeType = TYPEFactory.getNodeType(node);
		for ( TYPE t : disallowedTypes ) {
			if ( t == nodeType ) {
				errors.add(at + ": " + t + " is not a type allowed on this property");
				break;
			}
			
			// integer is a number ... 
			if ( t == TYPE.NUMBER && nodeType == TYPE.INTEGER ) {
				errors.add(at + ": " + t + " is not an allowed type for this property");
				break;
			}
		}
		
		return errors;
	}

}

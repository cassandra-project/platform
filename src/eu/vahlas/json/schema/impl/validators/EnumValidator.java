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
 * Implements "enum" validation on all types of objects as defined in
 * the paragraph 5.17 of the JSON Schema specification.<br/>
 * <br/>
 * This implementation relies on the <code>equals</code> method implemented
 * on the <code>JsonNode</code> object.
 * 
 */
public class EnumValidator implements JSONValidator, Serializable {

	private static final long serialVersionUID = -7163667264068815707L;
	private static final Logger LOG = LoggerFactory.getLogger(EnumValidator.class);
	
	public static final String PROPERTY = "enum";
	
	protected List<JsonNode> nodes;
	protected String error;
	
	public EnumValidator(JsonNode enumNode) {
		nodes = new ArrayList<JsonNode>();
		error = "[none]";
		
		if ( enumNode != null && enumNode.isArray() ) {
			error = "[";
			int i = 0;
			for ( JsonNode n : enumNode) {
				nodes.add(n);
				
				String v = n.getValueAsText();
				error = error + (i==0?"":", ") + v;
				i++;
				
			}
			error = error + "]";
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
		
		if ( !nodes.contains(node) ) {
			errors.add(at + ": does not have a value in the enumeration " + error);
		}
		
		return errors;
	}

}

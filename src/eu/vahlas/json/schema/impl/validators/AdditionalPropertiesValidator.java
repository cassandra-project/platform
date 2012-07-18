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
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.vahlas.json.schema.impl.JacksonSchema;
import eu.vahlas.json.schema.impl.JSONValidator;

public class AdditionalPropertiesValidator implements JSONValidator, Serializable {

	private static final long serialVersionUID = 7868457793256399879L;

	public static final String PROPERTY = "additionalProperties";
	private static final Logger LOG = LoggerFactory.getLogger(AdditionalPropertiesValidator.class);
	
	private boolean allowAdditionalProperties;
	private JacksonSchema additionalPropertiesSchema;
	private List<String> allowedProperties;
	
	public AdditionalPropertiesValidator(JsonNode propertiesNode, JsonNode additionalPropertiesNode) {
		allowAdditionalProperties = false;
		if ( additionalPropertiesNode.isBoolean() ) {
			allowAdditionalProperties = additionalPropertiesNode.getBooleanValue();
		}
		if ( additionalPropertiesNode.isObject() ) {
			allowAdditionalProperties = true;
			additionalPropertiesSchema = new JacksonSchema(additionalPropertiesNode);
			
		}
		
		allowedProperties = new ArrayList<String>();
		for (Iterator<String> it = propertiesNode.getFieldNames(); it.hasNext(); ) {
			allowedProperties.add(it.next());
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
		for ( Iterator<String> it = node.getFieldNames(); it.hasNext(); ) {
			String pname = it.next();
			if ( !allowedProperties.contains(pname) ) {
				if ( !allowAdditionalProperties ) {
					errors.add(at + "." + pname + ": is not defined in the schema and the schema does not allow additional properties");
				} else {
					if (additionalPropertiesSchema != null ) {
						errors.addAll(
								additionalPropertiesSchema.validate(node.get(pname), parent, at + "." + pname)
								);
					}
				}
			}
		}
		return errors;
	}
	

}

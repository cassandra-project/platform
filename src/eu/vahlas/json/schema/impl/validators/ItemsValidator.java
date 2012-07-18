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

import eu.vahlas.json.schema.impl.JacksonSchema;
import eu.vahlas.json.schema.impl.JSONValidator;

public class ItemsValidator implements JSONValidator, Serializable {

	private static final long serialVersionUID = -5023382376825229965L;
	private static final Logger LOG = LoggerFactory.getLogger(ItemsValidator.class);

	public static final String PROPERTY = "items";
	
	protected JacksonSchema schema;
	protected List<JacksonSchema> tupleSchema;
	
	public ItemsValidator(JsonNode itemSchema) {
		if ( itemSchema.isObject() ) {
			schema = new JacksonSchema(itemSchema);
		}
		
		if ( itemSchema.isArray() ) {
			tupleSchema = new ArrayList<JacksonSchema>();
			for ( JsonNode s : itemSchema ) {
				tupleSchema.add( new JacksonSchema(s) );
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
		
		int i = 0;
		for ( JsonNode n : node ) {
			if ( schema != null ) {
				errors.addAll( schema.validate(n, node, at + "[" + i + "]") );
			}
			
			if ( tupleSchema != null ) {
				if ( i >= tupleSchema.size() ) {
					errors.add(at + "[" + i + "]: no validator found at this index");
				} else {
					errors.addAll( tupleSchema.get(i).validate(n, node, at + "[" + i + "]") );
				}
			}
			
			i++;
		}
		return errors;
	}

}

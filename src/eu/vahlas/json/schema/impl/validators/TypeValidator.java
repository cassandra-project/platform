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

public class TypeValidator implements JSONValidator, Serializable {

	private static final long serialVersionUID = -637068450453946655L;
	private static final Logger LOG = LoggerFactory.getLogger(TypeValidator.class);
	
	public static final String PROPERTY = "type";
	
	private TYPE schemaType;
	private UnionTypeValidator unionTypeValidator;
	
	public TypeValidator(JsonNode schemaNode) {
		schemaType = TYPEFactory.getType(schemaNode);
		
		if ( schemaType == TYPE.UNION ) {
			unionTypeValidator = new UnionTypeValidator(schemaNode.get(PROPERTY));
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
		
		if ( schemaType == TYPE.UNION) {
			errors.addAll(unionTypeValidator.validate(node, parent, at));
			return errors;
		}
		
		TYPE nodeType = TYPEFactory.getNodeType(node);
		if ( nodeType != schemaType ) {
			if ( schemaType == TYPE.ANY )
				return errors;
			
			if ( schemaType == TYPE.NUMBER && nodeType == TYPE.INTEGER )
				return errors;
			
			errors.add(at + ": " + nodeType + " found, " + schemaType + " expected");
		}
		
		return errors;
	}

}

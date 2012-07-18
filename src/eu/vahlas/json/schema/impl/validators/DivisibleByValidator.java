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

public class DivisibleByValidator implements JSONValidator, Serializable {

	private static final long serialVersionUID = 8255703049783662141L;
	private static final Logger LOG = LoggerFactory.getLogger(DivisibleByValidator.class);
	
	public static final String PROPERTY = "divisibleBy";

	public long divisor = 0;
	
	public DivisibleByValidator(JsonNode divisibleByNode) {
		if ( divisibleByNode.isIntegralNumber() )
			divisor = divisibleByNode.getLongValue();
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
		
		if ( node.isIntegralNumber() ) {
			long nodeValue = node.getLongValue();
			if ( divisor != 0 && nodeValue % divisor != 0 ) {
				errors.add(at + ": must be divisible by " + divisor);
			}
		}
		
		return errors;
	}

}

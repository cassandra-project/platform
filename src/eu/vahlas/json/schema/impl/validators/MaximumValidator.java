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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.vahlas.json.schema.impl.JSONValidator;

public class MaximumValidator implements JSONValidator, Serializable {

	private static final long serialVersionUID = -6065577788738619222L;
	private static final Logger LOG = LoggerFactory.getLogger(MaximumValidator.class);

	public static final String PROPERTY = "maximum";
	public static final String PROPERTY_CANEQUAL = "maximumCanEqual";
	
	protected Number maximum;
	protected JsonParser.NumberType numberType;
	protected boolean canEqual = true;
	
	public MaximumValidator(JsonNode maximumNode, JsonNode maximumCanEqualNode) {
		if (maximumNode != null && maximumNode.isNumber()) {
			maximum = maximumNode.getNumberValue();
			numberType = maximumNode.getNumberType();
		}
		
		if (maximumCanEqualNode != null && maximumCanEqualNode.isBoolean()) {
			canEqual = maximumCanEqualNode.getBooleanValue();
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
		
		if ( maximum == null ) // should not happen in a well-written JSON Schema
			return errors;
		
		JsonParser.NumberType numberType = node.getNumberType();
		boolean greaterThanMax = false;
		switch(numberType) {
			case BIG_DECIMAL:
				if ( node.getDecimalValue()
		           .compareTo( (BigDecimal) maximum) > 0 ) {
					greaterThanMax = true;
				}
				break;
			case BIG_INTEGER:
				if ( node.getBigIntegerValue()
				   .compareTo( (BigInteger) maximum) > 0 ) {
					greaterThanMax = true;
				}
				break;
			case DOUBLE:
				if ( node.getDoubleValue() > maximum.doubleValue() ) {
					greaterThanMax = true;
				}
				break;
			case FLOAT:
				if ( node.getDoubleValue() > maximum.doubleValue() ) {
					greaterThanMax = true;
				}
				break;
			case INT:
				if ( node.getIntValue() > maximum.intValue() ) {
					greaterThanMax = true;
				}
				break;
			case LONG:
				if ( node.getLongValue() > maximum.longValue() ) {
					greaterThanMax = true;
				}
				break;
		}
		
		if ( greaterThanMax || (
				!canEqual 
				&& node.getNumberValue().equals(maximum)) ) {
			errors.add(at + ": must have a maximum value of " + maximum);
		}
		return errors;
	}


}

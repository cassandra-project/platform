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

public class MinimumValidator implements JSONValidator, Serializable {

	private static final long serialVersionUID = -3480112301401177525L;
	private static final Logger LOG = LoggerFactory.getLogger(MinimumValidator.class);
	
	public static final String PROPERTY = "minimum";
	public static final String PROPERTY_CANEQUAL = "minimumCanEqual";
	
	protected Number minimum;
	protected JsonParser.NumberType numberType;
	protected boolean canEqual = true;
	
	public MinimumValidator(JsonNode minimumNode, JsonNode minimumCanEqualNode) {
		if (minimumNode != null && minimumNode.isNumber()) {
			minimum = minimumNode.getNumberValue();
			numberType = minimumNode.getNumberType();
		}
		
		if (minimumCanEqualNode != null && minimumCanEqualNode.isBoolean()) {
			canEqual = minimumCanEqualNode.getBooleanValue();
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
		
		if ( minimum == null ) // should not happen in a well-written JSON Schema
			return errors;
		
		JsonParser.NumberType numberType = node.getNumberType();
		boolean smallerThanMin = false;
		switch(numberType) {
			case BIG_DECIMAL:
				if ( node.getDecimalValue()
		           .compareTo( (BigDecimal) minimum) < 0 ) {
					smallerThanMin = true;
				}
				break;
			case BIG_INTEGER:
				if ( node.getBigIntegerValue()
				   .compareTo( (BigInteger) minimum) < 0 ) {
					smallerThanMin = true;
				}
				break;
			case DOUBLE:
				if ( node.getDoubleValue() < minimum.doubleValue() ) {
					smallerThanMin = true;
				}
				break;
			case FLOAT:
				if ( node.getDoubleValue() < minimum.doubleValue() ) {
					smallerThanMin = true;
				}
				break;
			case INT:
				if ( node.getIntValue() < minimum.intValue() ) {
					smallerThanMin = true;
				}
				break;
			case LONG:
				if ( node.getLongValue() < minimum.longValue() ) {
					smallerThanMin = true;
				}
				break;
		}
		
		if ( smallerThanMin || (
				!canEqual 
				&& node.getNumberValue().equals(minimum)) ) {
			errors.add(at + ": must have a minimum value of " + minimum);
		}
		return errors;
	}

}

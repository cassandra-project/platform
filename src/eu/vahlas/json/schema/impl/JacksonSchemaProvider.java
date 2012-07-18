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

package eu.vahlas.json.schema.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.net.URL;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.vahlas.json.schema.JSONSchema;
import eu.vahlas.json.schema.JSONSchemaException;
import eu.vahlas.json.schema.JSONSchemaProvider;

public class JacksonSchemaProvider implements JSONSchemaProvider, Serializable {

	private static final long serialVersionUID = 7600020194154713323L;

	private static final Logger LOG = LoggerFactory.getLogger(JacksonSchemaProvider.class);

	protected ObjectMapper mapper;
	
	public JacksonSchemaProvider(ObjectMapper mapper) {
		this.mapper = mapper;
	}
	
	@Override
	public JSONSchema getSchema(String schema) {
		try {
			JsonNode schemaNode = mapper.readTree(schema);
			return new JacksonSchema(mapper, schemaNode);
		} catch (IOException ioe) {
			LOG.error("Failed to load json schema!", ioe);
			throw new JSONSchemaException(ioe);
		} 
	}

	@Override
	public JSONSchema getSchema(InputStream schemaStream) {
		try {
			JsonNode schemaNode = mapper.readTree(schemaStream);
			return new JacksonSchema(mapper, schemaNode);
		} catch (IOException ioe) {
			LOG.error("Failed to load json schema!", ioe);
			throw new JSONSchemaException(ioe);
		}
	}

	@Override
	public JSONSchema getSchema(Reader schemaReader) {
		try {
			JsonNode schemaNode = mapper.readTree(schemaReader);
			return new JacksonSchema(mapper, schemaNode);
		} catch (IOException ioe) {
			LOG.error("Failed to load json schema!", ioe);
			throw new JSONSchemaException(ioe);
		}
	}

	@Override
	public JSONSchema getSchema(URL schemaURL) {
		try {
			JsonNode schemaNode = mapper.readTree(schemaURL.openStream());
			return new JacksonSchema(mapper, schemaNode);
		} catch (IOException ioe) {
			LOG.error("Failed to load json schema!", ioe);
			throw new JSONSchemaException(ioe);
		}
	}
}
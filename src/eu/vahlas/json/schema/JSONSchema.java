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

package eu.vahlas.json.schema;

import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.List;

public interface JSONSchema {
	List<String> validate(String json);
	List<String> validate(InputStream jsonStream);
	List<String> validate(Reader jsonReader);
	List<String> validate(URL jsonURL);
}

/*
 * Copyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.epigraph.ideaplugin.schema.index;

import com.intellij.psi.stubs.StubIndexKey;
import ws.epigraph.schema.parser.psi.SchemaNamespaceDecl;
import ws.epigraph.schema.parser.psi.SchemaSupplementDef;
import ws.epigraph.schema.parser.psi.SchemaTypeDef;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaStubIndexKeys {
  public static final StubIndexKey<String, SchemaTypeDef> TYPE_SHORT_NAMES = StubIndexKey.createIndexKey("epigraph.schema.type.shortname");
  public static final StubIndexKey<String, SchemaTypeDef> TYPE_FQN = StubIndexKey.createIndexKey("epigraph.schema.type.fqn");
  public static final StubIndexKey<String, SchemaTypeDef> TYPES_BY_NAMESPACE = StubIndexKey.createIndexKey("epigraph.schema.type.by_namespace");

  // have similar indices for 'extends' and 'supplements' on records and vars?
  public static final StubIndexKey<String, SchemaSupplementDef> SUPPLEMENTS_BY_SOURCE = StubIndexKey.createIndexKey("epigraph.schema.supplement.by_source");
  public static final StubIndexKey<String, SchemaSupplementDef> SUPPLEMENTS_BY_SUPPLEMENTED = StubIndexKey.createIndexKey("epigraph.schema.supplement.by_supplemented");

  public static final StubIndexKey<String, SchemaNamespaceDecl> NAMESPACE_BY_NAME = StubIndexKey.createIndexKey("epigraph.schema.namespace.by_name");
}

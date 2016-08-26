package com.sumologic.epigraph.ideaplugin.schema.index;

import com.intellij.psi.stubs.StubIndexKey;
import io.epigraph.lang.parser.psi.SchemaNamespaceDecl;
import io.epigraph.lang.parser.psi.SchemaSupplementDef;
import io.epigraph.lang.parser.psi.SchemaTypeDef;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
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

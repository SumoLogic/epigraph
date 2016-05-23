package com.sumologic.epigraph.ideaplugin.schema.index;

import com.intellij.psi.stubs.StubIndexKey;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaTypeDef;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaStubIndexKeys {
  public static final StubIndexKey<String, SchemaTypeDef> TYPE_SHORT_NAMES = StubIndexKey.createIndexKey("epigraph.schema.type.shortname");
  public static final StubIndexKey<String, SchemaTypeDef> TYPE_FQN = StubIndexKey.createIndexKey("epigraph.schema.type.fqn");
  public static final StubIndexKey<String, SchemaTypeDef> TYPES_BY_NAMESPACE = StubIndexKey.createIndexKey("epigraph.schema.type.by_namespace");
}

package com.sumologic.epigraph.ideaplugin.schema.psi.stubs;

import com.intellij.psi.tree.IStubFileElementType;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public interface SchemaStubElementTypes {
  IStubFileElementType SCHEMA_FILE = new SchemaFileElementType();
}

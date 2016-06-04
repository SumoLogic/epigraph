package com.sumologic.epigraph.ideaplugin.schema.psi.stubs;

import com.intellij.psi.tree.IFileElementType;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public interface SchemaStubElementTypes {
  IFileElementType SCHEMA_FILE = new SchemaFileElementType();
}

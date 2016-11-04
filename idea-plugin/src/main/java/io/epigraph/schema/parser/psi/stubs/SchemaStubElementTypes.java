package io.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.tree.IFileElementType;

/**
 * @author <a href="mailto:konstantin.sobolev.com">Konstantin Sobolev</a>
 */
public interface SchemaStubElementTypes {
  IFileElementType SCHEMA_FILE = new SchemaFileElementType();
}

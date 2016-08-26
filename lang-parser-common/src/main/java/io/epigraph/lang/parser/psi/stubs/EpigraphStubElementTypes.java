package io.epigraph.lang.parser.psi.stubs;

import com.intellij.psi.tree.IFileElementType;
import io.epigraph.lang.idl.parser.psi.stubs.IdlFileElementType;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public interface EpigraphStubElementTypes {
  IFileElementType SCHEMA_FILE = new SchemaFileElementType();
  IFileElementType IDL_FILE = new IdlFileElementType();
}

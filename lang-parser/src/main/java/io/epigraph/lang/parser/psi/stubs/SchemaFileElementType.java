package io.epigraph.lang.parser.psi.stubs;

import com.intellij.psi.tree.IFileElementType;
import io.epigraph.lang.schema.SchemaLanguage;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaFileElementType extends IFileElementType {
  public SchemaFileElementType() {
    super("epigraph_schema.FILE", SchemaLanguage.INSTANCE);
  }
}

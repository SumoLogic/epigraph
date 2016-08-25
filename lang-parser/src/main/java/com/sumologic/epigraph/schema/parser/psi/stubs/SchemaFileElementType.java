package com.sumologic.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.tree.IFileElementType;
import io.epigraph.lang.EpigraphLanguage;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaFileElementType extends IFileElementType {
  public SchemaFileElementType() {
    super("epigraph_schema.FILE", EpigraphLanguage.INSTANCE);
  }
}

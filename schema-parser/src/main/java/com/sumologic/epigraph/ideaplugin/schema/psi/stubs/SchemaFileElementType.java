package com.sumologic.epigraph.ideaplugin.schema.psi.stubs;

import com.intellij.psi.tree.IFileElementType;
import com.sumologic.epigraph.ideaplugin.schema.SchemaLanguage;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaFileElementType extends IFileElementType {
  public SchemaFileElementType() {
    super("epigraph_schema.FILE", SchemaLanguage.INSTANCE);
  }
}

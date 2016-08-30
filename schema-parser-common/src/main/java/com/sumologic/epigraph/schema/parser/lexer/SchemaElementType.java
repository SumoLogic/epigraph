package com.sumologic.epigraph.schema.parser.lexer;

import com.intellij.psi.tree.IElementType;
import com.sumologic.epigraph.schema.parser.SchemaLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaElementType extends IElementType {
  public SchemaElementType(@NotNull @NonNls String debugName) {
    super(debugName, SchemaLanguage.INSTANCE);
  }
}

package io.epigraph.lang.schema.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaParserW extends SchemaParserAbs {
  @Override
  protected boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return parseSchema(b, l);
  }
}

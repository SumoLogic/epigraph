package io.epigraph.lang.schema.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaParserAbs extends SchemaParser {
  protected boolean parseSchema(PsiBuilder b, int l) {
    return schemaRoot(b, l);
  }

  protected boolean parseIdl(PsiBuilder b, int l) {
    return idlRoot(b, l);
  }
}

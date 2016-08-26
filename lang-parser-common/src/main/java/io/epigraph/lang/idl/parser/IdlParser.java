package io.epigraph.lang.idl.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import io.epigraph.lang.parser.SchemaParserAbs;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class IdlParser extends SchemaParserAbs {
  @Override
  protected boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return parseIdl(b, l);
  }
}

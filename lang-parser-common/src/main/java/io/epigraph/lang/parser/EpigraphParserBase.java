package io.epigraph.lang.parser;

import com.intellij.lang.PsiBuilder;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class EpigraphParserBase extends EpigraphParser {
  protected boolean parseSchema(PsiBuilder b, int l) {
    return schemaRoot(b, l);
  }

  protected boolean parseIdl(PsiBuilder b, int l) {
    return idlRoot(b, l);
  }
}

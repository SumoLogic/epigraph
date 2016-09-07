package io.epigraph.idl.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

/**
 * Customizable parser for a subset of IDL grammar
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class IdlSubParser extends IdlParser {
  @NotNull
  private final IElementType entryElementType;

  public IdlSubParser(@NotNull IElementType rootElementType) {
    this.entryElementType = rootElementType;
  }

  @Override
  public void parseLight(IElementType t, PsiBuilder b) {
    super.parseLight(t == null ? entryElementType : t, b);
  }
}

package com.sumologic.epigraph.ideaplugin.schema.features;

import com.intellij.codeInsight.hint.DeclarationRangeUtil;
import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaElementTypes.*;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaBraceMatcher implements PairedBraceMatcher {
  private static final BracePair[] pairs = new BracePair[]{
      new BracePair(S_CURLY_LEFT, S_CURLY_RIGHT, true),
      new BracePair(S_BRACKET_LEFT, S_BRACKET_RIGHT, false)
  };

  @Override
  public BracePair[] getPairs() {
    return pairs;
  }

  @Override
  public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType) {
    return true; // TODO
  }

  @Override
  public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
    PsiElement element = file.findElementAt(openingBraceOffset);
    if (element == null || element instanceof PsiFile) return openingBraceOffset;

    PsiElement parent = element.getParent();
    if (parent == null) return openingBraceOffset;
    TextRange range = DeclarationRangeUtil.getDeclarationRange(parent);
    return range.getStartOffset();
  }
}

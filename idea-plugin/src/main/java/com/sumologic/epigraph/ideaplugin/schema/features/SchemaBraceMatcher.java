package com.sumologic.epigraph.ideaplugin.schema.features;

import com.intellij.codeInsight.hint.DeclarationRangeUtil;
import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.sumologic.epigraph.schema.parser.psi.SchemaFieldDecl;
import com.sumologic.epigraph.schema.parser.psi.SchemaTypeDef;
import com.sumologic.epigraph.schema.parser.psi.SchemaVarTypeMemberDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.sumologic.epigraph.schema.parser.lexer.SchemaElementTypes.*;

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

    if (parent instanceof SchemaVarTypeMemberDecl || parent instanceof SchemaFieldDecl) {
      try {
        TextRange range = DeclarationRangeUtil.getDeclarationRange(parent);
        return range.getStartOffset();
      } catch (AssertionError e) {
        // TODO temporary hack, see https://intellij-support.jetbrains.com/hc/en-us/community/posts/206846629--AssertionError-Declaration-range-is-invalid-from-DeclarationRangeUtil
        return openingBraceOffset;
      }
    }

    PsiElement parent2 = parent.getParent();
    if (parent2 == null) return openingBraceOffset;

    if (parent2 instanceof SchemaTypeDef) {
      TextRange range = DeclarationRangeUtil.getDeclarationRange(parent2);
      return range.getStartOffset();
    }

    return openingBraceOffset;
  }
}

package com.sumologic.epigraph.ideaplugin.schema.features;

import com.intellij.codeInsight.hint.DeclarationRangeUtil;
import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import io.epigraph.lang.parser.psi.SchemaFieldDecl;
import io.epigraph.lang.parser.psi.SchemaTypeDef;
import io.epigraph.lang.parser.psi.SchemaVarTagDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.epigraph.lang.lexer.EpigraphElementTypes.*;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaBraceMatcher implements PairedBraceMatcher {
  private static final BracePair[] pairs = new BracePair[]{
      new BracePair(E_CURLY_LEFT, E_CURLY_RIGHT, true),
      new BracePair(E_BRACKET_LEFT, E_BRACKET_RIGHT, false),
      new BracePair(E_ANGLE_LEFT, E_ANGLE_RIGHT, false),
      new BracePair(E_PAREN_LEFT, E_PAREN_RIGHT, false)
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
    // TODO implement DeclarationRangeHandler's

    /*
    PsiElement element = file.findElementAt(openingBraceOffset);
    if (element == null || element instanceof PsiFile) return openingBraceOffset;

    PsiElement parent = element.getParent();
    if (parent == null) return openingBraceOffset;

    if (parent instanceof SchemaVarTagDecl || parent instanceof SchemaFieldDecl) {
      TextRange range = DeclarationRangeUtil.getDeclarationRange(parent);
      return range.getStartOffset();
    }

    PsiElement parent2 = parent.getParent();
    if (parent2 == null) return openingBraceOffset;

    if (parent2 instanceof SchemaTypeDef) {
      TextRange range = DeclarationRangeUtil.getDeclarationRange(parent2);
      return range.getStartOffset();
    }
    */
    return openingBraceOffset;
  }
}

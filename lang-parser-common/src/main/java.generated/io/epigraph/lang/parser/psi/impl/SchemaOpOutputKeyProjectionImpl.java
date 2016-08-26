// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.epigraph.lang.lexer.EpigraphElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import io.epigraph.lang.parser.psi.*;

public class SchemaOpOutputKeyProjectionImpl extends ASTWrapperPsiElement implements SchemaOpOutputKeyProjection {

  public SchemaOpOutputKeyProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOpOutputKeyProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SchemaOpOutputKeyProjectionPart> getOpOutputKeyProjectionPartList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaOpOutputKeyProjectionPart.class);
  }

  @Override
  @NotNull
  public PsiElement getBracketLeft() {
    return notNullChild(findChildByType(E_BRACKET_LEFT));
  }

  @Override
  @NotNull
  public PsiElement getBracketRight() {
    return notNullChild(findChildByType(E_BRACKET_RIGHT));
  }

}

// This is a generated file. Not intended for manual editing.
package ws.epigraph.url.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.url.lexer.UrlElementTypes.*;
import ws.epigraph.url.parser.psi.*;

public class UrlAnonMapImpl extends UrlTypeRefImpl implements UrlAnonMap {

  public UrlAnonMapImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull UrlVisitor visitor) {
    visitor.visitAnonMap(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof UrlVisitor) accept((UrlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public UrlTypeRef getTypeRef() {
    return findChildByClass(UrlTypeRef.class);
  }

  @Override
  @Nullable
  public UrlValueTypeRef getValueTypeRef() {
    return findChildByClass(UrlValueTypeRef.class);
  }

  @Override
  @Nullable
  public PsiElement getBracketLeft() {
    return findChildByType(U_BRACKET_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getBracketRight() {
    return findChildByType(U_BRACKET_RIGHT);
  }

  @Override
  @Nullable
  public PsiElement getComma() {
    return findChildByType(U_COMMA);
  }

  @Override
  @NotNull
  public PsiElement getMap() {
    return findNotNullChildByType(U_MAP);
  }

}

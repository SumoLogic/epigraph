// This is a generated file. Not intended for manual editing.
package ws.epigraph.idl.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.idl.lexer.IdlElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import ws.epigraph.idl.parser.psi.*;

public class IdlCreateOperationDefImpl extends ASTWrapperPsiElement implements IdlCreateOperationDef {

  public IdlCreateOperationDefImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitCreateOperationDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<IdlCreateOperationBodyPart> getCreateOperationBodyPartList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, IdlCreateOperationBodyPart.class);
  }

  @Override
  @Nullable
  public IdlOperationName getOperationName() {
    return findChildByClass(IdlOperationName.class);
  }

  @Override
  @NotNull
  public PsiElement getCreate() {
    return findNotNullChildByType(I_CREATE);
  }

  @Override
  @Nullable
  public PsiElement getCurlyLeft() {
    return findChildByType(I_CURLY_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getCurlyRight() {
    return findChildByType(I_CURLY_RIGHT);
  }

}

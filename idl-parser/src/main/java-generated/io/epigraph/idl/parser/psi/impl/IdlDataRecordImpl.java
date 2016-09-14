// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.epigraph.idl.lexer.IdlElementTypes.*;
import io.epigraph.idl.parser.psi.*;

public class IdlDataRecordImpl extends IdlDataValueImpl implements IdlDataRecord {

  public IdlDataRecordImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitDataRecord(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<IdlDataRecordEntry> getDataRecordEntryList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, IdlDataRecordEntry.class);
  }

  @Override
  @Nullable
  public IdlFqnTypeRef getFqnTypeRef() {
    return findChildByClass(IdlFqnTypeRef.class);
  }

  @Override
  @NotNull
  public PsiElement getCurlyLeft() {
    return findNotNullChildByType(I_CURLY_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getCurlyRight() {
    return findChildByType(I_CURLY_RIGHT);
  }

}

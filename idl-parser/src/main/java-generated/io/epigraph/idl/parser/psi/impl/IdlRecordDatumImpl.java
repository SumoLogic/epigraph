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

public class IdlRecordDatumImpl extends IdlDatumImpl implements IdlRecordDatum {

  public IdlRecordDatumImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitRecordDatum(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<IdlRecordDatumEntry> getRecordDatumEntryList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, IdlRecordDatumEntry.class);
  }

  @Override
  @Nullable
  public IdlTypeRef getTypeRef() {
    return findChildByClass(IdlTypeRef.class);
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

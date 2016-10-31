// This is a generated file. Not intended for manual editing.
package io.epigraph.schema.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.epigraph.schema.lexer.SchemaElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import io.epigraph.schema.parser.psi.*;
import io.epigraph.lang.Qn;

public class SchemaQnImpl extends ASTWrapperPsiElement implements SchemaQn {

  public SchemaQnImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitQn(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SchemaQnSegment> getQnSegmentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaQnSegment.class);
  }

  @NotNull
  public Qn getQn() {
    return SchemaPsiImplUtil.getQn(this);
  }

}

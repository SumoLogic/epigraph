// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.schema.lexer.SchemaElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import ws.epigraph.schema.parser.psi.*;

public class SchemaOperationDefImpl extends ASTWrapperPsiElement implements SchemaOperationDef {

  public SchemaOperationDefImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOperationDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaCreateOperationDef getCreateOperationDef() {
    return PsiTreeUtil.getChildOfType(this, SchemaCreateOperationDef.class);
  }

  @Override
  @Nullable
  public SchemaCustomOperationDef getCustomOperationDef() {
    return PsiTreeUtil.getChildOfType(this, SchemaCustomOperationDef.class);
  }

  @Override
  @Nullable
  public SchemaDeleteOperationDef getDeleteOperationDef() {
    return PsiTreeUtil.getChildOfType(this, SchemaDeleteOperationDef.class);
  }

  @Override
  @Nullable
  public SchemaReadOperationDef getReadOperationDef() {
    return PsiTreeUtil.getChildOfType(this, SchemaReadOperationDef.class);
  }

  @Override
  @Nullable
  public SchemaUpdateOperationDef getUpdateOperationDef() {
    return PsiTreeUtil.getChildOfType(this, SchemaUpdateOperationDef.class);
  }

}

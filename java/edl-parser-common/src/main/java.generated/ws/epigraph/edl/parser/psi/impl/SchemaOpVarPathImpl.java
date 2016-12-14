// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.edl.lexer.SchemaElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import ws.epigraph.edl.parser.psi.*;

public class SchemaOpVarPathImpl extends ASTWrapperPsiElement implements SchemaOpVarPath {

  public SchemaOpVarPathImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOpVarPath(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public SchemaOpModelPath getOpModelPath() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, SchemaOpModelPath.class));
  }

  @Override
  @NotNull
  public List<SchemaOpModelPathProperty> getOpModelPathPropertyList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaOpModelPathProperty.class);
  }

  @Override
  @Nullable
  public SchemaTagName getTagName() {
    return PsiTreeUtil.getChildOfType(this, SchemaTagName.class);
  }

  @Override
  @Nullable
  public PsiElement getColon() {
    return findChildByType(S_COLON);
  }

  @Override
  @Nullable
  public PsiElement getCurlyLeft() {
    return findChildByType(S_CURLY_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getCurlyRight() {
    return findChildByType(S_CURLY_RIGHT);
  }

}

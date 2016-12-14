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

public class SchemaOpRecordModelPathImpl extends ASTWrapperPsiElement implements SchemaOpRecordModelPath {

  public SchemaOpRecordModelPathImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOpRecordModelPath(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public SchemaOpFieldPathEntry getOpFieldPathEntry() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, SchemaOpFieldPathEntry.class));
  }

  @Override
  @NotNull
  public PsiElement getSlash() {
    return notNullChild(findChildByType(S_SLASH));
  }

}

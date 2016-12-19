// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EdlOpParam extends PsiElement {

  @NotNull
  List<EdlAnnotation> getAnnotationList();

  @Nullable
  EdlDatum getDatum();

  @Nullable
  EdlOpInputModelProjection getOpInputModelProjection();

  @NotNull
  List<EdlOpParam> getOpParamList();

  @Nullable
  EdlQid getQid();

  @Nullable
  EdlTypeRef getTypeRef();

  @Nullable
  PsiElement getColon();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

  @Nullable
  PsiElement getEq();

  @Nullable
  PsiElement getPlus();

  @NotNull
  PsiElement getSemicolon();

}

// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaOpParam extends PsiElement {

  @NotNull
  List<SchemaAnnotation> getAnnotationList();

  @Nullable
  SchemaDatum getDatum();

  @Nullable
  SchemaOpInputModelProjection getOpInputModelProjection();

  @NotNull
  List<SchemaOpParam> getOpParamList();

  @Nullable
  SchemaQid getQid();

  @Nullable
  SchemaTypeRef getTypeRef();

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

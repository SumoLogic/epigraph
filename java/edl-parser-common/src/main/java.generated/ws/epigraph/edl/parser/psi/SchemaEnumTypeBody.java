// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaEnumTypeBody extends AnnotationsHolder {

  @NotNull
  List<SchemaAnnotation> getAnnotationList();

  @NotNull
  List<SchemaEnumMemberDecl> getEnumMemberDeclList();

  @NotNull
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}

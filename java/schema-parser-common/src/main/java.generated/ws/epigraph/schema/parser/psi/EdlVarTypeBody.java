// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EdlVarTypeBody extends AnnotationsHolder {

  @NotNull
  List<EdlAnnotation> getAnnotationList();

  @NotNull
  List<EdlVarTagDecl> getVarTagDeclList();

  @NotNull
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}

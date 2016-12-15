// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EdlRecordTypeBody extends AnnotationsHolder {

  @NotNull
  List<EdlAnnotation> getAnnotationList();

  @NotNull
  List<EdlFieldDecl> getFieldDeclList();

  @NotNull
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}

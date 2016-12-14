// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaRecordTypeBody extends AnnotationsHolder {

  @NotNull
  List<SchemaAnnotation> getAnnotationList();

  @NotNull
  List<SchemaFieldDecl> getFieldDeclList();

  @NotNull
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}

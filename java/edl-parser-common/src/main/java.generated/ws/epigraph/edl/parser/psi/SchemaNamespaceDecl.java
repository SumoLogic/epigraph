// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import ws.epigraph.edl.parser.psi.stubs.SchemaNamespaceDeclStub;
import ws.epigraph.lang.Qn;

public interface SchemaNamespaceDecl extends PsiElement, StubBasedPsiElement<SchemaNamespaceDeclStub> {

  @NotNull
  List<SchemaAnnotation> getAnnotationList();

  @Nullable
  SchemaQn getQn();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

  @NotNull
  PsiElement getNamespace();

  @Nullable
  Qn getFqn();

  @NotNull
  String toString();

}

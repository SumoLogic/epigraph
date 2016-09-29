// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlNamespaceDecl extends PsiElement {

  @NotNull
  List<IdlAnnotation> getAnnotationList();

  @Nullable
  IdlFqn getFqn();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

  @NotNull
  PsiElement getNamespace();

}

// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaExtendsDecl extends PsiElement {

  @NotNull
  List<SchemaFqnTypeRef> getFqnTypeRefList();

  @NotNull
  PsiElement getExtends();

}

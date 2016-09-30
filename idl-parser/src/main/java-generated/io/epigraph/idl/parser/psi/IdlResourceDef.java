// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlResourceDef extends PsiElement {

  @NotNull
  List<IdlOperationDef> getOperationDefList();

  @NotNull
  IdlResourceName getResourceName();

  @NotNull
  IdlResourceType getResourceType();

  @NotNull
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

  @NotNull
  PsiElement getResource();

}

// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlData extends PsiElement {

  @NotNull
  List<IdlDataEntry> getDataEntryList();

  @Nullable
  IdlFqnTypeRef getFqnTypeRef();

  @NotNull
  PsiElement getAngleLeft();

  @Nullable
  PsiElement getAngleRight();

}

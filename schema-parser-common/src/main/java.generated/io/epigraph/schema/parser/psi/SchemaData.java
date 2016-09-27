// This is a generated file. Not intended for manual editing.
package io.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaData extends PsiElement {

  @NotNull
  List<SchemaDataEntry> getDataEntryList();

  @Nullable
  SchemaFqnTypeRef getFqnTypeRef();

  @NotNull
  PsiElement getAngleLeft();

  @Nullable
  PsiElement getAngleRight();

}
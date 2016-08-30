// This is a generated file. Not intended for manual editing.
package io.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaDataValue extends PsiElement {

  @Nullable
  SchemaDataPrimitive getDataPrimitive();

  @Nullable
  SchemaDataValue getDataValue();

  @NotNull
  List<SchemaFqnTypeRef> getFqnTypeRefList();

  @Nullable
  PsiElement getNull();

}

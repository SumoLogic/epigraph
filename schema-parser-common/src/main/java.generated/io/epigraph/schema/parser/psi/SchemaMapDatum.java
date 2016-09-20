// This is a generated file. Not intended for manual editing.
package io.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaMapDatum extends SchemaDatum {

  @Nullable
  SchemaFqnTypeRef getFqnTypeRef();

  @NotNull
  List<SchemaMapDatumEntry> getMapDatumEntryList();

  @NotNull
  PsiElement getParenLeft();

  @Nullable
  PsiElement getParenRight();

}

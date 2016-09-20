// This is a generated file. Not intended for manual editing.
package io.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaRecordDatum extends SchemaDatum {

  @Nullable
  SchemaFqnTypeRef getFqnTypeRef();

  @NotNull
  List<SchemaRecordDatumEntry> getRecordDatumEntryList();

  @NotNull
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}

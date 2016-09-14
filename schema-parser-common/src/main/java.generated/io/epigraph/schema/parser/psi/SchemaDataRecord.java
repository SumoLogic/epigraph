// This is a generated file. Not intended for manual editing.
package io.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaDataRecord extends SchemaDataValue {

  @NotNull
  List<SchemaDataRecordEntry> getDataRecordEntryList();

  @Nullable
  SchemaFqnTypeRef getFqnTypeRef();

  @NotNull
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}

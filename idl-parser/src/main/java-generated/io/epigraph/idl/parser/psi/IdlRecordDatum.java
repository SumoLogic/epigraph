// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlRecordDatum extends IdlDatum {

  @Nullable
  IdlFqnTypeRef getFqnTypeRef();

  @NotNull
  List<IdlRecordDatumEntry> getRecordDatumEntryList();

  @NotNull
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}

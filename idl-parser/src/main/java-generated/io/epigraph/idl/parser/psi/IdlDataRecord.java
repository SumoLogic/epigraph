// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlDataRecord extends IdlVarValue {

  @NotNull
  List<IdlDataRecordEntry> getDataRecordEntryList();

  @Nullable
  IdlFqnTypeRef getFqnTypeRef();

  @NotNull
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}

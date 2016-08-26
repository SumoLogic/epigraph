// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaOpOutputRecordModelProjection extends PsiElement {

  @NotNull
  List<SchemaOpOutputFieldProjection> getOpOutputFieldProjectionList();

  @NotNull
  List<SchemaOpOutputRecordPolyBranch> getOpOutputRecordPolyBranchList();

  @NotNull
  PsiElement getParenLeft();

  @NotNull
  PsiElement getParenRight();

}

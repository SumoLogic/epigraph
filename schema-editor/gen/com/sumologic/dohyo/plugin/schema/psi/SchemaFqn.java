// This is a generated file. Not intended for manual editing.
package com.sumologic.dohyo.plugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaFqn extends PsiElement {

  @NotNull
  List<SchemaFqnSegment> getFqnSegmentList();

  @NotNull
  String getFqnString();

  @Nullable
  SchemaFqnSegment getLastSegment();

}

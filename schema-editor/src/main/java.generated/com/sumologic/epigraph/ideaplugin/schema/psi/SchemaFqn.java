// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.sumologic.epigraph.ideaplugin.schema.brains.Fqn;

public interface SchemaFqn extends PsiElement {

  @NotNull
  List<SchemaFqnSegment> getFqnSegmentList();

  @NotNull
  Fqn getFqn();

}

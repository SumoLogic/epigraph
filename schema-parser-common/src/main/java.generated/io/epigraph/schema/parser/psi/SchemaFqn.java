// This is a generated file. Not intended for manual editing.
package io.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import io.epigraph.lang.Fqn;

public interface SchemaFqn extends PsiElement {

  @NotNull
  List<SchemaFqnSegment> getFqnSegmentList();

  @NotNull
  Fqn getFqn();

}

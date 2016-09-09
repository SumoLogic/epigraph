// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import io.epigraph.lang.Fqn;

public interface IdlFqn extends PsiElement {

  @NotNull
  List<IdlFqnSegment> getFqnSegmentList();

  @NotNull
  Fqn getFqn();

}

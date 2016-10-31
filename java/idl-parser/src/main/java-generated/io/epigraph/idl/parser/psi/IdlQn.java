// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import io.epigraph.lang.Qn;

public interface IdlQn extends PsiElement {

  @NotNull
  List<IdlQnSegment> getQnSegmentList();

  @NotNull
  Qn getQn();

}

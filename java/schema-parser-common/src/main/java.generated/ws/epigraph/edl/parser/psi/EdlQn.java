// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import ws.epigraph.lang.Qn;

public interface EdlQn extends PsiElement {

  @NotNull
  List<EdlQnSegment> getQnSegmentList();

  @NotNull
  Qn getQn();

}

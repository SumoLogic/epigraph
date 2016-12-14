// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import ws.epigraph.lang.Qn;

public interface SchemaQn extends PsiElement {

  @NotNull
  List<SchemaQnSegment> getQnSegmentList();

  @NotNull
  Qn getQn();

}

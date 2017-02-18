// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaOpOutputModelPolymorphicTail extends PsiElement {

  @Nullable
  SchemaOpOutputModelMultiTail getOpOutputModelMultiTail();

  @Nullable
  SchemaOpOutputModelSingleTail getOpOutputModelSingleTail();

  @NotNull
  PsiElement getTilda();

}

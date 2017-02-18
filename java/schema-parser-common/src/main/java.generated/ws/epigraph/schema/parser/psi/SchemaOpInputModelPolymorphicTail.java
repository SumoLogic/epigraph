// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaOpInputModelPolymorphicTail extends PsiElement {

  @Nullable
  SchemaOpInputModelMultiTail getOpInputModelMultiTail();

  @Nullable
  SchemaOpInputModelSingleTail getOpInputModelSingleTail();

  @NotNull
  PsiElement getTilda();

}

// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaOpInputModelProjection extends PsiElement {

  @Nullable
  SchemaOpInputListModelProjection getOpInputListModelProjection();

  @Nullable
  SchemaOpInputMapModelProjection getOpInputMapModelProjection();

  @Nullable
  SchemaOpInputModelPolymorphicTail getOpInputModelPolymorphicTail();

  @Nullable
  SchemaOpInputRecordModelProjection getOpInputRecordModelProjection();

}

// This is a generated file. Not intended for manual editing.
package ws.epigraph.url.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface UrlRecordDatum extends UrlDatum {

  @NotNull
  List<UrlRecordDatumEntry> getRecordDatumEntryList();

  @Nullable
  UrlTypeRef getTypeRef();

  @NotNull
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}

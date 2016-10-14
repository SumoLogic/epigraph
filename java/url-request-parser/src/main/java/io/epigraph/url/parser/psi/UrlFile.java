package io.epigraph.url.parser.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import io.epigraph.url.parser.UrlFileType;
import io.epigraph.url.parser.UrlLanguage;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UrlFile extends PsiFileBase {
  public UrlFile(@NotNull FileViewProvider viewProvider) {
    super(viewProvider, UrlLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public FileType getFileType() {
    return UrlFileType.INSTANCE;
  }

  @Override
  public String toString() {
    return "Epigraph URL file";
  }
}

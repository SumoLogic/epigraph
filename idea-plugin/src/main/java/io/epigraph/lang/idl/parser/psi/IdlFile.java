package io.epigraph.lang.idl.parser.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import io.epigraph.lang.idl.IdlLanguage;
import io.epigraph.lang.idl.parser.IdlFileType;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class IdlFile extends PsiFileBase {
  public IdlFile(@NotNull FileViewProvider viewProvider) {
    super(viewProvider, IdlLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public FileType getFileType() {
    return IdlFileType.INSTANCE;
  }
}

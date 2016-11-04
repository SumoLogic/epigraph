package ws.epigraph.idl.parser.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import ws.epigraph.idl.parser.IdlFileType;
import ws.epigraph.idl.parser.IdlLanguage;
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

  @Override
  public String toString() {
    return "Epigraph IDL file";
  }
}

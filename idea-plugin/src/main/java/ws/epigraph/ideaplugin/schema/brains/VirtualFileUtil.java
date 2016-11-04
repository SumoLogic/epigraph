package ws.epigraph.ideaplugin.schema.brains;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.LightVirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class VirtualFileUtil {
  public static VirtualFile getOriginalVirtualFile(@NotNull PsiFile psiFile) {
    VirtualFile virtualFile = psiFile.getVirtualFile();
    if (virtualFile == null) {
      virtualFile = psiFile.getViewProvider().getVirtualFile();
      if (virtualFile instanceof LightVirtualFile) {
        LightVirtualFile lightVirtualFile = (LightVirtualFile) virtualFile;
        virtualFile = lightVirtualFile.getOriginalFile();
      }
    }

    return virtualFile;
  }
}

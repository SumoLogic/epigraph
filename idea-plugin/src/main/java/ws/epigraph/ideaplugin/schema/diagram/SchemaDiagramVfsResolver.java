package ws.epigraph.ideaplugin.schema.diagram;

import com.intellij.diagram.DiagramVfsResolver;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.search.GlobalSearchScope;
import ws.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import ws.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import io.epigraph.lang.Qn;
import io.epigraph.schema.parser.psi.SchemaFile;
import io.epigraph.schema.parser.psi.SchemaTypeDef;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaDiagramVfsResolver implements DiagramVfsResolver<PsiNamedElement> {
  private static final String FILE_PREFIX = "file:";
  private static final String TYPE_PREFIX = "type:";

  @Override
  public String getQualifiedName(PsiNamedElement element) {
    if (element instanceof PsiFile) {
      PsiFile psiFile = (PsiFile) element;
      return FILE_PREFIX + psiFile.getVirtualFile().getUrl();
    }

    if (element instanceof SchemaTypeDef) {
      SchemaTypeDef typeDef = (SchemaTypeDef) element;
      return TYPE_PREFIX + SchemaPresentationUtil.getName(typeDef, true);
    }

    return null;
  }

  @Nullable
  @Override
  public PsiNamedElement resolveElementByFQN(String s, Project project) {
    if (s.startsWith(FILE_PREFIX)) {
      String name = s.substring(FILE_PREFIX.length());

      VirtualFile virtualFile = VirtualFileManager.getInstance().findFileByUrl(name);
      PsiFile psiFile = virtualFile == null ? null : PsiManager.getInstance(project).findFile(virtualFile);
      return psiFile instanceof SchemaFile ? psiFile : null;
    }

    if (s.startsWith(TYPE_PREFIX)) {
      String name = s.substring(TYPE_PREFIX.length());

      SchemaIndexUtil.findTypeDef(project, Qn.fromDotSeparated(name), GlobalSearchScope.allScope(project));
    }

    return null;
  }
}

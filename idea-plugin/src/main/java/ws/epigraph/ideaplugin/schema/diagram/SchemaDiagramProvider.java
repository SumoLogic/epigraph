package ws.epigraph.ideaplugin.schema.diagram;

import com.intellij.diagram.*;
import com.intellij.diagram.extras.DiagramExtras;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiNamedElement;
import io.epigraph.schema.parser.psi.SchemaFile;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaDiagramProvider extends DiagramProvider<PsiNamedElement> {
  public static final String ID = "EPIGRAPH_SCHEMA";

  // @formatter:off
  private final DiagramVisibilityManager visibilityManager = new EmptyDiagramVisibilityManager();
  private final DiagramNodeContentManager nodeContentManager = new SchemaDiagramNodeContentManager();
  private final DiagramElementManager<PsiNamedElement> elementManager = new SchemaDiagramElementManager();
  private final DiagramVfsResolver<PsiNamedElement> vfsResolver = new SchemaDiagramVfsResolver();
  private final DiagramRelationshipManager<PsiNamedElement> relationshipManager = new SchemaDiagramRelationshipManager();
  // @formatter:on

  private final DiagramExtras<PsiNamedElement> extras = new SchemaDiagramExtras();

  @Pattern("[a-zA-Z0-9_-]*")
  @Override
  public String getID() {
    return ID;
  }

  @Override
  public DiagramVisibilityManager createVisibilityManager() {
    return visibilityManager;
  }

  @Override
  public DiagramNodeContentManager getNodeContentManager() {
    return nodeContentManager;
  }

  @Override
  public DiagramElementManager<PsiNamedElement> getElementManager() {
    return elementManager;
  }

  @Override
  public DiagramVfsResolver<PsiNamedElement> getVfsResolver() {
    return vfsResolver;
  }

  @Override
  public DiagramRelationshipManager<PsiNamedElement> getRelationshipManager() {
    return relationshipManager;
  }

  @Override
  public String getPresentableName() {
    return "Epigraph Schema Diagram";
  }

  @NotNull
  @Override
  public DiagramExtras<PsiNamedElement> getExtras() {
    return extras;
  }

  @Override
  public DiagramDataModel<PsiNamedElement> createDataModel(@NotNull Project project,
                                                           @Nullable PsiNamedElement psiNamedElement,
                                                           @Nullable VirtualFile virtualFile,
                                                           DiagramPresentationModel diagramPresentationModel) {
    return new SchemaDiagramDataModel(project, (SchemaFile) psiNamedElement, this);
  }
}

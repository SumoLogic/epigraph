/*
 * Copyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.epigraph.ideaplugin.schema.diagram;

import com.intellij.diagram.*;
import com.intellij.diagram.extras.DiagramExtras;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiNamedElement;
import ws.epigraph.schema.parser.psi.EdlFile;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlDiagramProvider extends DiagramProvider<PsiNamedElement> {
  public static final String ID = "EPIGRAPH_EDL";

  // @formatter:off
  private final DiagramVisibilityManager visibilityManager = new EmptyDiagramVisibilityManager();
  private final DiagramNodeContentManager nodeContentManager = new EdlDiagramNodeContentManager();
  private final DiagramElementManager<PsiNamedElement> elementManager = new EdlDiagramElementManager();
  private final DiagramVfsResolver<PsiNamedElement> vfsResolver = new EdlDiagramVfsResolver();
  private final DiagramRelationshipManager<PsiNamedElement> relationshipManager = new EdlDiagramRelationshipManager();
  // @formatter:on

  private final DiagramExtras<PsiNamedElement> extras = new EdlDiagramExtras();

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
    return "Epigraph Declarations Diagram";
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
    return new EdlDiagramDataModel(project, (EdlFile) psiNamedElement, this);
  }
}

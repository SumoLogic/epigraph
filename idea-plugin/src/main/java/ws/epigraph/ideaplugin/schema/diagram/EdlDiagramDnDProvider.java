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

import com.intellij.diagram.extras.providers.DiagramDnDProvider;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiNamedElement;
import ws.epigraph.schema.parser.psi.EdlDefs;
import ws.epigraph.schema.parser.psi.EdlFile;
import ws.epigraph.schema.parser.psi.EdlTypeDef;
import ws.epigraph.schema.parser.psi.EdlTypeDefWrapper;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlDiagramDnDProvider implements DiagramDnDProvider<PsiNamedElement> {
  @Override
  public boolean isAcceptedForDnD(Object o, Project project) {
    return o instanceof EdlFile || o instanceof EdlTypeDef;
  }

  @Nullable
  @Override
  public PsiNamedElement[] wrapToModelObject(Object o, Project project) {
    if (o instanceof EdlTypeDef) return new PsiNamedElement[]{(PsiNamedElement) o};
    if (o instanceof EdlFile) {
      EdlFile edlFile = (EdlFile) o;
      EdlDefs defs = edlFile.getDefs();
      if (defs != null)
        return defs.getTypeDefWrapperList()
                   .stream()
                   .map(EdlTypeDefWrapper::getElement)
                   .toArray(PsiNamedElement[]::new);
    }

    return null;
  }
}

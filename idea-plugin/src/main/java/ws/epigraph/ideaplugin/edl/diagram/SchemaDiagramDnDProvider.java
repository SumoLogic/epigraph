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

package ws.epigraph.ideaplugin.edl.diagram;

import com.intellij.diagram.extras.providers.DiagramDnDProvider;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiNamedElement;
import ws.epigraph.edl.parser.psi.SchemaDefs;
import ws.epigraph.edl.parser.psi.SchemaFile;
import ws.epigraph.edl.parser.psi.SchemaTypeDef;
import ws.epigraph.edl.parser.psi.SchemaTypeDefWrapper;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaDiagramDnDProvider implements DiagramDnDProvider<PsiNamedElement> {
  @Override
  public boolean isAcceptedForDnD(Object o, Project project) {
    return o instanceof SchemaFile || o instanceof SchemaTypeDef;
  }

  @Nullable
  @Override
  public PsiNamedElement[] wrapToModelObject(Object o, Project project) {
    if (o instanceof SchemaTypeDef) return new PsiNamedElement[]{(PsiNamedElement) o};
    if (o instanceof SchemaFile) {
      SchemaFile schemaFile = (SchemaFile) o;
      SchemaDefs defs = schemaFile.getDefs();
      if (defs != null)
        return defs.getTypeDefWrapperList()
                   .stream()
                   .map(SchemaTypeDefWrapper::getElement)
                   .toArray(PsiNamedElement[]::new);
    }

    return null;
  }
}

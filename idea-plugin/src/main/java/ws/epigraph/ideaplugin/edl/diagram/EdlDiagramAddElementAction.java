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

import com.intellij.diagram.actions.DiagramDefaultAddElementAction;
import com.intellij.ide.util.gotoByName.GotoClassModel2;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiNamedElement;
import ws.epigraph.ideaplugin.edl.EdlBundle;
import ws.epigraph.edl.parser.EdlLanguage;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlDiagramAddElementAction extends DiagramDefaultAddElementAction<PsiNamedElement> {
  @Override
  public String getText() {
    return EdlBundle.message("diagram.add.type");
  }

  @Override
  protected GotoClassModel2 createModel(@NotNull Project project) {
    GotoClassModel2 model = super.createModel(project);
    model.setFilterItems(Collections.singleton(EdlLanguage.INSTANCE));
    return model;
  }
}

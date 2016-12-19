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

package ws.epigraph.ideaplugin.schema.brains.hierarchy;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.util.Query;
import com.intellij.util.QueryFactory;
import com.intellij.util.containers.ContainerUtil;
import ws.epigraph.schema.parser.psi.EdlTypeDef;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlTypeParentsSearch extends QueryFactory<EdlTypeDef, EdlTypeParentsSearch.SearchParameters> {
  public static final EdlTypeParentsSearch INSTANCE = new EdlTypeParentsSearch();

  public static class SearchParameters {
    @NotNull
    public final EdlTypeDef edlTypeDef;

    public SearchParameters(@NotNull EdlTypeDef edlTypeDef) {
      this.edlTypeDef = edlTypeDef;
    }
  }

  private EdlTypeParentsSearch() {
    registerExecutor(new EdlTypeParentsSearcher());
  }

  public static Query<EdlTypeDef> search(@NotNull final EdlTypeDef edlTypeDef) {
    return search(new SearchParameters(edlTypeDef));
  }

  public static Query<EdlTypeDef> search(@NotNull final SearchParameters parameters) {
    final Project project = parameters.edlTypeDef.getProject();
    return INSTANCE.createUniqueResultsQuery(parameters, ContainerUtil.canonicalStrategy(),
        edlTypeDef ->
            edlTypeDef == null ? null :
            ApplicationManager.getApplication().runReadAction(
            (Computable<SmartPsiElementPointer<EdlTypeDef>>) () ->
                SmartPointerManager.getInstance(project).createSmartPsiElementPointer(edlTypeDef)
        )
    );
  }
}

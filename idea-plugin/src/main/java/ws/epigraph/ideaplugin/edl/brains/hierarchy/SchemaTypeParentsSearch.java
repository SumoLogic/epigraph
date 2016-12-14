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

package ws.epigraph.ideaplugin.edl.brains.hierarchy;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.util.Query;
import com.intellij.util.QueryFactory;
import com.intellij.util.containers.ContainerUtil;
import ws.epigraph.edl.parser.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaTypeParentsSearch extends QueryFactory<SchemaTypeDef, SchemaTypeParentsSearch.SearchParameters> {
  public static final SchemaTypeParentsSearch INSTANCE = new SchemaTypeParentsSearch();

  public static class SearchParameters {
    @NotNull
    public final SchemaTypeDef schemaTypeDef;

    public SearchParameters(@NotNull SchemaTypeDef schemaTypeDef) {
      this.schemaTypeDef = schemaTypeDef;
    }
  }

  private SchemaTypeParentsSearch() {
    registerExecutor(new SchemaTypeParentsSearcher());
  }

  public static Query<SchemaTypeDef> search(@NotNull final SchemaTypeDef schemaTypeDef) {
    return search(new SearchParameters(schemaTypeDef));
  }

  public static Query<SchemaTypeDef> search(@NotNull final SearchParameters parameters) {
    final Project project = parameters.schemaTypeDef.getProject();
    return INSTANCE.createUniqueResultsQuery(parameters, ContainerUtil.canonicalStrategy(),
        schemaTypeDef ->
            schemaTypeDef == null ? null :
            ApplicationManager.getApplication().runReadAction(
            (Computable<SmartPsiElementPointer<SchemaTypeDef>>) () ->
                SmartPointerManager.getInstance(project).createSmartPsiElementPointer(schemaTypeDef)
        )
    );
  }
}

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

import com.intellij.util.Query;
import com.intellij.util.QueryFactory;
import ws.epigraph.schema.parser.psi.EdlTypeDef;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlDirectTypeInheritorsSearch extends QueryFactory<EdlTypeDef, EdlDirectTypeInheritorsSearch.SearchParameters> {
  public static final EdlDirectTypeInheritorsSearch INSTANCE = new EdlDirectTypeInheritorsSearch();

  public static class SearchParameters {
    public final EdlTypeDef edlTypeDef;

    public SearchParameters(EdlTypeDef edlTypeDef) {
      this.edlTypeDef = edlTypeDef;
    }
  }

  private EdlDirectTypeInheritorsSearch() {
    registerExecutor(new EdlDirectTypeInheritorsSearcher());
  }

  public static Query<EdlTypeDef> search(@NotNull final EdlTypeDef edlTypeDef) {
    return search(new SearchParameters(edlTypeDef));
  }

  public static Query<EdlTypeDef> search(@NotNull final SearchParameters parameters) {
    return INSTANCE.createUniqueResultsQuery(parameters);
  }
}

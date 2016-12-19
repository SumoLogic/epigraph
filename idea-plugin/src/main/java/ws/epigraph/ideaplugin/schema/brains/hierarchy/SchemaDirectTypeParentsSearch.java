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
import ws.epigraph.schema.parser.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaDirectTypeParentsSearch extends QueryFactory<SchemaTypeDef, SchemaDirectTypeParentsSearch.SearchParameters> {
  public static final SchemaDirectTypeParentsSearch INSTANCE = new SchemaDirectTypeParentsSearch();

  public static class SearchParameters {
    public final SchemaTypeDef schemaTypeDef;
    public final boolean includeExtends;
    public final boolean includeSupplements;
    public final boolean includeStandaloneSupplements;

    public SearchParameters(SchemaTypeDef schemaTypeDef,
                            boolean includeExtends,
                            boolean includeSupplements,
                            boolean includeStandaloneSupplements) {
      this.schemaTypeDef = schemaTypeDef;
      this.includeExtends = includeExtends;
      this.includeSupplements = includeSupplements;
      this.includeStandaloneSupplements = includeStandaloneSupplements;
    }

    public SearchParameters(SchemaTypeDef schemaTypeDef) {
      this(schemaTypeDef, true, true, true);
    }
  }

  private SchemaDirectTypeParentsSearch() {
    registerExecutor(new SchemaDirectTypeParentsSearcher());
  }

  public static Query<SchemaTypeDef> search(@NotNull final SchemaTypeDef schemaTypeDef) {
    return search(new SearchParameters(schemaTypeDef));
  }

  public static Query<SchemaTypeDef> search(@NotNull final SearchParameters parameters) {
    return INSTANCE.createUniqueResultsQuery(parameters);
  }
}

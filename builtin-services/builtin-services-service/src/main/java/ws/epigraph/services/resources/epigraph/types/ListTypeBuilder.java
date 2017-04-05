/*
 * Copyright 2017 Sumo Logic
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

package ws.epigraph.services.resources.epigraph.types;

import epigraph.schema.ListType;
import epigraph.schema.ListType_List;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.services.resources.epigraph.projections.output.datatypeprojection.OutputDataTypeProjection;
import ws.epigraph.services.resources.epigraph.projections.output.typenameprojection.OutputTypeNameProjectionProjection;
import ws.epigraph.services.resources.epigraph.projections.output.typeprojection._normalized.datumtype._normalized.listtype.OutputListTypeProjection;
import ws.epigraph.services.resources.epigraph.projections.output.typeprojection._normalized.datumtype._normalized.listtype.supertypes.OutputListType_ListProjection;
import ws.epigraph.types.ListTypeApi;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class ListTypeBuilder {

  private ListTypeBuilder() {}

  public static @NotNull ListType buildListType(
      @NotNull ListTypeApi type,
      @NotNull OutputListTypeProjection projection) {

    ListType.Builder builder = ListType.create();

    // name
    final OutputTypeNameProjectionProjection nameProjection = projection.name();
    if (nameProjection != null)
      builder.setName(TypeNameBuilder.buildTypeName(type.name(), nameProjection));

    // supertypes
    final OutputListType_ListProjection supertypesProjection = projection.supertypes();
    if (supertypesProjection != null) {
      final OutputListTypeProjection supertypeProjection = supertypesProjection.itemsProjection();

      ListType_List.Builder supertypesBuilder = ListType_List.create();

      for (final ListTypeApi supertype : type.supertypes())
        supertypesBuilder.add(buildListType(supertype, supertypeProjection));

      builder.setSupertypes(supertypesBuilder);
    }

    final OutputDataTypeProjection dataTypeProjection = projection.valueType();
    if (dataTypeProjection != null)
      builder.setValueType(DataTypeBuilder.buildDataType(type.dataType(), dataTypeProjection));

    return builder;
  }
}

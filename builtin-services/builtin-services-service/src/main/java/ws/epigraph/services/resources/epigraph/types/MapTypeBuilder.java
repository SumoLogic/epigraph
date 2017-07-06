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

import epigraph.schema.MapType;
import epigraph.schema.MapType_List;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.services._resources.epigraph.projections.output.datatypeprojection.OutputDataTypeProjection;
import ws.epigraph.services._resources.epigraph.projections.output.typenameprojection.OutputTypeNameProjectionProjection;
import ws.epigraph.services._resources.epigraph.projections.output.typeprojection._nt.datumtype.OutputDatumTypeProjection;
import ws.epigraph.services._resources.epigraph.projections.output.typeprojection._nt.datumtype._nt.maptype.OutputMapTypeProjection;
import ws.epigraph.services._resources.epigraph.projections.output.typeprojection._nt.datumtype._nt.maptype.supertypes.OutputMapType_ListProjection;
import ws.epigraph.types.MapTypeApi;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class MapTypeBuilder {

  private MapTypeBuilder() {}

  public static @NotNull MapType buildMapType(
      @NotNull MapTypeApi type,
      @NotNull OutputMapTypeProjection projection,
      @NotNull TypeBuilder.Context context) {

    MapType.Builder builder = MapType.create();

    // name
    final OutputTypeNameProjectionProjection nameProjection = projection.name();
    if (nameProjection != null)
      builder.setName(TypeNameBuilder.buildTypeName(type.name(), nameProjection));

    // supertypes
    final OutputMapType_ListProjection supertypesProjection = projection.supertypes();
    if (supertypesProjection != null) {
      final OutputMapTypeProjection supertypeProjection = supertypesProjection.itemsProjection();

      MapType_List.Builder supertypesBuilder = MapType_List.create();

      for (final MapTypeApi supertype : type.supertypes())
        supertypesBuilder.add(buildMapType(supertype, supertypeProjection, context));

      builder.setSupertypes(supertypesBuilder);
    }

    // keys
    final OutputDatumTypeProjection keyProjection = projection.keyType();
    if (keyProjection != null)
      builder.setKeyType(DatumTypeBuilder.buildDatumType(type.keyType(), keyProjection, context));

    // items
    final OutputDataTypeProjection dataTypeProjection = projection.valueType();
    if (dataTypeProjection != null)
      builder.setValueType(DataTypeBuilder.buildDataType(type.dataType(), dataTypeProjection, context));

    return builder;
  }
}

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

import epigraph.schema.PrimitiveType;
import epigraph.schema.PrimitiveType_List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.services.resources.epigraph.projections.output.typenameprojection._normalized.qualifiedtypename.OutputQualifiedTypeNameProjection;
import ws.epigraph.services.resources.epigraph.projections.output.typeprojection._normalized.datumtype._normalized.primitivetype.OutputPrimitiveTypeProjection;
import ws.epigraph.services.resources.epigraph.projections.output.typeprojection._normalized.datumtype._normalized.primitivetype.supertypes.OutputPrimitiveType_ListProjection;
import ws.epigraph.types.PrimitiveTypeApi;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class PrimitiveTypeBuilder {

  private PrimitiveTypeBuilder() {}

  public static @NotNull PrimitiveType buildPrimitiveType(
      @NotNull PrimitiveTypeApi type,
      @NotNull OutputPrimitiveTypeProjection projection) {

    PrimitiveType.Builder builder = PrimitiveType.create();

    // name
    final @Nullable OutputQualifiedTypeNameProjection nameProjection = projection.name();
    if (nameProjection != null)
      builder.setName(TypeNameBuilder.buildQualifiedTypeName(type.name(), nameProjection));

    // supertypes
    final OutputPrimitiveType_ListProjection supertypesProjection = projection.supertypes();
    if (supertypesProjection != null) {
      final OutputPrimitiveTypeProjection supertypeProjection = supertypesProjection.itemsProjection();

      PrimitiveType_List.Builder supertypesBuilder = PrimitiveType_List.create();

      for (final PrimitiveTypeApi supertype : type.supertypes())
        supertypesBuilder.add(buildPrimitiveType(supertype, supertypeProjection));

      builder.setSupertypes(supertypesBuilder);
    }

    return builder;
  }
}

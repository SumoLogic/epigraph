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

import epigraph.schema.DatumType;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.services._resources.epigraph.projections.output.typeprojection._normalized.datumtype.OutputDatumTypeProjection;
import ws.epigraph.types.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class DatumTypeBuilder {
  private DatumTypeBuilder() {}

  public static @NotNull DatumType buildDatumType(
      @NotNull DatumTypeApi type,
      @NotNull OutputDatumTypeProjection projection,
      @NotNull TypeBuilder.Context context) {

    // todo set meta-type

    switch (type.kind()) {
      case RECORD:
        return RecordTypeBuilder.buildRecordType((RecordTypeApi) type, projection.normalizedFor_RecordType(), context);
      case MAP:
        return MapTypeBuilder.buildMapType((MapTypeApi) type, projection.normalizedFor_MapType(), context);
      case LIST:
        return ListTypeBuilder.buildListType((ListTypeApi) type, projection.normalizedFor_ListType(), context);
      case PRIMITIVE:
        return PrimitiveTypeBuilder.buildPrimitiveType(
            (PrimitiveTypeApi) type,
            projection.normalizedFor_PrimitiveType()
        );
      default:
        throw new IllegalArgumentException("Unsupported kind: " + type.kind());
    }
  }

}

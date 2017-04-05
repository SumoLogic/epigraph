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

import epigraph.schema.DataType;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.services.resources.epigraph.projections.output.datatypeprojection.OutputDataTypeProjection;
import ws.epigraph.services.resources.epigraph.projections.output.tagprojection.OutputTag_Projection;
import ws.epigraph.services.resources.epigraph.projections.output.typeprojection.OutputType_Projection;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.types.TagApi;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class DataTypeBuilder {
  private DataTypeBuilder() {}

  public static @NotNull DataType buildDataType(
      @NotNull DataTypeApi dataType,
      @NotNull OutputDataTypeProjection projection) {

    DataType.Builder builder = DataType.create();

    // todo `required`

    // retro
    final OutputTag_Projection retroProjection = projection.retro();
    if (retroProjection != null) {
      final TagApi retroTag = dataType.defaultTag();
      builder.setRetro(retroTag == null ? null : TagBuilder.buildTag(retroTag, retroProjection));
    }

    final OutputType_Projection typeProjection = projection.type();
    if (typeProjection != null)
      builder.setType(TypeBuilder.buildType(dataType.type(), typeProjection));

    return builder;
  }
}

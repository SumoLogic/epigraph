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

import epigraph.schema.Type_;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.services.resources.epigraph.projections.output.typeprojection.OutputType_Projection;
import ws.epigraph.types.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class TypeBuilder {
  private TypeBuilder() {}

  public static @NotNull Type_ buildType(@NotNull TypeApi type, @NotNull OutputType_Projection projection) {
    // todo: `abstract` and `doc` fields support

    if (type.kind() == ws.epigraph.types.TypeKind.UNION)
      return VarTypeBuilder.buildVarType((UnionTypeApi) type, projection.normalizedFor_varType());
    else
      return DatumTypeBuilder.buildDatumType((DatumTypeApi) type, projection.normalizedFor_datumType());
  }
}

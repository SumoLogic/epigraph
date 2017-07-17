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
import ws.epigraph.services._resources.epigraph.projections.output.typeprojection.OutputType_Projection;
import ws.epigraph.types.DatumTypeApi;
import ws.epigraph.types.EntityTypeApi;
import ws.epigraph.types.TypeApi;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class TypeBuilder {
  private TypeBuilder() {}

//  public static @NotNull Type_ buildType(
//      @NotNull TypeApi type,
//      @NotNull OutputType_Projection projection,
//      @NotNull Context context) {
//
//    // todo: `abstract` and `doc` fields support
//
//    if (type.kind() == ws.epigraph.types.TypeKind.ENTITY)
//      return VarTypeBuilder.buildVarType((EntityTypeApi) type, projection.normalizedFor_VarType(), context);
//    else
//      return DatumTypeBuilder.buildDatumType((DatumTypeApi) type, projection.normalizedFor_DatumType(), context);
//  }
//
//  public static class Context {
//    public final Map<Key, Type_> visited = new HashMap<>();
//
//    static class Key {
//      final @NotNull TypeApi type;
//      final @NotNull Object projection;
//
//      Key(
//          final @NotNull TypeApi type,
//          final @NotNull Object projection) {
//        this.type = type;
//        this.projection = projection;
//      }
//
//      @Override
//      public boolean equals(final Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        final Key key = (Key) o;
//        return Objects.equals(type, key.type) &&
//               Objects.equals(projection, key.projection);
//      }
//
//      @Override
//      public int hashCode() {
//        return Objects.hash(type, projection);
//      }
//    }
//  }
}

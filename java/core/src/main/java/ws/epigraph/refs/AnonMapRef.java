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

package ws.epigraph.refs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.types.TypeApi;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class AnonMapRef implements TypeRef {

  private final @NotNull TypeRef keysType;
  private final @NotNull ValueTypeRef valuesType;

  public AnonMapRef(@NotNull TypeRef keysType, @NotNull ValueTypeRef valuesType) {
    this.keysType = keysType;
    this.valuesType = valuesType;
  }

  public @NotNull TypeRef keysType() { return keysType; }

  public @NotNull ValueTypeRef itemsType() { return valuesType; }

  @Override
  public @Nullable TypeApi resolve(@NotNull TypesResolver resolver) {
    return resolver.resolve(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AnonMapRef that = (AnonMapRef) o;
    return Objects.equals(valuesType, that.valuesType);
  }

  @Override
  public int hashCode() { return Objects.hash(valuesType); }

  @Override
  public String toString() {
    return "map[" + keysType + ',' + valuesType + ']';
  }
}

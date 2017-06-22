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

/* Created by yegor on 2017-05-17. */

package ws.epigraph.types;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.annotations.Annotations;

import java.util.Objects;

public class Tag implements TagApi {

  public final @NotNull String name;

  public final @NotNull DatumType type;

  private final @NotNull Annotations annotations;

  public Tag(@NotNull String name, @NotNull DatumType type, @NotNull Annotations annotations) {
    this.name = name;
    this.type = type;
    this.annotations = annotations;
  }

  @Override
  public @NotNull String name() { return name; }

  @Override
  public @NotNull DatumTypeApi type() { return type; }

  @Override
  public @NotNull Annotations annotations() { return annotations; }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Tag that = (Tag) o;
    return Objects.equals(name, that.name) && Objects.equals(type, that.type) &&
           Objects.equals(annotations, that.annotations);
  }

  @Override
  public int hashCode() { return Objects.hash(name, type, annotations); }

}

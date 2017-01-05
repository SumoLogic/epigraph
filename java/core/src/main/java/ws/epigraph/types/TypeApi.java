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

package ws.epigraph.types;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.names.TypeName;

import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface TypeApi {
  @NotNull TypeKind kind();

  @NotNull
  TypeName name();

  @NotNull Collection<@NotNull ? extends TypeApi> supertypes();

  /**
   * @see Class#isAssignableFrom(Class)
   */
  default boolean isAssignableFrom(@NotNull TypeApi type) {
    return type.equals(this) || type.supertypes().contains(this);
  }

  @NotNull Collection<@NotNull ? extends TagApi> tags();

  @NotNull Map<@NotNull String, @NotNull ? extends TagApi> tagsMap();
}

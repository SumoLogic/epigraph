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

/* Created by yegor on 2017-05-30. */

package ws.epigraph.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Data;
import ws.epigraph.names.TypeName;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Type extends TypeApi {

  @Override
  @NotNull TypeKind kind();

  @Override
  @NotNull TypeName name();

  /**
   * @return immediate (i.e. not transitive) supertypes of this type
   */
  @NotNull List<@NotNull ? extends Type> immediateSupertypes();

  /**
   * @return linearized supertypes of this type, in order of decreasing priority
   */
  @Override
  @NotNull List<@NotNull ? extends Type> supertypes();

  /** @see Class#isInstance(Object) */
  boolean isInstance(@Nullable Data data);

  @NotNull Collection<@NotNull ? extends Tag> immediateTags();

  @NotNull Data.Builder createDataBuilder();

  @Override
  @NotNull Collection<@NotNull ? extends Tag> tags();

  @Override
  @NotNull Map<@NotNull String, @NotNull ? extends Tag> tagsMap();

  @Override
  @NotNull DataTypeApi dataType();

  <D extends Data> @NotNull D checkAssignable(@NotNull D data) throws IllegalArgumentException;

  /** Ensures specified type is a subtype of this type. */
  <T extends Type> T checkAssignable(@NotNull T type) throws IllegalArgumentException;

  interface Raw extends Type {}

  interface Static<MyImmData extends Data.Imm.Static, MyDataBuilder extends Data.Builder.Static<MyImmData>>
      extends Type {

    @Override
    @NotNull MyDataBuilder createDataBuilder();

  }

}

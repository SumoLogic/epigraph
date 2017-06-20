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
import ws.epigraph.data.Datum;
import ws.epigraph.data.Val;
import ws.epigraph.errors.ErrorValue;

import java.util.Collection;
import java.util.List;

public interface DatumType extends Type, DatumTypeApi {

  @Override
  @NotNull List<@NotNull ? extends DatumType> immediateSupertypes();

  @Override
  @NotNull List<@NotNull ? extends DatumType> supertypes();

  @Override
  @NotNull Tag self();

  @Override
  @NotNull Collection<@NotNull ? extends Tag> immediateTags();

  /** @see Class#isInstance(Object) */
  boolean isInstance(@Nullable Datum datum);

  <D extends Datum> D checkAssignable(@NotNull D datum) throws IllegalArgumentException;

  <D extends Datum> D checkMeta(@Nullable D meta) throws IllegalArgumentException;

  @Override
  @NotNull DataType dataType();

  @Nullable DatumType declaredMetaType();

  @Override
  @Nullable DatumType metaType();

  @NotNull Val.Imm createValue(@Nullable ErrorValue errorOrNull);

  interface Raw extends DatumType, Type.Raw {

    @Override
    @NotNull Val.Imm.Raw createValue(@Nullable ErrorValue errorOrNull);

  }

  interface Static<
      MyImmDatum extends Datum.Imm.Static,
      MyDatumBuilder extends Datum.Builder.Static<MyImmDatum>,
      MyImmVal extends Val.Imm.Static,
      MyValBuilder extends Val.Builder.Static<MyImmVal, MyDatumBuilder>,
      MyImmData extends Data.Imm.Static,
      MyDataBuilder extends Data.Builder.Static<MyImmData>
  > extends DatumType, Type.Static<MyImmData, MyDataBuilder> {

    @Override
    @NotNull MyDataBuilder createDataBuilder();

    @Override
    @NotNull MyImmVal createValue(@Nullable ErrorValue errorOrNull);

  }

}

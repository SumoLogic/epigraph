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
import ws.epigraph.data.Data;
import ws.epigraph.data.ListDatum;
import ws.epigraph.data.Val;

import java.util.List;

public interface ListType extends DatumType, ListTypeApi {

  @Override
  @NotNull List<@NotNull ? extends ListType> immediateSupertypes();

  @Override
  @NotNull List<@NotNull ? extends ListType> supertypes();

  @Override
  @NotNull DataType elementType();

  @NotNull ListDatum.Builder createBuilder();

  interface Raw extends ListType, DatumType.Raw {}

  interface Static<
      MyImmDatum extends ListDatum.Imm.Static,
      MyDatumBuilder extends ListDatum.Builder.Static<MyImmDatum, MyValBuilder>,
      MyImmVal extends Val.Imm.Static,
      MyValBuilder extends Val.Builder.Static<MyImmVal, MyDatumBuilder>,
      MyImmData extends Data.Imm.Static,
      MyDataBuilder extends Data.Builder.Static<MyImmData>
      > extends ListType, DatumType.Static<MyImmDatum, MyDatumBuilder, MyImmVal, MyValBuilder, MyImmData, MyDataBuilder> {}

}

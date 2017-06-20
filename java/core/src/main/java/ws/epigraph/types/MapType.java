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
import ws.epigraph.data.Datum;
import ws.epigraph.data.MapDatum;
import ws.epigraph.data.Val;

import java.util.List;

public interface MapType extends DatumType, MapTypeApi {

  @Override
  @NotNull TypeKind kind();

  @Override
  @NotNull List<@NotNull ? extends MapType> immediateSupertypes();

  @Override
  @NotNull List<@NotNull ? extends MapType> supertypes();

  @Override
  @NotNull DatumType keyType();

  @Override
  @NotNull DataType valueType();

  @NotNull MapDatum.Builder createBuilder();

  interface Raw extends MapType, DatumType.Raw {}

  interface Static<
      K extends Datum.Imm.Static,
      MyImmDatum extends MapDatum.Imm.Static<K>,
      MyDatumBuilder extends MapDatum.Builder.Static<K, MyImmDatum, MyValBuilder>,
      MyImmVal extends Val.Imm.Static,
      MyValBuilder extends Val.Builder.Static<MyImmVal, MyDatumBuilder>,
      MyImmData extends Data.Imm.Static,
      MyDataBuilder extends Data.Builder.Static<MyImmData>
      > extends MapType, DatumType.Static<MyImmDatum, MyDatumBuilder, MyImmVal, MyValBuilder, MyImmData, MyDataBuilder> {}
}

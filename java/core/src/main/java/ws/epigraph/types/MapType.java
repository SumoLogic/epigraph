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

/* Created by yegor on 9/20/16. */

package ws.epigraph.types;

import ws.epigraph.data.Data;
import ws.epigraph.data.Datum;
import ws.epigraph.data.MapDatum;
import ws.epigraph.data.Val;
import ws.epigraph.names.TypeName;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public abstract class MapType extends DatumType {

  public final @NotNull DatumType keyType;
  public final @NotNull DataType valueType;

  protected MapType(
      @NotNull TypeName name,
      @NotNull List<@NotNull ? extends MapType> immediateSupertypes,
      @NotNull DatumType keyType,
      @NotNull DataType valueType
  ) {
    super(name, immediateSupertypes);
    this.keyType = keyType;
    this.valueType = valueType;
  }

  @Override
  public final @NotNull TypeKind kind() { return TypeKind.MAP; }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<@NotNull ? extends MapType> immediateSupertypes() {
    return (List<? extends MapType>) super.immediateSupertypes();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull Collection<@NotNull ? extends MapType> supertypes() {
    return (Collection<? extends MapType>) super.supertypes();
  }

  public @NotNull DatumType keyType() { return keyType; }

  public @NotNull DataType valueType() { return valueType; }

  public abstract @NotNull MapDatum.Builder createBuilder();


  public interface Static<
      K extends Datum.Imm.Static,
      MyImmDatum extends MapDatum.Imm.Static,
      MyBuilderDatum extends MapDatum.Builder.Static<K, MyImmDatum, MyBuilderVal>,
      MyImmVal extends Val.Imm.Static,
      MyBuilderVal extends Val.Builder.Static<MyImmVal, MyBuilderDatum>,
      MyImmData extends Data.Imm.Static,
      MyBuilderData extends Data.Builder.Static<MyImmData>
      > extends DatumType.Static<MyImmDatum, MyBuilderDatum, MyImmVal, MyBuilderVal, MyImmData, MyBuilderData> {}


}

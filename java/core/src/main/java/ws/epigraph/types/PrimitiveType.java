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

/* Created by yegor on 7/22/16. */

package ws.epigraph.types;

import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Data;
import ws.epigraph.data.PrimitiveDatum;
import ws.epigraph.data.Val;
import ws.epigraph.names.QualifiedTypeName;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public abstract class PrimitiveType<Native> extends DatumType implements PrimitiveTypeApi {

  protected PrimitiveType(
      @NotNull QualifiedTypeName name,
      @NotNull List<@NotNull ? extends PrimitiveType<Native>> immediateSupertypes,
      @Nullable DatumType declaredMetaType
  ) { super(name, immediateSupertypes, declaredMetaType); }

  @Override
  public final @NotNull TypeKind kind() { return TypeKind.PRIMITIVE; }

  @Override
  public @NotNull QualifiedTypeName name() { return (QualifiedTypeName) super.name(); }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<@NotNull ? extends PrimitiveType<Native>> immediateSupertypes() {
    return (List<? extends PrimitiveType<Native>>) super.immediateSupertypes();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<@NotNull ? extends PrimitiveType<Native>> supertypes() {
    return (List<? extends PrimitiveType<Native>>) super.supertypes();
  }

  public abstract @NotNull PrimitiveDatum.Builder<Native> createBuilder(@NotNull Native val);

  //public abstract @NotNull PrimitiveDatum.Imm<Native> createImmutable(@NotNull Native val);


  public interface Raw extends DatumType.Raw {} // TODO parameterize with Native?


  public interface Static<Native,
      MyImmDatum extends PrimitiveDatum.Imm.Static<Native>,
      MyDatumBuilder extends PrimitiveDatum.Builder.Static<Native, MyImmDatum>,
      MyImmVal extends Val.Imm.Static,
      MyBuilderVal extends Val.Builder.Static<MyImmVal, MyDatumBuilder>,
      MyImmData extends Data.Imm.Static,
      MyBuilderData extends Data.Builder.Static<MyImmData>
      > extends DatumType.Static<MyImmDatum, MyDatumBuilder, MyImmVal, MyBuilderVal, MyImmData, MyBuilderData> {

    @NotNull MyDatumBuilder createBuilder(@NotNull Native val);

    //@NotNull MyImmDatum createImmutable(@NotNull Native val);

  }


}

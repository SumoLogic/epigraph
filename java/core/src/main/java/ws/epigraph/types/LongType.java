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

/* Created by yegor on 9/6/16. */

package ws.epigraph.types;

import ws.epigraph.data.Data;
import ws.epigraph.data.LongDatum;
import ws.epigraph.data.Val;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.names.QualifiedTypeName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public abstract class LongType extends PrimitiveType<Long> {

  protected LongType(
      @NotNull QualifiedTypeName name,
      @NotNull List<@NotNull ? extends LongType> immediateSupertypes,
      @Nullable DatumType declaredMetaType
  ) { super(name, immediateSupertypes, declaredMetaType); }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<@NotNull ? extends LongType> immediateSupertypes() {
    return (List<? extends LongType>) super.immediateSupertypes();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<@NotNull ? extends LongType> supertypes() {
    return (List<? extends LongType>) super.supertypes();
  }

  @Override
  public abstract @NotNull LongDatum.Builder createBuilder(@NotNull Long val);


  public static final class Raw extends LongType implements PrimitiveType.Raw {

    protected Raw(
        @NotNull QualifiedTypeName name,
        @NotNull List<@NotNull ? extends LongType> immediateSupertypes,
        @Nullable DatumType declaredMetaType
    ) { super(name, immediateSupertypes, declaredMetaType); }

    @Override
    public @NotNull LongDatum.Builder.Raw createBuilder(@NotNull Long val) {
      return new LongDatum.Builder.Raw(this, val);
    }

    @Override
    public @NotNull Val.Imm.Raw createValue(@Nullable ErrorValue errorOrNull) {
      return Val.Imm.Raw.create(errorOrNull);
    }

    @Override
    public @NotNull Data.Builder.Raw createDataBuilder() { return new Data.Builder.Raw(this); }

  }


  public static abstract class Static<
      MyImmDatum extends LongDatum.Imm.Static,
      MyDatumBuilder extends LongDatum.Builder.Static<MyImmDatum, MyBuilderVal>,
      MyImmVal extends Val.Imm.Static,
      MyBuilderVal extends Val.Builder.Static<MyImmVal, MyDatumBuilder>,
      MyImmData extends Data.Imm.Static,
      MyDataBuilder extends Data.Builder.Static<MyImmData>
      > extends LongType implements
      PrimitiveType.Static<Long, MyImmDatum, MyDatumBuilder, MyImmVal, MyBuilderVal, MyImmData, MyDataBuilder> {

    private final @NotNull Function<LongDatum.Builder.@NotNull Raw, @NotNull MyDatumBuilder> datumBuilderConstructor;

    private final @NotNull Function<Val.Imm.@NotNull Raw, @NotNull MyImmVal> immValConstructor;

    private final @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor;

    protected Static(
        @NotNull QualifiedTypeName name,
        @NotNull List<? extends LongType> immediateSupertypes,
        @Nullable DatumType declaredMetaType,
        @NotNull Function<LongDatum.Builder.@NotNull Raw, @NotNull MyDatumBuilder> datumBuilderConstructor,
        @NotNull Function<Val.Imm.@NotNull Raw, @NotNull MyImmVal> immValConstructor,
        @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor
    ) {
      super(name, immediateSupertypes, declaredMetaType);
      this.datumBuilderConstructor = datumBuilderConstructor;
      this.immValConstructor = immValConstructor;
      this.dataBuilderConstructor = dataBuilderConstructor;
    }

    @Override
    public final @NotNull MyDatumBuilder createBuilder(@NotNull Long val) {
      return datumBuilderConstructor.apply(new LongDatum.Builder.Raw(this, val));
    }

    @Override
    public final @NotNull MyImmVal createValue(@Nullable ErrorValue errorOrNull) {
      return immValConstructor.apply(Val.Imm.Raw.create(errorOrNull));
    }

    @Override
    public final @NotNull MyDataBuilder createDataBuilder() {
      return dataBuilderConstructor.apply(new Data.Builder.Raw(this));
    }

  }


}

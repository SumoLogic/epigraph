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

import ws.epigraph.data.Data;
import ws.epigraph.data.IntegerDatum;
import ws.epigraph.data.Val;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.names.QualifiedTypeName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public abstract class IntegerType extends PrimitiveType<Integer> {

  protected IntegerType(
      @NotNull QualifiedTypeName name,
      @NotNull List<@NotNull ? extends IntegerType> immediateSupertypes,
      @Nullable DatumType declaredMetaType
  ) { super(name, immediateSupertypes, declaredMetaType); }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<@NotNull ? extends IntegerType> immediateSupertypes() {
    return (List<? extends IntegerType>) super.immediateSupertypes();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<@NotNull ? extends IntegerType> supertypes() {
    return (List<? extends IntegerType>) super.supertypes();
  }

  @Override
  public abstract @NotNull IntegerDatum.Builder createBuilder(@NotNull Integer val);


  public static final class Raw extends IntegerType implements PrimitiveType.Raw {

    protected Raw(
        @NotNull QualifiedTypeName name,
        @NotNull List<@NotNull ? extends IntegerType> immediateSupertypes,
        @Nullable DatumType declaredMetaType
    ) { super(name, immediateSupertypes, declaredMetaType); }

    @Override
    public @NotNull IntegerDatum.Builder.Raw createBuilder(@NotNull Integer val) {
      return new IntegerDatum.Builder.Raw(this, val);
    }

    @Override
    public @NotNull Val.Imm.Raw createValue(@Nullable ErrorValue errorOrNull) {
      return Val.Imm.Raw.create(errorOrNull);
    }

    @Override
    public @NotNull Data.Builder.Raw createDataBuilder() { return new Data.Builder.Raw(this); }

  }


  public abstract static class Static< // TODO MyType extends Type.Static<MyType>?
      MyImmDatum extends IntegerDatum.Imm.Static,
      MyDatumBuilder extends IntegerDatum.Builder.Static<MyImmDatum, MyBuilderVal>,
      MyImmVal extends Val.Imm.Static,
      MyBuilderVal extends Val.Builder.Static<MyImmVal, MyDatumBuilder>,
      MyImmData extends Data.Imm.Static,
      MyDataBuilder extends Data.Builder.Static<MyImmData>
      > extends IntegerType implements
      PrimitiveType.Static<Integer, MyImmDatum, MyDatumBuilder, MyImmVal, MyBuilderVal, MyImmData, MyDataBuilder> {

    private final @NotNull Function<IntegerDatum.Builder.@NotNull Raw, @NotNull MyDatumBuilder> datumBuilderConstructor;

    private final @NotNull Function<Val.Imm.@NotNull Raw, @NotNull MyImmVal> immValConstructor;

    private final @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor;

    protected Static(
        @NotNull QualifiedTypeName name,
        @NotNull List<? extends IntegerType> immediateSupertypes,
        @Nullable DatumType declaredMetaType,
        @NotNull Function<IntegerDatum.Builder.@NotNull Raw, @NotNull MyDatumBuilder> datumBuilderConstructor,
        @NotNull Function<Val.Imm.@NotNull Raw, @NotNull MyImmVal> immValConstructor,
        @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor
    ) {
      super(name, immediateSupertypes, declaredMetaType);
      this.datumBuilderConstructor = datumBuilderConstructor;
      this.immValConstructor = immValConstructor;
      this.dataBuilderConstructor = dataBuilderConstructor;
    }

    @Override
    public final @NotNull MyDatumBuilder createBuilder(@NotNull Integer val) {
      return datumBuilderConstructor.apply(new IntegerDatum.Builder.Raw(this, val));
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

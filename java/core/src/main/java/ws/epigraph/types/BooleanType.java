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

import ws.epigraph.annotations.Annotations;
import ws.epigraph.data.BooleanDatum;
import ws.epigraph.data.Data;
import ws.epigraph.data.Val;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.names.QualifiedTypeName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public abstract class BooleanType extends PrimitiveType<Boolean> {

  protected BooleanType(
      @NotNull QualifiedTypeName name,
      @NotNull List<@NotNull ? extends BooleanType> immediateSupertypes,
      @Nullable DatumType declaredMetaType,
      @NotNull Annotations annotations
  ) { super(name, immediateSupertypes, declaredMetaType, annotations); }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<@NotNull ? extends BooleanType> immediateSupertypes() {
    return (List<? extends BooleanType>) super.immediateSupertypes();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<@NotNull ? extends BooleanType> supertypes() {
    return (List<? extends BooleanType>) super.supertypes();
  }

  @Override
  public @NotNull PrimitiveTypeApi.PrimitiveKind primitiveKind() { return PrimitiveKind.BOOLEAN; }

  @Override
  public abstract @NotNull BooleanDatum.Builder createBuilder(@NotNull Boolean val);


  public static final class Raw extends BooleanType implements PrimitiveType.Raw {

    protected Raw(
        @NotNull QualifiedTypeName name,
        @NotNull List<@NotNull ? extends BooleanType> immediateSupertypes,
        @Nullable DatumType declaredMetaType,
        @NotNull Annotations annotations
    ) { super(name, immediateSupertypes, declaredMetaType, annotations); }

    @Override
    public @NotNull BooleanDatum.Builder.Raw createBuilder(@NotNull Boolean val) {
      return new BooleanDatum.Builder.Raw(this, val);
    }

    @Override
    public @NotNull Val.Imm createValueOfNullable(@Nullable Boolean val) {
      return val == null ? createValue(null) : createBuilder(val).asValue().toImmutable();
    }

    @Override
    public @NotNull Val.Imm.Raw createValue(@Nullable ErrorValue errorOrNull) {
      return Val.Imm.Raw.create(errorOrNull);
    }

    @Override
    public @NotNull Data.Builder.Raw createDataBuilder() { return new Data.Builder.Raw(this); }

  }


  public abstract static class Static< // TODO MyType extends Type.Static<MyType>?
      MyImmDatum extends BooleanDatum.Imm.Static,
      MyDatumBuilder extends BooleanDatum.Builder.Static<MyImmDatum, MyValBuilder>,
      MyImmVal extends Val.Imm.Static,
      MyValBuilder extends Val.Builder.Static<MyImmVal, MyDatumBuilder>,
      MyImmData extends Data.Imm.Static,
      MyDataBuilder extends Data.Builder.Static<MyImmData>
      > extends BooleanType implements
      PrimitiveType.Static<Boolean, MyImmDatum, MyDatumBuilder, MyImmVal, MyValBuilder, MyImmData, MyDataBuilder> {

    private final @NotNull Function<BooleanDatum.Builder.@NotNull Raw, @NotNull MyDatumBuilder> datumBuilderConstructor;

    private final @NotNull Function<Val.Imm.@NotNull Raw, @NotNull MyImmVal> immValConstructor;

    private final @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor;

    protected Static(
        @NotNull QualifiedTypeName name,
        @NotNull List<? extends BooleanType> immediateSupertypes,
        @Nullable DatumType declaredMetaType,
        @NotNull Function<BooleanDatum.Builder.@NotNull Raw, @NotNull MyDatumBuilder> datumBuilderConstructor,
        @NotNull Function<Val.Imm.@NotNull Raw, @NotNull MyImmVal> immValConstructor,
        @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor,
        @NotNull Annotations annotations
    ) {
      super(name, immediateSupertypes, declaredMetaType, annotations);
      this.datumBuilderConstructor = datumBuilderConstructor;
      this.immValConstructor = immValConstructor;
      this.dataBuilderConstructor = dataBuilderConstructor;
    }

    @Override
    public final @NotNull MyDatumBuilder createBuilder(@NotNull Boolean val) {
      return datumBuilderConstructor.apply(new BooleanDatum.Builder.Raw(this, val));
    }

    @Override
    public @NotNull MyImmVal createValueOfNullable(@Nullable Boolean val) {
      return val == null ? createValue(null) : createBuilder(val).asValue().toImmutable();
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

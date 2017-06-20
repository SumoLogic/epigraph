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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Data;
import ws.epigraph.data.ListDatum;
import ws.epigraph.data.Val;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.names.QualifiedTypeName;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public abstract class NamedListType extends ListTypeImpl {

  NamedListType(
      @NotNull QualifiedTypeName name,
      @NotNull List<@NotNull ? extends ListType> immediateSupertypes,
      @Nullable DatumType declaredMetaType,
      @NotNull DataType elementDataType
  ) { super(name, immediateSupertypes, elementDataType, declaredMetaType); }

  @Override
  public @NotNull QualifiedTypeName name() { return (QualifiedTypeName) super.name(); }


  public static final class Raw extends NamedListType implements ListType.Raw {

    public Raw(
        @NotNull QualifiedTypeName name,
        @NotNull List<? extends ListType> immediateSupertypes,
        @Nullable DatumType declaredMetaType,
        @NotNull DataType elementDataType
    ) { super(name, immediateSupertypes, declaredMetaType, elementDataType); }

    private static @Nullable Tag defaultTag(Type type, @Nullable Tag tag) {
      return tag == null ? null : type.tagsMap().get(tag.name);
    }

    @Override
    public @NotNull ListDatum.Builder createBuilder() { return new ListDatum.Builder.Raw(this); }

    @Override
    public @NotNull Val.Imm.Raw createValue(@Nullable ErrorValue errorOrNull) {
      return Val.Imm.Raw.create(errorOrNull);
    }

    @Override
    public @NotNull Data.Builder createDataBuilder() { return new Data.Builder.Raw(this); }

  }


  public abstract static class Static<
      MyImmDatum extends ListDatum.Imm.Static,
      MyDatumBuilder extends ListDatum.Builder.Static<MyImmDatum, MyBuilderVal>,
      MyImmVal extends Val.Imm.Static,
      MyBuilderVal extends Val.Builder.Static<MyImmVal, MyDatumBuilder>,
      MyImmData extends Data.Imm.Static,
      MyDataBuilder extends Data.Builder.Static<MyImmData>
      > extends NamedListType
      implements ListType.Static<MyImmDatum, MyDatumBuilder, MyImmVal, MyBuilderVal, MyImmData, MyDataBuilder> {

    private final @NotNull Function<ListDatum.Builder.@NotNull Raw, @NotNull MyDatumBuilder> datumBuilderConstructor;

    private final @NotNull Function<Val.Imm.@NotNull Raw, @NotNull MyImmVal> immValConstructor;

    private final @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor;

    @SafeVarargs
    protected Static(
        @NotNull QualifiedTypeName name,
        @Nullable DatumType declaredMetaType,
        @NotNull DataType elementDataType,
        @NotNull Function<ListDatum.Builder.@NotNull Raw, @NotNull MyDatumBuilder> datumBuilderConstructor,
        @NotNull Function<Val.Imm.@NotNull Raw, @NotNull MyImmVal> immValConstructor,
        @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor,
        @NotNull ListType.Static<
            ? super MyImmDatum,
            ? extends ListDatum.Builder.Static<? super MyImmDatum, ?>,
            ? super MyImmVal,
            ? extends Val.Builder.Static<? super MyImmVal, ?>,
            ? super MyImmData,
            ? extends Data.Builder.Static<? super MyImmData>
        >... immediateSupertypes
    ) {
      super(name, Arrays.asList(immediateSupertypes), declaredMetaType, elementDataType);
      this.datumBuilderConstructor = datumBuilderConstructor;
      this.immValConstructor = immValConstructor;
      this.dataBuilderConstructor = dataBuilderConstructor;
    }

    @SafeVarargs
    protected static <MyImmDatum, MyImmVal, MyImmData> @NotNull List<@NotNull ? extends ListType.Static<
        ? super MyImmDatum,
        ? extends ListDatum.Builder.Static<? super MyImmDatum, ?>,
        ? super MyImmVal,
        ? extends Val.Builder.Static<? super MyImmVal, ?>,
        ? super MyImmData,
        ? extends Data.Builder.Static<? super MyImmData>
    >> parents(ListType.Static<
        ? super MyImmDatum,
        ? extends ListDatum.Builder.Static<? super MyImmDatum, ?>,
        ? super MyImmVal,
        ? extends Val.Builder.Static<? super MyImmVal, ?>,
        ? super MyImmData,
        ? extends Data.Builder.Static<? super MyImmData>
    >... supertypes) { return Arrays.asList(supertypes); }

    @Override
    public final @NotNull MyDatumBuilder createBuilder() {
      return datumBuilderConstructor.apply(new ListDatum.Builder.Raw(this));
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

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

/* Created by yegor on 7/22/16. */

package ws.epigraph.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Data;
import ws.epigraph.data.ListDatum;
import ws.epigraph.data.Val;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.names.AnonListTypeName;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AnonListType extends ListTypeImpl {

  AnonListType(
      @NotNull List<@NotNull ? extends AnonListType> immediateSupertypes,
      @Nullable DatumType declaredMetaType,
      @NotNull DataType elementDataType
  ) { super(new AnonListTypeName(elementDataType.name), immediateSupertypes, elementDataType, declaredMetaType); }

  @Override
  public @NotNull AnonListTypeName name() { return (AnonListTypeName) super.name(); }

  public static final class Raw extends AnonListType implements ListType.Raw {

    public Raw(@Nullable DatumType declaredMetaType, @NotNull DataType elementDataType) {
      super(immediateSupertypes(elementDataType), declaredMetaType, elementDataType);
    }

    private static @NotNull List<@NotNull ? extends AnonListType.Raw> immediateSupertypes(@NotNull DataType elementDataType) {
      // FIXME too many new raw types - cache/resolve
      return elementDataType.type.immediateSupertypes().stream().map(st -> new AnonListType.Raw(
          ((DatumType) st).declaredMetaType(),
          new DataType(
              st,
              defaultTag(st, elementDataType.defaultTag)
          )
      )).collect(Collectors.toList());
    }

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
      > extends AnonListType
      implements ListType.Static<MyImmDatum, MyDatumBuilder, MyImmVal, MyBuilderVal, MyImmData, MyDataBuilder> {

    private final @NotNull Function<ListDatum.Builder.@NotNull Raw, @NotNull MyDatumBuilder> datumBuilderConstructor;

    private final @NotNull Function<Val.Imm.@NotNull Raw, @NotNull MyImmVal> immValConstructor;

    private final @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor;

    protected Static(
        @NotNull List<@NotNull ? extends AnonListType.Static<
            ?,// super MyImmDatum,
            ?,// extends ListDatum.Mut.Static<? super MyImmDatum>,
            ?,// super MyImmVal,
            ?,// extends Val.Mut.Static<? super MyImmVal, ? extends ListDatum.Mut.Static<? super MyImmDatum>>,
            ?,// super MyImmData,
            ? // extends Data.Mut.Static<? super MyImmData>
            >> immediateSupertypes,
        @Nullable DatumType declaredMetaType,
        @NotNull DataType elementDataType,
        @NotNull Function<ListDatum.Builder.@NotNull Raw, @NotNull MyDatumBuilder> datumBuilderConstructor,
        @NotNull Function<Val.Imm.@NotNull Raw, @NotNull MyImmVal> immValConstructor,
        @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor
    ) {
      super(immediateSupertypes, declaredMetaType, elementDataType);
      this.datumBuilderConstructor = datumBuilderConstructor;
      this.immValConstructor = immValConstructor;
      this.dataBuilderConstructor = dataBuilderConstructor;
    }

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

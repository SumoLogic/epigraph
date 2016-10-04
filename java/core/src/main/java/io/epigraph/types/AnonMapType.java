/* Created by yegor on 7/22/16. */

package io.epigraph.types;

import io.epigraph.data.Data;
import io.epigraph.data.Datum;
import io.epigraph.data.MapDatum;
import io.epigraph.data.Val;
import io.epigraph.errors.ErrorValue;
import io.epigraph.names.AnonMapTypeName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AnonMapType extends MapType {

  protected AnonMapType(
      @NotNull List<@NotNull ? extends AnonMapType> immediateSupertypes,
      @NotNull DatumType keyType,
      @NotNull DataType valueType
  ) { super(new AnonMapTypeName(keyType.name(), valueType.name), immediateSupertypes, keyType, valueType); }

  @Override
  public @NotNull AnonMapTypeName name() { return (AnonMapTypeName) super.name(); }


  public static final class Raw extends AnonMapType implements MapType.Raw {

    public Raw(@NotNull DatumType keyType, @NotNull DataType valueType) {
      super(immediateSupertypes(keyType, valueType), keyType, valueType);
    }

    private static @NotNull List<@NotNull ? extends AnonMapType.Raw> immediateSupertypes(
        @NotNull DatumType keyType,
        @NotNull DataType valueType
    ) {
      return valueType.type.immediateSupertypes().stream().map(st -> new AnonMapType.Raw(// FIXME too many new raw types
          keyType,
          new DataType(st, defaultTag(st, valueType.defaultTag))
      )).collect(Collectors.toList());
    }

    private static @Nullable Tag defaultTag(Type type, @Nullable Tag tag) {
      return tag == null ? null : type.tagsMap().get(tag.name);
    }

    @Override
    public @NotNull MapDatum.Builder createBuilder() { return new MapDatum.Builder.Raw(this); }

    @Override
    public @NotNull Val.Imm.Raw createValue(@Nullable ErrorValue errorOrNull) {
      return Val.Imm.Raw.create(errorOrNull);
    }

    @Override
    public @NotNull Data.Builder createDataBuilder() { return new Data.Builder.Raw(this); }

  }


  public static abstract class Static<
      K extends Datum.Imm.Static,
      MyImmDatum extends MapDatum.Imm.Static,
      MyDatumBuilder extends MapDatum.Builder.Static<K, MyImmDatum, MyBuilderVal>,
      MyImmVal extends Val.Imm.Static,
      MyBuilderVal extends Val.Builder.Static<MyImmVal, MyDatumBuilder>,
      MyImmData extends Data.Imm.Static,
      MyDataBuilder extends Data.Builder.Static<MyImmData>
      > extends AnonMapType
      implements MapType.Static<K, MyImmDatum, MyDatumBuilder, MyImmVal, MyBuilderVal, MyImmData, MyDataBuilder> {

    private final @NotNull Function<MapDatum.Builder.@NotNull Raw, @NotNull MyDatumBuilder> datumBuilderConstructor;

    private final @NotNull Function<Val.Imm.@NotNull Raw, @NotNull MyImmVal> immValConstructor;

    private final @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor;

    protected Static(
        @NotNull List<@NotNull ? extends AnonMapType.Static<
            K,
            ?,// super MyImmDatum,
            ?,// extends MapDatum.Mut.Static<? super MyImmDatum>,
            ?,// super MyImmVal,
            ?,// extends Val.Mut.Static<? super MyImmVal, ? extends MapDatum.Mut.Static<? super MyImmDatum>>,
            ?,// super MyImmData,
            ? // extends Data.Mut.Static<? super MyImmData>
            >> immediateSupertypes,
        @NotNull DatumType/*.Static<K, ?, ?, ?, ?, ?>*/ keyType,
        @NotNull DataType valueType,
        @NotNull Function<MapDatum.Builder.@NotNull Raw, @NotNull MyDatumBuilder> datumBuilderConstructor,
        @NotNull Function<Val.Imm.@NotNull Raw, @NotNull MyImmVal> immValConstructor,
        @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor
    ) {
      super(immediateSupertypes, keyType, valueType);
      this.datumBuilderConstructor = datumBuilderConstructor;
      this.immValConstructor = immValConstructor;
      this.dataBuilderConstructor = dataBuilderConstructor;
    }

    @Override
    public final @NotNull MyDatumBuilder createBuilder() {
      return datumBuilderConstructor.apply(new MapDatum.Builder.Raw(this));
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

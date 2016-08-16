/* Created by yegor on 8/15/16. */

package com.sumologic.epigraph.java

import java.nio.file.Path

import com.sumologic.epigraph.java.NewlineStringInterpolator.NewlineHelper
import com.sumologic.epigraph.schema.compiler.{CAnonListType, CContext, CType}

class AnonListGen(from: CAnonListType, ctx: CContext) extends JavaTypeGen[CAnonListType](from, ctx) {

  private val e = t.elementTypeRef.resolved

  override protected def relativeFilePath: Path = ???

  override def generate: String = sn"""\

  /**
   * Base interface for `${t.name.name}` datum.
   */
  interface List extends ${withCollections(t)}ListDatum.Static {

    ${elementName(e)}.List.Type type = new ${elementName(e)}.List.Type();

    // default tag values
    java.util.List<@Nullable ? extends ${elementName(e)}.Value> values();

    // default tag datums
    java.util.List<@Nullable ? extends ${elementName(e)}> datums();

    /**
     * Class for `${t.name.name}` datum type.
     */
    final class Type extends AnonListType.Static<
        ${elementName(e)}.List.Imm,
        ${elementName(e)}.List.Builder,
        ${elementName(e)}.List.Imm.Value,
        ${elementName(e)}.List.Builder.Value,
        ${elementName(e)}.List.Imm.Data,
        ${elementName(e)}.List.Builder.Data
    > {

      Type() {
        super(
            false,
            ${elementName(e)}.type,
            ${elementName(e)}.List.Builder::new,
            ${elementName(e)}.List.Builder.Value::new,
            ${elementName(e)}.List.Builder.Data::new
        );
      }
${if (ctx.hasAnonListOf(t)) { sn"""\

      @Override
      protected @NotNull Supplier<ListType> listTypeSupplier() { return () -> ${elementName(t)}.List.type; }
""" } else ""
  }\

    }

    /**
     * Base interface for `${t.name.name}` value (holding a datum or an error).
     */
    interface Value extends Val.Static {

      @Override
      @NotNull ${elementName(e)}.List.Imm.Value toImmutable();

      @Override
      @Nullable ${elementName(e)}.List getDatum();

    }

    /**
     * Base interface for `${t.name.name}` data (holding single default representation of the type).
     */
    interface Data extends io.epigraph.data.Data.Static {

      @Override
      @NotNull ${elementName(e)}.List.Imm.Data toImmutable();

      @Nullable ${elementName(e)}.List.Value get_value(); // default tag value (TODO: if defined)

      @Nullable ${elementName(e)}.List get(); // default tag datum (TODO: if defined)

    }

    /**
     * Immutable interface for `${t.name.name}` datum.
     */
    interface Imm extends ${elementName(e)}.List, ListDatum.Imm.Static {

      // default tag values
      @Override
      java.util.List<@Nullable ? extends ${elementName(e)}.Imm.Value> values();

      // default tag datums
      @Override
      java.util.List<@Nullable ? extends ${elementName(e)}.Imm> datums();

      /** Private implementation of `${elementName(e)}.Imm` interface. */
      final class Impl extends ListDatum.Imm.Static.Impl<${elementName(e)}.List.Imm> implements ${elementName(e)}.List.Imm {

        Impl(@NotNull ListDatum.Imm.Raw raw) { super(${elementName(e)}.List.type, raw); }

        // method is private to not expose datas() for non-union types (so simple type can be replaced with union type without breaking backwards-compatibility)
        private java.util.List<@NotNull ? extends ${elementName(e)}.Imm.Data> datas() {
          return (java.util.List<? extends ${elementName(e)}.Imm.Data>) _raw()._elements();
        }

        @Override // implied default tag values
        public java.util.List<@Nullable ? extends ${elementName(e)}.Imm.Value> values() {
          return new Unmodifiable.ListView<${elementName(e)}.Imm.Data, ${elementName(e)}.Imm.Value>(
              datas(),
              ${elementName(e)}.Imm.Data::get_value
          );
        }

        @Override // implied default tag datums
        public java.util.List<@Nullable ? extends ${elementName(e)}.Imm> datums() {
          return new Unmodifiable.ListView<${elementName(e)}.Imm.Data, ${elementName(e)}.Imm>(datas(), ${elementName(e)}.Imm.Data::get);
        }

      }

      /**
       * Immutable interface for `${t.name.name}` value (holding an immutable datum or an error).
       */
      interface Value extends ${elementName(e)}.List.Value, Val.Imm.Static {

        @Override
        @Nullable ${elementName(e)}.List.Imm getDatum();

        /** Private implementation of `${elementName(e)}.Imm.Value` interface. */
        final class Impl extends Val.Imm.Static.Impl<${elementName(e)}.List.Imm.Value, ${elementName(e)}.List.Imm>
            implements ${elementName(e)}.List.Imm.Value {

          Impl(@NotNull Val.Imm.Raw raw) { super(${elementName(e)}.List.type, raw); }

        }

      }

      /**
       * Immutable interface for `${t.name.name}` data (holding single default representation of the type).
       */
      interface Data extends ${elementName(e)}.List.Data, io.epigraph.data.Data.Imm.Static {

        @Override
        @Nullable ${elementName(e)}.List.Imm.Value get_value(); // TODO if default is defined

        @Override
        @Nullable ${elementName(e)}.List.Imm get(); // TODO if default is defined

        /** Private implementation of `${elementName(e)}.Imm.Data` interface. */
        final class Impl extends io.epigraph.data.Data.Imm.Static.Impl<${elementName(e)}.List.Imm.Data>
            implements ${elementName(e)}.List.Imm.Data {

          Impl(@NotNull io.epigraph.data.Data.Imm.Raw raw) { super(${elementName(e)}.List.type, raw); }

          @Override
          public @Nullable ${elementName(e)}.List.Imm.Value get_value() {
            return (${elementName(e)}.List.Imm.Value) _raw()._getValue(${elementName(e)}.List.type.self);
          }

          @Override
          public @Nullable ${elementName(e)}.List.Imm get() {
            return Util.apply(${elementName(e)}.List.Imm.Value::getDatum, get_value());
          }

        }

      }

    }

    /**
     * Builder for `${t.name.name}` datum.
     */
    final class Builder extends ListDatum.Mut.Static<${elementName(e)}.List.Imm> implements ${elementName(e)}.List {

      Builder(@NotNull ListDatum.Mut.Raw raw) { super(${elementName(e)}.List.type, raw, ${elementName(e)}.List.Imm.Impl::new); }

      // method is private to not expose datas() for non-union types (so simple type can be replaced with union type without breaking backwards-compatibility)
      private java.util.List<${elementName(e)}.Builder.@NotNull Data> datas() {
        return (java.util.List<${elementName(e)}.Builder.Data>) _raw()._elements();
      }

      @Override
      public java.util.List<${elementName(e)}.Builder.Value> values() {
        return new ListView<>(
            datas(),
            ${elementName(e)}.Builder.Data::get_value,
            ${elementName(e)}.Builder.Data::set_value,
            ${elementName(e)}.type::createMutableData
        );
      }

      @Override
      public java.util.List<${elementName(e)}.@Nullable Builder> datums() {
        return new ListView<>(
            datas(),
            ${elementName(e)}.Builder.Data::get,
            ${elementName(e)}.Builder.Data::set,
            ${elementName(e)}.type::createMutableData
        );
      }

      /**
       * Builder for `${t.name.name}` value (holding a builder or an error).
       */
      public static final class Value extends Val.Mut.Static<${elementName(e)}.List.Imm.Value, ${elementName(e)}.List.Builder>
          implements ${elementName(e)}.List.Value {

        Value(@NotNull Val.Mut.Raw raw) { super(raw, ${elementName(e)}.List.Imm.Value.Impl::new); }

      }

      /**
       * Builder for `${t.name.name}` data (holding single default representation of the type).
       */
      public static final class Data extends io.epigraph.data.Data.Mut.Static<${elementName(e)}.List.Imm.Data>
          implements ${elementName(e)}.List.Data {

        Data(@NotNull io.epigraph.data.Data.Mut.Raw raw) {
          super(${elementName(e)}.List.type, raw, ${elementName(e)}.List.Imm.Data.Impl::new);
        }

        // default tag value getter
        @Override
        public @Nullable ${elementName(e)}.List.Builder.Value get_value() {
          return (${elementName(e)}.List.Builder.Value) _raw()._getValue(${elementName(e)}.List.type.self);
        }

        // default tag datum getter
        @Override
        public @Nullable ${elementName(e)}.List.Builder get() {
          return Util.apply(${elementName(e)}.List.Builder.Value::getDatum, get_value());
        }


        // default tag value setter
        public void set_value(@Nullable ${elementName(e)}.List.Builder.Value value) {
          _raw()._setValue(${elementName(e)}.List.type.self, value);
        }

        // default tag datum setter (TODO: if defined)
        public void set(@Nullable ${elementName(e)}.List.Builder datum) {
          _raw()._getOrCreateTagValue(${elementName(e)}.List.type.self)._raw().setDatum(datum);
        }

      }


    }

  }
"""

}

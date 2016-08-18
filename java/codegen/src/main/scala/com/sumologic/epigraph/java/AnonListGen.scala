/* Created by yegor on 8/15/16. */

package com.sumologic.epigraph.java

import java.nio.file.Path

import com.sumologic.epigraph.java.NewlineStringInterpolator.NewlineHelper
import com.sumologic.epigraph.schema.compiler.{CAnonListType, CAnonMapType, CContext, CType, CTypeDef}

class AnonListGen(from: CAnonListType, ctx: CContext) extends JavaTypeGen[CAnonListType](from, ctx) {

  private val e = t.elementTypeRef.resolved

  // TODO respect annotations changing namespace/type names for scala
  protected override def relativeFilePath: Path =
    GenUtils.fqnToPath(getNamedTypeComponent(t).name.fqn.removeLastSegment()).resolve(ln(t) + ".java")

  override def generate: String = sn"""\
package ${pn(t)};

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base interface for `${t.name.name}` datum.
 */
public interface ${baseName(t)} extends${withParents(t)} io.epigraph.data.ListDatum.Static {

  ${baseName(t)}.Type type = new ${baseName(t)}.Type();

  // default tag values // TODO - if declared
  java.util.List<@Nullable ? extends ${lqn(e, t)}.Value> values();

  // default tag datums // TODO - if declared
  java.util.List<@Nullable ? extends ${lqn(e, t)}> datums();

  /**
   * Class for `${t.name.name}` datum type.
   */
  final class Type extends io.epigraph.types.AnonListType.Static<
      ${baseName(t)}.Imm,
      ${baseName(t)}.Builder,
      ${baseName(t)}.Imm.Value,
      ${baseName(t)}.Builder.Value,
      ${baseName(t)}.Imm.Data,
      ${baseName(t)}.Builder.Data
  > {

    Type() {
      super(
          false,
          ${lqn(e, t)}.type,
          ${baseName(t)}.Builder::new,
          ${baseName(t)}.Builder.Value::new,
          ${baseName(t)}.Builder.Data::new
      );
    }
${ctx.getAnonListOf(t).map { lt => sn"""\

    @Override
    protected @NotNull java.util.function.Supplier<io.epigraph.types.ListType> listTypeSupplier() { return () -> ${lqn(lt, t)}.type; }
""" }.getOrElse("")
  }\

  }

  /**
   * Base interface for `${t.name.name}` value (holding a datum or an error).
   */
  interface Value extends${withParents(t, _ + ".Value")} io.epigraph.data.Val.Static {

    @Override
    @NotNull ${baseName(t)}.Imm.Value toImmutable();

    @Override
    @Nullable ${baseName(t)} getDatum();

  }

  /**
   * Base interface for `${t.name.name}` data (holding single default representation of the type).
   */
  interface Data extends${withParents(t, _ + ".Data")} io.epigraph.data.Data.Static {

    @Override
    @NotNull ${baseName(t)}.Imm.Data toImmutable();

    @Nullable ${baseName(t)}.Value get_value(); // default tag value (TODO: if defined)

    @Nullable ${baseName(t)} get(); // default tag datum (TODO: if defined)

  }

  /**
   * Immutable interface for `${t.name.name}` datum.
   */
  interface Imm extends ${baseName(t)},${withParents(t, _ + ".Imm")} io.epigraph.data.ListDatum.Imm.Static {

    // default tag values
    @Override
    java.util.List<@Nullable ? extends ${lqn(e, t)}.Imm.Value> values();

    // default tag datums
    @Override
    java.util.List<@Nullable ? extends ${lqn(e, t)}.Imm> datums();

    /** Private implementation of `${lqn(e, t)}.Imm` interface. */
    final class Impl extends io.epigraph.data.ListDatum.Imm.Static.Impl<${baseName(t)}.Imm> implements ${baseName(t)}.Imm {

      Impl(@NotNull io.epigraph.data.ListDatum.Imm.Raw raw) { super(${baseName(t)}.type, raw); }

      // method is private to not expose datas() for non-union types (so simple type can be replaced with union type without breaking backwards-compatibility)
      private java.util.List<@NotNull ? extends ${lqn(e, t)}.Imm.Data> datas() {
        return (java.util.List<? extends ${lqn(e, t)}.Imm.Data>) _raw()._elements();
      }

      @Override // implied default tag values
      public java.util.List<@Nullable ? extends ${lqn(e, t)}.Imm.Value> values() {
        return new io.epigraph.util.Unmodifiable.ListView<${lqn(e, t)}.Imm.Data, ${lqn(e, t)}.Imm.Value>(
            datas(),
            ${lqn(e, t)}.Imm.Data::get_value
        );
      }

      @Override // implied default tag datums
      public java.util.List<@Nullable ? extends ${lqn(e, t)}.Imm> datums() {
        return new io.epigraph.util.Unmodifiable.ListView<${lqn(e, t)}.Imm.Data, ${lqn(e, t)}.Imm>(datas(), ${lqn(e, t)}.Imm.Data::get);
      }

    }

    /**
     * Immutable interface for `${t.name.name}` value (holding an immutable datum or an error).
     */
    interface Value extends ${baseName(t)}.Value,${withParents(t, _ + ".Imm.Value")} io.epigraph.data.Val.Imm.Static {

      @Override
      @Nullable ${baseName(t)}.Imm getDatum();

      /** Private implementation of `${lqn(e, t)}.Imm.Value` interface. */
      final class Impl extends io.epigraph.data.Val.Imm.Static.Impl<${baseName(t)}.Imm.Value, ${baseName(t)}.Imm>
          implements ${baseName(t)}.Imm.Value {

        Impl(@NotNull io.epigraph.data.Val.Imm.Raw raw) { super(${baseName(t)}.type, raw); }

      }

    }

    /**
     * Immutable interface for `${t.name.name}` data (holding single default representation of the type).
     */
    interface Data extends ${baseName(t)}.Data,${withParents(t, _ + ".Imm.Data")} io.epigraph.data.Data.Imm.Static {

      @Override
      @Nullable ${baseName(t)}.Imm.Value get_value(); // TODO if default is defined

      @Override
      @Nullable ${baseName(t)}.Imm get(); // TODO if default is defined

      /** Private implementation of `${lqn(e, t)}.Imm.Data` interface. */
      final class Impl extends io.epigraph.data.Data.Imm.Static.Impl<${baseName(t)}.Imm.Data>
          implements ${baseName(t)}.Imm.Data {

        Impl(@NotNull io.epigraph.data.Data.Imm.Raw raw) { super(${baseName(t)}.type, raw); }

        @Override
        public @Nullable ${baseName(t)}.Imm.Value get_value() {
          return (${baseName(t)}.Imm.Value) _raw()._getValue(${baseName(t)}.type.self);
        }

        @Override
        public @Nullable ${baseName(t)}.Imm get() {
          return io.epigraph.util.Util.apply(${baseName(t)}.Imm.Value::getDatum, get_value());
        }

      }

    }

  }

  /**
   * Builder for `${t.name.name}` datum.
   */
  final class Builder extends io.epigraph.data.ListDatum.Mut.Static<${baseName(t)}.Imm> implements ${baseName(t)} {

    Builder(@NotNull io.epigraph.data.ListDatum.Mut.Raw raw) { super(${baseName(t)}.type, raw, ${baseName(t)}.Imm.Impl::new); }

    // method is private to not expose datas() for non-union types (so simple type can be replaced with union type without breaking backwards-compatibility)
    private java.util.List<${lqn(e, t)}.Builder.@NotNull Data> datas() {
      return (java.util.List<${lqn(e, t)}.Builder.Data>) _raw()._elements();
    }

    @Override
    public java.util.List<${lqn(e, t)}.Builder.Value> values() {
      return new io.epigraph.util.ListView<>(
          datas(),
          ${lqn(e, t)}.Builder.Data::get_value,
          ${lqn(e, t)}.Builder.Data::set_value,
          ${lqn(e, t)}.type::createMutableData
      );
    }

    @Override
    public java.util.List<${lqn(e, t)}.@Nullable Builder> datums() {
      return new io.epigraph.util.ListView<>(
          datas(),
          ${lqn(e, t)}.Builder.Data::get,
          ${lqn(e, t)}.Builder.Data::set,
          ${lqn(e, t)}.type::createMutableData
      );
    }

    /**
     * Builder for `${t.name.name}` value (holding a builder or an error).
     */
    public static final class Value extends io.epigraph.data.Val.Mut.Static<${baseName(t)}.Imm.Value, ${baseName(t)}.Builder>
        implements ${baseName(t)}.Value {

      Value(@NotNull io.epigraph.data.Val.Mut.Raw raw) { super(raw, ${baseName(t)}.Imm.Value.Impl::new); }

    }

    /**
     * Builder for `${t.name.name}` data (holding single default representation of the type).
     */
    public static final class Data extends io.epigraph.data.Data.Mut.Static<${baseName(t)}.Imm.Data>
        implements ${baseName(t)}.Data {

      Data(@NotNull io.epigraph.data.Data.Mut.Raw raw) {
        super(${baseName(t)}.type, raw, ${baseName(t)}.Imm.Data.Impl::new);
      }

      // default tag value getter
      @Override
      public @Nullable ${baseName(t)}.Builder.Value get_value() {
        return (${baseName(t)}.Builder.Value) _raw()._getValue(${baseName(t)}.type.self);
      }

      // default tag datum getter
      @Override
      public @Nullable ${baseName(t)}.Builder get() {
        return io.epigraph.util.Util.apply(${baseName(t)}.Builder.Value::getDatum, get_value());
      }


      // default tag value setter
      public void set_value(@Nullable ${baseName(t)}.Builder.Value value) {
        _raw()._setValue(${baseName(t)}.type.self, value);
      }

      // default tag datum setter (TODO: if defined)
      public void set(@Nullable ${baseName(t)}.Builder datum) {
        _raw()._getOrCreateTagValue(${baseName(t)}.type.self)._raw().setDatum(datum);
      }

    }


  }

}
"""

}

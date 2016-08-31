/* Created by yegor on 8/15/16. */

package com.sumologic.epigraph.java

import java.nio.file.Path

import com.sumologic.epigraph.java.NewlineStringInterpolator.NewlineHelper
import com.sumologic.epigraph.schema.compiler.{CAnonListType, CContext, CVarTypeDef}

class AnonListGen(from: CAnonListType, ctx: CContext) extends JavaTypeGen[CAnonListType](from, ctx) {

  private val e = t.elementTypeRef.resolved

  private val ev = t.elementValueType

  // TODO respect annotations changing namespace/type names for scala
  protected override def relativeFilePath: Path =
  JavaGenUtils.fqnToPath(getNamedTypeComponent(t).name.fqn.removeLastSegment()).resolve(ln(t) + ".java")

  override def generate: String = /*@formatter:off*/sn"""\
package ${pn(t)};

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base (read) interface for `${t.name.name}` datum.
 */
public interface $ln extends${withParents(t)} io.epigraph.data.ListDatum.Static {

  $ln.Type type = new $ln.Type();
${t.effectiveDefaultElementTagName match { // default element tag (if any) views
      case None => ""
      case Some(dtn) => sn"""\

  ${"/**"}
   * Returns list view of element default tag datums. Elements where the tag datum is not set will be `null`.
   */
  @NotNull java.util.List<@Nullable ? extends ${lqn(tt(t.elementTypeRef, dtn), t)}> datums();

  ${"/**"}
   * Returns list view of element default tag values. Elements where the tag value is not set will be `null`.
   */
  @NotNull java.util.List<@Nullable ? extends ${lqn(tt(t.elementTypeRef, dtn), t)}.Value> values();
"""
  }
}\
${e match { // element tags views (for vartypes)
    case evt: CVarTypeDef => sn"""\

  ${"/**"}
   * Returns list view of element data.
   */
  @NotNull java.util.List<@NotNull ? extends ${lqn(e, t)}> datas();
${
      evt.effectiveTags.map { tag => sn"""\

  ${"/**"}
   * Returns list view of `${tag.name}` tag datums. Elements where the tag value is not set will be `null`.
   */
  @NotNull java.util.List<@Nullable ? extends ${lqn(tt(t.elementTypeRef, tag.name), t)}> ${jn(tag.name + "Datums")}();

  ${"/**"}
   * Returns list view of `${tag.name}` tag values. Elements where the tag value is not set will be `null`.
   */
  @NotNull java.util.List<@Nullable ? extends ${lqn(tt(t.elementTypeRef, tag.name), t)}.Value> ${jn(tag.name + "Values")}();
"""
      }.mkString
}\
"""
    case _ => ""
  }
}\

  /**
   * Class for `${t.name.name}` type.
   */
  final class Type extends io.epigraph.types.AnonListType.Static<
      $ln.Imm,
      $ln.Builder,
      $ln.Imm.Value,
      $ln.Builder.Value,
      $ln.Imm.Data,
      $ln.Builder.Data
  > {

    Type() {
      super(
          false,
          ${lqn(e, t)}.type,
          $ln.Builder::new,
          $ln.Builder.Value::new,
          $ln.Builder.Data::new
      );
    }
$listSupplier\

  }

  /**
   * Base interface for `${t.name.name}` value (holding a datum or an error).
   */
  interface Value extends${withParents(".Value")} io.epigraph.data.Val.Static {

    @Override
    @NotNull $ln.Imm.Value toImmutable();

    @Override
    @Nullable $ln getDatum();

  }

  /**
   * Base interface for `${t.name.name}` data (holding single default representation of the type).
   */
  interface Data extends${withParents(".Data")} io.epigraph.data.Data.Static {

    @Override
    @NotNull $ln.Imm.Data toImmutable();

    /**
     * Returns `${t.name.name}` datum.
     */
    @Nullable $ln.Value get();

    /**
     * Returns `${t.name.name}` value.
     */
    @Nullable $ln.Value get_();

  }

  /**
   * Immutable interface for `${t.name.name}` datum.
   */
  interface Imm extends $ln,${withParents(".Imm")} io.epigraph.data.ListDatum.Imm.Static {
${t.effectiveDefaultElementTagName match { // default element tag (if any) views
      case None => ""
      case Some(dtn) => sn"""\

  ${"/**"}
   * Returns immutable list view of element default tag datums. Elements where the tag datum is not set will be `null`.
   */
  @Override
  @NotNull java.util.List<@Nullable ? extends ${lqn(tt(t.elementTypeRef, dtn), t)}.Imm> datums();

  ${"/**"}
   * Returns immutable list view of element default tag values. Elements where the tag value is not set will be `null`.
   */
  @Override
  @NotNull java.util.List<@Nullable ? extends ${lqn(tt(t.elementTypeRef, dtn), t)}.Imm.Value> values();
"""
  }
}\
${e match { // element tags (for vartypes)
    case evt: CVarTypeDef => sn"""\

  ${"/**"}
   * Returns immutable list view of element data.
   */
  @NotNull java.util.List<@NotNull ? extends ${lqn(e, t)}.Imm> datas();
${
      evt.effectiveTags.map { tag => sn"""\

  ${"/**"}
   * Returns immutable list view of `${tag.name}` tag datums. Elements where the tag value is not set will be `null`.
   */
  @NotNull java.util.List<@Nullable ? extends ${lqn(tt(t.elementTypeRef, tag.name), t)}.Imm> ${jn(tag.name + "Datums")}();

  ${"/**"}
   * Returns immutable list view of `${tag.name}` tag values. Elements where the tag value is not set will be `null`.
   */
  @NotNull java.util.List<@Nullable ? extends ${lqn(tt(t.elementTypeRef, tag.name), t)}.Imm.Value> ${jn(tag.name + "Values")}();
"""
      }.mkString
}\
"""
    case _ => ""
  }
}\

    /** Private implementation of `${lqn(e, t)}.Imm` interface. */
    final class Impl extends io.epigraph.data.ListDatum.Imm.Static.Impl<$ln.Imm> implements $ln.Imm {

      Impl(@NotNull io.epigraph.data.ListDatum.Imm.Raw raw) { super($ln.type, raw); }

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
    interface Value extends $ln.Value,${withParents(".Imm.Value")} io.epigraph.data.Val.Imm.Static {

      @Override
      @Nullable $ln.Imm getDatum();

      /** Private implementation of `${lqn(e, t)}.Imm.Value` interface. */
      final class Impl extends io.epigraph.data.Val.Imm.Static.Impl<$ln.Imm.Value, $ln.Imm>
          implements $ln.Imm.Value {

        Impl(@NotNull io.epigraph.data.Val.Imm.Raw raw) { super($ln.type, raw); }

      }

    }

    /**
     * Immutable interface for `${t.name.name}` data (holding single default representation of the type).
     */
    interface Data extends $ln.Data,${withParents(".Imm.Data")} io.epigraph.data.Data.Imm.Static {

      @Override
      @Nullable $ln.Imm.Value get_value(); // TODO if default is defined

      @Override
      @Nullable $ln.Imm get(); // TODO if default is defined

      /** Private implementation of `${lqn(e, t)}.Imm.Data` interface. */
      final class Impl extends io.epigraph.data.Data.Imm.Static.Impl<$ln.Imm.Data>
          implements $ln.Imm.Data {

        Impl(@NotNull io.epigraph.data.Data.Imm.Raw raw) { super($ln.type, raw); }

        @Override
        public @Nullable $ln.Imm.Value get_value() {
          return ($ln.Imm.Value) _raw()._getValue($ln.type.self);
        }

        @Override
        public @Nullable $ln.Imm get() {
          return io.epigraph.util.Util.apply($ln.Imm.Value::getDatum, get_value());
        }

      }

    }

  }

  /**
   * Builder for `${t.name.name}` datum.
   */
  final class Builder extends io.epigraph.data.ListDatum.Mut.Static<$ln.Imm> implements $ln {

    Builder(@NotNull io.epigraph.data.ListDatum.Mut.Raw raw) { super($ln.type, raw, $ln.Imm.Impl::new); }

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
    public static final class Value extends io.epigraph.data.Val.Mut.Static<$ln.Imm.Value, $ln.Builder>
        implements $ln.Value {

      Value(@NotNull io.epigraph.data.Val.Mut.Raw raw) { super(raw, $ln.Imm.Value.Impl::new); }

    }

    /**
     * Builder for `${t.name.name}` data (holding single default representation of the type).
     */
    public static final class Data extends io.epigraph.data.Data.Mut.Static<$ln.Imm.Data>
        implements $ln.Data {

      Data(@NotNull io.epigraph.data.Data.Mut.Raw raw) {
        super($ln.type, raw, $ln.Imm.Data.Impl::new);
      }

      // default tag value getter
      @Override
      public @Nullable $ln.Builder.Value get_value() {
        return ($ln.Builder.Value) _raw()._getValue($ln.type.self);
      }

      // default tag datum getter
      @Override
      public @Nullable $ln.Builder get() {
        return io.epigraph.util.Util.apply($ln.Builder.Value::getDatum, get_value());
      }


      // default tag value setter
      public void set_value(@Nullable $ln.Builder.Value value) {
        _raw()._setValue($ln.type.self, value);
      }

      // default tag datum setter (TODO: if defined)
      public void set(@Nullable $ln.Builder datum) {
        _raw()._getOrCreateTagValue($ln.type.self)._raw().setDatum(datum);
      }

    }


  }

}
"""//@formatter:on

}

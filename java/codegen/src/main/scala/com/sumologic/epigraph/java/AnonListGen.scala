/* Created by yegor on 8/15/16. */

package com.sumologic.epigraph.java

import java.nio.file.Path

import com.sumologic.epigraph.java.NewlineStringInterpolator.NewlineHelper
import com.sumologic.epigraph.schema.compiler.{CAnonListType, CContext, CType, CVarTypeDef}

class AnonListGen(from: CAnonListType, ctx: CContext) extends JavaTypeGen[CAnonListType](from, ctx) {

  /** element value type */
  private val ev = t.elementDataType

  /** element type ref */
  private val etr = ev.typeRef

  /** element type */
  private val et = etr.resolved

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
${t.effectiveDefaultElementTagName match { // default element tag (if defined) views
      case None => ""
      case Some(dtn) => sn"""\

  ${"/**"}
   * Returns list view of element default tag datums. Elements where the tag datum is not set will be `null`.
   */
  @NotNull java.util.List<@Nullable ? extends ${lqn(tt(etr, dtn), t)}> datums();

  ${"/**"}
   * Returns list view of element default tag values. Elements where the tag value is not set will be `null`.
   */
  @NotNull java.util.List<@Nullable ? extends ${lqn(tt(etr, dtn), t)}.Value> values();
"""
  }
}\
${et match { // element tags views (for vartypes)
    case evt: CVarTypeDef => sn"""\

  ${"/**"}
   * Returns list view of element data.
   */
  @NotNull java.util.List<@NotNull ? extends ${lqn(et, t)}> datas();
${
      evt.effectiveTags.map { tag => sn"""\

  /**
   * Returns list view of `${tag.name}` tag datums. Elements where the tag value is not set will be `null`.
   */
  @NotNull java.util.List<@Nullable ? extends ${lqn(tt(etr, tag.name), t)}> ${jn(tag.name + "Datums")}();

  /**
   * Returns list view of `${tag.name}` tag values. Elements where the tag value is not set will be `null`.
   */
  @NotNull java.util.List<@Nullable ? extends ${lqn(tt(etr, tag.name), t)}.Value> ${jn(tag.name + "Values")}();
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
          java.util.Arrays.asList(${parents(".type")}),
          ${dataTypeExpr(ev, t)},
          $ln.Builder::new,
          $ln.Builder.Value::new,
          $ln.Builder.Data::new
      );
    }

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
    @Nullable $ln get();

    /**
     * Returns `${t.name.name}` value.
     */
    @Nullable $ln.Value get_();

  }

  /**
   * Immutable interface for `${t.name.name}` datum.
   */
  interface Imm extends $ln,${withParents(".Imm")} io.epigraph.data.ListDatum.Imm.Static {
${t.effectiveDefaultElementTagName match { // default element tag (if defined) views
      case None => ""
      case Some(dtn) => sn"""\

  ${"/**"}
   * Returns immutable list view of element default tag datums. Elements where the tag datum is not set will be `null`.
   */
  @Override
  @NotNull java.util.List<@Nullable ? extends ${lqn(tt(etr, dtn), t)}.Imm> datums();

  ${"/**"}
   * Returns immutable list view of element default tag values. Elements where the tag value is not set will be `null`.
   */
  @Override
  @NotNull java.util.List<@Nullable ? extends ${lqn(tt(etr, dtn), t)}.Imm.Value> values();
"""
  }
}\
${et match { // element tags (for vartypes)
    case evt: CVarTypeDef => sn"""\

  ${"/**"}
   * Returns immutable list view of elements data.
   */
  @NotNull java.util.List<@NotNull ? extends ${lqn(et, t)}.Imm> datas();
${
      evt.effectiveTags.map { tag => sn"""\

  /**
   * Returns immutable list view of `${tag.name}` tag datums. Elements where the tag value is not set will be `null`.
   */
  @NotNull java.util.List<@Nullable ? extends ${lqn(tt(etr, tag.name), t)}.Imm> ${jn(tag.name + "Datums")}();

  /**
   * Returns immutable list view of `${tag.name}` tag values. Elements where the tag value is not set will be `null`.
   */
  @NotNull java.util.List<@Nullable ? extends ${lqn(tt(etr, tag.name), t)}.Imm.Value> ${jn(tag.name + "Values")}();
"""
      }.mkString
}\
"""
    case _ => ""
  }
}\

    /** Private implementation of `$ln.Imm` interface. */
    final class Impl extends io.epigraph.data.ListDatum.Imm.Static.Impl<$ln.Imm> implements $ln.Imm {

      Impl(@NotNull io.epigraph.data.ListDatum.Imm.Raw raw) {
        super($ln.type, raw);
      }
${t.effectiveDefaultElementTagName match { // default element tag (if defined) views
      case None => ""
      case Some(dtn) => sn"""\

      ${"/**"}
       * Returns immutable list view of element default tag datums. Elements where the tag datum is not set will be `null`.
       */
      @Override
      public @NotNull java.util.List<@Nullable ? extends ${lqn(tt(etr, dtn), t)}.Imm> datums() {
        return new io.epigraph.util.Unmodifiable.ListView<${lqn(et, t)}.Imm${vt(et, "", ".Data")}, ${lqn(tt(etr, dtn), t)}.Imm>(
            datas(),
            ${lqn(et, t)}.Imm${vt(et, "", ".Data")}::get${vt(et, up(dtn), "")}
        );
      }

      ${"/**"}
       * Returns immutable list view of element default tag values. Elements where the tag value is not set will be `null`.
       */
      @Override
      public @NotNull java.util.List<@Nullable ? extends ${lqn(tt(etr, dtn), t)}.Imm.Value> values() {
        return new io.epigraph.util.Unmodifiable.ListView<${lqn(et, t)}.Imm${vt(et, "", ".Data")}, ${lqn(tt(etr, dtn), t)}.Imm.Value>(
            datas(),
            ${lqn(et, t)}.Imm${vt(et, "", ".Data")}::get_${vt(et, up(dtn), "")}
        );
      }
"""
  }
}\
${et match { // element tags (for vartypes)
    case evt: CVarTypeDef => sn"""\

      ${"/**"}
       * Returns immutable list view of elements data.
       */
      @Override
      public @NotNull java.util.List<@NotNull ? extends ${lqn(et, t)}.Imm> datas() {
        return io.epigraph.util.Util.castEx(_raw()._elements());
      }
${
      evt.effectiveTags.map { tag => sn"""\

      /**
       * Returns immutable list view of `${tag.name}` tag datums. Elements where the tag value is not set will be `null`.
       */
      public @NotNull java.util.List<@Nullable ? extends ${lqn(tt(etr, tag.name), t)}.Imm> ${jn(tag.name + "Datums")}() {
        return new io.epigraph.util.Unmodifiable.ListView<${lqn(et, t)}.Imm${vt(et, "", ".Data")}, ${lqn(tt(etr, tag.name), t)}.Imm>(
            datas(),
            ${lqn(et, t)}.Imm${vt(et, "", ".Data")}::get${vt(et, up(tag.name), "")}
        );
      }

      /**
       * Returns immutable list view of `${tag.name}` tag values. Elements where the tag value is not set will be `null`.
       */
      public @NotNull java.util.List<@Nullable ? extends ${lqn(tt(etr, tag.name), t)}.Imm.Value> ${jn(tag.name + "Values")}() {
        return new io.epigraph.util.Unmodifiable.ListView<${lqn(et, t)}.Imm${vt(et, "", ".Data")}, ${lqn(tt(etr, tag.name), t)}.Imm.Value>(
            datas(),
            ${lqn(et, t)}.Imm${vt(et, "", ".Data")}::get_${vt(et, up(tag.name), "")}
        );
      }
"""
      }.mkString
}\
"""
    case _ => sn"""\

      // method is private to not expose datas() for non-union types (so simple type can be replaced with union type while preserving backwards-compatibility)
      private @NotNull java.util.List<@NotNull ? extends ${lqn(et, t)}.Imm.Data> datas() {
        return io.epigraph.util.Util.castEx(_raw()._elements());
      }
"""
  }
}\

    }

    /**
     * Immutable interface for `${t.name.name}` value (holding an immutable datum or an error).
     */
    interface Value extends $ln.Value,${withParents(".Imm.Value")} io.epigraph.data.Val.Imm.Static {

      @Override
      @Nullable $ln.Imm getDatum();

      /** Private implementation of `${lqn(et, t)}.Imm.Value` interface. */
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
      @Nullable $ln.Imm get();

      @Override
      @Nullable $ln.Imm.Value get_();

      /** Private implementation of `$ln.Imm.Data` interface. */
      final class Impl extends io.epigraph.data.Data.Imm.Static.Impl<$ln.Imm.Data>
          implements $ln.Imm.Data {

        Impl(@NotNull io.epigraph.data.Data.Imm.Raw raw) { super($ln.type, raw); }

        @Override
        public @Nullable $ln.Imm get() {
          return io.epigraph.util.Util.apply(get_(), $ln.Imm.Value::getDatum);
        }

        @Override
        public @Nullable $ln.Imm.Value get_() {
          return ($ln.Imm.Value) _raw()._getValue($ln.type.self);
        }

      }

    }

  }

  /**
   * Builder for `${t.name.name}` datum.
   */
  final class Builder extends io.epigraph.data.ListDatum.Mut.Static<$ln.Imm> implements $ln {

    Builder(@NotNull io.epigraph.data.ListDatum.Mut.Raw raw) {
      super($ln.type, raw, $ln.Imm.Impl::new);
    }
${t.effectiveDefaultElementTagName match { // default element tag (if defined) views
      case None => ""
      case Some(dtn) => sn"""\

      ${"/**"}
       * Returns list view of element default tag datum builders. Elements where the tag datum is not set will be `null`.
       */
      @Override
      public @NotNull java.util.List<${lqn(tt(etr, dtn), t)}.@Nullable Builder> datums() {
        return new io.epigraph.util.ListView<>(
            datas(),
            ${lqn(et, t)}.Builder${vt(et, "", ".Data")}::get${vt(et, up(dtn), "")},
            ${lqn(et, t)}.Builder${vt(et, "", ".Data")}::set${vt(et, up(dtn), "")},
            ${lqn(et, t)}.type::createDataBuilder
        );
      }

      ${"/**"}
       * Returns list view of element default tag value builders. Elements where the tag value is not set will be `null`.
       */
      @Override
      public @NotNull java.util.List<${lqn(tt(etr, dtn), t)}.Builder.@Nullable Value> values() {
        return new io.epigraph.util.ListView<>(
            datas(),
            ${lqn(et, t)}.Builder${vt(et, "", ".Data")}::get_${vt(et, up(dtn), "")},
            ${lqn(et, t)}.Builder${vt(et, "", ".Data")}::set_${vt(et, up(dtn), "")},
            ${lqn(et, t)}.type::createDataBuilder
        );
      }
"""
  }
}\
${et match { // element tags (for vartypes)
    case evt: CVarTypeDef => sn"""\

      ${"/**"}
       * Returns list view of element data builders.
       */
      @Override
      public @NotNull java.util.List<${lqn(et, t)}.@NotNull Builder> datas() {
        return io.epigraph.util.Util.cast(_raw()._elements());
      }
${
      evt.effectiveTags.map { tag => sn"""\

      /**
       * Returns list view of elements `${tag.name}` tag datum builders. Elements where the tag value is not set will be `null`.
       */
      public @NotNull java.util.List<${lqn(tt(etr, tag.name), t)}.@Nullable Builder> ${jn(tag.name + "Datums")}() {
        return new io.epigraph.util.ListView<>(
            datas(),
            ${lqn(et, t)}.Builder${vt(et, "", ".Data")}::get${vt(et, up(tag.name), "")},
            ${lqn(et, t)}.Builder${vt(et, "", ".Data")}::set${vt(et, up(tag.name), "")},
            ${lqn(et, t)}.type::createDataBuilder
        );
      }

      /**
       * Returns list view of elements `${tag.name}` tag value builders. Elements where the tag value is not set will be `null`.
       */
      public @NotNull java.util.List<${lqn(tt(etr, tag.name), t)}.Builder.@Nullable Value> ${jn(tag.name + "Values")}() {
        return new io.epigraph.util.ListView<>(
            datas(),
            ${lqn(et, t)}.Builder${vt(et, "", ".Data")}::get_${vt(et, up(tag.name), "")},
            ${lqn(et, t)}.Builder${vt(et, "", ".Data")}::set_${vt(et, up(tag.name), "")},
            ${lqn(et, t)}.type::createDataBuilder
        );
      }
"""
      }.mkString
}\
"""
    case _ => sn"""\

      // method is private to not expose datas() for non-union types (so simple type can be replaced with union type while preserving backwards-compatibility)
      private @NotNull java.util.List<${lqn(et, t)}.Builder.@NotNull Data> datas() {
        return io.epigraph.util.Util.cast(_raw()._elements());
      }
"""
  }
}\

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

      // default tag datum getter
      @Override
      public @Nullable $ln.Builder get() {
        return io.epigraph.util.Util.apply(get_(), $ln.Builder.Value::getDatum);
      }

      // default tag value getter
      @Override
      public @Nullable $ln.Builder.Value get_() {
        return ($ln.Builder.Value) _raw()._getValue($ln.type.self);
      }

      // default tag datum setter
      public void set(@Nullable $ln.Builder datum) {
        _raw()._getOrCreateTagValue($ln.type.self)._raw().setDatum(datum);
      }

      // default tag error setter // TODO simplified DatumTypeData implementation with this method
      public void set(@NotNull io.epigraph.errors.ErrorValue error) {
        _raw()._getOrCreateTagValue($ln.type.self)._raw().setError(error);
      }

      // default tag value setter
      public void set_(@Nullable $ln.Builder.Value value) {
        _raw()._setValue($ln.type.self, value);
      }

    }

  }

}
"""//@formatter:on

}

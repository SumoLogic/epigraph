/* Created by yegor on 8/15/16. */

package com.sumologic.epigraph.java

import java.nio.file.Path

import com.sumologic.epigraph.java.NewlineStringInterpolator.NewlineHelper
import com.sumologic.epigraph.schema.compiler.{CAnonListType, CContext, CVarTypeDef}

class AnonListGen(from: CAnonListType, ctx: CContext) extends JavaTypeGen[CAnonListType](from, ctx)
    with DatumTypeJavaGen {

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

  $ln.Type type = $ln.Type.instance();

  static @NotNull $ln.Builder create() { return $ln.Type.instance().createBuilder(); }
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
//
//  /**
//   * Returns list view of `${tag.name}` tag datums. Elements where the tag value is not set will be `null`.
//   */
//  @NotNull java.util.List<@Nullable ? extends ${lqn(tt(etr, tag.name), t)}> ${jn(tag.name + "Datums")}();
//
//  /**
//   * Returns list view of `${tag.name}` tag values. Elements where the tag value is not set will be `null`.
//   */
//  @NotNull java.util.List<@Nullable ? extends ${lqn(tt(etr, tag.name), t)}.Value> ${jn(tag.name + "Values")}();
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

    private static final class Holder { public static $ln.Type instance = new $ln.Type(); }

    public static $ln.Type instance() { return Holder.instance; }

    private Type() {
      super(
          java.util.Arrays.asList(${parents(".Type.instance()")}),
          ${dataTypeExpr(ev, t)},
          $ln.Builder::new,
          $ln.Imm.Value.Impl::new,
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
    @Nullable $ln.Value get$$();

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
//
//  /**
//   * Returns immutable list view of `${tag.name}` tag datums. Elements where the tag value is not set will be `null`.
//   */
//  @NotNull java.util.List<@Nullable ? extends ${lqn(tt(etr, tag.name), t)}.Imm> ${jn(tag.name + "Datums")}();
//
//  /**
//   * Returns immutable list view of `${tag.name}` tag values. Elements where the tag value is not set will be `null`.
//   */
//  @NotNull java.util.List<@Nullable ? extends ${lqn(tt(etr, tag.name), t)}.Imm.Value> ${jn(tag.name + "Values")}();
"""
      }.mkString
}\
"""
    case _ => ""
  }
}\

    /** Private implementation of `$ln.Imm` interface. */
    final class Impl extends io.epigraph.data.ListDatum.Imm.Static.Impl<$ln.Imm, $ln.Imm.Value> implements $ln.Imm {

      Impl(@NotNull io.epigraph.data.ListDatum.Imm.Raw raw) { super($ln.Type.instance(), raw, $ln.Imm.Value.Impl::new); }
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
            ${lqn(et, t)}.Imm${vt(et, "", ".Data")}::get${vt(et, up(dtn), "")}$$
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
        return io.epigraph.util.Util.castEx(_raw().elements());
      }
${
      evt.effectiveTags.map { tag => sn"""\
//
//      /**
//       * Returns immutable list view of `${tag.name}` tag datums. Elements where the tag value is not set will be `null`.
//       */
//      public @NotNull java.util.List<@Nullable ? extends ${lqn(tt(etr, tag.name), t)}.Imm> ${jn(tag.name + "Datums")}() {
//        return new io.epigraph.util.Unmodifiable.ListView<${lqn(et, t)}.Imm${vt(et, "", ".Data")}, ${lqn(tt(etr, tag.name), t)}.Imm>(
//            datas(),
//            ${lqn(et, t)}.Imm${vt(et, "", ".Data")}::get${vt(et, up(tag.name), "")}
//        );
//      }
//
//      /**
//       * Returns immutable list view of `${tag.name}` tag values. Elements where the tag value is not set will be `null`.
//       */
//      public @NotNull java.util.List<@Nullable ? extends ${lqn(tt(etr, tag.name), t)}.Imm.Value> ${jn(tag.name + "Values")}() {
//        return new io.epigraph.util.Unmodifiable.ListView<${lqn(et, t)}.Imm${vt(et, "", ".Data")}, ${lqn(tt(etr, tag.name), t)}.Imm.Value>(
//            datas(),
//            ${lqn(et, t)}.Imm${vt(et, "", ".Data")}::get${vt(et, up(tag.name), "")}$$
//        );
//      }
"""
      }.mkString
}\
"""
    case _ => sn"""\

      // method is private to not expose datas() for non-union types (so simple type can be replaced with union type while preserving backwards-compatibility)
      private @NotNull java.util.List<@NotNull ? extends ${lqn(et, t)}.Imm.Data> datas() {
        return io.epigraph.util.Util.castEx(_raw().elements());
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

        Impl(@NotNull io.epigraph.data.Val.Imm.Raw raw) { super(raw); }

      }

    }

    /**
     * Immutable interface for `${t.name.name}` data (holding single default representation of the type).
     */
    interface Data extends $ln.Data,${withParents(".Imm.Data")} io.epigraph.data.Data.Imm.Static {

      @Override
      @Nullable $ln.Imm get();

      @Override
      @Nullable $ln.Imm.Value get$$();

      /** Private implementation of `$ln.Imm.Data` interface. */
      final class Impl extends io.epigraph.data.Data.Imm.Static.Impl<$ln.Imm.Data>
          implements $ln.Imm.Data {

        Impl(@NotNull io.epigraph.data.Data.Imm.Raw raw) { super($ln.Type.instance(), raw); }

        @Override
        public @Nullable $ln.Imm get() {
          return io.epigraph.util.Util.apply(get$$(), $ln.Imm.Value::getDatum);
        }

        @Override
        public @Nullable $ln.Imm.Value get$$() {
          return ($ln.Imm.Value) _raw().getValue($ln.Type.instance().self);
        }

      }

    }

  }

  /**
   * Builder for `${t.name.name}` datum.
   */
  final class Builder extends io.epigraph.data.ListDatum.Builder.Static<$ln.Imm, $ln.Builder.Value> implements $ln {

    Builder(@NotNull io.epigraph.data.ListDatum.Builder.Raw raw) {
      super($ln.Type.instance(), raw, $ln.Imm.Impl::new, $ln.Builder.Value::new);
    }
${t.effectiveDefaultElementTagName match { // default element tag (if defined) views
      case None => ""
      case Some(dtn) => sn"""\

    ${"/**"} Returns modifiable list view of default `$dtn` tag element datums. Elements where the tag datum is not set will be `null`. */
    @Override
    public @NotNull java.util.List<@Nullable ${lqn(tt(etr, dtn), t)}> datums() {
      return new io.epigraph.util.ListView<>(
          datas(),
          ${lqn(et, t)}${vt(et, "", ".Data")}::get${vt(et, up(dtn), "")},
          v -> ${lqn(et, t)}.Type.instance().createDataBuilder().set${vt(et, up(dtn), "")}(v)
      );
    }

    ${"/**"} Returns list view of element default tag value builders. Elements where the tag value is not set will be `null`. */
    @Override
    public @NotNull java.util.List<${lqn(tt(etr, dtn), t)}.@Nullable Value> values() {
      return new io.epigraph.util.ListView<>(
          datas(),
          ${lqn(et, t)}${vt(et, "", ".Data")}::get${vt(et, up(dtn), "")}$$,
          v -> ${lqn(et, t)}.Type.instance().createDataBuilder().set${vt(et, up(dtn), "")}$$(v)
      );
    }

    ${"/**"} Adds${vt(et, s" default `$dtn` tag", "")} datum to the list. */
    public @NotNull $ln.Builder add(@Nullable ${lqn(tt(etr, dtn), t)} datum) {
      datas().add(${lqn(et, t)}.Type.instance().createDataBuilder().set${vt(et, up(dtn), "")}(datum));
      return this;
    }

    ${"/**"} Adds${vt(et, s" default `$dtn` tag", "")} value to the list. */
    public @NotNull $ln.Builder add$$(@Nullable ${lqn(tt(etr, dtn), t)}.Value value) {
      datas().add(${lqn(et, t)}.Type.instance().createDataBuilder().set${vt(et, up(dtn), "")}$$(value));
      return this;
    }

"""
  }
}\
${et match { // data view (for vartypes)
    case evt: CVarTypeDef => sn"""\

    ${"/**"}
     * Returns modifiable list view of element data builders.
     */
    @Override
    public @NotNull java.util.List<@NotNull ${lqn(et, t)}> datas() {
      return io.epigraph.util.Util.cast(_raw().elements());
    }
${
      evt.effectiveTags.map { tag => sn"""\
//
//    /**
//     * Returns modifiable list view of elements `${tag.name}` tag datums. Elements where the tag value is not set will be `null`.
//     */
//    public @NotNull java.util.List<@Nullable ${lqn(tt(etr, tag.name), t)}> ${jn(tag.name + "Datums")}() {
//      return new io.epigraph.util.ListView<>(
//          datas(),
//          ${lqn(et, t)}${vt(et, "", ".Data")}::get${vt(et, up(tag.name), "")},
//          v -> ${lqn(et, t)}.Type.instance().createDataBuilder().set${vt(et, up(tag.name), "")}(v)
//      );
//    }
//
//    /**
//     * Returns modifiable list view of elements `${tag.name}` tag values. Elements where the tag value is not set will be `null`.
//     */
//    public @NotNull java.util.List<${lqn(tt(etr, tag.name), t)}.@Nullable Value> ${jn(tag.name + "Values")}() {
//      return new io.epigraph.util.ListView<>(
//          datas(),
//          ${lqn(et, t)}${vt(et, "", ".Data")}::get${vt(et, up(tag.name), "")}$$,
//          v -> ${lqn(et, t)}.Type.instance().createDataBuilder().set${vt(et, up(tag.name), "")}(v)
//      );
//    }
"""
      }.mkString
}\
"""
    case _ => sn"""\

      // method is private to not expose datas() for non-union types (so simple type can be replaced with union type while preserving backwards-compatibility)
      private @NotNull java.util.List<${lqn(et, t)}.@NotNull Data> datas() {
        return io.epigraph.util.Util.cast(_raw().elements());
      }
"""
  }
}\

$builderValueAndDataBuilder\

  }

}
"""//@formatter:on

}

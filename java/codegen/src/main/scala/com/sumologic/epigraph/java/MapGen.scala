/* Created by yegor on 10/3/16. */
/* Created by yegor on 9/25/16. */

package com.sumologic.epigraph.java

import java.nio.file.Path

import com.sumologic.epigraph.java.NewlineStringInterpolator.NewlineHelper
import com.sumologic.epigraph.schema.compiler.{CAnonMapType, CContext, CMapType, CVarTypeDef}

abstract class MapGen[Type >: Null <: CMapType](from: Type, ctx: CContext) extends JavaTypeGen[Type](from, ctx)
    with DatumTypeJavaGen {

  /** key type ref */
  private val ktr = t.keyTypeRef

  /** key type */
  private val kt = ktr.resolved

  /** value data type */
  private val vv = t.valueDataType

  /** value type ref */
  private val vtr = vv.typeRef

  /** value type */
  private val vt = vtr.resolved

  override def generate: String = /*@formatter:off*/sn"""\
package ${pn(t)};

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base (read) interface for `${t.name.name}` datum.
 */
public interface $ln extends${withParents(t)} io.epigraph.data.MapDatum.Static<${lqn(kt, t)}.Imm> {

  $ln.Type type = $ln.Type.instance();

  static @NotNull $ln.Builder create() { return $ln.Type.instance().createBuilder(); }
${t.effectiveDefaultValueTagName match { // default value tag (if defined) views
      case None => ""
      case Some(dtn) => sn"""\

  ${"/**"}
   * Returns map view of element default tag datums. Elements where the tag datum is not set will be `null`.
   */
  @NotNull java.util.Map<${lqn(kt, t)}.@NotNull Imm, @Nullable ? extends ${lqn(tt(vtr, dtn), t)}> datums();

  ${"/**"}
   * Returns map view of element default tag values. Elements where the tag value is not set will be `null`.
   */
  @NotNull java.util.Map<${lqn(kt, t)}.@NotNull Imm, @Nullable ? extends ${lqn(tt(vtr, dtn), t)}.Value> values();
"""
  }
}\
${vt match { // element tags views (for vartypes)
    case evt: CVarTypeDef => sn"""\

  ${"/**"}
   * Returns map view of element data.
   */
  @NotNull java.util.Map<@NotNull ? extends ${lqn(kt, t)}.Imm, @NotNull ? extends ${lqn(vt, t)}> datas();
${
      evt.effectiveTags.map { tag => sn"""\
//
//  /**
//   * Returns map view of `${tag.name}` tag datums. Elements where the tag value is not set will be `null`.
//   */
//  @NotNull java.util.Map<${lqn(kt, t)}.@NotNull Imm, @Nullable ? extends ${lqn(tt(vtr, tag.name), t)}> ${jn(tag.name + "Datums")}();
//
//  /**
//   * Returns map view of `${tag.name}` tag values. Elements where the tag value is not set will be `null`.
//   */
//  @NotNull java.util.Map<${lqn(kt, t)}.@NotNull Imm, @Nullable ? extends ${lqn(tt(vtr, tag.name), t)}.Value> ${jn(tag.name + "Values")}();
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
  final class Type extends io.epigraph.types.AnonMapType.Static<
      ${lqn(kt, t)}.Imm,
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
          ${lqn(kt, t)}.Type.instance(),
          ${dataTypeExpr(vv, t)},
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
  interface Imm extends $ln,${withParents(".Imm")} io.epigraph.data.MapDatum.Imm.Static<${lqn(kt, t)}.Imm> {
${t.effectiveDefaultValueTagName match { // default value tag (if defined) views
      case None => ""
      case Some(dtn) => sn"""\

  ${"/**"}
   * Returns immutable map view of element default tag datums. Elements where the tag datum is not set will be `null`.
   */
  @Override
  @NotNull java.util.Map<${lqn(kt, t)}.@NotNull Imm, @Nullable ? extends ${lqn(tt(vtr, dtn), t)}.Imm> datums();

  ${"/**"}
   * Returns immutable map view of element default tag values. Elements where the tag value is not set will be `null`.
   */
  @Override
  @NotNull java.util.Map<${lqn(kt, t)}.@NotNull Imm, @Nullable ? extends ${lqn(tt(vtr, dtn), t)}.Imm.Value> values();
"""
  }
}\
${vt match { // element tags (for vartypes)
    case evt: CVarTypeDef => sn"""\

  ${"/**"}
   * Returns immutable map view of elements data.
   */
  @NotNull java.util.Map<@NotNull ? extends ${lqn(kt, t)}.Imm, @NotNull ? extends ${lqn(vt, t)}.Imm> datas();
${
      evt.effectiveTags.map { tag => sn"""\
//
//  /**
//   * Returns immutable map view of `${tag.name}` tag datums. Elements where the tag value is not set will be `null`.
//   */
//  @NotNull java.util.Map<${lqn(kt, t)}.@NotNull Imm, @Nullable ? extends ${lqn(tt(vtr, tag.name), t)}.Imm> ${jn(tag.name + "Datums")}();
//
//  /**
//   * Returns immutable map view of `${tag.name}` tag values. Elements where the tag value is not set will be `null`.
//   */
//  @NotNull java.util.Map<${lqn(kt, t)}.@NotNull Imm, @Nullable ? extends ${lqn(tt(vtr, tag.name), t)}.Imm.Value> ${jn(tag.name + "Values")}();
"""
      }.mkString
}\
"""
    case _ => ""
  }
}\

    /** Private implementation of `$ln.Imm` interface. */
    final class Impl extends io.epigraph.data.MapDatum.Imm.Static.Impl<${lqn(kt, t)}.Imm, $ln.Imm, $ln.Imm.Value> implements $ln.Imm {

      Impl(@NotNull io.epigraph.data.MapDatum.Imm.Raw raw) { super($ln.Type.instance(), raw, $ln.Imm.Value.Impl::new); }
${t.effectiveDefaultValueTagName match { // default value tag (if defined) views
      case None => ""
      case Some(dtn) => sn"""\

      ${"/**"}
       * Returns immutable map view of element default tag datums. Elements where the tag datum is not set will be `null`.
       */
      @Override
      public @NotNull java.util.Map<${lqn(kt, t)}.@NotNull Imm, @Nullable ? extends ${lqn(tt(vtr, dtn), t)}.Imm> datums() {
        return new io.epigraph.util.Unmodifiable.MapView<${lqn(kt, t)}.Imm, ${lqn(vt, t)}.Imm${vt(vt, "", ".Data")}, ${lqn(tt(vtr, dtn), t)}.Imm>(
            datas(),
            ${lqn(vt, t)}.Imm${vt(vt, "", ".Data")}::get${vt(vt, up(dtn), "")}
        );
      }

      ${"/**"}
       * Returns immutable map view of element default tag values. Elements where the tag value is not set will be `null`.
       */
      @Override
      public @NotNull java.util.Map<${lqn(kt, t)}.@NotNull Imm, @Nullable ? extends ${lqn(tt(vtr, dtn), t)}.Imm.Value> values() {
        return new io.epigraph.util.Unmodifiable.MapView<${lqn(kt, t)}.Imm, ${lqn(vt, t)}.Imm${vt(vt, "", ".Data")}, ${lqn(tt(vtr, dtn), t)}.Imm.Value>(
            datas(),
            ${lqn(vt, t)}.Imm${vt(vt, "", ".Data")}::get${vt(vt, up(dtn), "")}$$
        );
      }
"""
  }
}\
${vt match { // element tags (for vartypes)
    case evt: CVarTypeDef => sn"""\

      ${"/**"}
       * Returns immutable map view of elements data.
       */
      @Override
      public @NotNull java.util.Map<@NotNull ? extends ${lqn(kt, t)}.Imm, @NotNull ? extends ${lqn(vt, t)}.Imm> datas() {
        return io.epigraph.util.Util.castEx(_raw().elements());
      }
${
      evt.effectiveTags.map { tag => sn"""\
//
//      /**
//       * Returns immutable map view of `${tag.name}` tag datums. Elements where the tag value is not set will be `null`.
//       */
//      public @NotNull java.util.Map<${lqn(kt, t)}.@NotNull Imm, @Nullable ? extends ${lqn(tt(vtr, tag.name), t)}.Imm> ${jn(tag.name + "Datums")}() {
//        return new io.epigraph.util.Unmodifiable.MapView<${lqn(kt, t)}.Imm, ${lqn(vt, t)}.Imm${vt(vt, "", ".Data")}, ${lqn(tt(vtr, tag.name), t)}.Imm>(
//            datas(),
//            ${lqn(vt, t)}.Imm${vt(vt, "", ".Data")}::get${vt(vt, up(tag.name), "")}
//        );
//      }
//
//      /**
//       * Returns immutable map view of `${tag.name}` tag values. Elements where the tag value is not set will be `null`.
//       */
//      public @NotNull java.util.Map<${lqn(kt, t)}.@NotNull Imm, @Nullable ? extends ${lqn(tt(vtr, tag.name), t)}.Imm.Value> ${jn(tag.name + "Values")}() {
//        return new io.epigraph.util.Unmodifiable.MapView<${lqn(kt, t)}.Imm, ${lqn(vt, t)}.Imm${vt(vt, "", ".Data")}, ${lqn(tt(vtr, tag.name), t)}.Imm.Value>(
//            datas(),
//            ${lqn(vt, t)}.Imm${vt(vt, "", ".Data")}::get${vt(vt, up(tag.name), "")}$$
//        );
//      }
"""
      }.mkString
}\
"""
    case _ => sn"""\

      // method is private to not expose datas() for non-union types (so simple type can be replaced with union type while preserving backwards-compatibility)
      private @NotNull java.util.Map<@NotNull ? extends ${lqn(kt, t)}.Imm, @NotNull ? extends ${lqn(vt, t)}.Imm.Data> datas() {
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

      /** Private implementation of `${lqn(vt, t)}.Imm.Value` interface. */
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
  final class Builder extends io.epigraph.data.MapDatum.Builder.Static<${lqn(kt, t)}.Imm, $ln.Imm, $ln.Builder.Value> implements $ln {

    Builder(@NotNull io.epigraph.data.MapDatum.Builder.Raw raw) {
      super($ln.Type.instance(), raw, $ln.Imm.Impl::new, $ln.Builder.Value::new);
    }
${t.effectiveDefaultValueTagName match { // default value tag (if defined) views
      case None => ""
      case Some(dtn) => sn"""\

    ${"/**"} Returns modifiable map view of default `$dtn` tag element datums. Elements where the tag datum is not set will be `null`. */
    @Override
    public @NotNull java.util.Map<${lqn(kt, t)}.@NotNull Imm, ${lqn("@Nullable ", tt(vtr, dtn), t)}> datums() {
      return new io.epigraph.util.MapView<>(
          datas(),
          ${lqn(vt, t)}${vt(vt, "", ".Data")}::get${vt(vt, up(dtn), "")},
          v -> ${lqn(vt, t)}.Type.instance().createDataBuilder().set${vt(vt, up(dtn), "")}(v)
      );
    }

    ${"/**"} Returns map view of element default tag value builders. Elements where the tag value is not set will be `null`. */
    @Override
    public @NotNull java.util.Map<${lqn(kt, t)}.@NotNull Imm, ${lqn(tt(vtr, dtn), t)}.@Nullable Value> values() {
      return new io.epigraph.util.MapView<>(
          datas(),
          ${lqn(vt, t)}${vt(vt, "", ".Data")}::get${vt(vt, up(dtn), "")}$$,
          v -> ${lqn(vt, t)}.Type.instance().createDataBuilder().set${vt(vt, up(dtn), "")}$$(v)
      );
    }

    ${"/**"} Associates specified${vt(vt, s" default `$dtn` tag", "")} datum with specified key in this map. */
    public @NotNull $ln.Builder put(@NotNull ${lqn(kt, t)}.Imm key, @Nullable ${lqn(tt(vtr, dtn), t)} datum) {
      datas().put(key, ${lqn(vt, t)}.Type.instance().createDataBuilder().set${vt(vt, up(dtn), "")}(datum));
      return this;
    }

    ${"/**"} Associates specified${vt(vt, s" default `$dtn` tag", "")} value with specified key in this map. */
    public @NotNull $ln.Builder put$$(@NotNull ${lqn(kt, t)}.Imm key, @Nullable ${lqn(tt(vtr, dtn), t)}.Value value) {
      datas().put(key, ${lqn(vt, t)}.Type.instance().createDataBuilder().set${vt(vt, up(dtn), "")}$$(value));
      return this;
    }
"""
  }
}\
${vt match { // data view (for vartypes)
    case evt: CVarTypeDef => sn"""\

    ${"/**"} Returns modifiable map view of element data builders. */
    @Override
    public @NotNull java.util.Map<${lqn(kt, t)}.@NotNull Imm, ${lqn("@NotNull ", vt, t)}> datas() {
      return io.epigraph.util.Util.cast(_raw().elements());
    }

    ${"/**"} Associates specified data with specified key in this map. */
    public @NotNull $ln.Builder put_(@NotNull ${lqn(kt, t)}.Imm key, @NotNull ${lqn(vt, t)} data) {
      datas().put(key, data);
      return this;
    }
${
      evt.effectiveTags.map { tag => sn"""\
//
//    /**
//     * Returns modifiable map view of elements `${tag.name}` tag datums. Elements where the tag value is not set will be `null`.
//     */
//    public @NotNull java.util.Map<${lqn(kt, t)}.@NotNull Imm, ${lqn("@Nullable ", tt(vtr, tag.name), t)}> ${jn(tag.name + "Datums")}() {
//      return new io.epigraph.util.MapView<>(
//          datas(),
//          ${lqn(vt, t)}${vt(vt, "", ".Data")}::get${vt(vt, up(tag.name), "")},
//          v -> ${lqn(vt, t)}.Type.instance().createDataBuilder().set${vt(vt, up(tag.name), "")}(v)
//      );
//    }
//
//    /**
//     * Returns modifiable map view of elements `${tag.name}` tag values. Elements where the tag value is not set will be `null`.
//     */
//    public @NotNull java.util.Map<${lqn(kt, t)}.@NotNull Imm, ${lqn(tt(vtr, tag.name), t)}.@Nullable Value> ${jn(tag.name + "Values")}() {
//      return new io.epigraph.util.MapView<>(
//          datas(),
//          ${lqn(vt, t)}${vt(vt, "", ".Data")}::get${vt(vt, up(tag.name), "")}$$,
//          v -> ${lqn(vt, t)}.Type.instance().createDataBuilder().set${vt(vt, up(tag.name), "")}(v)
//      );
//    }
"""
      }.mkString
}\
"""
    case _ => sn"""\

      // method is private to not expose datas() for non-union types (so simple type can be replaced with union type while preserving backwards-compatibility)
      private @NotNull java.util.Map<${lqn(kt, t)}.@NotNull Imm, ${lqn(vt, t)}.@NotNull Data> datas() {
        return io.epigraph.util.Util.cast(_raw().elements());
      }
"""
  }
}\

    /**
     * Builder value for `${t.name.name}` (holding a builder or an error).
     */
    public static final class Value extends io.epigraph.data.Val.Builder.Static<$ln.Imm.Value, $ln.Builder> implements $ln.Value {

      Value(@NotNull io.epigraph.data.Val.Builder.Raw raw) { super(raw, $ln.Imm.Value.Impl::new); }

    }

$dataBuilder\

  }

}
"""//@formatter:on

}

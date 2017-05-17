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

/* Created by yegor on 10/3/16. */
/* Created by yegor on 9/25/16. */

package ws.epigraph.java

import ws.epigraph.compiler.{CMapType, CVarTypeDef}
import ws.epigraph.java.JavaGenNames.{jn, lqn, pn, tt}
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper

abstract class MapGen[Type >: Null <: CMapType](from: Type, ctx: GenContext) extends JavaTypeGen[Type](from, ctx)
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
${JavaGenUtils.topLevelComment}\
package ${pn(t)};

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base (read) interface for `${t.name.name}` datum.
 */
@javax.annotation.Generated("${getClass.getCanonicalName}")
public interface $ln extends${JavaGenUtils.withParents(t)} ws.epigraph.data.MapDatum.Static<${lqn(kt, t)}.Imm> {

  $ln.Type type = $ln.Type.instance();

  static @NotNull $ln.Builder create() { return $ln.Type.instance().createBuilder(); }

  @Override
  @NotNull $ln.Imm toImmutable();
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
${t.meta match {
    case Some(mt) => sn"""\

  ${"/**"}
   * @return meta-data instance
   */
  @Nullable ${lqn(mt, t)} meta();
"""
    case None => ""
  }
}\

  /**
   * Class for `${t.name.name}` type.
   */
  final class Type extends ws.epigraph.types.AnonMapType.Static<
      ${lqn(kt, t)}.Imm,
      $ln.Imm,
      $ln.Builder,
      $ln.Value.Imm,
      $ln.Value.Builder,
      $ln.Data.Imm,
      $ln.Data.Builder
  > {

    private static final class Holder { public static $ln.Type instance = new $ln.Type(); }

    public static $ln.Type instance() { return Holder.instance; }

    private Type() {
      super(
          java.util.Arrays.asList(${parents(".Type.instance()")}),
          ${t.meta.map{mt => lqn(mt, t, _ + ".type")}.getOrElse("null")},
          ${lqn(kt, t)}.Type.instance(),
          ${dataTypeExpr(vv, t)},
          $ln.Builder::new,
          $ln.Value.Imm.Impl::new,
          $ln.Data.Builder::new
      );
    }

  }

  /**
   * Builder for `${t.name.name}` datum.
   */
  final class Builder extends ws.epigraph.data.MapDatum.Builder.Static<${lqn(kt, t)}.Imm, $ln.Imm, $ln.Value.Builder> implements $ln {

    Builder(@NotNull ws.epigraph.data.MapDatum.Builder.Raw raw) {
      super($ln.Type.instance(), raw, $ln.Imm.Impl::new, $ln.Value.Builder::new);
    }
${t.effectiveDefaultValueTagName match { // default value tag (if defined) views
      case None => ""
      case Some(dtn) => sn"""\

    ${"/**"} Returns modifiable map view of default `$dtn` tag element datums. Elements where the tag datum is not set will be `null`. */
    @Override
    public @NotNull java.util.Map<${lqn(kt, t)}.@NotNull Imm, ${lqn("@Nullable ", tt(vtr, dtn), t)}> datums() {
      return new ws.epigraph.util.MapView<>(
          datas(),
          ${lqn(vt, t)}${vt(vt, "", ".Data")}::get${vt(vt, up(dtn), "")},
          v -> ${lqn(vt, t)}.Type.instance().createDataBuilder().set${vt(vt, up(dtn), "")}(v)
      );
    }

    ${"/**"} Returns map view of element default tag value builders. Elements where the tag value is not set will be `null`. */
    @Override
    public @NotNull java.util.Map<${lqn(kt, t)}.@NotNull Imm, ${lqn(tt(vtr, dtn), t)}.@Nullable Value> values() {
      return new ws.epigraph.util.MapView<>(
          datas(),
          ${lqn(vt, t)}${vt(vt, "", ".Data")}::get${vt(vt, up(dtn), "")}_,
          v -> ${lqn(vt, t)}.Type.instance().createDataBuilder().set${vt(vt, up(dtn), "")}_(v)
      );
    }

    ${"/**"} Associates specified${vt(vt, s" default `$dtn` tag", "")} datum with specified key in this map. */
    public @NotNull $ln.Builder put(@NotNull ${lqn(kt, t)} key, @Nullable ${lqn(tt(vtr, dtn), t)} datum) {
      datas().put(key.toImmutable(), ${lqn(vt, t)}.Type.instance().createDataBuilder().set${vt(vt, up(dtn), "")}(datum));
      return this;
    }

    ${"/**"} Associates specified${vt(vt, s" default `$dtn` tag", "")} error with specified key in this map. */
    public @NotNull $ln.Builder putError(@NotNull ${lqn(kt, t)} key, @NotNull ws.epigraph.errors.ErrorValue error) {
      datas().put(key.toImmutable(), ${lqn(vt, t)}.Type.instance().createDataBuilder().set${vt(vt, up(dtn), "")}_Error(error));
      return this;
    }

    ${"/**"} Associates specified${vt(vt, s" default `$dtn` tag", "")} value with specified key in this map. */
    public @NotNull $ln.Builder put_(@NotNull ${lqn(kt, t)} key, @Nullable ${lqn(tt(vtr, dtn), t)}.Value value) {
      datas().put(key.toImmutable(), ${lqn(vt, t)}.Type.instance().createDataBuilder().set${vt(vt, up(dtn), "")}_(value));
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
      return ws.epigraph.util.Util.cast(_raw().elements());
    }

    ${"/**"} Associates specified data with specified key in this map. */
    public @NotNull $ln.Builder put$$(@NotNull ${lqn(kt, t)} key, @NotNull ${lqn(vt, t)} data) {
      datas().put(key.toImmutable(), data);
      return this;
    }
${
      evt.effectiveTags.map { tag => sn"""\
//
//    /**
//     * Returns modifiable map view of elements `${tag.name}` tag datums. Elements where the tag value is not set will be `null`.
//     */
//    public @NotNull java.util.Map<${lqn(kt, t)}.@NotNull Imm, ${lqn("@Nullable ", tt(vtr, tag.name), t)}> ${jn(tag.name + "Datums")}() {
//      return new ws.epigraph.util.MapView<>(
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
//      return new ws.epigraph.util.MapView<>(
//          datas(),
//          ${lqn(vt, t)}${vt(vt, "", ".Data")}::get${vt(vt, up(tag.name), "")}_,
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
        return ws.epigraph.util.Util.cast(_raw().elements());
      }
"""
  }
}\
${t.meta match {
    case Some(mt) => sn"""\

    ${"/**"}
     * @return meta-data instance
     */
    @Override
    public @Nullable ${lqn(mt, t)} meta() {
      return (${lqn(mt, t)}) _raw().meta();
    }

    ${"/**"}
     * Sets meta-data value
     *
     * @param meta new meta-data value
     *
     * @return {@code this}
     */
    public @NotNull $ln.Builder setMeta(@Nullable ${lqn(mt,t)} meta) {
       _raw().setMeta(meta);
       return this;
    }
"""
    case None => ""
  }
}\

  }

  /**
   * Immutable interface for `${t.name.name}` datum.
   */
  interface Imm extends $ln,${withParents(".Imm")} ws.epigraph.data.MapDatum.Imm.Static<${lqn(kt, t)}.Imm> {
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
    @NotNull java.util.Map<${lqn(kt, t)}.@NotNull Imm, @Nullable ? extends ${lqn(tt(vtr, dtn), t)}.Value.Imm> values();
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
//    /**
//     * Returns immutable map view of `${tag.name}` tag datums. Elements where the tag value is not set will be `null`.
//     */
//    @NotNull java.util.Map<${lqn(kt, t)}.@NotNull Imm, @Nullable ? extends ${lqn(tt(vtr, tag.name), t)}.Imm> ${jn(tag.name + "Datums")}();
//
//    /**
//     * Returns immutable map view of `${tag.name}` tag values. Elements where the tag value is not set will be `null`.
//     */
//    @NotNull java.util.Map<${lqn(kt, t)}.@NotNull Imm, @Nullable ? extends ${lqn(tt(vtr, tag.name), t)}.Value.Imm> ${jn(tag.name + "Values")}();
"""
      }.mkString
}\
"""
    case _ => ""
  }
}\
${t.meta match {
    case Some(mt) => sn"""\

    ${"/**"}
     * @return meta-data instance
     */
    @Override
    @Nullable ${lqn(mt, t)}.Imm meta();
"""
    case None => ""
  }
}\

    /** Private implementation of `$ln.Imm` interface. */
    final class Impl extends ws.epigraph.data.MapDatum.Imm.Static.Impl<${lqn(kt, t)}.Imm, $ln.Imm, $ln.Value.Imm> implements $ln.Imm {

      Impl(@NotNull ws.epigraph.data.MapDatum.Imm.Raw raw) { super($ln.Type.instance(), raw, $ln.Value.Imm.Impl::new); }
${t.effectiveDefaultValueTagName match { // default value tag (if defined) views
      case None => ""
      case Some(dtn) => sn"""\

      ${"/**"}
       * Returns immutable map view of element default tag datums. Elements where the tag datum is not set will be `null`.
       */
      @Override
      public @NotNull java.util.Map<${lqn(kt, t)}.@NotNull Imm, @Nullable ? extends ${lqn(tt(vtr, dtn), t)}.Imm> datums() {
        return new ws.epigraph.util.Unmodifiable.MapView<${lqn(kt, t)}.Imm, ${lqn(vt, t)}${vt(vt, "", ".Data")}.Imm, ${lqn(tt(vtr, dtn), t)}.Imm>(
            datas(),
            ${lqn(vt, t)}${vt(vt, "", ".Data")}.Imm::get${vt(vt, up(dtn), "")}
        );
      }

      ${"/**"}
       * Returns immutable map view of element default tag values. Elements where the tag value is not set will be `null`.
       */
      @Override
      public @NotNull java.util.Map<${lqn(kt, t)}.@NotNull Imm, @Nullable ? extends ${lqn(tt(vtr, dtn), t)}.Value.Imm> values() {
        return new ws.epigraph.util.Unmodifiable.MapView<${lqn(kt, t)}.Imm, ${lqn(vt, t)}${vt(vt, "", ".Data")}.Imm, ${lqn(tt(vtr, dtn), t)}.Value.Imm>(
            datas(),
            ${lqn(vt, t)}${vt(vt, "", ".Data")}.Imm::get${vt(vt, up(dtn), "")}_
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
        return ws.epigraph.util.Util.castEx(_raw().elements());
      }
${
      evt.effectiveTags.map { tag => sn"""\
//
//      /**
//       * Returns immutable map view of `${tag.name}` tag datums. Elements where the tag value is not set will be `null`.
//       */
//      public @NotNull java.util.Map<${lqn(kt, t)}.@NotNull Imm, @Nullable ? extends ${lqn(tt(vtr, tag.name), t)}.Imm> ${jn(tag.name + "Datums")}() {
//        return new ws.epigraph.util.Unmodifiable.MapView<${lqn(kt, t)}.Imm, ${lqn(vt, t)}${vt(vt, "", ".Data")}.Imm, ${lqn(tt(vtr, tag.name), t)}.Imm>(
//            datas(),
//            ${lqn(vt, t)}${vt(vt, "", ".Data")}.Imm::get${vt(vt, up(tag.name), "")}
//        );
//      }
//
//      /**
//       * Returns immutable map view of `${tag.name}` tag values. Elements where the tag value is not set will be `null`.
//       */
//      public @NotNull java.util.Map<${lqn(kt, t)}.@NotNull Imm, @Nullable ? extends ${lqn(tt(vtr, tag.name), t)}.Value.Imm> ${jn(tag.name + "Values")}() {
//        return new ws.epigraph.util.Unmodifiable.MapView<${lqn(kt, t)}.Imm, ${lqn(vt, t)}${vt(vt, "", ".Data")}.Imm, ${lqn(tt(vtr, tag.name), t)}.Value.Imm>(
//            datas(),
//            ${lqn(vt, t)}${vt(vt, "", ".Data")}.Imm::get${vt(vt, up(tag.name), "")}_
//        );
//      }
"""
      }.mkString
}\
"""
    case _ => sn"""\

    // method is private to not expose datas() for non-union types (so simple type can be replaced with union type while preserving backwards-compatibility)
    private @NotNull java.util.Map<@NotNull ? extends ${lqn(kt, t)}.Imm, @NotNull ? extends ${lqn(vt, t)}.Data.Imm> datas() {
      return ws.epigraph.util.Util.castEx(_raw().elements());
    }
"""
  }
}\
${t.meta match {
      case Some(mt) => sn"""\

      ${"/**"}
       * @return meta-data instance
       */
      @Override
      public @Nullable ${lqn(mt, t)}.Imm meta() {
        return (${lqn(mt, t)}.Imm) _raw().meta();
      }
"""
    case None => ""
  }
}\

    }

  }

$datumValue\

$datumData\

}
"""/*@formatter:on*/

}

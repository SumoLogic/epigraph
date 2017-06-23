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

/* Created by yegor on 10/3/16. */
/* Created by yegor on 9/25/16. */

package ws.epigraph.java

import ws.epigraph.compiler.{CDataType, CMapType, CTypeRef, CEntityTypeDef}
import ws.epigraph.java.JavaGenNames.{jn, lqn, pn, pnq2, tt}
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper

abstract class MapGen[Type >: Null <: CMapType](from: Type, ctx: GenContext) extends JavaTypeGen[Type](from, ctx)
    with DatumTypeJavaGen {

  /** key type ref */
  protected val ktr: CTypeRef = t.keyTypeRef

  /** key type */
  protected val kt: ktr.Type = ktr.resolved

  /** value data type */
  protected val vv: CDataType = t.valueDataType

  /** value type ref */
  protected val vtr: CTypeRef = vv.typeRef

  /** value type */
  protected val vt: vtr.Type = vtr.resolved

  protected def genTypeClass(ogc: ObjectGenContext):String

  override def generate: String = {
    val ogc = new ObjectGenContext(ctx, pnq2(t), true)

    val typeClass = genTypeClass(ogc)

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}\
package ${pn(t)};

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
${ObjectGenUtils.genImports(ogc)}\

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
  @NotNull java.util.Map<${lqn(kt, t)}.${NotNull_}Imm, ${Nullable_}? extends ${lqn(tt(vtr, dtn), t)}> datums();

  ${"/**"}
   * Returns map view of element default tag values. Elements where the tag value is not set will be `null`.
   */
  @NotNull java.util.Map<${lqn(kt, t)}.${NotNull_}Imm, ${Nullable_}? extends ${lqn(tt(vtr, dtn), t)}.Value> values();
"""
  }
}\
${vt match { // element tags views (for vartypes)
    case evt: CEntityTypeDef => sn"""\

  ${"/**"}
   * Returns map view of element data.
   */
  @NotNull java.util.Map<${NotNull_}? extends ${lqn(kt, t)}.Imm, ${NotNull_}? extends ${lqn(vt, t)}> datas();
${
      evt.effectiveTags.map { tag => sn"""\
//
//  /**
//   * Returns map view of `${tag.name}` tag datums. Elements where the tag value is not set will be `null`.
//   */
//  @NotNull java.util.Map<${lqn(kt, t)}.${NotNull_}Imm, ${Nullable_}? extends ${lqn(tt(vtr, tag.name), t)}> ${jn(tag.name + "Datums")}();
//
//  /**
//   * Returns map view of `${tag.name}` tag values. Elements where the tag value is not set will be `null`.
//   */
//  @NotNull java.util.Map<${lqn(kt, t)}.${NotNull_}Imm, ${Nullable_}? extends ${lqn(tt(vtr, tag.name), t)}.Value> ${jn(tag.name + "Values")}();
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
   $typeClass\

  /**
   * Builder for `${t.name.name}` datum.
   */
  final class Builder extends ws.epigraph.data.MapDatum.Builder.Static<${lqn(kt, t)}.Imm, $ln.Imm, $ln.Value.Builder> implements $ln {

    Builder(@NotNull ws.epigraph.data.MapDatum.Builder.Raw raw) {
      super($ln.Type.instance(), raw, $ln.Imm.Impl::new, $ln.Value.Builder::new);
    }
${t.effectiveDefaultValueTagName match { // default value tag (if defined) views
      case None => ""
      case Some(dtn) =>

        def primitiveKeyType(nativeType: String) = nativeType
        def nonPrimitiveKeyType: String = lqn(kt, t)
        val keyType = JavaGenUtils.builtInPrimitives.get(ktr.resolved.name.name).map(primitiveKeyType).getOrElse(nonPrimitiveKeyType)

        def primitiveKeyExpr = s"${lqn(tt(ktr, dtn), t)}.create(key)"
        val nonPrimitiveKeyExpr = "key"
        val keyExpr = if (JavaGenUtils.builtInPrimitives.contains(ktr.resolved.name.name)) primitiveKeyExpr else nonPrimitiveKeyExpr

        def primitiveValueType(nativeType: String) = nativeType
        def nonPrimitiveValueType: String = lqn(tt(vtr, dtn), t)
        val valueType = JavaGenUtils.builtInPrimitives.get(vtr.resolved.name.name).map(primitiveValueType).getOrElse(nonPrimitiveValueType)

        def primitiveValueExpr = s"${lqn(tt(vtr, dtn), t)}.create(datum)"
        val nonPrimitiveValueExpr = "datum"
        val valueExpr = if (JavaGenUtils.builtInPrimitives.contains(vtr.resolved.name.name)) primitiveValueExpr else nonPrimitiveValueExpr

        sn"""\

    ${"/**"} Returns modifiable map view of default `$dtn` tag element datums. Elements where the tag datum is not set will be `null`. */
    @Override
    public @NotNull java.util.Map<${lqn(kt, t)}.${NotNull_}Imm, ${lqn(Nullable_, tt(vtr, dtn), t)}> datums() {
      return new ws.epigraph.util.MapView<>(
          datas(),
          ${lqn(vt, t)}${vt(vt, "", ".Data")}::get${vt(vt, up(dtn), "")},
          v -> ${lqn(vt, t)}.Type.instance().createDataBuilder().set${vt(vt, up(dtn), "")}(v)
      );
    }

    ${"/**"} Returns map view of element default tag value builders. Elements where the tag value is not set will be `null`. */
    @Override
    public @NotNull java.util.Map<${lqn(kt, t)}.${NotNull_}Imm, ${lqn(tt(vtr, dtn), t)}.${Nullable_}Value> values() {
      return new ws.epigraph.util.MapView<>(
          datas(),
          ${lqn(vt, t)}${vt(vt, "", ".Data")}::get${vt(vt, up(dtn), "")}_,
          v -> ${lqn(vt, t)}.Type.instance().createDataBuilder().set${vt(vt, up(dtn), "")}_(v)
      );
    }

    ${"/**"} Associates specified${vt(vt, s" default `$dtn` tag", "")} datum with specified key in this map. */
    public @NotNull $ln.Builder put(@NotNull $keyType key, @Nullable $valueType datum) {
      datas().put($keyExpr.toImmutable(), ${lqn(vt, t)}.Type.instance().createDataBuilder().set${vt(vt, up(dtn), "")}($valueExpr));
      return this;
    }

    ${"/**"} Associates specified${vt(vt, s" default `$dtn` tag", "")} error with specified key in this map. */
    public @NotNull $ln.Builder putError(@NotNull ${lqn(kt, t)} key, @NotNull ws.epigraph.errors.ErrorValue error) {
      datas().put(key.toImmutable(), ${lqn(vt, t)}.Type.instance().createDataBuilder().set${vt(vt, up(dtn), "")}_Error(error));
      return this;
    }

    ${"/**"} Associates specified${vt(vt, s" default `$dtn` tag", "")} value with specified key in this map or removes it if {@code value} is {@code null}. */
    public @NotNull $ln.Builder put_(@NotNull ${lqn(kt, t)} key, @Nullable ${lqn(tt(vtr, dtn), t)}.Value value) {
      if (value == null)
        datas().remove(key.toImmutable());
      else
        datas().put(key.toImmutable(), ${lqn(vt, t)}.Type.instance().createDataBuilder().set${vt(vt, up(dtn), "")}_(value));
      return this;
    }
"""
  }
}\
${vt match { // data view (for vartypes)
    case evt: CEntityTypeDef => sn"""\

    ${"/**"} Returns modifiable map view of element data builders. */
    @Override
    public @NotNull java.util.Map<${lqn(kt, t)}.${NotNull_}Imm, ${lqn(NotNull_, vt, t)}> datas() {
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
//    public @NotNull java.util.Map<${lqn(kt, t)}.${NotNull_}Imm, ${lqn("@Nullable ", tt(vtr, tag.name), t)}> ${jn(tag.name + "Datums")}() {
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
//    public @NotNull java.util.Map<${lqn(kt, t)}.${NotNull_}Imm, ${lqn(tt(vtr, tag.name), t)}.${Nullable_}Value> ${jn(tag.name + "Values")}() {
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
      private @NotNull java.util.Map<${lqn(kt, t)}.${NotNull_}Imm, ${lqn(vt, t)}.${NotNull_}Data> datas() {
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
    @NotNull java.util.Map<${lqn(kt, t)}.${NotNull_}Imm, ${Nullable_}? extends ${lqn(tt(vtr, dtn), t)}.Imm> datums();

    ${"/**"}
     * Returns immutable map view of element default tag values. Elements where the tag value is not set will be `null`.
     */
    @Override
    @NotNull java.util.Map<${lqn(kt, t)}.${NotNull_}Imm, ${Nullable_}? extends ${lqn(tt(vtr, dtn), t)}.Value.Imm> values();
"""
  }
}\
${vt match { // element tags (for vartypes)
    case evt: CEntityTypeDef => sn"""\

    ${"/**"}
     * Returns immutable map view of elements data.
     */
    @NotNull java.util.Map<${NotNull_}? extends ${lqn(kt, t)}.Imm, ${NotNull_}? extends ${lqn(vt, t)}.Imm> datas();
${
        evt.effectiveTags.map { tag => sn"""\
//
//    /**
//     * Returns immutable map view of `${tag.name}` tag datums. Elements where the tag value is not set will be `null`.
//     */
//    @NotNull java.util.Map<${lqn(kt, t)}.${NotNull_}Imm, ${Nullable_}? extends ${lqn(tt(vtr, tag.name), t)}.Imm> ${jn(tag.name + "Datums")}();
//
//    /**
//     * Returns immutable map view of `${tag.name}` tag values. Elements where the tag value is not set will be `null`.
//     */
//    @NotNull java.util.Map<${lqn(kt, t)}.${NotNull_}Imm, ${Nullable_}? extends ${lqn(tt(vtr, tag.name), t)}.Value.Imm> ${jn(tag.name + "Values")}();
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
      public @NotNull java.util.Map<${lqn(kt, t)}.${NotNull_}Imm, ${Nullable_}? extends ${lqn(tt(vtr, dtn), t)}.Imm> datums() {
        return new ws.epigraph.util.Unmodifiable.MapView<${lqn(kt, t)}.Imm, ${lqn(vt, t)}${vt(vt, "", ".Data")}.Imm, ${lqn(tt(vtr, dtn), t)}.Imm>(
            datas(),
            ${lqn(vt, t)}${vt(vt, "", ".Data")}.Imm::get${vt(vt, up(dtn), "")}
        );
      }

      ${"/**"}
       * Returns immutable map view of element default tag values. Elements where the tag value is not set will be `null`.
       */
      @Override
      public @NotNull java.util.Map<${lqn(kt, t)}.${NotNull_}Imm, ${Nullable_}? extends ${lqn(tt(vtr, dtn), t)}.Value.Imm> values() {
        return new ws.epigraph.util.Unmodifiable.MapView<${lqn(kt, t)}.Imm, ${lqn(vt, t)}${vt(vt, "", ".Data")}.Imm, ${lqn(tt(vtr, dtn), t)}.Value.Imm>(
            datas(),
            ${lqn(vt, t)}${vt(vt, "", ".Data")}.Imm::get${vt(vt, up(dtn), "")}_
        );
      }
"""
  }
}\
${vt match { // element tags (for vartypes)
    case evt: CEntityTypeDef => sn"""\

      ${"/**"}
       * Returns immutable map view of elements data.
       */
      @Override
      public @NotNull java.util.Map<${NotNull_}? extends ${lqn(kt, t)}.Imm, ${NotNull_}? extends ${lqn(vt, t)}.Imm> datas() {
        return ws.epigraph.util.Util.castEx(_raw().elements());
      }
${
      evt.effectiveTags.map { tag => sn"""\
//
//      /**
//       * Returns immutable map view of `${tag.name}` tag datums. Elements where the tag value is not set will be `null`.
//       */
//      public @NotNull java.util.Map<${lqn(kt, t)}.${NotNull_}Imm, ${Nullable_}? extends ${lqn(tt(vtr, tag.name), t)}.Imm> ${jn(tag.name + "Datums")}() {
//        return new ws.epigraph.util.Unmodifiable.MapView<${lqn(kt, t)}.Imm, ${lqn(vt, t)}${vt(vt, "", ".Data")}.Imm, ${lqn(tt(vtr, tag.name), t)}.Imm>(
//            datas(),
//            ${lqn(vt, t)}${vt(vt, "", ".Data")}.Imm::get${vt(vt, up(tag.name), "")}
//        );
//      }
//
//      /**
//       * Returns immutable map view of `${tag.name}` tag values. Elements where the tag value is not set will be `null`.
//       */
//      public @NotNull java.util.Map<${lqn(kt, t)}.${NotNull_}Imm, ${Nullable_}? extends ${lqn(tt(vtr, tag.name), t)}.Value.Imm> ${jn(tag.name + "Values")}() {
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
      private @NotNull java.util.Map<${NotNull_}? extends ${lqn(kt, t)}.Imm, ${NotNull_}? extends ${lqn(vt, t)}.Data.Imm> datas() {
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

}

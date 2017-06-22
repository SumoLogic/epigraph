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

package ws.epigraph.java

import ws.epigraph.compiler.{CDataType, CListType, CTypeRef, CEntityTypeDef}
import ws.epigraph.java.JavaGenNames.{jn, lqn, pn, tt}
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper

abstract class ListGen[Type >: Null <: CListType](from: Type, ctx: GenContext) extends JavaTypeGen[Type](from, ctx)
    with DatumTypeJavaGen {

  /** element value type */
  protected val ev: CDataType = t.elementDataType

  /** element type ref */
  protected val etr: CTypeRef = ev.typeRef

  /** element type */
  protected val et: etr.Type = etr.resolved

  protected def genTypeClass(ogc: ObjectGenContext):String

  override def generate: String = {
    val ogc = new ObjectGenContext(ctx)
    ogc.addImport("org.jetbrains.annotations.NotNull")
    ogc.addImport("org.jetbrains.annotations.Nullable")

    val typeClass = genTypeClass(ogc)

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}\
package ${pn(t)};

${ObjectGenUtils.genImports(ogc)}\

/**
 * Base (read) interface for `${t.name.name}` datum.
 */
@javax.annotation.Generated("${getClass.getCanonicalName}")
public interface $ln extends${JavaGenUtils.withParents(t)} ws.epigraph.data.ListDatum.Static {

  $ln.Type type = $ln.Type.instance();

  static @NotNull $ln.Builder create() { return $ln.Type.instance().createBuilder(); }

  @Override
  @NotNull $ln.Imm toImmutable();
${t.effectiveDefaultElementTagName match { // default element tag (if defined) views
      case None => ""
      case Some(dtn) => sn"""\

  ${"/**"}
   * Returns list view of element default tag datums. Elements where the tag datum is not set will be `null`.
   */
  @NotNull java.util.List<${Nullable_}? extends ${lqn(tt(etr, dtn), t)}> datums();

  ${"/**"}
   * Returns list view of element default tag values. Elements where the tag value is not set will be `null`.
   */
  @NotNull java.util.List<${Nullable_}? extends ${lqn(tt(etr, dtn), t)}.Value> values();
"""
  }
}\
${et match { // element tags views (for vartypes)
    case evt: CEntityTypeDef => sn"""\

  ${"/**"}
   * Returns list view of element data.
   */
  @NotNull java.util.List<${NotNull_}? extends ${lqn(et, t)}> datas();
${
      evt.effectiveTags.map { tag => sn"""\
//
//  /**
//   * Returns list view of `${tag.name}` tag datums. Elements where the tag value is not set will be `null`.
//   */
//  @NotNull java.util.List<${Nullable_}? extends ${lqn(tt(etr, tag.name), t)}> ${jn(tag.name + "Datums")}();
//
//  /**
//   * Returns list view of `${tag.name}` tag values. Elements where the tag value is not set will be `null`.
//   */
//  @NotNull java.util.List<${Nullable_}? extends ${lqn(tt(etr, tag.name), t)}.Value> ${jn(tag.name + "Values")}();
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
  final class Builder extends ws.epigraph.data.ListDatum.Builder.Static<$ln.Imm, $ln.Value.Builder> implements $ln {

    Builder(@NotNull ws.epigraph.data.ListDatum.Builder.Raw raw) {
      super($ln.Type.instance(), raw, $ln.Imm.Impl::new, $ln.Value.Builder::new);
    }
${t.effectiveDefaultElementTagName match { // default element tag (if defined) views
      case None => ""
      case Some(dtn) =>

        def genPrimitiveAdd(nativeType: String): String =
        sn"""\
    ${"/**"} Adds${vt(et, s" default `$dtn` tag", "")} datum element to the list. */
    public @NotNull $ln.Builder add(@Nullable $nativeType datum) {
      datas().add(${lqn(et, t)}.Type.instance().createDataBuilder().set${vt(et, up(dtn), "")}(
        datum == null ? null : ${lqn(tt(etr, dtn), t)}.create(datum)
      ));
      return this;
    }
"""

        def genNonPrimitiveAdd: String =
        sn"""\
    ${"/**"} Adds${vt(et, s" default `$dtn` tag", "")} datum element to the list. */
    public @NotNull $ln.Builder add(@Nullable ${lqn(tt(etr, dtn), t)} datum) {
      datas().add(${lqn(et, t)}.Type.instance().createDataBuilder().set${vt(et, up(dtn), "")}(datum));
      return this;
    }
"""

        val add = JavaGenUtils.builtInPrimitives
          .get(etr.resolved.name.name)
          .map(genPrimitiveAdd)
          .getOrElse(genNonPrimitiveAdd)

        sn"""\

    ${"/**"} Returns modifiable list view of default `$dtn` tag element datums. Elements where the tag datum is not set will be `null`. */
    @Override
    public @NotNull java.util.List<${lqn(Nullable_, tt(etr, dtn), t)}> datums() {
      return new ws.epigraph.util.ListView<>(
          datas(),
          ${lqn(et, t)}${vt(et, "", ".Data")}::get${vt(et, up(dtn), "")},
          v -> ${lqn(et, t)}.Type.instance().createDataBuilder().set${vt(et, up(dtn), "")}(v)
      );
    }

    ${"/**"} Returns list view of element default tag value builders. Elements where the tag value is not set will be `null`. */
    @Override
    public @NotNull java.util.List<${lqn(tt(etr, dtn), t)}.${Nullable_}Value> values() {
      return new ws.epigraph.util.ListView<>(
          datas(),
          ${lqn(et, t)}${vt(et, "", ".Data")}::get${vt(et, up(dtn), "")}_,
          v -> ${lqn(et, t)}.Type.instance().createDataBuilder().set${vt(et, up(dtn), "")}_(v)
      );
    }

$add\

    ${"/**"} Adds${vt(et, s" default `$dtn` tag", "")} error element to the list. */
    public @NotNull $ln.Builder addError(@NotNull ws.epigraph.errors.ErrorValue error) {
      datas().add(${lqn(et, t)}.Type.instance().createDataBuilder().set${vt(et, up(dtn), "")}_Error(error));
      return this;
    }

    ${"/**"} Adds${vt(et, s" default `$dtn` tag", "")} value element to the list. */
    public @NotNull $ln.Builder add_(@Nullable ${lqn(tt(etr, dtn), t)}.Value value) {
      datas().add(${lqn(et, t)}.Type.instance().createDataBuilder().set${vt(et, up(dtn), "")}_(value));
      return this;
    }
"""
  }
}\
${et match { // data view (for vartypes)
    case evt: CEntityTypeDef => sn"""\

    ${"/**"} Returns modifiable list view of element data builders. */
    @Override
    public @NotNull java.util.List<${lqn(NotNull_, et, t)}> datas() {
      return ws.epigraph.util.Util.cast(_raw().elements());
    }

    ${"/**"} Adds data element to the list. */
    public @NotNull $ln.Builder add(@NotNull ${lqn(et, t)} data) {
      datas().add(data);
      return this;
    }
${
      evt.effectiveTags.map { tag => sn"""\
//
//    /**
//     * Returns modifiable list view of elements `${tag.name}` tag datums. Elements where the tag value is not set will be `null`.
//     */
//    public @NotNull java.util.List<${lqn("@Nullable ", tt(etr, tag.name), t)}> ${jn(tag.name + "Datums")}() {
//      return new ws.epigraph.util.ListView<>(
//          datas(),
//          ${lqn(et, t)}${vt(et, "", ".Data")}::get${vt(et, up(tag.name), "")},
//          v -> ${lqn(et, t)}.Type.instance().createDataBuilder().set${vt(et, up(tag.name), "")}(v)
//      );
//    }
//
//    /**
//     * Returns modifiable list view of elements `${tag.name}` tag values. Elements where the tag value is not set will be `null`.
//     */
//    public @NotNull java.util.List<${lqn(tt(etr, tag.name), t)}.${Nullable_}Value> ${jn(tag.name + "Values")}() {
//      return new ws.epigraph.util.ListView<>(
//          datas(),
//          ${lqn(et, t)}${vt(et, "", ".Data")}::get${vt(et, up(tag.name), "")}_,
//          v -> ${lqn(et, t)}.Type.instance().createDataBuilder().set${vt(et, up(tag.name), "")}(v)
//      );
//    }
"""
      }.mkString
}\
"""
    case _ => sn"""\

      // method is private to not expose datas() for non-union types (so simple type can be replaced with union type while preserving backwards-compatibility)
      private @NotNull java.util.List<${lqn(et, t)}.${NotNull_}Data> datas() {
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
  interface Imm extends $ln,${withParents(".Imm")} ws.epigraph.data.ListDatum.Imm.Static {
${t.effectiveDefaultElementTagName match { // default element tag (if defined) views
      case None => ""
      case Some(dtn) => sn"""\

    ${"/**"}
     * Returns immutable list view of element default tag datums. Elements where the tag datum is not set will be `null`.
     */
    @Override
    @NotNull java.util.List<${Nullable_}? extends ${lqn(tt(etr, dtn), t)}.Imm> datums();

    ${"/**"}
     * Returns immutable list view of element default tag values. Elements where the tag value is not set will be `null`.
     */
    @Override
  @NotNull java.util.List<${Nullable_}? extends ${lqn(tt(etr, dtn), t)}.Value.Imm> values();
"""
  }
}\
${et match { // element tags (for vartypes)
    case evt: CEntityTypeDef => sn"""\

    ${"/**"}
     * Returns immutable list view of elements data.
     */
    @NotNull java.util.List<${NotNull_}? extends ${lqn(et, t)}.Imm> datas();
${
      evt.effectiveTags.map { tag => sn"""\
//
//    /**
//     * Returns immutable list view of `${tag.name}` tag datums. Elements where the tag value is not set will be `null`.
//     */
//    @NotNull java.util.List<${Nullable_}? extends ${lqn(tt(etr, tag.name), t)}.Imm> ${jn(tag.name + "Datums")}();
//
//    /**
//     * Returns immutable list view of `${tag.name}` tag values. Elements where the tag value is not set will be `null`.
//     */
//    @NotNull java.util.List<${Nullable_}? extends ${lqn(tt(etr, tag.name), t)}.Value.Imm> ${jn(tag.name + "Values")}();
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
    final class Impl extends ws.epigraph.data.ListDatum.Imm.Static.Impl<$ln.Imm, $ln.Value.Imm> implements $ln.Imm {

      Impl(@NotNull ws.epigraph.data.ListDatum.Imm.Raw raw) { super($ln.Type.instance(), raw, $ln.Value.Imm.Impl::new); }
${t.effectiveDefaultElementTagName match { // default element tag (if defined) views
      case None => ""
      case Some(dtn) => sn"""\

      ${"/**"}
       * Returns immutable list view of element default tag datums. Elements where the tag datum is not set will be `null`.
       */
      @Override
      public @NotNull java.util.List<${Nullable_}? extends ${lqn(tt(etr, dtn), t)}.Imm> datums() {
        return new ws.epigraph.util.Unmodifiable.ListView<${lqn(et, t)}${vt(et, "", ".Data")}.Imm, ${lqn(tt(etr, dtn), t)}.Imm>(
            datas(),
            ${lqn(et, t)}${vt(et, "", ".Data")}.Imm::get${vt(et, up(dtn), "")}
        );
      }

      ${"/**"}
       * Returns immutable list view of element default tag values. Elements where the tag value is not set will be `null`.
       */
      @Override
      public @NotNull java.util.List<${Nullable_}? extends ${lqn(tt(etr, dtn), t)}.Value.Imm> values() {
        return new ws.epigraph.util.Unmodifiable.ListView<${lqn(et, t)}${vt(et, "", ".Data")}.Imm, ${lqn(tt(etr, dtn), t)}.Value.Imm>(
            datas(),
            ${lqn(et, t)}${vt(et, "", ".Data")}.Imm::get${vt(et, up(dtn), "")}_
        );
      }
"""
  }
}\
${et match { // element tags (for vartypes)
    case evt: CEntityTypeDef => sn"""\

      ${"/**"}
       * Returns immutable list view of elements data.
       */
      @Override
      public @NotNull java.util.List<${NotNull_}? extends ${lqn(et, t)}.Imm> datas() {
        return ws.epigraph.util.Util.castEx(_raw().elements());
      }
${
      evt.effectiveTags.map { tag => sn"""\
//
//      /**
//       * Returns immutable list view of `${tag.name}` tag datums. Elements where the tag value is not set will be `null`.
//       */
//      public @NotNull java.util.List<${Nullable_}? extends ${lqn(tt(etr, tag.name), t)}.Imm> ${jn(tag.name + "Datums")}() {
//        return new ws.epigraph.util.Unmodifiable.ListView<${lqn(et, t)}${vt(et, "", ".Data")}.Imm, ${lqn(tt(etr, tag.name), t)}.Imm>(
//            datas(),
//            ${lqn(et, t)}${vt(et, "", ".Data")}.Imm::get${vt(et, up(tag.name), "")}
//        );
//      }
//
//      /**
//       * Returns immutable list view of `${tag.name}` tag values. Elements where the tag value is not set will be `null`.
//       */
//      public @NotNull java.util.List<${Nullable_}? extends ${lqn(tt(etr, tag.name), t)}.Value.Imm> ${jn(tag.name + "Values")}() {
//        return new ws.epigraph.util.Unmodifiable.ListView<${lqn(et, t)}${vt(et, "", ".Data")}.Imm, ${lqn(tt(etr, tag.name), t)}.Value.Imm>(
//            datas(),
//            ${lqn(et, t)}${vt(et, "", ".Data")}.Imm::get${vt(et, up(tag.name), "")}_
//        );
//      }
"""
      }.mkString
}\
"""
    case _ => sn"""\

      // method is private to not expose datas() for non-union types (so simple type can be replaced with union type while preserving backwards-compatibility)
      private @NotNull java.util.List<${NotNull_}? extends ${lqn(et, t)}.Data.Imm> datas() {
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

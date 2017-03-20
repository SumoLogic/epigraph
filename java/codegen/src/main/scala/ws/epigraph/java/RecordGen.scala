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

/* Created by yegor on 7/11/16. */

package ws.epigraph.java

import ws.epigraph.compiler._
import ws.epigraph.java.JavaGenNames.{fcn, pn, lqn, lqrn, lqdrn, tt, dttr, tcr, qnameArgs}
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper

class RecordGen(from: CRecordTypeDef, ctx: GenContext) extends JavaTypeDefGen[CRecordTypeDef](from, ctx)
    with DatumTypeJavaGen {

  protected def generate: String = /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
package ${pn(t)};

import ws.epigraph.types.RecordType.Field;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base (read) interface for `${t.name.name}` datum.
 */
public interface $ln extends${JavaGenUtils.withParents(t)} ws.epigraph.data.RecordDatum.Static {

  @NotNull $ln.Type type = $ln.Type.instance();

  static @NotNull $ln.Builder create() { return $ln.Type.instance().createBuilder(); }

  @Override
  @NotNull $ln.Imm toImmutable();
${t.effectiveFields.map { f => sn"""\

  ${"/**"} Field `${f.name}`. */
  @NotNull Field ${fcn(f)} = new Field("${f.name}", ${lqrn(f.typeRef, t)}.Type.instance().dataType(${vt(f.typeRef, tcr(f.valueDataType, t), "")}));
"""
  }.mkString
}\
${t.effectiveFields.map { f => sn"""\
${  f.valueDataType.typeRef.resolved match { // data accessors (for union typed fields)
      case vartype: CVarTypeDef => sn"""\

  /** Returns `${f.name}` field data. */
  //@Override TODO where applicable
  @Nullable ${lqdrn(f.typeRef, t)} get${up(f.name)}$$();
"""
      case _: CDatumType => "" // no data accessors for datum fields
      case unexpected => throw new UnsupportedOperationException(unexpected.name.name)
    }
}\
${  f.effectiveDefaultTagName match { // default tag accessors (implied or explicit, if any)
      case None => ""
      case Some(dtn) => 
        
        def genPrimitiveGetter(nativeType: String): String =
        sn"""\
  /** Returns `${f.name}` field datum${vt(f.typeRef, s" for default `$dtn` tag", "")}. */
  //@Override TODO where applicable
  @Nullable $nativeType get${up(f.name)}();
"""
        def genNonPrimitiveGetter: String =
        sn"""\
  /** Returns `${f.name}` field datum${vt(f.typeRef, s" for default `$dtn` tag", "")}. */
  //@Override TODO where applicable
  @Nullable ${lqn(tt(f.typeRef, dtn), t)} get${up(f.name)}();
"""
        val getter = JavaGenUtils.builtInPrimitives
          .get(f.typeRef.resolved.name.name)
          .map(genPrimitiveGetter)
          .getOrElse(genNonPrimitiveGetter)
        
        sn"""\

$getter\

  /** Returns `${f.name}` field entry${vt(f.typeRef, s" for default `$dtn` tag", "")}. */
  //@Override TODO where applicable
  @Nullable ${lqn(tt(f.typeRef, dtn), t)}.Value get${up(f.name)}_();
"""
    }
}\
"""
  }.mkString
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
   * Class for `${t.name.name}` datum type.
   */
  class Type extends ws.epigraph.types.RecordType.Static<
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
          new ws.epigraph.names.QualifiedTypeName(${qnameArgs(t.name.fqn).mkString("\"", "\", \"", "\"")}),
          java.util.Arrays.asList(${t.linearizedParents.map(lqn(_, t, _ + ".Type.instance()")).mkString(", ")}),
          ${t.meta.map{mt => lqn(mt, t, _ + ".type")}.getOrElse("null")},
          $ln.Builder::new,
          $ln.Imm.Value.Impl::new,
          $ln.Builder.Data::new
      );
    }

    @Override
    public @NotNull java.util.List<@NotNull Field> immediateFields() {
      return java.util.Arrays.asList(\
${t.declaredFields.map { f => sn"""
          ${ln + '.' + fcn(f)}"""
  }.mkString(",")
}
      );
    }

  }

  /**
   * Base interface for `${t.name.name}` value (holding a datum or an error).
   */
  interface Value extends${withParents(".Value")} ws.epigraph.data.Val.Static {

    @Override
    @Nullable $ln getDatum();

    @Override
    @NotNull $ln.Imm.Value toImmutable();

  }

  /**
   * Base interface for `${t.name.name}` data (holding single default representation of the type).
   */
  interface Data extends${withParents(".Data")} ws.epigraph.data.Data.Static {

    @Override
    @NotNull $ln.Imm.Data toImmutable();

    @Nullable $ln get();

    @Nullable $ln.Value get_();

  }

  /**
   * Immutable interface for `${t.name.name}` datum.
   */
  interface Imm extends $ln,${withParents(".Imm")} ws.epigraph.data.RecordDatum.Imm.Static {
${t.effectiveFields.map { f => sn"""\
${  f.valueDataType.typeRef.resolved match { // data accessors (for union typed fields)
      case vartype: CVarTypeDef => sn"""\

    /** Returns immutable `${f.name}` field data. */
    @Override
    @Nullable ${lqdrn(f.typeRef, t)}.Imm get${up(f.name)}$$();
"""
      case _: CDatumType => "" // no data accessors for datum fields
      case unexpected => throw new UnsupportedOperationException(unexpected.name.name)
    }
}\
${  f.effectiveDefaultTagName match { // default tag accessors (implied or explicit, if any)
      case None => ""
      case Some(dtn) =>
        def genPrimitiveGetter(nativeType: String): String =
        sn"""\
    /** Returns immutable `${f.name}` field datum${vt(f.typeRef, s" for default `$dtn` tag", "")}. */
    @Override
    @Nullable $nativeType get${up(f.name)}();
"""
        def genNonPrimitiveGetter: String =
        sn"""\
    /** Returns immutable `${f.name}` field datum${vt(f.typeRef, s" for default `$dtn` tag", "")}. */
    @Override
    @Nullable ${lqn(tt(f.typeRef, dtn), t)}.Imm get${up(f.name)}();
"""
        val getter = JavaGenUtils.builtInPrimitives
          .get(f.typeRef.resolved.name.name)
          .map(genPrimitiveGetter)
          .getOrElse(genNonPrimitiveGetter)

        sn"""\

$getter\

    /** Returns immutable `${f.name}` field entry${vt(f.typeRef, s" for default `$dtn` tag", "")}. */
    @Override
    @Nullable ${lqn(tt(f.typeRef, dtn), t)}.Imm.Value get${up(f.name)}_();
"""
    }
}\
"""
  }.mkString
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
    final class Impl extends ws.epigraph.data.RecordDatum.Imm.Static.Impl<$ln.Imm, $ln.Imm.Value> implements $ln.Imm {

      private Impl(@NotNull ws.epigraph.data.RecordDatum.Imm.Raw raw) { super($ln.Type.instance(), raw, $ln.Imm.Value.Impl::new); }
${t.effectiveFields.map { f => sn"""\
${  f.valueDataType.typeRef.resolved match { // data accessors (for union typed fields)
      case vartype: CVarTypeDef => sn"""\

        /** Returns immutable `${f.name}` field data. */
        @Override
        public @Nullable ${lqdrn(f.typeRef, t)}.Imm get${up(f.name)}$$() {
          return (${lqdrn(f.typeRef, t)}.Imm) _raw().getData($ln.${fcn(f)});
        }
"""
      case _: CDatumType => "" // no data accessors for datum fields
      case unexpected => throw new UnsupportedOperationException(unexpected.name.name)
    }
}\
${  f.effectiveDefaultTagName match { // default tag accessors (implied or explicit, if any)
      case None => ""
      case Some(dtn) =>

        def genPrimitiveGetter(nativeType: String): String =
        sn"""\
        /** Returns immutable `${f.name}` field datum${vt(f.typeRef, s" for default `$dtn` tag", "")}. */
        @Override
        public @Nullable $nativeType get${up(f.name)}() {
          return get${up(f.name)}_().getDatum().getVal();
        }
"""
        def genNonPrimitiveGetter: String =
        sn"""\
        /** Returns immutable `${f.name}` field datum${vt(f.typeRef, s" for default `$dtn` tag", "")}. */
        @Override
        public @Nullable ${lqn(tt(f.typeRef, dtn), t)}.Imm get${up(f.name)}() {
          return ws.epigraph.util.Util.apply(get${up(f.name)}_(), ${lqn(tt(f.typeRef, dtn), t)}.Imm.Value::getDatum);
        }
"""
        val getter = JavaGenUtils.builtInPrimitives
          .get(f.typeRef.resolved.name.name)
          .map(genPrimitiveGetter)
          .getOrElse(genNonPrimitiveGetter)

        sn"""\

$getter\

        /** Returns immutable `${f.name}` field entry${vt(f.typeRef, s" for default `$dtn` tag", "")}. */
        @Override
        public @Nullable ${lqn(tt(f.typeRef, dtn), t)}.Imm.Value get${up(f.name)}_() {
          return (${lqn(tt(f.typeRef, dtn), t)}.Imm.Value) _raw().getValue($ln.${fcn(f)}, ${dttr(f.valueDataType, dtn, t)});
        }
"""
    }
}\
"""
  }.mkString
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

    /**
     * Immutable interface for `${t.name.name}` value (holding an immutable datum or an error).
     */
    interface Value extends $ln.Value,${withParents(".Imm.Value")} ws.epigraph.data.Val.Imm.Static {

      @Override
      @Nullable $ln.Imm getDatum();

      /** Private implementation of `$ln.Imm.Value` interface. */
      final class Impl extends ws.epigraph.data.Val.Imm.Static.Impl<$ln.Imm.Value, $ln.Imm>
          implements $ln.Imm.Value {

        public Impl(@NotNull ws.epigraph.data.Val.Imm.Raw raw) { super(raw); }

      }

    }

    /**
     * Immutable interface for `${t.name.name}` data (holding single default representation of the type).
     */
    interface Data extends $ln.Data,${withParents(".Imm.Data")} ws.epigraph.data.Data.Imm.Static {

      @Override
      @Nullable $ln.Imm get();

      @Override
      @Nullable $ln.Imm.Value get_();

      /** Private implementation of `$ln.Imm.Data` interface. */
      final class Impl extends ws.epigraph.data.Data.Imm.Static.Impl<$ln.Imm.Data>
          implements $ln.Imm.Data {

        protected Impl(@NotNull ws.epigraph.data.Data.Imm.Raw raw) { super($ln.Type.instance(), raw); }

        @Override
        public @Nullable $ln.Imm get() {
          return ($ln.Imm) _raw().getDatum($ln.Type.instance().self);
        }

        @Override
        public @Nullable $ln.Imm.Value get_() {
          return ($ln.Imm.Value) _raw().getValue($ln.Type.instance().self);
        }

      }

    }

  }

  /**
   * Builder for `${t.name.name}` datum.
   */
  final class Builder extends ws.epigraph.data.RecordDatum.Builder.Static<$ln.Imm, $ln.Builder.Value> implements $ln {

    private Builder(@NotNull ws.epigraph.data.RecordDatum.Builder.Raw raw) { super($ln.Type.instance(), raw, $ln.Imm.Impl::new, $ln.Builder.Value::new); }
${t.effectiveFields.map { f => // for each effective field
    sn"""\
${  f.valueDataType.typeRef.resolved match { // data accessors (for union typed fields)
      case vartype: CVarTypeDef => sn"""\

    /** Returns `${f.name}` field data. */
    @Override
    public @Nullable ${lqdrn(f.typeRef, t)} get${up(f.name)}$$() { return (${lqdrn(f.typeRef, t)}) _raw().getData($ln.${fcn(f)}); }

    /** Sets `${f.name}` field data. */
    public @NotNull $ln.Builder set${up(f.name)}$$(@Nullable ${lqrn(f.typeRef, t)} ${fcn(f)}) {
      _raw().setData($ln.${fcn(f)}, ${fcn(f)}); return this;
    }

"""
      case _: CDatumType => "" // no data accessors for datum fields
      case unexpected => throw new UnsupportedOperationException(unexpected.name.name)
    }
}\
${  f.effectiveDefaultTagName match { // default tag (implied or explicit, if any)
      case None => ""
      case Some(dtn) =>
        
        def genPrimitiveGetter(nativeType: String): String =
        sn"""\
    /** Returns `${f.name}` field datum${vt(f.typeRef, s" for default `$dtn` tag", "")}. */
    @Override
    public @Nullable $nativeType get${up(f.name)}() {
      return get${up(f.name)}_().getDatum().getVal();
    }
"""
        def genNonPrimitiveGetter: String =
        sn"""\
    /** Returns `${f.name}` field datum${vt(f.typeRef, s" for default `$dtn` tag", "")}. */
    @Override
    public @Nullable ${lqn(tt(f.typeRef, dtn), t)} get${up(f.name)}() {
      return ws.epigraph.util.Util.apply(get${up(f.name)}_(), ${lqn(tt(f.typeRef, dtn), t)}.Value::getDatum);
    }
"""

        def genPrimitiveSetter(nativeType: String): String =
        sn"""\
    /** Sets `${f.name}` field to specified ${vt(f.typeRef, s"default `$dtn` tag ", "")}datum. */
    public @NotNull $ln.Builder set${up(f.name)}(@Nullable $nativeType ${fcn(f)}) {
      if (${fcn(f)} == null)
        _raw().setData($ln.${fcn(f)}, ${lqrn(f.typeRef, t)}.Type.instance().createDataBuilder().set${vt(f.typeRef, up(dtn), "")}_(${lqn(tt(f.typeRef, dtn), t)}.type.createValue(null)));
      else
        _raw().setData($ln.${fcn(f)}, ${lqrn(f.typeRef, t)}.Type.instance().createDataBuilder().set${vt(f.typeRef, up(dtn), "")}(${lqn(tt(f.typeRef, dtn), t)}.create(${fcn(f)})));
      return this;
    }
"""
        def genNonPrimitiveSetter: String =
        sn"""\
    /** Sets `${f.name}` field to specified ${vt(f.typeRef, s"default `$dtn` tag ", "")}datum. */
    public @NotNull $ln.Builder set${up(f.name)}(@Nullable ${lqn(tt(f.typeRef, dtn), t)} ${fcn(f)}) {
      _raw().setData($ln.${fcn(f)}, ${lqrn(f.typeRef, t)}.Type.instance().createDataBuilder().set${vt(f.typeRef, up(dtn), "")}(${fcn(f)}));
      return this;
    }
"""
        val getter = JavaGenUtils.builtInPrimitives
          .get(f.typeRef.resolved.name.name)
          .map(genPrimitiveGetter)
          .getOrElse(genNonPrimitiveGetter)
        
        val setter = JavaGenUtils.builtInPrimitives
          .get(f.typeRef.resolved.name.name)
          .map(genPrimitiveSetter)
          .getOrElse(genNonPrimitiveSetter)

        sn"""\

$getter\

$setter\

    /** Sets `${f.name}` field to specified ${vt(f.typeRef, s"default `$dtn` tag ", "")}error. */
    public @NotNull $ln.Builder set${up(f.name)}_Error(@NotNull ws.epigraph.errors.ErrorValue error) {
      _raw().setData($ln.${fcn(f)}, ${lqrn(f.typeRef, t)}.Type.instance().createDataBuilder().set${vt(f.typeRef, up(dtn), "")}_Error(error));
      return this;
    }

    /** Returns `${f.name}` field entry${vt(f.typeRef, s" for default `$dtn` tag", "")}. */
    @Override
    public @Nullable ${lqn(tt(f.typeRef, dtn), t)}.Value get${up(f.name)}_() {
      return (${lqn(tt(f.typeRef, dtn), t)}.Value) _raw().getValue($ln.${fcn(f)}, ${dttr(f.valueDataType, dtn, t)});
    }
"""
    }
}\
"""
  }.mkString
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

$builderValueAndDataBuilder\

  }

}
"""/*@formatter:on*/

}

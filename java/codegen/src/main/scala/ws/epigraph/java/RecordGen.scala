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

/* Created by yegor on 7/11/16. */

package ws.epigraph.java

import ws.epigraph.compiler._
import ws.epigraph.java.JavaGenNames.{fcn, pn, lqn, lqrn, lqdrn, tt, dttr, tcr, qnameArgs}
import ws.epigraph.java.NewlineStringInterpolator.{i, NewlineHelper}

class RecordGen(from: CRecordTypeDef, ctx: GenContext) extends JavaTypeDefGen[CRecordTypeDef](from, ctx)
  with DatumTypeJavaGen {

  protected def generate: String = {
    val ogc = new ObjectGenContext(ctx)
    ogc.addImport("org.jetbrains.annotations.NotNull")
    ogc.addImport("org.jetbrains.annotations.Nullable")
    ogc.addImport("ws.epigraph.annotations.Annotations")

    val annotations = new AnnotationsGen(from.annotations).generate(ogc)

    val fields = t.effectiveFields.map { f => /*@formatter:off*/sn"""\
  ${"/**"} Field `${f.name}`. */
  @NotNull Field ${fcn(f)} = new Field(${if (f.annotations.isEmpty) s""""${f.name}", ${lqrn(f.typeRef, t)}.Type.instance().dataType(${vt(f.typeRef, tcr(f.valueDataType, t), "")}), Annotations.EMPTY);""" else sn"""
    "${f.name}",
     ${lqrn(f.typeRef, t)}.Type.instance().dataType(${vt(f.typeRef, tcr(f.valueDataType, t), "")}),
     ${i(new AnnotationsGen(f.annotations).generate(ogc))}
  );"""}
"""/*@formatter:on*/
    }

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}\
package ${pn(t)};

import ws.epigraph.types.Field;

${ObjectGenUtils.genImports(ogc)}\

/**
 * Base (read) interface for `${t.name.name}` datum.
 */
@javax.annotation.Generated("${getClass.getName}")
public interface $ln extends${JavaGenUtils.withParents(t)} ws.epigraph.data.RecordDatum.Static {

  @NotNull $ln.Type type = $ln.Type.instance();

  static @NotNull $ln.Builder create() { return $ln.Type.instance().createBuilder(); }

  @Override
  @NotNull $ln.Imm toImmutable();

${fields.mkString}\
${t.effectiveFields.map { f =>
    val d = retro(f) // append '$' to getters/setters if retro tag is present
    val getterOverride = if (f.host == t && f.superfields.isEmpty) "" else sn"""\
  @Override
"""
    sn"""\
${  f.valueDataType.typeRef.resolved match { // data accessors (for union typed fields)
      case vartype: CEntityTypeDef => sn"""\

  /** Returns `${f.name}` field data. */
$getterOverride\
  @Nullable ${lqdrn(f.typeRef, t)} get${up(f.name)}$d();
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
$getterOverride\
  @Nullable $nativeType get${up(f.name)}();
"""
        def genNonPrimitiveGetter: String =
        sn"""\
  /** Returns `${f.name}` field datum${vt(f.typeRef, s" for default `$dtn` tag", "")}. */
$getterOverride\
  @Nullable ${lqn(tt(f.typeRef, dtn), t)} get${up(f.name)}();
"""
        val getter = JavaGenUtils.builtInPrimitives
          .get(f.typeRef.resolved.name.name)
          .map(genPrimitiveGetter)
          .getOrElse(genNonPrimitiveGetter)

        sn"""\

$getter\

  /** Returns `${f.name}` field entry${vt(f.typeRef, s" for default `$dtn` tag", "")}. */
$getterOverride\
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
      $ln.Value.Imm,
      $ln.Value.Builder,
      $ln.Data.Imm,
      $ln.Data.Builder
  > {

$typeInstance\

    private Type() {
      super(
          new ws.epigraph.names.QualifiedTypeName(${qnameArgs(t.name.fqn).mkString("\"", "\", \"", "\"")}),
          java.util.Arrays.asList(${parents(".Type.instance()")}),
          ${t.meta.map{mt => lqn(mt, t, _ + ".type")}.getOrElse("null")},
          $ln.Builder::new,
          $ln.Value.Imm.Impl::new,
          $ln.Data.Builder::new,
          ${i(annotations)}
      );
    }

    @Override
    public @NotNull java.util.List<${NotNull_}Field> immediateFields() {
      return java.util.Arrays.asList(\
${t.declaredFields.map { f => sn"""
          ${ln + '.' + fcn(f)}"""
  }.mkString(",")
}
      );
    }

  }

  /**
   * Builder for `${t.name.name}` datum.
   */
  final class Builder extends ws.epigraph.data.RecordDatum.Builder.Static<$ln.Imm, $ln.Value.Builder> implements $ln$setters {

    private Builder(@NotNull ws.epigraph.data.RecordDatum.Builder.Raw raw) { super($ln.Type.instance(), raw, $ln.Imm.Impl::new, $ln.Value.Builder::new); }
${t.effectiveFields.map { f => // for each effective field
    val d = retro(f) // append '$' to getters/setters if retro tag is present
    sn"""\
${  f.valueDataType.typeRef.resolved match { // data accessors (for union typed fields)
      case vartype: CEntityTypeDef => sn"""\

    /** Returns `${f.name}` field data. */
    @Override
    public @Nullable ${lqdrn(f.typeRef, t)} get${up(f.name)}$d() { return (${lqdrn(f.typeRef, t)}) _raw().getData($ln.${fcn(f)}); }

    /** Sets `${f.name}` field data. */
    public @NotNull $ln.Builder set${up(f.name)}$d(@Nullable ${lqrn(f.typeRef, t)} ${fcn(f)}) {
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
      ${lqn(tt(f.typeRef, dtn), t)}.Value value = get${up(f.name)}_();
      ${lqn(tt(f.typeRef, dtn), t)} datum = value == null ? null : value.getDatum();
      return datum == null ? null : datum.getVal();
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
    //@Override TODO where applicable
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
    //@Override TODO where applicable
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
    //@Override TODO where applicable
    public @NotNull $ln.Builder set${up(f.name)}_Error(@NotNull ws.epigraph.errors.ErrorValue error) {
      _raw().setData($ln.${fcn(f)}, ${lqrn(f.typeRef, t)}.Type.instance().createDataBuilder().set${vt(f.typeRef, up(dtn), "")}_Error(error));
      return this;
    }

    /** Returns `${f.name}` field entry${vt(f.typeRef, s" for default `$dtn` tag", "")}. */
    @Override
    public @Nullable ${lqn(tt(f.typeRef, dtn), t)}.Value get${up(f.name)}_() {
      return (${lqn(tt(f.typeRef, dtn), t)}.Value) _raw().getValue($ln.${fcn(f)}, ${dttr(f.valueDataType, dtn, t)});
    }

    /** Sets `${f.name}` field entry${vt(f.typeRef, s" for default `$dtn` tag", "")} or removes it if {@code value} is {@code null}. */
    //@Override TODO where applicable
    public @Nullable void set${up(f.name)}_(@Nullable ${lqn(tt(f.typeRef, dtn), t)}.Value value) {
      _raw().setData($ln.${fcn(f)}, value == null ? null : ${lqrn(f.typeRef, t)}.Type.instance().createDataBuilder().set${vt(f.typeRef, up(dtn), "")}_(value));
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

  }

  /**
   * Immutable interface for `${t.name.name}` datum.
   */
  interface Imm extends $ln,${withParents(".Imm")} ws.epigraph.data.RecordDatum.Imm.Static {
${t.effectiveFields.map { f =>
    val d = retro(f) // append '$' to getters/setters if retro tag is present
    sn"""\
${  f.valueDataType.typeRef.resolved match { // data accessors (for union typed fields)
      case vartype: CEntityTypeDef => sn"""\

    /** Returns immutable `${f.name}` field data. */
    @Override
    @Nullable ${lqdrn(f.typeRef, t)}.Imm get${up(f.name)}$d();
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
    @Nullable ${lqn(tt(f.typeRef, dtn), t)}.Value.Imm get${up(f.name)}_();
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
    final class Impl extends ws.epigraph.data.RecordDatum.Imm.Static.Impl<$ln.Imm, $ln.Value.Imm> implements $ln.Imm {

      private Impl(@NotNull ws.epigraph.data.RecordDatum.Imm.Raw raw) { super($ln.Type.instance(), raw, $ln.Value.Imm.Impl::new); }
${t.effectiveFields.map { f =>
    val d = retro(f) // append '$' to getters/setters if retro tag is present
    sn"""\
${  f.valueDataType.typeRef.resolved match { // data accessors (for union typed fields)
      case vartype: CEntityTypeDef => sn"""\

        /** Returns immutable `${f.name}` field data. */
        @Override
        public @Nullable ${lqdrn(f.typeRef, t)}.Imm get${up(f.name)}$d() {
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
          ${lqn(tt(f.typeRef, dtn), t)}.Value value = get${up(f.name)}_();
          ${lqn(tt(f.typeRef, dtn), t)} datum = value == null ? null : value.getDatum();
          return datum == null ? null : datum.getVal();
        }
"""
        def genNonPrimitiveGetter: String =
        sn"""\
        /** Returns immutable `${f.name}` field datum${vt(f.typeRef, s" for default `$dtn` tag", "")}. */
        @Override
        public @Nullable ${lqn(tt(f.typeRef, dtn), t)}.Imm get${up(f.name)}() {
          return ws.epigraph.util.Util.apply(get${up(f.name)}_(), ${lqn(tt(f.typeRef, dtn), t)}.Value.Imm::getDatum);
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
        public @Nullable ${lqn(tt(f.typeRef, dtn), t)}.Value.Imm get${up(f.name)}_() {
          return (${lqn(tt(f.typeRef, dtn), t)}.Value.Imm) _raw().getValue($ln.${fcn(f)}, ${dttr(f.valueDataType, dtn, t)});
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

  }
${t.effectiveFields.map { f => // for each effective field
    val d = retro(f) // append '$' to getters/setters if retro tag is present
    val setterOverride = if (invariantSuperfields(f).isEmpty) "" else sn"""\
    @Override
"""
    sn"""\

  interface Set${up(f.name)} ${superSetters(f)}{
${  f.valueDataType.typeRef.resolved match { // data accessors (for union typed fields)
      case vartype: CEntityTypeDef => sn"""\

    /** Sets `${f.name}` field data. */
$setterOverride\
    @NotNull Set${up(f.name)} set${up(f.name)}$d(@Nullable ${lqrn(f.typeRef, t)} ${fcn(f)});

"""
      case _: CDatumType => "" // no data accessors for datum fields
      case unexpected => throw new UnsupportedOperationException(unexpected.name.name)
    }
}\
${  f.effectiveDefaultTagName match { // default tag (implied or explicit, if any)
      case None => ""
      case Some(dtn) =>

        def genPrimitiveSetter(nativeType: String): String =
        sn"""\
    /** Sets `${f.name}` field to specified ${vt(f.typeRef, s"default `$dtn` tag ", "")}datum. */
$setterOverride\
    @NotNull Set${up(f.name)} set${up(f.name)}(@Nullable $nativeType ${fcn(f)});
"""
        def genNonPrimitiveSetter: String = sn"""\
    /** Sets `${f.name}` field to specified ${vt(f.typeRef, s"default `$dtn` tag ", "")}datum. */
$setterOverride\
    @NotNull Set${up(f.name)} set${up(f.name)}(@Nullable ${lqn(tt(f.typeRef, dtn), t)} ${fcn(f)});
"""
        val setter = JavaGenUtils.builtInPrimitives
          .get(f.typeRef.resolved.name.name)
          .map(genPrimitiveSetter)
          .getOrElse(genNonPrimitiveSetter)

        sn"""\

$setter\

    /** Sets `${f.name}` field to specified ${vt(f.typeRef, s"default `$dtn` tag ", "")}error. */
$setterOverride\
    @NotNull Set${up(f.name)} set${up(f.name)}_Error(@NotNull ws.epigraph.errors.ErrorValue error);

"""
    }
}\
  }
"""
  }.mkString
}\

$datumValue\

$datumData\

}
"""/*@formatter:on*/
  }

    def superSetters(f: CField): String = {
        val superfields = invariantSuperfields(f)
        if (superfields.isEmpty) "" else {
            "extends " + superfields.map(
                sf => sn"${lqn(sf.host, f.host)}.Set${up(sf.name)}"
            ).mkString(", ") + " "
        }
    }

    def invariantSuperfields(f: CField): Seq[CField] = if (f.host == t) f.superfields.filter(
        sf => sf.valueDataType == f.valueDataType
    ) else Seq(f)

    // append '$' to field getters/setters if their value type has retro tag
    def retro(f: CField): String = if (f.effectiveDefaultTagName.isDefined) "$" else ""

    def setters: String = if (t.effectiveFields.isEmpty) "" else t.effectiveFields.map(
        f => s"Set${up(f.name)}"
    ).mkString(",\n      ", ", ", "")

}

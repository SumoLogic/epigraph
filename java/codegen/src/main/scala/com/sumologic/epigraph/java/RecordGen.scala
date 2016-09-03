/* Created by yegor on 7/11/16. */

package com.sumologic.epigraph.java

import com.sumologic.epigraph.java.NewlineStringInterpolator.NewlineHelper
import com.sumologic.epigraph.schema.compiler._

class RecordGen(from: CRecordTypeDef, ctx: CContext) extends JavaTypeDefGen[CRecordTypeDef](from, ctx) {

  protected def generate: String = /*@formatter:off*/sn"""\
/*
 * Standard header
 */

package ${pn(t)};

import io.epigraph.types.RecordType.Field;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base (read) interface for `${t.name.name}` datum.
 */
public interface $ln extends${withParents(t)} io.epigraph.data.RecordDatum.Static {

  @NotNull $ln.Type type = new $ln.Type();
${t.effectiveFields.map { f => // for each effective field
    sn"""\

  ${"/**"}
   * Field `${f.name}`.
   */
  @NotNull Field ${jn(f.name)} = new Field("${f.name}", ${lqrn(f.typeRef, t)}.type, ${f.isAbstract});
${  f.effectiveDefaultTagName match { // default datum and value getters (if any)
      case None => ""
      case Some(dtn) => sn"""\

  /**
   * Returns default tag datum for `${f.name}` field.
   */
  @Nullable ${lqn(tt(f.typeRef, dtn), t)} get${up(f.name)}();

  /**
   * Returns default tag value for `${f.name}` field.
   */
  @Nullable ${lqn(tt(f.typeRef, dtn), t)}.Value get_${up(f.name)}();
"""
    }
}\
${ f.valueDataType.typeRef.resolved match { // tags datum and value getters
      case vartype: CVarTypeDef => vartype.effectiveTags.map { tag => sn"""\

  /**
   * Returns `${tag.name}` tag datum for `${f.name}` field.
   */
  @Nullable ${lqrn(tag.typeRef, t)} get${up(f.name)}${up(tag.name)}();

  /**
   * Returns `${tag.name}` tag value for `${f.name}` field.
   */
  @Nullable ${lqrn(tag.typeRef, t)}.Value get_${up(f.name)}${up(tag.name)}();
"""
        }.mkString
      case _: CDatumType => ""
      case unexpected => throw new UnsupportedOperationException(unexpected.name.name)
    }
  }\
"""
  }.mkString
}\

  /**
   * Class for `${t.name.name}` datum type.
   */
  class Type extends io.epigraph.types.RecordType.Static<
      $ln.Imm,
      $ln.Builder,
      $ln.Imm.Value,
      $ln.Builder.Value,
      $ln.Imm.Data,
      $ln.Builder.Data
  > {

    private Type() {
      super(
          new io.epigraph.names.QualifiedTypeName(${qnameArgs(t.name.fqn).mkString("\"", "\", \"", "\"")}),
          java.util.Arrays.asList(${t.linearizedParents.map(lqn(_, t, _ + ".type")).mkString(", ")}),
          ${t.isPolymorphic},
          $ln.Builder::new,
          $ln.Builder.Value::new,
          $ln.Builder.Data::new
      );
    }

    @Override
    public @NotNull java.util.List<@NotNull Field> immediateFields() {
      return java.util.Arrays.asList(\
${t.declaredFields.map { f => sn"""
          ${ln + '.' + jn(f.name)}"""
  }.mkString(",")
}
      );
    }
$listSupplier\

  }

  /**
   * Base interface for `${t.name.name}` value (holding a datum or an error).
   */
  interface Value extends${withParents(".Value")} io.epigraph.data.Val.Static {

    @Override
    @Nullable $ln getDatum();

    @Override
    @NotNull $ln.Imm.Value toImmutable();

  }

  /**
   * Base interface for `${t.name.name}` data (holding single default representation of the type).
   */
  interface Data extends${withParents(".Data")} io.epigraph.data.Data.Static {

    @Override
    @NotNull $ln.Imm.Data toImmutable();

    @Nullable $ln get();

    @Nullable $ln.Value get_();

  }

  /**
   * Immutable interface for `${t.name.name}` datum.
   */
  interface Imm extends $ln,${withParents(".Imm")} io.epigraph.data.RecordDatum.Imm.Static {
${t.effectiveFields.map { f => // for each effective field
    sn"""\
${  f.effectiveDefaultTagName match { // default getter (if any)
      case None => ""
      case Some(dtn) => sn"""\

  /**
   * Returns immutable default tag datum for `${f.name}` field.
   */
  @Override
  @Nullable ${lqn(tt(f.typeRef, dtn), t)}.Imm get${up(f.name)}();

  /**
   * Returns immutable default tag value for `${f.name}` field.
   */
  @Override
  @Nullable ${lqn(tt(f.typeRef, dtn), t)}.Imm.Value get_${up(f.name)}();
"""
    }
}\
${ f.valueDataType.typeRef.resolved match { // tag getters
      case vartype: CVarTypeDef => vartype.effectiveTags.map { tag => sn"""\

  /**
   * Returns immutable `${tag.name}` tag datum for `${f.name}` field.
   */
  @Override
  @Nullable ${lqrn(tag.typeRef, t)}.Imm get${up(f.name)}${up(tag.name)}();

  /**
   * Returns immutable `${tag.name}` tag value for `${f.name}` field.
   */
  @Override
  @Nullable ${lqrn(tag.typeRef, t)}.Imm.Value get_${up(f.name)}${up(tag.name)}();
"""
      }.mkString
      case _: CDatumType => ""
      case unexpected => throw new UnsupportedOperationException(unexpected.name.name)
    }
}\
"""
  }.mkString
}\

    /** Private implementation of `$ln.Imm` interface. */
    final class Impl extends io.epigraph.data.RecordDatum.Imm.Static.Impl<$ln.Imm> implements $ln.Imm {

      private Impl(@NotNull io.epigraph.data.RecordDatum.Imm.Raw raw) { super($ln.type, raw); }
${t.effectiveFields.map { f => // for each effective field
    sn"""\
${  f.effectiveDefaultTagName match { // default getter
      case None => ""
      case Some(dtn) => sn"""\

        /**
         * Returns immutable default tag datum for `${f.name}` field.
         */
        @Override
        public @Nullable ${lqn(tt(f.typeRef, dtn), t)}.Imm get${up(f.name)}() {
          return io.epigraph.util.Util.apply(get_${up(f.name)}(), ${lqn(tt(f.typeRef, dtn), t)}.Imm.Value::getDatum);
        }

        /**
         * Returns immutable default tag value for `${f.name}` field.
         */
        @Override
        public @Nullable ${lqn(tt(f.typeRef, dtn), t)}.Imm.Value get_${up(f.name)}() {
          return (${lqn(tt(f.typeRef, dtn), t)}.Imm.Value) _raw()._getValue($ln.${jn(f.name)}, ${trn(f.valueDataType, dtn, t)});
        }
"""
    }
}\
${ // tag getters
    f.valueDataType.typeRef.resolved match {
      case vartype: CVarTypeDef => vartype.effectiveTags.map { tag => sn"""\

        /**
         * Returns immutable `${tag.name}` tag datum for `${f.name}` field.
         */
        @Override
        public @Nullable ${lqrn(tag.typeRef, t)}.Imm get${up(f.name)}${up(tag.name)}() {
          return io.epigraph.util.Util.apply(get_${up(f.name)}${up(tag.name)}(), ${lqn(tt(f.typeRef, tag.name), t)}.Imm.Value::getDatum);
        }

        /**
         * Returns immutable `${tag.name}` tag value for `${f.name}` field.
         */
        @Override
        public @Nullable ${lqrn(tag.typeRef, t)}.Imm.Value get_${up(f.name)}${up(tag.name)}() {
          return (${lqn(tt(f.typeRef, tag.name), t)}.Imm.Value) _raw()._getValue($ln.${jn(f.name)}, ${trn(f.valueDataType, tag.name, t)});
        }
"""
      }.mkString
      case _: CDatumType => ""
      case unexpected => throw new UnsupportedOperationException(unexpected.name.name)
    }
}\
"""
  }.mkString
}\

    }

    /**
     * Immutable interface for `${t.name.name}` value (holding an immutable datum or an error).
     */
    interface Value extends $ln.Value,${withParents(".Imm.Value")} io.epigraph.data.Val.Imm.Static {

      @Override
      @Nullable $ln.Imm getDatum();

      /** Private implementation of `$ln.Imm.Value` interface. */
      final class Impl extends io.epigraph.data.Val.Imm.Static.Impl<$ln.Imm.Value, $ln.Imm>
          implements $ln.Imm.Value {

        public Impl(@NotNull io.epigraph.data.Val.Imm.Raw raw) { super($ln.type, raw); }

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

        protected Impl(@NotNull io.epigraph.data.Data.Imm.Raw raw) { super($ln.type, raw); }

        @Override
        public @Nullable $ln.Imm get() {
          return ($ln.Imm) _raw()._getDatum($ln.type.self);
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
  final class Builder extends io.epigraph.data.RecordDatum.Mut.Static<$ln.Imm> implements $ln {

    private Builder(@NotNull io.epigraph.data.RecordDatum.Mut.Raw raw) { super($ln.type, raw, $ln.Imm.Impl::new); }
${t.effectiveFields.map { f => // for each effective field
    sn"""\
${  f.effectiveDefaultTagName match { // default tag (if any)
      case None => ""
      case Some(dtn) => sn"""\

    /**
     * Returns default tag datum builder for${poly(f)}`${f.name}` field.
     */
    @Override
    public @Nullable ${lqn(tt(f.typeRef, dtn), t)}${poly(f, "", ".Builder")} get${up(f.name)}() {
      return io.epigraph.util.Util.apply(get_${up(f.name)}(), ${lqn(tt(f.typeRef, dtn), t)}${poly(f, "", ".Builder")}.Value::getDatum);
    }

    /**
     * Sets default tag datum builder for${poly(f)} `${f.name}` field.
     */
    public ${poly(f, s"<${ln(tt(f.typeRef, dtn))}Builder extends ${lqn(tt(f.typeRef, dtn), t)} & io.epigraph.data.Datum.Mut.Static>\n        ", "")}@NotNull $ln.Builder set${up(f.name)}(@Nullable ${poly(f, s"${ln(tt(f.typeRef, dtn))}Builder", s"${lqn(tt(f.typeRef, dtn), t)}.Builder")} ${jn(f.name)}) {
      _raw().getOrCreateFieldData($ln.${jn(f.name)})._raw()._setDatum(${trn(f.valueDataType, dtn, t)}, ${jn(f.name)});
      return this;
    }

    /**
     * Sets default tag error for `${f.name}` field.
     */
    public @NotNull $ln.Builder set${up(f.name)}(@NotNull io.epigraph.errors.ErrorValue error) {
      _raw().getOrCreateFieldData($ln.${jn(f.name)})._raw()._setError(${trn(f.valueDataType, dtn, t)}, error);
      return this;
    }

    /**
     * Returns default tag value builder for${poly(f)} `${f.name}` field.
     */
    @Override
    public @Nullable ${lqn(tt(f.typeRef, dtn), t)}${poly(f, "", ".Builder")}.Value get_${up(f.name)}() {
      return (${lqn(tt(f.typeRef, dtn), t)}${poly(f, "", ".Builder")}.Value) _raw()._getValue($ln.${jn(f.name)}, ${trn(f.valueDataType, dtn, t)});
    }
"""
    }
}\
${ f.valueDataType.typeRef.resolved match { // tag getters
      case vartype: CVarTypeDef => vartype.effectiveTags.map { tag => // for each effective tag
        sn"""\

    /**
     * Returns `${tag.name}` tag datum builder for${poly(f)} `${f.name}` field.
     */
    @Override
    public @Nullable ${lqrn(tag.typeRef, t)}${poly(f, "", ".Builder")} get${up(f.name)}${up(tag.name)}() {
      return io.epigraph.util.Util.apply(get_${up(f.name)}${up(tag.name)}(), ${lqn(tt(f.typeRef, tag.name), t)}${poly(f, "", ".Builder")}.Value::getDatum);
    }

    /**
     * Sets `${tag.name}` tag datum builder for${poly(f)} `${f.name}` field.
     */
    public ${poly(f, s"<${ln(tt(f.typeRef, tag.name))}Builder extends ${lqn(tt(f.typeRef, tag.name), t)} & io.epigraph.data.Datum.Mut.Static>\n        ", "")}@NotNull $ln.Builder set${up(f.name)}${up(tag.name)}(@Nullable ${poly(f, s"${ln(tt(f.typeRef, tag.name))}Builder", s"${lqn(tt(f.typeRef, tag.name), t)}.Builder")} ${jn(f.name)}${up(tag.name)}) {
      _raw().getOrCreateFieldData($ln.${jn(f.name)})._raw()._setDatum(${trn(f.valueDataType, tag.name, t)}, ${jn(f.name)}${up(tag.name)});
      return this;
    }

    /**
     * Sets `${tag.name}` tag error for `${f.name}` field.
     */
    public @NotNull $ln.Builder set${up(f.name)}${up(tag.name)}(@NotNull io.epigraph.errors.ErrorValue error) {
      _raw().getOrCreateFieldData($ln.${jn(f.name)})._raw()._setError(${trn(f.valueDataType, tag.name, t)}, error);
      return this;
    }

    /**
     * Returns `${tag.name}` tag value builder for${poly(f)} `${f.name}` field.
     */
    @Override
    public @Nullable ${lqrn(tag.typeRef, t)}${poly(f, "", ".Builder")}.Value get_${up(f.name)}${up(tag.name)}() {
      return (${lqn(tt(f.typeRef, tag.name), t)}${poly(f, "", ".Builder")}.Value) _raw()._getValue($ln.${jn(f.name)}, ${trn(f.valueDataType, tag.name, t)});
    }
"""
      }.mkString
      case _: CDatumType => ""
      case unexpected => throw new UnsupportedOperationException(unexpected.name.name)
    }
}\
"""
  }.mkString
}\

    /**
     * Builder for `${t.name.name}` value (holding a builder or an error).
     */
    final static class Value extends io.epigraph.data.Val.Mut.Static<$ln.Imm.Value, $ln.Builder>
        implements $ln.Value {

      public Value(@NotNull io.epigraph.data.Val.Mut.Raw raw) { super(raw, $ln.Imm.Value.Impl::new); }

    }

    /**
     * Builder for `${t.name.name}` data (holding single default representation of the type).
     */
    final static class Data extends io.epigraph.data.Data.Mut.Static<$ln.Imm.Data>
        implements $ln.Data {

      protected Data(@NotNull io.epigraph.data.Data.Mut.Raw raw) {
        super($ln.type, raw, $ln.Imm.Data.Impl::new);
      }

      @Override
      public @Nullable $ln.Builder get() {
        return ($ln.Builder) _raw()._getDatum($ln.type.self);
      }

      @Override
      public @Nullable $ln.Builder.Value get_() {
        return ($ln.Builder.Value) _raw()._getValue($ln.type.self);
      }

      public void set(@Nullable $ln.Builder datum) {
        _raw()._setDatum($ln.type.self, datum);
      }

      public void set_(@Nullable $ln.Builder.Value value) {
        _raw()._setValue($ln.type.self, value);
      }

    }

  }

}
"""/*@formatter:on*/

  private def poly(f: CField): String = poly(f, " polymorphic", "")

  private def poly(f: CField, yes: => String, no: => String): String = if (f.valueDataType.polymorphic) yes else no

}

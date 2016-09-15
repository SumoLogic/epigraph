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

  static @NotNull $ln.Builder create() { return $ln.type.createBuilder(); }
${t.effectiveFields.map { f => // for each effective field
    sn"""\

  ${"/**"}
   * Field `${f.name}`.
   */
  @NotNull Field ${jn(f.name)} = new Field("${f.name}", ${lqrn(f.typeRef, t)}.type.dataType(${f.valueDataType.polymorphic}${vt(f.typeRef, s", ${tcr(f.valueDataType, t)}", "")}), ${f.isAbstract});
${  f.effectiveDefaultTagName match { // default tag datum and value getters (if any)
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
${  f.effectiveDefaultTagName match {// default tag datum and value getters (if any)
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
"""
  }.mkString
}\

    /** Private implementation of `$ln.Imm` interface. */
    final class Impl extends io.epigraph.data.RecordDatum.Imm.Static.Impl<$ln.Imm> implements $ln.Imm {

      private Impl(@NotNull io.epigraph.data.RecordDatum.Imm.Raw raw) { super($ln.type, raw); }
${t.effectiveFields.map { f => // for each effective field
    sn"""\
${  f.effectiveDefaultTagName match { // default tag datum and value getters (if any)
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
          return (${lqn(tt(f.typeRef, dtn), t)}.Imm.Value) _raw().getValue($ln.${jn(f.name)}, ${dttr(f.valueDataType, dtn, t)});
        }
"""
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
          return ($ln.Imm) _raw().getDatum($ln.type.self);
        }

        @Override
        public @Nullable $ln.Imm.Value get_() {
          return ($ln.Imm.Value) _raw().getValue($ln.type.self);
        }

      }

    }

  }

  /**
   * Builder for `${t.name.name}` datum.
   */
  final class Builder extends io.epigraph.data.RecordDatum.Builder.Static<$ln.Imm> implements $ln {

    private Builder(@NotNull io.epigraph.data.RecordDatum.Builder.Raw raw) { super($ln.type, raw, $ln.Imm.Impl::new); }
${t.effectiveFields.map { f => // for each effective field
    sn"""\
${  f.effectiveDefaultTagName match { // default tag (implied or explicit, if any)
      case None => ""
      case Some(dtn) => sn"""\

    /**
     * Returns default tag datum builder for `${f.name}` field.
     */
    @Override
    public @Nullable ${lqn(tt(f.typeRef, dtn), t)}.Builder get${up(f.name)}() {
      return io.epigraph.util.Util.apply(get_${up(f.name)}(), ${lqn(tt(f.typeRef, dtn), t)}.Builder.Value::getDatum);
    }

    /**
     * Sets default tag datum builder for `${f.name}` field.
     */
    public @NotNull $ln.Builder set${up(f.name)}(@Nullable ${lqn(tt(f.typeRef, dtn), t)}.Builder ${jn(f.name)}) {
      _raw().getOrCreateFieldData($ln.${jn(f.name)})._raw().setDatum(${dttr(f.valueDataType, dtn, t)}, ${jn(f.name)});
      return this;
    }

    /**
     * Sets default tag error for `${f.name}` field.
     */
    public @NotNull $ln.Builder set${up(f.name)}(@NotNull io.epigraph.errors.ErrorValue error) {
      _raw().getOrCreateFieldData($ln.${jn(f.name)})._raw().setError(${dttr(f.valueDataType, dtn, t)}, error);
      return this;
    }

    /**
     * Returns default tag value builder for `${f.name}` field.
     */
    @Override
    public @Nullable ${lqn(tt(f.typeRef, dtn), t)}.Builder.Value get_${up(f.name)}() {
      return (${lqn(tt(f.typeRef, dtn), t)}.Builder.Value) _raw().getValue($ln.${jn(f.name)}, ${dttr(f.valueDataType, dtn, t)});
    }
"""
    }
}\
${  f.valueDataType.typeRef.resolved match { // data accessors (for union types)
      case vartype: CVarTypeDef => sn"""\

    /**
     * Returns `${f.typeRef.name.name}` data builder for `${f.name}` field.
     */
    //@Override
    public @Nullable ${lqdrn(f.typeRef, t)}.Builder get${up(f.name)}_Data() {
      io.epigraph.data.Data.@Nullable Builder data = _raw().getData($ln.${jn(f.name)});
      return data != null && data.type() == ${lqrn(f.typeRef, t)}.type ? (${lqdrn(f.typeRef, t)}.Builder) data : null;
    }

    /**
     * Sets `${f.typeRef.name.name}` data builder for `${f.name}` field.
     */
    public @NotNull $ln.Builder set${up(f.name)}_Data(@Nullable ${lqdrn(f.typeRef, t)}.Builder ${jn(f.name)}) {
      _raw().setData($ln.${jn(f.name)}, ${jn(f.name)});
      return this;
    }

"""
      case _: CDatumType => ""
      case unexpected => throw new UnsupportedOperationException(unexpected.name.name)
    }
}\
${  if (f.valueDataType.polymorphic) f.valueDataType.typeRef.resolved match { // polymorphic accessors
      case ftype: CVarTypeDef => sn"""\

    /**
     * Returns polymorphic data builder for `${f.name}` field.
     */
    //@Override
    public <B extends io.epigraph.data.Data.Builder.Static<? extends ${lqn(ftype, t)}.Imm> & ${lqn(ftype, t)}>
    @Nullable B get${up(f.name)}_Poly() {
      return (B) _raw().getData($ln.${jn(f.name)});
    }

//    /**
//     * Sets `${f.typeRef.name.name}` data builder for `${f.name}` field.
//     */
//    public @NotNull $ln.Builder set${up(f.name)}_Data(@Nullable ${lqdrn(f.typeRef, t)}.Builder ${jn(f.name)}) {
//      _raw().setData($ln.${jn(f.name)}, ${jn(f.name)});
//      return this;
//    }

"""
      case _: CDatumType => ""
      case unexpected => throw new UnsupportedOperationException(unexpected.name.name)
    } else ""
}\
"""
  }.mkString
}\

    /**
     * Builder for `${t.name.name}` value (holding a builder or an error).
     */
    final static class Value extends io.epigraph.data.Val.Builder.Static<$ln.Imm.Value, $ln.Builder>
        implements $ln.Value {

      public Value(@NotNull io.epigraph.data.Val.Builder.Raw raw) { super(raw, $ln.Imm.Value.Impl::new); }

    }

    /**
     * Builder for `${t.name.name}` data (holding single default representation of the type).
     */
    final static class Data extends io.epigraph.data.Data.Builder.Static<$ln.Imm.Data>
        implements $ln.Data {

      protected Data(@NotNull io.epigraph.data.Data.Builder.Raw raw) {
        super($ln.type, raw, $ln.Imm.Data.Impl::new);
      }

      @Override
      public @Nullable $ln.Builder get() {
        return ($ln.Builder) _raw().getDatum($ln.type.self);
      }

      @Override
      public @Nullable $ln.Builder.Value get_() {
        return ($ln.Builder.Value) _raw().getValue($ln.type.self);
      }

      public void set(@Nullable $ln.Builder datum) {
        _raw().setDatum($ln.type.self, datum);
      }

      public void set_(@Nullable $ln.Builder.Value value) {
        _raw().setValue($ln.type.self, value);
      }

    }

  }

}
"""/*@formatter:on*/

  private def poly(f: CField): String = poly(f, " polymorphic", "")

  private def poly(f: CField, yes: => String, no: => String): String = if (f.valueDataType.polymorphic) yes else no

}

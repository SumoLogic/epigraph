/* Created by yegor on 7/11/16. */

package com.sumologic.epigraph.java

import com.sumologic.epigraph.java.NewlineStringInterpolator.NewlineHelper
import com.sumologic.epigraph.schema.compiler._

class RecordGen(from: CRecordTypeDef, ctx: CContext) extends JavaTypeDefGen[CRecordTypeDef](from, ctx)
    with DatumTypeJavaGen {

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

  @NotNull $ln.Type type = $ln.Type.instance();

  static @NotNull $ln.Builder create() { return $ln.Type.instance().createBuilder(); }
${t.effectiveFields.map { f => sn"""\

  ${"/**"} Field `${f.name}`. */
  @NotNull Field ${jn(f.name)} = new Field("${f.name}", ${lqrn(f.typeRef, t)}.Type.instance().dataType(${f.valueDataType.polymorphic}${vt(f.typeRef, s", ${tcr(f.valueDataType, t)}", "")}));
"""
  }.mkString
}\
${t.effectiveFields.map { f => sn"""\
${  f.valueDataType.typeRef.resolved match { // data accessors (for union typed fields)
      case vartype: CVarTypeDef => sn"""\

  /** Returns `${f.name}` field data. */
  //@Override TODO where applicable
  @Nullable ${lqdrn(f.typeRef, t)} get${up(f.name)}_();
"""
      case _: CDatumType => "" // no data accessors for datum fields
      case unexpected => throw new UnsupportedOperationException(unexpected.name.name)
    }
}\
${  f.effectiveDefaultTagName match { // default tag accessors (implied or explicit, if any)
      case None => ""
      case Some(dtn) => sn"""\

  /** Returns `${f.name}` field datum${vt(f.typeRef, s" for default `$dtn` tag", "")}. */
  //@Override TODO where applicable
  @Nullable ${lqn(tt(f.typeRef, dtn), t)} get${up(f.name)}();

  /** Returns `${f.name}` field entry${vt(f.typeRef, s" for default `$dtn` tag", "")}. */
  //@Override TODO where applicable
  @Nullable ${lqn(tt(f.typeRef, dtn), t)}.Value get${up(f.name)}$$();
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

    private static final class Holder { public static $ln.Type instance = new $ln.Type(); }

    public static $ln.Type instance() { return Holder.instance; }

    private Type() {
      super(
          new io.epigraph.names.QualifiedTypeName(${qnameArgs(t.name.fqn).mkString("\"", "\", \"", "\"")}),
          java.util.Arrays.asList(${t.linearizedParents.map(lqn(_, t, _ + ".Type.instance()")).mkString(", ")}),
          $ln.Builder::new,
          $ln.Imm.Value.Impl::new,
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

    @Nullable $ln.Value get$$();

  }

  /**
   * Immutable interface for `${t.name.name}` datum.
   */
  interface Imm extends $ln,${withParents(".Imm")} io.epigraph.data.RecordDatum.Imm.Static {
${t.effectiveFields.map { f => sn"""\
${  f.valueDataType.typeRef.resolved match { // data accessors (for union typed fields)
      case vartype: CVarTypeDef => sn"""\

    /** Returns immutable `${f.name}` field data. */
    @Override
    @Nullable ${lqdrn(f.typeRef, t)}.Imm get${up(f.name)}_();
"""
      case _: CDatumType => "" // no data accessors for datum fields
      case unexpected => throw new UnsupportedOperationException(unexpected.name.name)
    }
}\
${  f.effectiveDefaultTagName match { // default tag accessors (implied or explicit, if any)
      case None => ""
      case Some(dtn) => sn"""\

    /** Returns immutable `${f.name}` field datum${vt(f.typeRef, s" for default `$dtn` tag", "")}. */
    @Override
    @Nullable ${lqn(tt(f.typeRef, dtn), t)}.Imm get${up(f.name)}();

    /** Returns immutable `${f.name}` field entry${vt(f.typeRef, s" for default `$dtn` tag", "")}. */
    @Override
    @Nullable ${lqn(tt(f.typeRef, dtn), t)}.Imm.Value get${up(f.name)}$$();
"""
    }
}\
"""
  }.mkString
}\

    /** Private implementation of `$ln.Imm` interface. */
    final class Impl extends io.epigraph.data.RecordDatum.Imm.Static.Impl<$ln.Imm, $ln.Imm.Value> implements $ln.Imm {

      private Impl(@NotNull io.epigraph.data.RecordDatum.Imm.Raw raw) { super($ln.Type.instance(), raw, $ln.Imm.Value.Impl::new); }
${t.effectiveFields.map { f => sn"""\
${  f.valueDataType.typeRef.resolved match { // data accessors (for union typed fields)
      case vartype: CVarTypeDef => sn"""\

        /** Returns immutable `${f.name}` field data. */
        @Override
        public @Nullable ${lqdrn(f.typeRef, t)}.Imm get${up(f.name)}_() {
          return (${lqdrn(f.typeRef, t)}.Imm) _raw().getData($ln.${jn(f.name)});
        }
"""
      case _: CDatumType => "" // no data accessors for datum fields
      case unexpected => throw new UnsupportedOperationException(unexpected.name.name)
    }
}\
${  f.effectiveDefaultTagName match { // default tag accessors (implied or explicit, if any)
      case None => ""
      case Some(dtn) => sn"""\

        /** Returns immutable `${f.name}` field datum${vt(f.typeRef, s" for default `$dtn` tag", "")}. */
        @Override
        public @Nullable ${lqn(tt(f.typeRef, dtn), t)}.Imm get${up(f.name)}() {
          return io.epigraph.util.Util.apply(get${up(f.name)}$$(), ${lqn(tt(f.typeRef, dtn), t)}.Imm.Value::getDatum);
        }

        /** Returns immutable `${f.name}` field entry${vt(f.typeRef, s" for default `$dtn` tag", "")}. */
        @Override
        public @Nullable ${lqn(tt(f.typeRef, dtn), t)}.Imm.Value get${up(f.name)}$$() {
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

        public Impl(@NotNull io.epigraph.data.Val.Imm.Raw raw) { super(raw); }

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

        protected Impl(@NotNull io.epigraph.data.Data.Imm.Raw raw) { super($ln.Type.instance(), raw); }

        @Override
        public @Nullable $ln.Imm get() {
          return ($ln.Imm) _raw().getDatum($ln.Type.instance().self);
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
  final class Builder extends io.epigraph.data.RecordDatum.Builder.Static<$ln.Imm, $ln.Builder.Value> implements $ln {

    private Builder(@NotNull io.epigraph.data.RecordDatum.Builder.Raw raw) { super($ln.Type.instance(), raw, $ln.Imm.Impl::new, $ln.Builder.Value::new); }
${t.effectiveFields.map { f => // for each effective field
    sn"""\
${  f.valueDataType.typeRef.resolved match { // data accessors (for union typed fields)
      case vartype: CVarTypeDef => sn"""\

    /** Returns `${f.name}` field data. */
    @Override
    public @Nullable ${lqdrn(f.typeRef, t)} get${up(f.name)}_() { return (${lqdrn(f.typeRef, t)}) _raw().getData($ln.${jn(f.name)}); }

    /** Sets `${f.name}` field data. */
    public @NotNull $ln.Builder set${up(f.name)}_(@Nullable ${lqrn(f.typeRef, t)} ${jn(f.name)}) {
      _raw().setData($ln.${jn(f.name)}, ${jn(f.name)}); return this;
    }

"""
      case _: CDatumType => "" // no data accessors for datum fields
      case unexpected => throw new UnsupportedOperationException(unexpected.name.name)
    }
}\
${  f.effectiveDefaultTagName match { // default tag (implied or explicit, if any)
      case None => ""
      case Some(dtn) => sn"""\

    /** Returns `${f.name}` field datum${vt(f.typeRef, s" for default `$dtn` tag", "")}. */
    @Override
    public @Nullable ${lqn(tt(f.typeRef, dtn), t)} get${up(f.name)}() {
      return io.epigraph.util.Util.apply(get${up(f.name)}$$(), ${lqn(tt(f.typeRef, dtn), t)}.Value::getDatum);
    }

    /** Sets `${f.name}` field to specified ${vt(f.typeRef, s"default `$dtn` tag ", "")}datum. */
    public @NotNull $ln.Builder set${up(f.name)}(@Nullable ${lqn(tt(f.typeRef, dtn), t)} ${jn(f.name)}) {
      _raw().setData($ln.${jn(f.name)}, ${lqrn(f.typeRef, t)}.Type.instance().createDataBuilder().set${vt(f.typeRef, up(dtn), "")}(${jn(f.name)}));
      return this;
    }

    /** Sets `${f.name}` field to specified ${vt(f.typeRef, s"default `$dtn` tag ", "")}error. */
    public @NotNull $ln.Builder set${up(f.name)}$$Error(@NotNull io.epigraph.errors.ErrorValue error) {
      _raw().setData($ln.${jn(f.name)}, ${lqrn(f.typeRef, t)}.Type.instance().createDataBuilder().set${vt(f.typeRef, up(dtn), "")}$$Error(error));
      return this;
    }

    /** Returns `${f.name}` field entry${vt(f.typeRef, s" for default `$dtn` tag", "")}. */
    @Override
    public @Nullable ${lqn(tt(f.typeRef, dtn), t)}.Value get${up(f.name)}$$() {
      return (${lqn(tt(f.typeRef, dtn), t)}.Builder.Value) _raw().getValue($ln.${jn(f.name)}, ${dttr(f.valueDataType, dtn, t)});
    }
"""
    }
}\
"""
  }.mkString
}\

$builderValueAndDataBuilder\

  }

}
"""/*@formatter:on*/

  private def poly(f: CField): String = poly(f, " polymorphic", "")

  private def poly(f: CField, yes: => String, no: => String): String = if (f.valueDataType.polymorphic) yes else no

}

/* Created by yegor on 7/11/16. */

package com.sumologic.epigraph.java

import com.sumologic.epigraph.java.NewlineStringInterpolator.NewlineHelper
import com.sumologic.epigraph.schema.compiler.{CContext, CPrimitiveTypeDef, CTypeKind}

class PrimitiveGen(from: CPrimitiveTypeDef, ctx: CContext) extends JavaTypeDefGen[CPrimitiveTypeDef](from, ctx)
    with DatumTypeJavaGen {

  protected override def generate: String = /*@formatter:off*/sn"""\
/*
 * Standard header
 */

package ${pn(t)};

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base interface for `${t.name.name}` datum.
 */
public interface $ln extends${withParents(t)} io.epigraph.data.${kind(t)}Datum.Static {

  @NotNull $ln.Type type = new $ln.Type();

  static @NotNull $ln.Builder create(@NotNull ${native(t)} val) { return $ln.type.createBuilder(val); }

  /**
   * Class for `${t.name.name}` datum type.
   */
  final class Type extends io.epigraph.types.${kind(t)}Type.Static<
      $ln.Imm,
      $ln.Builder,
      $ln.Imm.Value,
      $ln.Builder.Value,
      $ln.Imm.Data,
      $ln.Builder.Data
  > {

    Type() {
      super(
          new io.epigraph.names.QualifiedTypeName(${qnameArgs(t.name.fqn).mkString("\"", "\", \"", "\"")}),
          java.util.Arrays.asList(${t.linearizedParents.map(javaQName(_, t) + ".type").mkString(", ")}),
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

    /** Returns default tag datum. */
    @Nullable $ln get();

    /** Returns default tag value. */
    @Nullable $ln.Value get$$();

  }

  /**
   * Immutable interface for `${t.name.name}` datum.
   */
  interface Imm extends $ln,${withParents(".Imm")} io.epigraph.data.${kind(t)}Datum.Imm.Static {

    /** Private implementation of `$ln.Imm` interface. */
    final class Impl extends io.epigraph.data.${kind(t)}Datum.Imm.Static.Impl<$ln.Imm, $ln.Imm.Value> implements $ln.Imm {

      Impl(@NotNull io.epigraph.data.${kind(t)}Datum.Imm.Raw raw) { super($ln.type, raw, $ln.Imm.Value.Impl::new); }

    }

    /**
     * Immutable interface for `${t.name.name}` value (holding an immutable datum or an error).
     */
    interface Value extends $ln.Value,${withParents(".Imm.Value")} io.epigraph.data.Val.Imm.Static {

      /** Returns immutable default tag datum. */
      @Override
      @Nullable $ln.Imm getDatum();

      /** Private implementation of `$ln.Imm.Value` interface. */
      final class Impl extends io.epigraph.data.Val.Imm.Static.Impl<$ln.Imm.Value, $ln.Imm>
          implements $ln.Imm.Value {

        Impl(@NotNull io.epigraph.data.Val.Imm.Raw raw) { super(raw); }

      }

    }

    /**
     * Immutable interface for `${t.name.name}` data (holding single default representation of the type).
     */
    interface Data extends $ln.Data,${withParents(".Imm.Data")} io.epigraph.data.Data.Imm.Static {

      /** Returns immutable default tag datum. */
      @Override
      @Nullable $ln.Imm get();

      /** Returns immutable default tag value. */
      @Override
      @Nullable $ln.Imm.Value get$$();

      /** Private implementation of `$ln.Imm.Data` interface. */
      final class Impl extends io.epigraph.data.Data.Imm.Static.Impl<$ln.Imm.Data> implements $ln.Imm.Data {

        Impl(@NotNull io.epigraph.data.Data.Imm.Raw raw) { super($ln.type, raw); }

        @Override
        public @Nullable $ln.Imm get() {
          $ln.Imm.Value value = get$$();
          return value == null ? null : value.getDatum();
        }

        @Override
        public @Nullable $ln.Imm.Value get$$() { return ($ln.Imm.Value) _raw().getValue($ln.type.self); }

      }

    }

  }

  /**
   * Builder for `${t.name.name}` datum.
   */
  final class Builder extends io.epigraph.data.${kind(t)}Datum.Builder.Static<$ln.Imm, $ln.Builder.Value> implements $ln {

    Builder(@NotNull io.epigraph.data.${kind(t)}Datum.Builder.Raw raw) { super($ln.type, raw, $ln.Imm.Impl::new, $ln.Builder.Value::new); }

$builderValueAndDataBuilder\

  }

}
"""/*@formatter:on*/

  private def kind(t: CPrimitiveTypeDef): String =
    PrimitiveGen.Kinds.getOrElse(t.kind, throw new UnsupportedOperationException(t.kind.name))

  private def native(t: CPrimitiveTypeDef): String =
    PrimitiveGen.Natives.getOrElse(t.kind, throw new UnsupportedOperationException(t.kind.name))

}

object PrimitiveGen {

  private val Kinds: Map[CTypeKind, String] = Map(
    CTypeKind.STRING -> "String",
    CTypeKind.INTEGER -> "Integer",
    CTypeKind.LONG -> "Long",
    CTypeKind.DOUBLE -> "Double",
    CTypeKind.BOOLEAN -> "Boolean"
  )

  private val Natives: Map[CTypeKind, String] = Map(
    CTypeKind.STRING -> "java.lang.String",
    CTypeKind.INTEGER -> "java.lang.Integer",
    CTypeKind.LONG -> "java.lang.Long",
    CTypeKind.DOUBLE -> "java.lang.Double",
    CTypeKind.BOOLEAN -> "java.lang.Boolean"
  )

}

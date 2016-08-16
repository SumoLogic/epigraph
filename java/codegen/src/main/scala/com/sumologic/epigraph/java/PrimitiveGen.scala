/* Created by yegor on 7/11/16. */

package com.sumologic.epigraph.java

import com.sumologic.epigraph.java.NewlineStringInterpolator.NewlineHelper
import com.sumologic.epigraph.schema.compiler.{CContext, CPrimitiveTypeDef, CTypeKind}

class PrimitiveGen(from: CPrimitiveTypeDef, ctx: CContext) extends JavaTypeDefGen[CPrimitiveTypeDef](from, ctx) {

  protected override def generate: String = sn"""\
/*
 * Standard header
 */

package ${javaFqn(t.name.fqn.removeLastSegment())};

import io.epigraph.data.${Kind(t)}Datum;
import io.epigraph.data.ListDatum;
import io.epigraph.data.Val;
import io.epigraph.names.AnonListTypeName;
import io.epigraph.names.QualifiedTypeName;
import io.epigraph.types.AnonListType;
import io.epigraph.types.${Kind(t)}Type;
import io.epigraph.types.ListType;
import io.epigraph.util.ListView;
import io.epigraph.util.Unmodifiable;
import io.epigraph.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Supplier;

/**
 * Base interface for `${t.name.name}` datum.
 */
public interface ${baseName(t)} extends${withParents(t, baseName)} ${withCollections(t)}${Kind(t)}Datum.Static {

  @NotNull ${baseName(t)}.Type type = new ${baseName(t)}.Type();

  /**
   * Class for `${t.name.name}` datum type.
   */
  final class Type extends ${Kind(t)}Type.Static<
      ${baseName(t)}.Imm,
      ${baseName(t)}.Builder,
      ${baseName(t)}.Imm.Value,
      ${baseName(t)}.Builder.Value,
      ${baseName(t)}.Imm.Data,
      ${baseName(t)}.Builder.Data
  > {

    Type() {
      super(
          new QualifiedTypeName(${qnameArgs(t.name.fqn).map(javaName).mkString("\"", "\", \"", "\"")}),
          Arrays.asList(${t.linearizedParents.map(javaQName(_, t) + ".type").mkString(", ")}),
          ${t.isPolymorphic},
          ${baseName(t)}.Builder::new,
          ${baseName(t)}.Builder.Value::new,
          ${baseName(t)}.Builder.Data::new
      );
    }
${if (ctx.hasAnonListOf(t)) { sn"""\

    @Override
    protected @NotNull Supplier<ListType> listTypeSupplier() { return () -> ${baseName(t)}.List.type; }
""" } else ""
}\

  }

  /**
   * Base interface for `${t.name.name}` value (holding a datum or an error).
   */
  interface Value extends Val.Static {

    @Override
    @Nullable ${baseName(t)} getDatum();

    @Override
    @NotNull ${baseName(t)}.Imm.Value toImmutable();

  }

  /**
   * Base interface for `${t.name.name}` data (holding single default representation of the type).
   */
  interface Data extends io.epigraph.data.Data.Static {

    @Override
    @NotNull ${baseName(t)}.Imm.Data toImmutable();

    @Nullable ${baseName(t)}.Value get_value(); // default tag value

    @Nullable ${baseName(t)} get(); // default tag datum

  }

  /**
   * Immutable interface for `${t.name.name}` datum.
   */
  interface Imm extends ${baseName(t)}, ${Kind(t)}Datum.Imm.Static {

    /** Private implementation of `${baseName(t)}.Imm` interface. */
    final class Impl extends ${Kind(t)}Datum.Imm.Static.Impl implements ${baseName(t)}.Imm {

      Impl(@NotNull ${Kind(t)}Datum.Imm.Raw raw) { super(${baseName(t)}.type, raw); }

    }

    /**
     * Immutable interface for `${t.name.name}` value (holding an immutable datum or an error).
     */
    interface Value extends ${baseName(t)}.Value, Val.Imm.Static {

      @Override
      @Nullable ${baseName(t)}.Imm getDatum();

      /** Private implementation of `${baseName(t)}.Imm.Value` interface. */
      final class Impl extends Val.Imm.Static.Impl<${baseName(t)}.Imm.Value, ${baseName(t)}.Imm> implements ${baseName(t)}.Imm.Value {

        Impl(@NotNull Val.Imm.Raw raw) { super(${baseName(t)}.type, raw); }

      }

    }

    /**
     * Immutable interface for `${t.name.name}` data (holding single default representation of the type).
     */
    interface Data extends ${baseName(t)}.Data, io.epigraph.data.Data.Imm.Static {

      @Override
      @Nullable ${baseName(t)}.Imm.Value get_value(); // implied default self-tag value

      @Override
      @Nullable ${baseName(t)}.Imm get(); // implied default self-tag datum

      /** Private implementation of `${baseName(t)}.Imm.Data` interface. */
      final class Impl extends io.epigraph.data.Data.Imm.Static.Impl<${baseName(t)}.Imm.Data> implements ${baseName(t)}.Imm.Data {

        Impl(@NotNull io.epigraph.data.Data.Imm.Raw raw) { super(${baseName(t)}.type, raw); }

        @Override
        public @Nullable ${baseName(t)}.Imm.Value get_value() {
          return (${baseName(t)}.Imm.Value) _raw()._getValue(${baseName(t)}.type.self);
        }

        @Override
        public @Nullable ${baseName(t)}.Imm get() {
          ${baseName(t)}.Imm.Value value = get_value();
          return value == null ? null : value.getDatum();
        }

      }

    }

  }

  /**
   * Builder for `${t.name.name}` datum.
   */
  final class Builder extends ${Kind(t)}Datum.Mut.Static<${baseName(t)}.Imm> implements ${baseName(t)} {

    Builder(@NotNull ${Kind(t)}Datum.Mut.Raw raw) { super(${baseName(t)}.type, raw, ${baseName(t)}.Imm.Impl::new); }

    /**
     * Builder for `${t.name.name}` value (holding a builder or an error).
     */
    public static final class Value extends Val.Mut.Static<${baseName(t)}.Imm.Value, ${baseName(t)}.Builder> implements ${baseName(t)}.Value {

      Value(@NotNull Val.Mut.Raw raw) { super(raw, ${baseName(t)}.Imm.Value.Impl::new); }

    }

    /**
     * Builder for `${t.name.name}` data (holding single default representation of the type).
     */
    public static final class Data extends io.epigraph.data.Data.Mut.Static<${baseName(t)}.Imm.Data> implements ${baseName(t)}.Data {

      Data(@NotNull io.epigraph.data.Data.Mut.Raw raw) {
        super(${baseName(t)}.type, raw, ${baseName(t)}.Imm.Data.Impl::new);
      }

      @Override
      public @Nullable ${baseName(t)}.Builder.Value get_value() {
        return (${baseName(t)}.Builder.Value) _raw()._getValue(${baseName(t)}.type.self);
      }

      @Override
      public @Nullable ${baseName(t)}.Builder get() {
        return Util.apply(${baseName(t)}.Builder.Value::getDatum, get_value());
      }

      // implied default tag value
      public void set_value(@Nullable ${baseName(t)}.Builder.Value value) { _raw()._setValue(${baseName(t)}.type.self, value); }

      // implied default tag datum
      public void set(@Nullable ${baseName(t)}.Builder datum) {
        _raw()._getOrCreateTagValue(${baseName(t)}.type.self)._raw().setDatum(datum);
      }

    }

  }

}
${generateCollections(t)}\
"""

  private def Kind(t: CPrimitiveTypeDef): String =
    PrimitiveGen.Kinds.getOrElse(t.kind, throw new UnsupportedOperationException(t.kind.name))

}

object PrimitiveGen {

  private val Kinds: Map[CTypeKind, String] = Map(
    CTypeKind.STRING -> "String",
    CTypeKind.INTEGER -> "Integer",
    CTypeKind.LONG -> "Long",
    CTypeKind.DOUBLE -> "Double",
    CTypeKind.BOOLEAN -> "Boolean"
  )

}

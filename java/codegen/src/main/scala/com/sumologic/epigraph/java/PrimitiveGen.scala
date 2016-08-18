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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base interface for `${t.name.name}` datum.
 */
public interface ${baseName(t)} extends${withParents(t)} io.epigraph.data.${Kind(t)}Datum.Static {

  @NotNull ${baseName(t)}.Type type = new ${baseName(t)}.Type();

  /**
   * Class for `${t.name.name}` datum type.
   */
  final class Type extends io.epigraph.types.${Kind(t)}Type.Static<
      ${baseName(t)}.Imm,
      ${baseName(t)}.Builder,
      ${baseName(t)}.Imm.Value,
      ${baseName(t)}.Builder.Value,
      ${baseName(t)}.Imm.Data,
      ${baseName(t)}.Builder.Data
  > {

    Type() {
      super(
          new io.epigraph.names.QualifiedTypeName(${qnameArgs(t.name.fqn).map(javaName).mkString("\"", "\", \"", "\"")}),
          java.util.Arrays.asList(${t.linearizedParents.map(javaQName(_, t) + ".type").mkString(", ")}),
          ${t.isPolymorphic},
          ${baseName(t)}.Builder::new,
          ${baseName(t)}.Builder.Value::new,
          ${baseName(t)}.Builder.Data::new
      );
    }
${ctx.getAnonListOf(t).map { lt => sn"""\

    @Override
    protected @NotNull java.util.function.Supplier<io.epigraph.types.ListType> listTypeSupplier() {
      return () -> ${lqn(lt, t)}.type;
    }
""" }.getOrElse("")
}\

  }

  /**
   * Base interface for `${t.name.name}` value (holding a datum or an error).
   */
  interface Value extends${withParents(t, _ + ".Value")} io.epigraph.data.Val.Static {

    @Override
    @Nullable ${baseName(t)} getDatum();

    @Override
    @NotNull ${baseName(t)}.Imm.Value toImmutable();

  }

  /**
   * Base interface for `${t.name.name}` data (holding single default representation of the type).
   */
  interface Data extends${withParents(t, _ + ".Data")} io.epigraph.data.Data.Static {

    @Override
    @NotNull ${baseName(t)}.Imm.Data toImmutable();

    @Nullable ${baseName(t)}.Value get_value(); // default tag value

    @Nullable ${baseName(t)} get(); // default tag datum

  }

  /**
   * Immutable interface for `${t.name.name}` datum.
   */
  interface Imm extends ${baseName(t)},${withParents(t, _ + ".Imm")} io.epigraph.data.${Kind(t)}Datum.Imm.Static {

    /** Private implementation of `${baseName(t)}.Imm` interface. */
    final class Impl extends io.epigraph.data.${Kind(t)}Datum.Imm.Static.Impl implements ${baseName(t)}.Imm {

      Impl(@NotNull io.epigraph.data.${Kind(t)}Datum.Imm.Raw raw) { super(${baseName(t)}.type, raw); }

    }

    /**
     * Immutable interface for `${t.name.name}` value (holding an immutable datum or an error).
     */
    interface Value extends ${baseName(t)}.Value,${withParents(t, _ + ".Imm.Value")} io.epigraph.data.Val.Imm.Static {

      @Override
      @Nullable ${baseName(t)}.Imm getDatum();

      /** Private implementation of `${baseName(t)}.Imm.Value` interface. */
      final class Impl extends io.epigraph.data.Val.Imm.Static.Impl<${baseName(t)}.Imm.Value, ${baseName(t)}.Imm>
          implements ${baseName(t)}.Imm.Value {

        Impl(@NotNull io.epigraph.data.Val.Imm.Raw raw) { super(${baseName(t)}.type, raw); }

      }

    }

    /**
     * Immutable interface for `${t.name.name}` data (holding single default representation of the type).
     */
    interface Data extends ${baseName(t)}.Data,${withParents(t, _ + ".Imm.Data")} io.epigraph.data.Data.Imm.Static {

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
  final class Builder extends io.epigraph.data.${Kind(t)}Datum.Mut.Static<${baseName(t)}.Imm> implements ${baseName(t)} {

    Builder(@NotNull io.epigraph.data.${Kind(t)}Datum.Mut.Raw raw) { super(${baseName(t)}.type, raw, ${baseName(t)}.Imm.Impl::new); }

    /**
     * Builder for `${t.name.name}` value (holding a builder or an error).
     */
    public static final class Value extends io.epigraph.data.Val.Mut.Static<${baseName(t)}.Imm.Value, ${baseName(t)}.Builder> implements ${baseName(t)}.Value {

      Value(@NotNull io.epigraph.data.Val.Mut.Raw raw) { super(raw, ${baseName(t)}.Imm.Value.Impl::new); }

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
        return io.epigraph.util.Util.apply(${baseName(t)}.Builder.Value::getDatum, get_value());
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

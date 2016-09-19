/* Created by yegor on 7/13/16. */

package com.sumologic.epigraph.java

import com.sumologic.epigraph.java.NewlineStringInterpolator.NewlineHelper
import com.sumologic.epigraph.schema.compiler._

class VarTypeGen(from: CVarTypeDef, ctx: CContext) extends JavaTypeDefGen[CVarTypeDef](from, ctx) {

  protected def generate: String = /*@formatter:off*/sn"""\
/*
 * Standard header
 */

package ${pn(t)};

import io.epigraph.types.Type.Tag;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base (read) interface for `${t.name.name}` data.
 */
public interface $ln extends${withParents(t)} io.epigraph.data.Data.Static {

  @NotNull $ln.Type type = new $ln.Type();

  /** Returns new builder for `${t.name.name}` data. */
  static @NotNull $ln.Builder create() { return $ln.type.createDataBuilder(); }
${t.effectiveTags.map { tag => sn"""\

  ${"/**"} Tag `${tag.name}`. */
  @NotNull Tag ${jn(tag.name)} = new Tag("${tag.name}", ${lqrn(tag.typeRef, t)}.type);

  ${"/**"} Returns `${tag.name}` tag datum. */
  @Nullable ${lqrn(tag.typeRef, t)} get${up(tag.name)}();

  ${"/**"} Returns `${tag.name}` tag value. */
  @Nullable ${lqrn(tag.typeRef, t)}.Value get${up(tag.name)}$$();
"""
  }.mkString
}\

  ${"/**"}
   * Class for `${t.name.name}` type.
   */
  class Type extends io.epigraph.types.UnionType.Static<$ln.Imm, $ln.Builder> {

    private Type() {
      super(
          new io.epigraph.names.QualifiedTypeName(${qnameArgs(t.name.fqn).mkString("\"", "\", \"", "\"")}),
          java.util.Arrays.asList(${t.linearizedParents.map(lqn(_, t, _ + ".type")).mkString(", ")}),
          $ln.Builder::new
      );
    }

    @Override
    public @NotNull java.util.List<@NotNull Tag> immediateTags() {
      return java.util.Arrays.asList(\
${t.declaredTags.map { tag => sn"""
          ${ln + '.' + jn(tag.name)}"""
  }.mkString(",")
}
      );
    }

  }

  /**
   * Immutable interface for `${t.name.name}` data.
   */
  interface Imm extends $ln,${withParents(".Imm")} io.epigraph.data.Data.Imm.Static {
${t.effectiveTags.map { tag => sn"""\

    ${"/**"} Returns immutable `${tag.name}` tag datum. */
    @Override
    @Nullable ${lqrn(tag.typeRef, t)}.Imm get${up(tag.name)}();

    ${"/**"} Returns immutable `${tag.name}` tag value. */
    @Override
    @Nullable ${lqrn(tag.typeRef, t)}.Imm.Value get${up(tag.name)}$$();
"""
  }.mkString
}\

    /** Private implementation of `$ln.Imm` interface. */
    final class Impl extends io.epigraph.data.Data.Imm.Static.Impl<$ln.Imm> implements $ln.Imm {

      private Impl(@NotNull io.epigraph.data.Data.Imm.Raw raw) { super($ln.type, raw); }
${t.effectiveTags.map { tag => sn"""\

      ${"/**"} Returns immutable `${tag.name}` tag datum. */
      @Override
      public @Nullable ${lqrn(tag.typeRef, t)}.Imm get${up(tag.name)}() {
        return io.epigraph.util.Util.apply(get${up(tag.name)}$$(), ${lqrn(tag.typeRef, t)}.Imm.Value::getDatum);
      }

      ${"/**"} Returns immutable `${tag.name}` tag value. */
      @Override
      public @Nullable ${lqrn(tag.typeRef, t)}.Imm.Value get${up(tag.name)}$$() {
        return (${lqrn(tag.typeRef, t)}.Imm.Value) _raw().getValue($ln.${jn(tag.name)});
      }
"""
  }.mkString
}\

    }

  }

  /**
   * Builder for `${t.name.name}` data.
   */
  final class Builder extends io.epigraph.data.Data.Builder.Static<$ln.Imm> implements $ln {

    private Builder(@NotNull io.epigraph.data.Data.Builder.Raw raw) { super($ln.type, raw, $ln.Imm.Impl::new); }
${t.effectiveTags.map { tag => // for each effective tag
    sn"""\

    ${"/**"} Returns `${tag.name}` tag datum. */
    @Override
    public @Nullable ${lqrn(tag.typeRef, t)} get${up(tag.name)}() {
      return io.epigraph.util.Util.apply(get${up(tag.name)}$$(), ${lqrn(tag.typeRef, t)}.Value::getDatum);
    }

    ${"/**"} Sets `${tag.name}` tag datum. */
    public @NotNull $ln.Builder set${up(tag.name)}(@Nullable ${lqrn(tag.typeRef, t)} ${jn(tag.name)}) {
      _raw().setDatum($ln.${jn(tag.name)}, ${jn(tag.name)}); return this; // TODO return set${up(tag.name)}$$(${jn(tag.name)}.asValue());
    }

    ${"/**"} Sets `${tag.name}` tag error. */
    public @NotNull $ln.Builder set${up(tag.name)}(@NotNull io.epigraph.errors.ErrorValue error) {
      _raw().setError($ln.${jn(tag.name)}, error); return this;
    }

    ${"/**"} Returns `${tag.name}` tag value. */
    @Override
    public @Nullable ${lqrn(tag.typeRef, t)}.Value get${up(tag.name)}$$() {
      return (${lqrn(tag.typeRef, t)}.Value) _raw().getValue($ln.${jn(tag.name)});
    }

    ${"/**"} Sets `${tag.name}` tag value. */
    public @NotNull $ln.Builder set${up(tag.name)}(@Nullable ${lqrn(tag.typeRef, t)}.Value ${jn(tag.name)}Value) {
      _raw().setValue($ln.${jn(tag.name)}, ${jn(tag.name)}Value); return this;
    }
"""
  }.mkString
}\

  }

}
"""/*@formatter:on*/

}

/* Created by yegor on 7/13/16. */

package com.sumologic.epigraph.java

import com.sumologic.epigraph.java.NewlineStringInterpolator.NewlineHelper
import com.sumologic.epigraph.schema.compiler._

class VarTypeGen(from: CVarTypeDef, ctx: CContext) extends JavaTypeDefGen[CVarTypeDef](from, ctx) {

  protected def generate: String = sn"""\
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
${t.effectiveTags.map { tag => // for each effective tag
    sn"""\

  ${"/**"}
   * Tag `${tag.name}`.
   */
  @NotNull Tag ${jn(tag.name)} = new Tag("${tag.name}", ${lqrn(tag.typeRef, t)}.type);

  ${"/**"}
   * Returns `${tag.name}` tag datum.
   */
  @Nullable ${lqrn(tag.typeRef, t)} get${up(tag.name)}();

  ${"/**"}
   * Returns `${tag.name}` tag value.
   */
  @Nullable ${lqrn(tag.typeRef, t)}.Value get_${up(tag.name)}();
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
          ${t.isPolymorphic},
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
$listSupplier\

  }

  /**
   * Immutable interface for `${t.name.name}` data.
   */
  interface Imm extends $ln,${withParents(".Imm")} io.epigraph.data.Data.Imm.Static {
${t.effectiveTags.map { tag => // for each effective tag
    sn"""\

    ${"/**"}
     * Returns immutable `${tag.name}` tag datum.
     */
    @Override
    @Nullable ${lqrn(tag.typeRef, t)}.Imm get${up(tag.name)}();

    ${"/**"}
     * Returns immutable `${tag.name}` tag value.
     */
    @Override
    @Nullable ${lqrn(tag.typeRef, t)}.Imm.Value get_${up(tag.name)}();
"""
  }.mkString
}\

    /** Private implementation of `$ln.Imm` interface. */
    final class Impl extends io.epigraph.data.Data.Imm.Static.Impl<$ln.Imm> implements $ln.Imm {

      private Impl(@NotNull io.epigraph.data.Data.Imm.Raw raw) { super($ln.type, raw); }
${t.effectiveTags.map { tag => // for each effective tag
    sn"""\

      ${"/**"}
       * Returns immutable `${tag.name}` tag datum.
       */
      @Override
      public @Nullable ${lqrn(tag.typeRef, t)}.Imm get${up(tag.name)}() {
        return io.epigraph.util.Util.apply(get_${up(tag.name)}(), ${lqrn(tag.typeRef, t)}.Imm.Value::getDatum);
      }

      ${"/**"}
       * Returns immutable `${tag.name}` tag value.
       */
      @Override
      public @Nullable ${lqrn(tag.typeRef, t)}.Imm.Value get_${up(tag.name)}() {
        return (${lqrn(tag.typeRef, t)}.Imm.Value) _raw()._getValue($ln.${jn(tag.name)});
      }
"""
  }.mkString
}\

    }

  }

  /**
   * Builder for `${t.name.name}` data.
   */
  final class Builder extends io.epigraph.data.Data.Mut.Static<$ln.Imm> implements $ln {

    private Builder(@NotNull io.epigraph.data.Data.Mut.Raw raw) { super($ln.type, raw, $ln.Imm.Impl::new); }
${t.effectiveTags.map { tag => // for each effective tag
    sn"""\

    ${"/**"}
     * Returns `${tag.name}` tag datum builder.
     */
    @Override
    public @Nullable ${lqrn(tag.typeRef, t)}.Builder get${up(tag.name)}() {
      return io.epigraph.util.Util.apply(get_${up(tag.name)}(), ${lqrn(tag.typeRef, t)}.Builder.Value::getDatum);
    }

    public @NotNull $ln.Builder set${up(tag.name)}(@Nullable ${lqrn(tag.typeRef, t)}.Builder ${jn(tag.name)}) {
      _raw()._getOrCreateTagValue($ln.${jn(tag.name)})._raw().setDatum(${jn(tag.name)});
      return this;
    }

    public @NotNull $ln.Builder set${up(tag.name)}(@NotNull io.epigraph.errors.ErrorValue error) {
      _raw()._getOrCreateTagValue($ln.${jn(tag.name)})._raw().setError(error);
      return this;
    }

    ${"/**"}
     * Returns `${tag.name}` tag value builder.
     */
    @Override
    public @Nullable ${lqrn(tag.typeRef, t)}.Builder.Value get_${up(tag.name)}() {
      return (${lqrn(tag.typeRef, t)}.Builder.Value) _raw()._getValue($ln.${jn(tag.name)});
    }
"""
  }.mkString
}\

  }

}
"""

}

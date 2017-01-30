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

/* Created by yegor on 7/13/16. */

package ws.epigraph.java

import ws.epigraph.compiler._
import ws.epigraph.java.JavaGenNames.{jn, pn, lqn, lqrn, qnameArgs}
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper

class VarTypeGen(from: CVarTypeDef, ctx: GenContext) extends JavaTypeDefGen[CVarTypeDef](from, ctx) {

  protected def generate: String = /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
package ${pn(t)};

import ws.epigraph.types.Type.Tag;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base (read) interface for `${t.name.name}` data.
 */
public interface $ln extends${withParents(t)} ws.epigraph.data.Data.Static {

  @NotNull $ln.Type type = $ln.Type.instance();

  /** Returns new builder for `${t.name.name}` data. */
  static @NotNull $ln.Builder create() { return $ln.Type.instance().createDataBuilder(); }
${t.effectiveTags.map { tag => sn"""\

  ${"/**"} Tag `${tag.name}`. */
  @NotNull Tag ${jn(tag.name)} = new Tag("${tag.name}", ${lqrn(tag.typeRef, t)}.Type.instance());

  ${"/**"} Returns `${tag.name}` tag datum. */
  @Nullable ${lqrn(tag.typeRef, t)} get${up(tag.name)}();

  ${"/**"} Returns `${tag.name}` tag value. */
  @Nullable ${lqrn(tag.typeRef, t)}.Value get${up(tag.name)}_();
"""
  }.mkString
}\

  ${"/**"}
   * Class for `${t.name.name}` type.
   */
  final class Type extends ws.epigraph.types.UnionType.Static<$ln.Imm, $ln.Builder> {

    private static final class Holder { public static $ln.Type instance = new $ln.Type(); }

    public static $ln.Type instance() { return Holder.instance; }

    private Type() {
      super(
          new ws.epigraph.names.QualifiedTypeName(${qnameArgs(t.name.fqn).mkString("\"", "\", \"", "\"")}),
          java.util.Arrays.asList(${t.linearizedParents.map(lqn(_, t, _ + ".Type.instance()")).mkString(", ")}),
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
  interface Imm extends $ln,${withParents(".Imm")} ws.epigraph.data.Data.Imm.Static {
${t.effectiveTags.map { tag => sn"""\

    ${"/**"} Returns immutable `${tag.name}` tag datum. */
    @Override
    @Nullable ${lqrn(tag.typeRef, t)}.Imm get${up(tag.name)}();

    ${"/**"} Returns immutable `${tag.name}` tag value. */
    @Override
    @Nullable ${lqrn(tag.typeRef, t)}.Imm.Value get${up(tag.name)}_();
"""
  }.mkString
}\

    /** Private implementation of `$ln.Imm` interface. */
    final class Impl extends ws.epigraph.data.Data.Imm.Static.Impl<$ln.Imm> implements $ln.Imm {

      private Impl(@NotNull ws.epigraph.data.Data.Imm.Raw raw) { super($ln.Type.instance(), raw); }
${t.effectiveTags.map { tag => sn"""\

      ${"/**"} Returns immutable `${tag.name}` tag datum. */
      @Override
      public @Nullable ${lqrn(tag.typeRef, t)}.Imm get${up(tag.name)}() {
        return ws.epigraph.util.Util.apply(get${up(tag.name)}_(), ${lqrn(tag.typeRef, t)}.Imm.Value::getDatum);
      }

      ${"/**"} Returns immutable `${tag.name}` tag value. */
      @Override
      public @Nullable ${lqrn(tag.typeRef, t)}.Imm.Value get${up(tag.name)}_() {
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
  final class Builder extends ws.epigraph.data.Data.Builder.Static<$ln.Imm> implements $ln {

    private Builder(@NotNull ws.epigraph.data.Data.Builder.Raw raw) { super($ln.Type.instance(), raw, $ln.Imm.Impl::new); }
${t.effectiveTags.map { tag => // for each effective tag
    sn"""\

    ${"/**"} Returns `${tag.name}` tag datum. */
    @Override
    public @Nullable ${lqrn(tag.typeRef, t)} get${up(tag.name)}() {
      return ws.epigraph.util.Util.apply(get${up(tag.name)}_(), ${lqrn(tag.typeRef, t)}.Value::getDatum);
    }

    ${"/**"} Sets `${tag.name}` tag datum. */
    public @NotNull $ln.Builder set${up(tag.name)}(@Nullable ${lqrn(tag.typeRef, t)} ${jn(tag.name)}) {
      _raw().setDatum($ln.${jn(tag.name)}, ${jn(tag.name)}); return this; // TODO return set${up(tag.name)}_(${jn(tag.name)}.asValue());
    }

    ${"/**"} Sets `${tag.name}` tag error. */
    public @NotNull $ln.Builder set${up(tag.name)}_Error(@NotNull ws.epigraph.errors.ErrorValue error) {
      _raw().setError($ln.${jn(tag.name)}, error); return this;
    }

    ${"/**"} Returns `${tag.name}` tag value. */
    @Override
    public @Nullable ${lqrn(tag.typeRef, t)}.Value get${up(tag.name)}_() {
      return (${lqrn(tag.typeRef, t)}.Value) _raw().getValue($ln.${jn(tag.name)});
    }

    ${"/**"} Sets `${tag.name}` tag value. */
    public @NotNull $ln.Builder set${up(tag.name)}_(@Nullable ${lqrn(tag.typeRef, t)}.Value ${jn(tag.name)}Value) {
      _raw().setValue($ln.${jn(tag.name)}, ${jn(tag.name)}Value); return this;
    }
"""
  }.mkString
}\

  }

}
"""/*@formatter:on*/

}

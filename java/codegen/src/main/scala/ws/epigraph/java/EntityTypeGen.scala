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

/* Created by yegor on 7/13/16. */

package ws.epigraph.java

import ws.epigraph.compiler._
import ws.epigraph.java.JavaGenNames.{lqrn, pn, pnq2, qnameArgs, tcn}
import ws.epigraph.java.NewlineStringInterpolator.{NewlineHelper, i}
import ws.epigraph.lang.Qn
class EntityTypeGen(from: CEntityTypeDef, ctx: GenContext) extends JavaTypeDefGen[CEntityTypeDef](from, ctx) {

  protected def generate: String = {
    val namespace: Qn = pnq2(t)
    val ogc = new ObjectGenContext(ctx, namespace, true)
    val ann = ogc.use("ws.epigraph.annotations.Annotations")

    val annotations = new AnnotationsGen(from.annotations).generate(ogc)

    val tags = t.effectiveTags.map { tag => /*@formatter:off*/sn"""\
  ${"/**"} Tag `${tag.name}`. */
  @NotNull Tag ${tcn(tag)} = new Tag(${if (tag.annotations.isEmpty) s""""${tag.name}", ${lqrn(tag.typeRef, t)}.Type.instance(), $ann.EMPTY);""" else sn"""
    "${tag.name}",
     ${lqrn(tag.typeRef, t)}.Type.instance(),
     ${i(new AnnotationsGen(tag.annotations).generate(ogc))}
  );"""}

  ${"/**"} Returns `${tag.name}` tag datum. */
  @Nullable ${lqrn(tag.typeRef, t)} get${up(tag.name)}();

  ${"/**"} Returns `${tag.name}` tag value. */
  @Nullable ${lqrn(tag.typeRef, t)}.Value get${up(tag.name)}_();
"""/*@formatter:on*/
    }

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}\
package ${pn(t)};

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.types.Tag;
${ObjectGenUtils.genImports(ogc)}\

/**
 * Base (read) interface for `${t.name.name}` data.
 */
${JavaGenUtils.generatedAnnotation(this)}
public interface $ln extends${JavaGenUtils.withParents(t)} ws.epigraph.data.Data.Static {

  @NotNull $ln.Type type = $ln.Type.instance();

  /** Returns new builder for `${t.name.name}` data. */
  static @NotNull $ln.Builder create() { return $ln.Type.instance().createDataBuilder(); }

${tags.mkString}\

  ${"/**"}
   * Class for `${t.name.name}` type.
   */
  final class Type extends ws.epigraph.types.EntityType.Static<$ln.Imm, $ln.Builder> {

$typeInstance\

    private Type() {
      super(
          new ws.epigraph.names.QualifiedTypeName(${qnameArgs(t.name.fqn).mkString("\"", "\", \"", "\"")}),
          java.util.Arrays./*<ws.epigraph.types.EntityType.Static<
            ? super $ln.Imm, ? extends ws.epigraph.data.Data.Builder.Static<? super $ln.Imm>
          >>*/asList(${parents(".Type.instance()")}),
          $ln.Builder::new,
          ${i(annotations)}
      );
    }

    @Override
    public @NotNull java.util.List<${NotNull_}Tag> immediateTags() {
      return java.util.Arrays.asList(\
${t.declaredTags.map { tag => sn"""
          $ln.${tcn(tag)}"""
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
    @Nullable ${lqrn(tag.typeRef, t)}.Value.Imm get${up(tag.name)}_();
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
        return ws.epigraph.util.Util.apply(get${up(tag.name)}_(), ${lqrn(tag.typeRef, t)}.Value.Imm::getDatum);
      }

      ${"/**"} Returns immutable `${tag.name}` tag value. */
      @Override
      public @Nullable ${lqrn(tag.typeRef, t)}.Value.Imm get${up(tag.name)}_() {
        return (${lqrn(tag.typeRef, t)}.Value.Imm) _raw().getValue($ln.${tcn(tag)});
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
    public @NotNull $ln.Builder set${up(tag.name)}(@Nullable ${lqrn(tag.typeRef, t)} ${tcn(tag)}) {
      _raw().setDatum($ln.${tcn(tag)}, ${tcn(tag)}); return this; // TODO return set${up(tag.name)}_(${tcn(tag)}.asValue());
    }

    ${"/**"} Sets `${tag.name}` tag error. */
    public @NotNull $ln.Builder set${up(tag.name)}_Error(@NotNull ws.epigraph.errors.ErrorValue error) {
      _raw().setError($ln.${tcn(tag)}, error); return this;
    }

    ${"/**"} Returns `${tag.name}` tag value. */
    @Override
    public @Nullable ${lqrn(tag.typeRef, t)}.Value get${up(tag.name)}_() {
      return (${lqrn(tag.typeRef, t)}.Value) _raw().getValue($ln.${tcn(tag)});
    }

    ${"/**"} Sets `${tag.name}` tag value. */
    public @NotNull $ln.Builder set${up(tag.name)}_(@Nullable ${lqrn(tag.typeRef, t)}.Value ${tcn(tag)}Value) {
      _raw().setValue($ln.${tcn(tag)}, ${tcn(tag)}Value); return this;
    }
"""
  }.mkString
}\

  }

}
"""/*@formatter:on*/
  }

}

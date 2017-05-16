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

/* Created by yegor on 7/11/16. */

package ws.epigraph.java

import ws.epigraph.compiler.{CPrimitiveTypeDef, CTypeKind}
import ws.epigraph.java.JavaGenNames.{lqn, pn, javaQName, qnameArgs}
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper

class PrimitiveGen(from: CPrimitiveTypeDef, ctx: GenContext) extends JavaTypeDefGen[CPrimitiveTypeDef](from, ctx)
    with DatumTypeJavaGen {

  protected override def generate: String = /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}\
package ${pn(t)};

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Generated;

/**
 * Base interface for `${t.name.name}` datum.
 */
@Generated("${getClass.getName}")
public interface $ln extends${JavaGenUtils.withParents(t)} ws.epigraph.data.${kind(t)}Datum.Static {

  @NotNull $ln.Type type = $ln.Type.instance();

  static @NotNull $ln.Builder create(@NotNull ${native(t)} val) { return $ln.Type.instance().createBuilder(val); }

  @Override
  @NotNull $ln.Imm toImmutable();
${t.meta match {
    case Some(mt) => sn"""\

  ${"/**"}
   * @return meta-data instance
   */
  @Nullable ${lqn(mt, t)} meta();
"""
    case None => ""
  }
}\

  /**
   * Class for `${t.name.name}` datum type.
   */
  final class Type extends ws.epigraph.types.${kind(t)}Type.Static<
      Imm, Builder, Value.Imm, Value.Builder, Data.Imm, Data.Builder
  > {

    private static final class Holder { public static $ln.Type instance = new $ln.Type(); }

    public static $ln.Type instance() { return Holder.instance; }

    private Type() {
      super(
          new ws.epigraph.names.QualifiedTypeName(${qnameArgs(t.name.fqn).mkString("\"", "\", \"", "\"")}),
          java.util.Arrays.asList(${parents(".Type.instance()")}),
          ${t.meta.map{mt => lqn(mt, t, _ + ".type")}.getOrElse("null")},
          $ln.Builder::new,
          $ln.Value.Imm.Impl::new,
          $ln.Data.Builder::new
      );
    }

  }

  /**
   * Builder for `${t.name.name}` datum.
   */
  final class Builder extends ws.epigraph.data.${kind(t)}Datum.Builder.Static<$ln.Imm, $ln.Value.Builder> implements $ln {

    Builder(@NotNull ws.epigraph.data.${kind(t)}Datum.Builder.Raw raw) { super($ln.Type.instance(), raw, $ln.Imm.Impl::new, $ln.Value.Builder::new); }
${t.meta match {
    case Some(mt) => sn"""\

    ${"/**"}
     * @return meta-data instance
     */
    @Override
    public @Nullable ${lqn(mt, t)} meta() {
      return (${lqn(mt, t)}) _raw().meta();
    }

    ${"/**"}
     * Sets meta-data value
     *
     * @param meta new meta-data value
     *
     * @return {@code this}
     */
    public @NotNull $ln.Builder setMeta(@Nullable ${lqn(mt,t)} meta) {
       _raw().setMeta(meta);
       return this;
    }
"""
    case None => ""
  }
}\

  }

  /**
   * Immutable interface for `${t.name.name}` datum.
   */
  interface Imm extends $ln,${withParents(".Imm")} ws.epigraph.data.${kind(t)}Datum.Imm.Static {
${t.meta match {
    case Some(mt) => sn"""\

    ${"/**"}
     * @return meta-data instance
     */
    @Override
    @Nullable ${lqn(mt, t)}.Imm meta();
"""
    case None => ""
  }
}\

    /** Private implementation of `$ln.Imm` interface. */
    final class Impl extends ws.epigraph.data.${kind(t)}Datum.Imm.Static.Impl<$ln.Imm, $ln.Value.Imm> implements $ln.Imm {

      Impl(@NotNull ws.epigraph.data.${kind(t)}Datum.Imm.Raw raw) { super($ln.Type.instance(), raw, $ln.Value.Imm.Impl::new); }
${t.meta match {
      case Some(mt) => sn"""\

      ${"/**"}
       * @return meta-data instance
       */
      @Override
      public @Nullable ${lqn(mt, t)}.Imm meta() {
        return (${lqn(mt, t)}.Imm) _raw().meta();
      }
"""
    case None => ""
  }
}\

    }

  }

$datumValue\

$datumData\

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

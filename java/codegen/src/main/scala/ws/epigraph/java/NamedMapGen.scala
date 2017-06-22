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

/* Created by yegor on 10/3/16. */

package ws.epigraph.java

import ws.epigraph.compiler.CMapTypeDef
import ws.epigraph.java.JavaGenNames.{lqn, qnameArgs}
import ws.epigraph.java.NewlineStringInterpolator.{i,NewlineHelper}

class NamedMapGen(from: CMapTypeDef, ctx: GenContext) extends MapGen[CMapTypeDef](from, ctx) with DatumTypeJavaGen {
  override protected def genTypeClass(ogc: ObjectGenContext): String = sn"""\
  final class Type extends ws.epigraph.types.NamedMapType.Static<
      ${lqn(kt, t)}.Imm,
      $ln.Imm,
      $ln.Builder,
      $ln.Value.Imm,
      $ln.Value.Builder,
      $ln.Data.Imm,
      $ln.Data.Builder
  > {

$typeInstance\

    private Type() {
      super(
          new ws.epigraph.names.QualifiedTypeName(${qnameArgs(t.name.fqn).mkString("\"", "\", \"", "\"")}),
          ${t.meta.map{mt => lqn(mt, t, _ + ".type")}.getOrElse("null")},
          ${lqn(kt, t)}.Type.instance(),
          ${dataTypeExpr(vv, t)},
          $ln.Builder::new,
          $ln.Value.Imm.Impl::new,
          $ln.Data.Builder::new,
          ${i(new AnnotationsGen(from.annotations).generate(ogc))},
          ${parents(".Type.instance()")}
      );
    }

  }
"""
}

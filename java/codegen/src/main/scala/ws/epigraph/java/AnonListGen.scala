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

/* Created by yegor on 8/15/16. */

package ws.epigraph.java

import ws.epigraph.compiler.CAnonListType
import ws.epigraph.java.JavaGenNames.lqn
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper

class AnonListGen(from: CAnonListType, ctx: GenContext) extends ListGen[CAnonListType](from, ctx) {
  override protected def genTypeClass: String = sn"""\
  final class Type extends ws.epigraph.types.AnonListType.Static<
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
          java.util.Arrays.asList(${ parents(".Type.instance()") }),
          ${ t.meta.map { mt => lqn(mt, t, _ + ".type") }.getOrElse("null") },
          ${ dataTypeExpr(ev, t) },
          $ln.Builder::new,
          $ln.Value.Imm.Impl::new,
          $ln.Data.Builder::new
      );
    }

  }
"""
}

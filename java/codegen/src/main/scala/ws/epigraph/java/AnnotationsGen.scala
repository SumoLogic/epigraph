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

package ws.epigraph.java

import ws.epigraph.annotations.Annotations
import ws.epigraph.java.NewlineStringInterpolator.{NewlineHelper, i}
import ws.epigraph.java.ObjectGenerators.gen

import scala.collection.JavaConversions._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class AnnotationsGen(anns: Annotations) extends ObjectGen[Annotations](anns) {

  override protected def generateObject(o: String, ctx: ObjectGenContext): String =
    if (anns.equals(Annotations.EMPTY)) o + ".EMPTY"
    else {
      val dt = ctx.use("ws.epigraph.types.DatumTypeApi")
      val a = ctx.use("ws.epigraph.annotations.Annotation")
      /*@formatter:off*/sn"""\
new $o(
  ${i(ObjectGenUtils.genHashMap(dt, a, anns.asMap().entrySet().map{e =>
        (ObjectGenUtils.genTypeExpr(e.getKey, ctx.gctx), gen(e.getValue, ctx))}, ctx))
   }
)"""/*@formatter:on*/
    }

}

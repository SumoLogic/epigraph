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

package ws.epigraph.java.service.projections.op.delete

import ws.epigraph.java.NewlineStringInterpolator.{NewlineHelper, i}
import ws.epigraph.java.service.ServiceGenUtils.{genFieldExpr, genLinkedMap, genTypeExpr}
import ws.epigraph.java.service.ServiceObjectGen.gen
import ws.epigraph.java.service.{ServiceGenContext, ServiceObjectGen}
import ws.epigraph.projections.op.delete.{OpDeleteFieldProjectionEntry, OpDeleteRecordModelProjection}
import ws.epigraph.types.{RecordType, RecordTypeApi, TypeApi}

import scala.collection.JavaConversions._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class OpDeleteRecordModelProjectionGen(p: OpDeleteRecordModelProjection)
  extends ServiceObjectGen[OpDeleteRecordModelProjection](p) {

  override protected def generateObject(ctx: ServiceGenContext): String = {
    ctx.addImport(classOf[RecordType].getName)

    /*@formatter:off*/sn"""\
new OpDeleteRecordModelProjection(
  ${genTypeExpr(p.model().asInstanceOf[TypeApi], ctx.gctx)},
  ${i(gen(p.params(), ctx))},
  ${i(gen(p.annotations(), ctx))},
  ${i(genLinkedMap("String", "OpDeleteFieldProjectionEntry", p.fieldProjections().entrySet().map{e =>
      ("\"" + e.getKey + "\"", genFieldProjectionEntry(p.model(), e.getValue, ctx))}, ctx))},
  ${gen(p.location(), ctx)}
)"""/*@formatter:on*/
  }

  private def genFieldProjectionEntry(
    t: RecordTypeApi,
    fpe: OpDeleteFieldProjectionEntry,
    ctx: ServiceGenContext): String = {

    ctx.addImport(classOf[OpDeleteFieldProjectionEntry].getName)

    /*@formatter:off*/sn"""\
new OpDeleteFieldProjectionEntry(
  ${genFieldExpr(t.asInstanceOf[TypeApi], fpe.field().name(), ctx.gctx)},
  ${i(gen(fpe.fieldProjection(), ctx))},
  ${gen(fpe.location(), ctx)}
)"""/*@formatter:on*/
  }
}
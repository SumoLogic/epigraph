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

package ws.epigraph.java.service

import ws.epigraph.java.service.projections.op._
import ws.epigraph.java.{ObjectGenContext, ObjectGenerators}
import ws.epigraph.projections.gen.ProjectionReferenceName
import ws.epigraph.projections.op._
import ws.epigraph.schema.operations._

object ServiceObjectGenerators extends ObjectGenerators {

  def genOpt(obj: Any, ctx: ObjectGenContext): String =
    Option(ObjectGenerators.genOpt(obj, ctx)).getOrElse {
      if (obj == null) "null"
      else obj match {

        case prn: ProjectionReferenceName => new ProjectionReferenceNameGen(prn).generate(ctx)
        case prns: ProjectionReferenceName.StringRefNameSegment =>
          new ProjectionReferenceName_StringSegmentGen(prns).generate(ctx)
        case prnt: ProjectionReferenceName.TypeRefNameSegment =>
          new ProjectionReferenceName_TypeSegmentGen(prnt).generate(ctx)

        case param: OpParam => new OpParamGen(param).generate(ctx)
        case params: OpParams => new OpParamsGen(params).generate(ctx)
        case kp: AbstractOpKeyPresence => new OpKeyPresenceGen(kp).generate(ctx)

        case oovp: OpEntityProjection => new OpEntityProjectionGen(oovp).generate(ctx)
        case oormp: OpRecordModelProjection => new OpRecordModelProjectionGen(oormp).generate(ctx)
        case oofp: OpFieldProjection => new OpFieldProjectionGen(oofp).generate(ctx)
        case oommp: OpMapModelProjection => new OpMapModelProjectionGen(oommp).generate(ctx)
        case ookp: OpKeyProjection => new OpKeyProjectionGen(ookp).generate(ctx)
        case oolmp: OpListModelProjection => new OpListModelProjectionGen(oolmp).generate(ctx)
        case oopmp: OpPrimitiveModelProjection => new OpPrimitiveModelProjectionGen(oopmp).generate(ctx)

        case rod: ReadOperationDeclaration => new ReadOperationDeclarationGen(rod).generate(ctx)
        case cod: CreateOperationDeclaration => new CreateOperationDeclarationGen(cod).generate(ctx)
        case uod: UpdateOperationDeclaration => new UpdateOperationDeclarationGen(uod).generate(ctx)
        case dod: DeleteOperationDeclaration => new DeleteOperationDeclarationGen(dod).generate(ctx)
        case cod: CustomOperationDeclaration => new CustomOperationDeclarationGen(cod).generate(ctx)

        case _ => null
      }

    }
}

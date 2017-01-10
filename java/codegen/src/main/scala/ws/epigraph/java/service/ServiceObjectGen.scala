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

package ws.epigraph.java.service

import ws.epigraph.gdata.{GData, GDatum}
import ws.epigraph.java.service.gdata.{GDataGen, GDatumGen}
import ws.epigraph.java.service.projections.op.delete._
import ws.epigraph.java.service.projections.op.input._
import ws.epigraph.java.service.projections.op.output._
import ws.epigraph.java.service.projections.op.path._
import ws.epigraph.java.service.projections.op.{OpKeyPresenceGen, OpParamGen, OpParamsGen}
import ws.epigraph.java.service.projections.{AnnotationGen, AnnotationsGen}
import ws.epigraph.lang.{Qn, TextLocation}
import ws.epigraph.projections.op.delete._
import ws.epigraph.projections.op.input._
import ws.epigraph.projections.op.output._
import ws.epigraph.projections.op.path._
import ws.epigraph.projections.op.{OpKeyPresence, OpParam, OpParams}
import ws.epigraph.projections.{Annotation, Annotations}
import ws.epigraph.refs.{TypeRef, ValueTypeRef}
import ws.epigraph.schema.operations._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
abstract class ServiceObjectGen[T](val obj: T) extends AbstractServiceGen {

  override def generate(ctx: ServiceGenContext): String = {
    if (obj == null) "null"
    else {
      ctx.addImport(obj.getClass.getCanonicalName)
      generateObject(ctx)
    }
  }

  protected def generateObject(ctx: ServiceGenContext): String
}

object ServiceObjectGen {
  def gen(obj: Any, ctx: ServiceGenContext): String =
    if (obj == null) "null"
    else obj match {

      case s: String => s""""${escapeString(s)}""""
      case i: java.lang.Integer => i.toString
      case l: java.lang.Long => l.toString + "L"
      case d: java.lang.Double => d.toString + "d"
      case f: java.lang.Float => f.toString + "f"
      case b: java.lang.Boolean => b.toString

      case qn: Qn => new QnGen(qn).generate(ctx)
      case tr: TypeRef => new TypeRefGen(tr).generate(ctx)
      case vtr: ValueTypeRef => new ValueTypeRefGen(vtr).generate(ctx)
      case tl: TextLocation => new TextLocationGen(tl).generate(ctx)

      case gdata: GData => new GDataGen(gdata).generate(ctx)
      case gdatum: GDatum => new GDatumGen(gdatum).generate(ctx)

      case ann: Annotation => new AnnotationGen(ann).generate(ctx)
      case anns: Annotations => new AnnotationsGen(anns).generate(ctx)

      case param: OpParam => new OpParamGen(param).generate(ctx)
      case params: OpParams => new OpParamsGen(params).generate(ctx)
      case kp: OpKeyPresence => new OpKeyPresenceGen(kp).generate(ctx)

      case ovp: OpVarPath => new OpVarPathGen(ovp).generate(ctx)
      case ormp: OpRecordModelPath => new OpRecordModelPathGen(ormp).generate(ctx)
      case ofp: OpFieldPath => new OpFieldPathGen(ofp).generate(ctx)
      case ommp: OpMapModelPath => new OpMapModelPathGen(ommp).generate(ctx)
      case opkp: OpPathKeyProjection => new OpPathKeyProjectionGen(opkp).generate(ctx)
      case opmp: OpPrimitiveModelPath => new OpPrimitiveModelPathGen(opmp).generate(ctx)

      case oivp: OpInputVarProjection => new OpInputVarProjectionGen(oivp).generate(ctx)
      case oirmp: OpInputRecordModelProjection => new OpInputRecordModelProjectionGen(oirmp).generate(ctx)
      case oifp: OpInputFieldProjection => new OpInputFieldProjectionGen(oifp).generate(ctx)
      case oimmp: OpInputMapModelProjection => new OpInputMapModelProjectionGen(oimmp).generate(ctx)
      case oikp: OpInputKeyProjection => new OpInputKeyProjectionGen(oikp).generate(ctx)
      case oilmp: OpInputListModelProjection => new OpInputListModelProjectionGen(oilmp).generate(ctx)
      case oipmp: OpInputPrimitiveModelProjection => new OpInputPrimitiveModelProjectionGen(oipmp).generate(ctx)

      case oovp: OpOutputVarProjection => new OpOutputVarProjectionGen(oovp).generate(ctx)
      case oormp: OpOutputRecordModelProjection => new OpOutputRecordModelProjectionGen(oormp).generate(ctx)
      case oofp: OpOutputFieldProjection => new OpOutputFieldProjectionGen(oofp).generate(ctx)
      case oommp: OpOutputMapModelProjection => new OpOutputMapModelProjectionGen(oommp).generate(ctx)
      case ookp: OpOutputKeyProjection => new OpOutputKeyProjectionGen(ookp).generate(ctx)
      case oolmp: OpOutputListModelProjection => new OpOutputListModelProjectionGen(oolmp).generate(ctx)
      case oopmp: OpOutputPrimitiveModelProjection => new OpOutputPrimitiveModelProjectionGen(oopmp).generate(ctx)
        
      case odvp: OpDeleteVarProjection => new OpDeleteVarProjectionGen(odvp).generate(ctx)
      case odrmp: OpDeleteRecordModelProjection => new OpDeleteRecordModelProjectionGen(odrmp).generate(ctx)
      case odfp: OpDeleteFieldProjection => new OpDeleteFieldProjectionGen(odfp).generate(ctx)
      case odmmp: OpDeleteMapModelProjection => new OpDeleteMapModelProjectionGen(odmmp).generate(ctx)
      case odkp: OpDeleteKeyProjection => new OpDeleteKeyProjectionGen(odkp).generate(ctx)
      case odlmp: OpDeleteListModelProjection => new OpDeleteListModelProjectionGen(odlmp).generate(ctx)
      case odpmp: OpDeletePrimitiveModelProjection => new OpDeletePrimitiveModelProjectionGen(odpmp).generate(ctx)

      case rod: ReadOperationDeclaration => new ReadOperationDeclarationGen(rod).generate(ctx)
      case cod: CreateOperationDeclaration => new CreateOperationDeclarationGen(cod).generate(ctx)
      case uod: UpdateOperationDeclaration => new UpdateOperationDeclarationGen(uod).generate(ctx)
      case dod: DeleteOperationDeclaration => new DeleteOperationDeclarationGen(dod).generate(ctx)
      case cod: CustomOperationDeclaration => new CustomOperationDeclarationGen(cod).generate(ctx)

      case _ =>
        try {
          new NativePrimitiveGen(obj).generate(ctx)
        } catch {
          case _: IllegalArgumentException =>
            throw new IllegalArgumentException("Unsupported object kind: " + obj.getClass.getName)
        }
    }

  def escapeString(s: String): String = // todo proper string escaping
    s
    .replace("\\", "\\\\")
    .replace("\t", "\\t")
    .replace("\n", "\\n")
    .replace("\"", "\\\"")
}

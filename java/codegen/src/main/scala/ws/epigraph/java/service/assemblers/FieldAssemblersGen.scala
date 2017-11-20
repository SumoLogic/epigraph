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

package ws.epigraph.java.service.assemblers

import java.nio.file.Path

import ws.epigraph.compiler.CDatumType
import ws.epigraph.java._
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.JavaGenNames.{ln, lqn2}
import ws.epigraph.java.service.projections.req.output.ReqOutputRecordModelProjectionGen
import ws.epigraph.lang.Qn
import ws.epigraph.util.JavaNames

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class FieldAssemblersGen(rag: RecordAsmGen, val ctx: GenContext) extends JavaGen with Fragments {
  private val cType: CDatumType = JavaGenUtils.toCType(rag.g.op.`type`())

  val namespace: Qn = rag.g.namespace

  val shortClassName: String = ln(cType) + "FieldAssemblers"

  val fullClassName: String = namespace.append(shortClassName).toString

  override def relativeFilePath: Path = JavaGenUtils.fqnToPath(namespace).resolve(shortClassName + ".java")

  private lazy val parentOpt: Option[FieldAssemblersGen] = rag.g.parentClassGenOpt.map(
    pg => pg.asInstanceOf[ReqOutputRecordModelProjectionGen].assemblerGen.fieldAssemblersGen
  )

  def methodName(fieldName: String): String = JavaNames.javaName(fieldName)

  case class AsmSupplier(
    fieldName: String,
    overloaded: Boolean,
    pg: ReqOutputRecordModelProjectionGen) {
    private val fieldPart = rag.fieldPart(fieldName).get

    val projectionType: Fragment = Fragment.imp(fieldPart.fieldGen.fullClassName)

    val assemblerResultType: Fragment = Fragment.imp(lqn2(fieldPart.fieldType, namespace.toString))

    val resultTypeSuffix: String = if (fieldPart.isEntity) "" else ".Value"

    def gen: Fragment = Fragment(/*@formatter:off*/sn"""\
  /**
   * Builds {@code $fieldName} field value
   *
   * @param dto data transfer object
   * @param projection request projection
   * @param ctx assembly context
   *
   * @return field value
   */
  public ${frag.notNull}$assemblerResultType$resultTypeSuffix ${methodName(fieldName)}(${frag.notNull}D dto, ${frag.notNull}$projectionType projection, ${frag.notNull}${frag.assemblerContext} ctx);
"""/*@formatter:on*/
    )
  }

  val asmSuppliers: Seq[AsmSupplier] = rag.g.fieldProjections.toSeq.map { case (fieldName, (parentGenOpt, _)) =>
    AsmSupplier(
      fieldName,
      parentGenOpt.isDefined,
      parentGenOpt.getOrElse(rag.g).asInstanceOf[ReqOutputRecordModelProjectionGen]
    )
  }


  override protected def generate: String = {
    if (rag.g.invalidParentClassGenerator) {
      throw new TryLaterException(s"Can't generate $fullClassName because parent projection wasn't created yet")
    }

    val parentImp: Option[Fragment] = parentOpt.map(p => Fragment.imp(p.fullClassName))

    val extendsClause: Fragment = parentImp.map(ip => Fragment(s"extends $ip<D> ")).getOrElse(Fragment.empty)

    interpolate(
      namespace, Fragment(
        /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
package $namespace;\
${Fragment.emptyLine}\
${Fragment.imports}\
${Fragment.emptyLine}\
/**
 * Field assemblers for {@code ${ln(cType)}} type
 */
${JavaGenUtils.generatedAnnotation(this)}
public interface $shortClassName<D> $extendsClause{
${Fragment.join(asmSuppliers.map(_.gen), Fragment.emptyLine)}\
}"""/*@formatter:on*/
      )
    )
  }
}

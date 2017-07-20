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

import ws.epigraph.compiler.{CDatumType, CField, CType, CTypeKind}
import ws.epigraph.java.{GenContext, JavaGen, JavaGenUtils}
import ws.epigraph.java.service.projections.req.output.{ReqOutputFieldProjectionGen, ReqOutputProjectionGen, ReqOutputRecordModelProjectionGen}
import ws.epigraph.lang.Qn
import ws.epigraph.java.JavaGenNames.{jn, ln, lqn2}
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper

// currently unused: can't figure out correct type variance
/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class RecordFieldAsmsGen(
  projectionGen: ReqOutputRecordModelProjectionGen,
  override val ctx: GenContext) extends JavaGen {

  private val namespace: Qn = projectionGen.namespace

  private val cType: CDatumType = JavaGenUtils.toCType(projectionGen.op.`type`())

  val shortClassName: String = ln(cType) + "FieldAsms"

  val fullClassName: String = namespace.append(shortClassName).toString

  override def relativeFilePath: Path = JavaGenUtils.fqnToPath(namespace).resolve(shortClassName + ".java")

  case class FieldParts(field: CField, fieldGen: ReqOutputProjectionGen) {
    def fieldName: String = jn(field.name)

    def fieldType: CType = field.typeRef.resolved

    def isEntity: Boolean = fieldType.kind == CTypeKind.ENTITY

    //def assemblerType: String = s"Asm<? super D, ? super ${ fieldGen.fullClassName }, ? extends ${
    def assemblerType: String = s"Asm<D, ${ fieldGen.fullClassName }, ? extends ${
      lqn2(
        fieldType,
        namespace.toString
      )
    }${ if (isEntity) "" else ".Value" }>"

    def generate: String = /*@formatter:off*/sn"""\
    /** @return {@code $fieldName} field assembler */
    @NotNull $assemblerType $fieldName();
"""/*@formatter:on*/
  }

  private def fieldGenerators(g: ReqOutputRecordModelProjectionGen): Map[String, (CField, ReqOutputFieldProjectionGen)] =
//    g.parentClassGenOpt.map(pg => fieldGenerators(pg.asInstanceOf[ReqOutputRecordModelProjectionGen])).getOrElse(Map()) ++
    g.fieldGenerators.map { case (f, p) => f.name -> (f, p) }


  override protected def generate: String = {
    val parentGenOpt: Option[RecordFieldAsmsGen] = projectionGen
      .parentClassGenOpt.map(_.asInstanceOf[ReqOutputRecordModelProjectionGen].assemblerGen.asInstanceOf[RecordAsmGen2].fieldAsmsGen) // second `asInstanceOf` can be removed after switching to RecordAsmGen2

    val extendsClause = parentGenOpt.map(pg => "extends " + pg.fullClassName + "<D> ").getOrElse("")

    val fps: Seq[FieldParts] = fieldGenerators(projectionGen).map { case (_, (f, fg)) =>
      FieldParts(f, fg.dataProjectionGen)
    }.toSeq

    val imports: Set[String] = Set(
      "org.jetbrains.annotations.NotNull",
      "ws.epigraph.assembly.Asm"
    )

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
package $namespace;

${JavaGenUtils.generateImports(imports)}

/**
 * Field assemblers for {@code ${ln(cType)}} type
 */
${JavaGenUtils.generatedAnnotation(this)}
public interface $shortClassName<D> $extendsClause{
${fps.map {fp => fp.generate}.mkString("\n")}\
}"""/*@formatter:on*/
  }
}

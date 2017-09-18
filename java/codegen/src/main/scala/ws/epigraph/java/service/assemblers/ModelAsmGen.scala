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
import ws.epigraph.java.JavaGenNames.{ln, lqn2}
import ws.epigraph.java.JavaGenUtils
import ws.epigraph.java.NewlineStringInterpolator.{NewlineHelper, i}
import ws.epigraph.java.service.projections.req.ReqModelProjectionGen
import ws.epigraph.java.service.projections.req.output.ReqOutputModelProjectionGen
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.output.OpOutputModelProjection
import ws.epigraph.types.DatumTypeApi

/**
 * Base trait for model assembler generators
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
trait ModelAsmGen extends AsmGen {
  protected type G <: ReqOutputModelProjectionGen

  protected def g: G

  override protected val namespace: Qn = g.namespace

  protected val nsString: String = namespace.toString

  protected val cType: CDatumType = JavaGenUtils.toCType(g.op.`type`())

  override protected val shortClassName: String = ln(cType) + "Asm"

  override def relativeFilePath: Path = JavaGenUtils.fqnToPath(g.namespace).resolve(shortClassName + ".java")

  protected val t: importManager.ImportedName = importManager.use(lqn2(cType, nsString))

  protected val projectionName: importManager.ImportedName = importManager.use(g.fullClassName)

  importManager.use(namespace.append(shortClassName))

  case class TailParts(tailProjectionGen: ReqModelProjectionGen) {
    def `type`: CDatumType = JavaGenUtils.toCType(tailProjectionGen.op.`type`())

    val tailGenName: importManager.ImportedName = importManager.use(tailProjectionGen.fullClassName)

    val tailResultType: importManager.ImportedName = importManager.use(lqn2(`type`, nsString))

    def assembler: String = JavaGenUtils.lo(ln(`type`)) + "Asm"

    def assemblerType: String = s"${ Imports.assembler }<? super D, ? super $tailGenName, ? extends $tailResultType.Value>"

    def javadoc: String = s"$assembler {@code ${ ln(`type`) }} value assembler"
  }

  protected val tps: Seq[TailParts] = g.normalizedTailGenerators.values.map { tg => TailParts(tg) }.toSeq

  protected val hasTails: Boolean = g.normalizedTailGenerators.nonEmpty

  protected def defaultBuild: String

  protected lazy val nonTailsBuild: String = /*@formatter:off*/sn"""{
      ${i(defaultBuild)}
    }"""/*@formatter:on*/

  protected lazy val tailsBuild: String = /*@formatter:off*/sn"""
      return $projectionName.dispatcher.dispatch(
          p,
          ${if (hasTails) "typeExtractor.apply(dto)" else s"$t.type"},
${if (tps.nonEmpty) tps.map { tp => s"tp -> ${tp.assembler}.assemble(dto, tp, ctx)" }.mkString("          ",",\n          ",",\n") else ""}\
          () -> {
            ${i(defaultBuild)}
          }
      );"""/*@formatter:on*/

  // meta stuff

  protected lazy val metaProjection: OpOutputModelProjection[_, _, _, _] =
    g.op.metaProjection().asInstanceOf[OpOutputModelProjection[_, _, _, _]]

  protected lazy val hasMeta: Boolean = g.metaGeneratorOpt.isDefined

  protected lazy val metaCType: CDatumType = JavaGenUtils.toCType(metaProjection.`type`().asInstanceOf[DatumTypeApi])

  protected val metaGeneratorNameOpt: Option[importManager.ImportedName] = g.metaGeneratorOpt.map(
    mg => importManager.use(
      mg.fullClassName
    )
  )

  protected lazy val metaAsmType: String = s"${ Imports.assembler }<? super D, ? super ${ metaGeneratorNameOpt.get }, ? extends ${
    lqn2(
      metaCType,
      g.namespace.toString
    )
  }>"
}

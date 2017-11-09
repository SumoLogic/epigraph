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
class FieldAssemblersGen(rag: RecordAsmGen, val ctx: GenContext) extends JavaGen {
  private val cType: CDatumType = JavaGenUtils.toCType(rag.g.op.`type`())

  val namespace: Qn = rag.g.namespace

  val shortClassName: String = ln(cType) + "FieldAssemblers"

  val fullClassName: String = namespace.append(shortClassName).toString

  override def relativeFilePath: Path = JavaGenUtils.fqnToPath(namespace).resolve(shortClassName + ".java")

  private lazy val importManager: ImportManager = new ImportManager(namespace)

  private lazy val parentOpt: Option[FieldAssemblersGen] = rag.g.parentClassGenOpt.map(
    pg => pg.asInstanceOf[ReqOutputRecordModelProjectionGen].assemblerGen.fieldAssemblersGen
  )

  def methodName(fieldName: String): String = JavaNames.javaName(fieldName)

  case class AsmSupplier(fieldName: String, overloaded: Boolean, pg: ReqOutputRecordModelProjectionGen, importManager: ImportManager) {
    private val fieldPart = rag.fieldPart(fieldName).get

    val projectionType: importManager.ImportedName = importManager.use(fieldPart.fieldGen.fullClassName)

    val assemblerResultType: importManager.ImportedName = importManager.use(
      lqn2(
        fieldPart.fieldType,
        namespace.toString
      )
    )

    val resultTypeSuffix: String = if (fieldPart.isEntity) "" else ".Value"

    def gen: String = /*@formatter:off*/sn"""\
  /**
   * Builds {@code $fieldName} field value
   *
   * @param dto data transfer object
   * @param projection request projection
   * @param ctx assembly context
   *
   * @return field value
   */
  public ${Imports.notNull}$assemblerResultType$resultTypeSuffix ${methodName(fieldName)}(${Imports.notNull}D dto, ${Imports.notNull}$projectionType projection, ${Imports.notNull}${Imports.assemblerContext} ctx);
"""/*@formatter:on*/
  }

  val asmSuppliers: Seq[AsmSupplier] = rag.g.fieldProjections.toSeq.map { case (fieldName, (parentGenOpt, _)) =>
    AsmSupplier(
      fieldName,
      parentGenOpt.isDefined,
      parentGenOpt.getOrElse(rag.g).asInstanceOf[ReqOutputRecordModelProjectionGen],
      importManager
    )
  }


  override protected def generate: String = {
    if (rag.g.invalidParentClassGenerator) {
      throw new TryLaterException(s"Can't generate $fullClassName because parent projection wasn't created yet")
    }

    val parentImp = parentOpt.map(p => importManager.use(p.fullClassName))

    closeImports()

    val extendsClause: String = parentImp.map(ip => s"extends $ip<D> ").getOrElse("")

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
package $namespace;

${JavaGenUtils.generateImports(importManager.imports)}

/**
 * Field assemblers for {@code ${ln(cType)}} type
 */
${JavaGenUtils.generatedAnnotation(this)}
public interface $shortClassName<D> $extendsClause{
${asmSuppliers.map(_.gen).mkString("\n")}\
}"""/*@formatter:on*/
  }

  protected object Imports {
    val notNull: ImportManager.Imported =
      if (ctx.java8Annotations) importManager.use("org.jetbrains.annotations.NotNull").prepend("@").append(" ") else ImportManager.empty
    val nullable: ImportManager.Imported =
      if (ctx.java8Annotations) importManager.use("org.jetbrains.annotations.Nullable").prepend("@").append(" ") else ImportManager.empty
    val func: ImportManager.Imported = importManager.use("java.util.function.Function")
    val assembler: ImportManager.Imported = importManager.use("ws.epigraph.assembly.Asm")
    val assemblerContext: ImportManager.Imported = importManager.use("ws.epigraph.assembly.AsmContext")
    val _type: ImportManager.Imported = importManager.use("ws.epigraph.types.Type")
    val errValue: ImportManager.Imported = importManager.use("ws.epigraph.errors.ErrorValue")
  }

  protected def closeImports(): Unit = {
    val _ = Imports.assembler // cause lazy eval
    importManager.close()
  }
}

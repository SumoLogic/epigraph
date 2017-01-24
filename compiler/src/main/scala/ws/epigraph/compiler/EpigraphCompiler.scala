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

/* Created by yegor on 6/8/16. */

package ws.epigraph.compiler

import java.io.IOException
import java.util
import java.util.Collections

import com.intellij.lang.ParserDefinition
import com.intellij.psi.PsiFile
import org.intellij.grammar.LightPsi
import org.jetbrains.annotations.Nullable
import ws.epigraph.psi.{PsiProcessingError, PsiProcessingException}
import ws.epigraph.schema.parser.psi._
import ws.epigraph.schema.parser.{ResourcesSchemaPsiParser, SchemaParserDefinition}

import scala.collection.JavaConversions._
import scala.collection.{GenTraversableOnce, mutable}

class EpigraphCompiler(
  private val sources: util.Collection[_ <: Source],
  private val dependencies: util.Collection[_ <: Source] = Collections.emptyList()
) {

  println(sources.map(_.name).mkString("Sources: [\n", ",\n", "\n]")) // TODO use log or remove
  println(dependencies.map(_.name).mkString("Dependencies: [\n", ",\n", "\n]")) // TODO use log or remove

  private val spd = new SchemaParserDefinition

  implicit val ctx: CContext = new CContext

  implicit private val PPConfig = EpigraphCompiler.PPConfig

  @throws[EpigraphCompilerException]("if compilation failed")
  def compile(): CContext = {

    import CPhase._


    // parse schema files

    ctx.phase(PARSE)

    val schemaFiles: Seq[SchemaFile] = parseSourceFiles(sources)
    val dependencySchemaFiles: Seq[SchemaFile] = parseSourceFiles(dependencies)

    handleErrors(1)


    // instantiate compiler schema files and resolve type references

    ctx.phase(RESOLVE_TYPEREFS)

    schemaFiles.map(sf => new CSchemaFile(sf, false)) // compiler schema file adds itself to ctx
    dependencySchemaFiles.map(sf => new CSchemaFile(sf, true))

    registerDefinedTypes()

    handleErrors(2)

    resolveTypeRefs()

    handleErrors(3)


    // apply supplements and compute supertypes //

    ctx.phase(COMPUTE_SUPERTYPES)

    applySupplementingTypeDefs()
    applySupplements() // FIXME track injecting `supplement`s
    computeSupertypes()

    handleErrors(4)


    // verify data types

    ctx.phase(INHERIT_FROM_SUPERTYPES)

    validateTagRefs()
    validateRecordFields()
    // ensure all anonymous parents are auto-created
    ctx.typeDefs.values() foreach (_.linearizedParents)
    ctx.anonListTypes.values() foreach (_.linearizedParents)
    ctx.anonMapTypes.values() foreach (_.linearizedParents)

    handleErrors(5)

    // compile resources

    ctx.phase(RESOURCES)

    parseResources()

    handleErrors(6)

    //printSchemaFiles(ctx.schemaFiles.values)

    ctx

  }

  private def parseSourceFiles(sources: util.Collection[_ <: Source]): Seq[SchemaFile] = {

    val schemaFiles: Seq[SchemaFile] = sources.par.flatMap{ source =>
      try {
        parseFile(source, spd) match {
          case sf: SchemaFile =>
            Seq(sf)
          case _ =>
            ctx.errors.add(CError(source.name, CErrorPosition.NA, "Couldn't parse"))
            Nil
        }
      } catch {
        case ioe: IOException =>
          ctx.errors.add(CError(source.name, CErrorPosition.NA, "Couldn't read"))
          Nil
      }
    }(collection.breakOut)

    schemaFiles.foreach{ sf => ctx.errors.addAll(ParseErrorsDumper.collectParseErrors(sf)) }

    schemaFiles
  }

  @throws[IOException]
  private def parseFile(source: Source, parserDefinition: ParserDefinition): PsiFile =
    LightPsi.parseFile(source.name, source.text, parserDefinition)

  private def registerDefinedTypes(): Unit = {
    ctx.schemaFiles.values.par foreach { csf =>
      csf.typeDefs foreach { ct =>
        val old: CTypeDef = ctx.typeDefs.putIfAbsent(ct.name, ct)
        if (old != null) ctx.errors.add(
          CError(
            csf.filename,
            csf.position(ct.name.psi),
            s"Type '${ct.name.name}' already defined at '${old.csf.location(old.psi.getQid)}'"
          )
        )
      }
    }
  }

  private def resolveTypeRefs(): Unit = ctx.schemaFiles.values.par foreach { csf =>
    csf.typerefs foreach {
      case ctr: CTypeDefRef =>
        @Nullable val refType = ctx.typeDefs.get(ctr.name)
        if (refType == null) {
          ctx.errors.add(CError(csf.filename, csf.position(ctr.name.psi), s"Not found: type '${ctr.name.name}'"))
        } else {
          ctr.resolveTo(refType)
        }
      case ctr: CAnonListTypeRef =>
        ctr.resolveTo(ctx.getOrCreateAnonListOf(ctr.name.elementDataType))
      case ctr: CAnonMapTypeRef =>
        ctr.resolveTo(ctx.getOrCreateAnonMapOf(ctr.name.keyTypeRef, ctr.name.valueDataType))
    }
  }

  private def applySupplementingTypeDefs(): Unit = ctx.typeDefs.elements foreach { typeDef =>
    typeDef.supplementedTypeRefs foreach { subRef =>
      subRef.resolved.injectedTypes.add(typeDef) // TODO capture injector source?
    }
  }

  private def applySupplements(): Unit = ctx.schemaFiles.values foreach { csf =>
    csf.supplements foreach { supplement =>
      val sup = supplement.sourceRef.resolved
      supplement.targetRefs foreach (_.resolved.injectedTypes.add(sup))
    }
  }

  /** Compute supertypes for all (named and anonymous) collected types */
  private def computeSupertypes(): Unit = {
    val visited = mutable.Stack[CType]()
    ctx.typeDefs.elements foreach { typeDef => typeDef.computeSupertypes(visited); assert(visited.isEmpty) }
    ctx.anonListTypes.values() foreach (anonListType => anonListType.linearizedParents)
    ctx.anonMapTypes.values() foreach (anonMapType => anonMapType.linearizedParents)
  }

  private def validateTagRefs(): Unit = ctx.schemaFiles.values foreach { csf =>
    csf.dataTypes foreach { cdt => cdt.effectiveDefaultTagName }
    // TODO: list element, map value, and field value tags?
  }

  private def validateRecordFields(): Unit = ctx.typeDefs.values foreach {
    case rt: CRecordTypeDef => rt.effectiveFields ne null
    case _ =>
  }

  private def parseResources(): Unit = {
    ctx.schemaFiles.values().par.foreach{ csf =>
      val typesResolver = new CTypesResolver(csf)
      val errors = new java.util.ArrayList[PsiProcessingError]

      try {
        val resourcesSchema = ResourcesSchemaPsiParser.parseResourcesSchema(csf.psi, typesResolver, errors)
        handlePsiErrors(csf, errors)
        ctx.resourcesSchemas.put(csf, resourcesSchema)
      } catch {
        case e: PsiProcessingException => handlePsiErrors(csf, e.errors())
      }
    }
  }

  private def handlePsiErrors(csf: CSchemaFile, psiErrors: Traversable[PsiProcessingError]): Unit = {
    lazy val reporter = ErrorReporter.reporter(csf)
    psiErrors.foreach{ e => reporter.error(e.message(), e.location()) }
  }

  @throws[EpigraphCompilerException]
  private def handleErrors(exitCode: Int): Unit = if (ctx.errors.nonEmpty) {
    EpigraphCompiler.renderErrors(ctx)
    throw new EpigraphCompilerException(exitCode.toString, ctx.errors, null)
  }


  private def printSchemaFiles(schemaFiles: GenTraversableOnce[CSchemaFile]): Unit = schemaFiles foreach pprint.pprintln

}

object EpigraphCompiler {
  //      import pprint.Config.Colors._
  implicit private val PPConfig = pprint.Config(
    width = 120, colors = pprint.Colors(fansi.Color.Green, fansi.Color.LightBlue)
  )

  def renderErrors(ctx: CContext): Unit = ctx.errors foreach pprint.pprintln
}


class EpigraphCompilerException(
  message: String,
  val errors: util.Collection[CError],
  cause: Throwable = null
) extends RuntimeException(message, cause)

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
import org.slf4s.Logging
import ws.epigraph.psi.{DefaultPsiProcessingContext, PsiProcessingContext, PsiProcessingError, PsiProcessingException}
import ws.epigraph.schema.parser.psi._
import ws.epigraph.schema.parser.{ResourcesSchemaPsiParser, SchemaParserDefinition}

import scala.collection.JavaConversions._
import scala.collection.{GenTraversableOnce, mutable}

class EpigraphCompiler(
  private val sources: util.Collection[_ <: Source],
  private val dependencies: util.Collection[_ <: Source] = Collections.emptyList()
) extends Logging {

  {
    val files = if (sources.size == 1) "file" else "files"
    val deps = if (dependencies.size == 1) "dependency" else "dependencies"
    log.info(s"Compiling ${sources.size} Epigraph source $files and ${dependencies.size} $deps")
    sources foreach { s => log.debug(s"Source: ${s.name}") }
    dependencies foreach { s => log.debug(s"Dependency: ${s.name}") }
  }

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

    // other types-related tasks

    ctx.phase(OTHER_TYPES)

    validateVarTypeTags()
    validateMapKeyTypes()

    handleErrors(6)

    // compile resources

    ctx.phase(RESOURCES)

    parseResources()

    handleErrors(7)

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

    private def validateVarTypeTags(): Unit = ctx.typeDefs.values().foreach{
      case vt: CVarTypeDef =>
        vt.declaredTags.foreach {tag =>
          tag.typeRef.resolved match {
            case dt: CDatumType =>
            case t => ctx.errors.add(
              CError(
                vt.csf.filename,
                tag.csf.position(tag.psi),
                s"Type `${t.name.name}` of tag `${tag.name}` in type `${vt.name.name}` is not a datum type"
              )
            )
          }
        }
      case _ =>
    }

    private def validateMapKeyTypes(): Unit = ctx.typeDefs.values().foreach{
    case md: CMapTypeDef =>
      md.keyTypeRef.resolved match {
        case kdt: CDatumType =>
          if (kdt.meta.isDefined)
            ctx.errors.add(
              CError(
                md.csf.filename,
                md.csf.position(md.psi),
                s"Map type '${md.name.name}' key type '${kdt.name.name}' should not have a meta-type"
              )
            )
        case kt => ctx.errors.add(
          CError(
            md.csf.filename,
            md.csf.position(md.psi),
            s"Map type '${md.name.name}' key type '${kt.name.name}' is not a datum type"
          )
        )
      }
    case _ =>
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
      val context: PsiProcessingContext = new DefaultPsiProcessingContext

      try {
        val resourcesSchema = ResourcesSchemaPsiParser.parseResourcesSchema(csf.psi, typesResolver, context)
        handlePsiErrors(csf, context.errors())
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

  private def printSchemaFiles(schemaFiles: GenTraversableOnce[CSchemaFile]): Unit = schemaFiles foreach {
    sf => log.debug(pprint.stringify(sf))
  }

}

object EpigraphCompiler extends Logging {

  //      import pprint.Config.Colors._
  implicit private val PPConfig = pprint.Config(
    width = 120, colors = pprint.Colors(fansi.Color.Green, fansi.Color.LightBlue)
  )

  def renderErrors(ctx: CContext): Unit = {
    // bring pretty printers into scope
    import CPrettyPrinters._
    ctx.errors foreach {err => log.error(pprint.stringify(err)) }
  }

}


class EpigraphCompilerException(
  message: String,
  val errors: util.Collection[CError],
  cause: Throwable = null
) extends RuntimeException(message, cause)

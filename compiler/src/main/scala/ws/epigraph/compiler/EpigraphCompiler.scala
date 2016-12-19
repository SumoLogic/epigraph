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
import com.intellij.psi.{PsiElement, PsiFile, PsiRecursiveElementWalkingVisitor}
import org.intellij.grammar.LightPsi
import org.jetbrains.annotations.Nullable
import ws.epigraph.schema.parser.EdlParserDefinition
import ws.epigraph.schema.parser.psi._

import scala.collection.JavaConversions._
import scala.collection.{GenTraversableOnce, mutable}

class EpigraphCompiler(
                        private val sources: util.Collection[_ <: Source],
                        private val dependencies: util.Collection[_ <: Source] = Collections.emptyList()
                      ) {

  println(sources.map(_.name).mkString("Sources: [\n", ",\n", "\n]")) // TODO use log or remove
  println(dependencies.map(_.name).mkString("Dependencies: [\n", ",\n", "\n]")) // TODO use log or remove

  private val spd = new EdlParserDefinition

  implicit val ctx: CContext = new CContext

  //      import pprint.Config.Colors._
  implicit private val PPConfig = pprint.Config(
    width = 120, colors = pprint.Colors(fansi.Color.Green, fansi.Color.LightBlue)
  )

  @throws[EpigraphCompilerException]("if compilation failed")
  def compile(): CContext = {

    import CPhase._


    // parse schema files

    ctx.phase(PARSE)

    val edlFiles: Seq[EdlFile] = parseSourceFiles(sources ++ dependencies)

    handleErrors(1)


    // instantiate compiler schema files and resolve type references

    ctx.phase(RESOLVE_TYPEREFS)

    edlFiles.map(new CEdlFile(_)) // compiler edl file adds itself to ctx
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


    //printEdlFiles(ctx.edlFiles.values)

    ctx

  }

  private def parseSourceFiles(sources: util.Collection[Source]): Seq[EdlFile] = {

    val edlFiles: Seq[EdlFile] = sources.par.flatMap{ source =>
      try {
        parseFile(source, spd) match {
          case sf: EdlFile =>
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

    edlFiles.foreach{ sf => ctx.errors.addAll(ParseErrorsDumper.collectParseErrors(sf)) }

    edlFiles
  }

  @throws[IOException]
  private def parseFile(source: Source, parserDefinition: ParserDefinition): PsiFile =
    LightPsi.parseFile(source.name, source.text, parserDefinition)

  private def registerDefinedTypes(): Unit = {
    ctx.edlFiles.values.par foreach { csf =>
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

      // extra pass of registering all type refs, should pick up all stuff from resource declarations
      csf.psi.accept(new PsiRecursiveElementWalkingVisitor() {
        override def visitElement(element: PsiElement): Unit = element match {
          case etr: EdlTypeRef => CTypeRef(csf, etr)
          case e => super.visitElement(e)
        }
      })
    }
  }

  private def resolveTypeRefs(): Unit = ctx.edlFiles.values.par foreach { csf =>
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

  private def applySupplements(): Unit = ctx.edlFiles.values foreach { csf =>
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

  private def validateTagRefs(): Unit = ctx.edlFiles.values foreach { csf =>
    csf.dataTypes foreach { cdt => cdt.effectiveDefaultTagName }
    // TODO: list element, map value, and field value tags?
  }

  private def validateRecordFields(): Unit = ctx.typeDefs.values foreach {
    case rt: CRecordTypeDef => rt.effectiveFields ne null
    case _ =>
  }

  @throws[EpigraphCompilerException]
  private def handleErrors(exitCode: Int): Unit = if (ctx.errors.nonEmpty) {
    renderErrors(ctx)
    throw new EpigraphCompilerException(exitCode.toString, ctx.errors, null)
  }

  private def renderErrors(ctx: CContext): Unit = ctx.errors foreach pprint.pprintln

  private def printEdlFiles(edlFiles: GenTraversableOnce[CEdlFile]): Unit = edlFiles foreach pprint.pprintln

}


class EpigraphCompilerException(
                                 message: String,
                                 val errors: util.Collection[CError],
                                 cause: Throwable = null
                               ) extends RuntimeException(message, cause)
/* Created by yegor on 6/8/16. */

package com.sumologic.epigraph.schema.compiler

import java.io.{File, IOException}
import java.util
import java.util.Collections

import com.intellij.lang.ParserDefinition
import com.intellij.psi.PsiFile
import com.sumologic.epigraph.schema.compiler.CPrettyPrinters._
import com.sumologic.epigraph.schema.parser.SchemaParserDefinition
import com.sumologic.epigraph.schema.parser.psi.SchemaFile
import com.sumologic.epigraph.util.JavaFunction
import org.intellij.grammar.LightPsi
import org.jetbrains.annotations.Nullable

import scala.collection.JavaConversions._
import scala.collection.{GenTraversableOnce, mutable}

class SchemaCompiler(
    private val sources: util.Collection[Source],
    private val dependencies: util.Collection[Source] = Collections.emptyList()
) {

  println(sources.map(_.name).mkString("Sources: [", ", ", "]")) // TODO use log or remove
  println(dependencies.map(_.name).mkString("Dependencies: [", ", ", "]")) // TODO use log or remove

  private val spd: SchemaParserDefinition = new SchemaParserDefinition

  implicit val ctx: CContext = new CContext

  //      import pprint.Config.Colors._
  implicit private val PPConfig = pprint.Config(
    width = 120, colors = pprint.Colors(fansi.Color.Green, fansi.Color.LightBlue)
  )

  @throws[SchemaCompilerException]("if compilation failed")
  def compile(): CContext = {

    import CPhase._

    ctx.phase(PARSE)

    val schemaFiles: Seq[SchemaFile] = parseSourceFiles(sources ++ dependencies)

    handleErrors(1)
    ctx.phase(RESOLVE_TYPEREFS)

    val cSchemaFiles: Seq[CSchemaFile] = schemaFiles.map(new CSchemaFile(_))
    //printSchemaFiles(cSchemaFiles)

    registerDefinedTypes(cSchemaFiles)
    //pprint.pprintln(ctx.types.keys.toSeq)

    resolveTypeRefs(cSchemaFiles)
    //pprint.pprintln(ctx.anonListTypes.toMap)
    //pprint.pprintln(ctx.anonMapTypes.toMap)

    handleErrors(2)
    ctx.phase(COMPUTE_SUPERTYPES)

    applySupplementingTypeDefs()
    applySupplements(cSchemaFiles) // FIXME track injecting `supplement`s
    computeSupertypes()
    //printSchemaFiles(cSchemaFiles)

    handleErrors(3)
    ctx.phase(INHERIT_FROM_SUPERTYPES)

    //printSchemaFiles(cSchemaFiles)
    handleErrors(4)

    ctx
  }

  // TODO below should be private/protected

  def parseSourceFiles(sources: util.Collection[Source]): Seq[SchemaFile] = {

    val schemaFiles: Seq[SchemaFile] = sources.par.flatMap { source =>
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
          ctx.errors.add(CError(source.name, CErrorPosition.NA, "File not found"))
          Nil
      }
    }(collection.breakOut)

    schemaFiles.foreach { sf => ctx.errors.addAll(ParseErrorsDumper.collectParseErrors(sf)) }

    schemaFiles
  }

  @throws[IOException]
  def parseFile(source: Source, parserDefinition: ParserDefinition): PsiFile =
    LightPsi.parseFile(source.name, source.text, parserDefinition)

  def registerDefinedTypes(cSchemaFiles: Seq[CSchemaFile]): Unit = {
    cSchemaFiles.par foreach { csf =>
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

  private val AnonListTypeConstructor = JavaFunction[CAnonListTypeName, CAnonListType](new CAnonListType(_))

  private val AnonMapTypeConstructor = JavaFunction[CAnonMapTypeName, CAnonMapType](new CAnonMapType(_))

  def resolveTypeRefs(cSchemaFiles: Seq[CSchemaFile]): Unit = cSchemaFiles.par foreach { csf =>
    csf.typerefs foreach {
      case ctr: CTypeDefRef =>
        @Nullable val refType = ctx.typeDefs.get(ctr.name)
        if (refType == null) {
          ctx.errors.add(CError(csf.filename, csf.position(ctr.psi), s"Not found: type '${ctr.name.name}'"))
        } else {
          ctr.resolveTo(refType)
        }
      case ctr: CAnonListTypeRef =>
        ctr.resolveTo(ctx.anonListTypes.computeIfAbsent(ctr.name, AnonListTypeConstructor))
      case ctr: CAnonMapTypeRef =>
        ctr.resolveTo(ctx.anonMapTypes.computeIfAbsent(ctr.name, AnonMapTypeConstructor))
    }
  }

  def applySupplementingTypeDefs(): Unit = ctx.typeDefs.elements foreach { typeDef =>
    typeDef.declaredSupplementees foreach { subRef =>
      subRef.resolved.injectedSupertypes.add(typeDef) // TODO capture injector source?
    }
  }

  def applySupplements(cSchemaFiles: Seq[CSchemaFile]): Unit = cSchemaFiles foreach { csf =>
    csf.supplements foreach { supplement =>
      val sup = supplement.sourceRef.resolved
      supplement.targetRefs foreach (_.resolved.injectedSupertypes.add(sup))
    }
  }

  def computeSupertypes(): Unit = {
    val visited = mutable.Stack[CTypeDef]()
    ctx.typeDefs.elements foreach { typeDef =>
      typeDef.computeSupertypes(visited); assert(visited.isEmpty)
    }
  }

  @throws[SchemaCompilerException]
  def handleErrors(exitCode: Int): Unit = { // FIXME it should not exit but return some error code
    if (ctx.errors.nonEmpty) {
      renderErrors(ctx)
      throw new SchemaCompilerException(exitCode.toString, ctx.errors, null)
    }
  }

  def renderErrors(ctx: CContext): Unit = {
    ctx.errors foreach pprint.pprintln
  }

  def printSchemaFiles(schemaFiles: GenTraversableOnce[CSchemaFile]): Unit = {
    schemaFiles foreach pprint.pprintln
  }

}

object SchemaCompiler {

  def main(args: Array[String]) {
    new SchemaCompiler(testPaths.map(path => new FileSource(new File(path)))).compile()
  }

  def testcompile: CContext = {
    new SchemaCompiler(testPaths.map(path => new FileSource(new File(path)))).compile()
  }

  val testPaths: Seq[String] = Seq(
    "epigraph-builtin-types/src/main/epigraph/epigraph/builtinTypes.esc",
    "epigraph-schema/src/main/epigraph/epigraph/schema/names.esc",
    "epigraph-schema/src/main/epigraph/epigraph/schema/types.esc",
    "epigraph-schema/src/main/epigraph/epigraph/schema/Documented.esc",
    "epigraph-schema/src/test/epigraph/example/abstract.esc",
    "epigraph-schema-compiler/src/test/epigraph/example/compilerExamples.esc"
    //"blah"
  )

}

class SchemaCompilerException(
    message: String,
    val errors: util.Collection[CError],
    cause: Throwable = null
) extends RuntimeException(message, cause)
/* Created by yegor on 6/8/16. */

package com.sumologic.epigraph.schema.compiler

import java.io.{File, IOException}

import com.intellij.lang.ParserDefinition
import com.intellij.openapi.util.io.FileUtil
import com.intellij.psi.PsiFile
import com.sumologic.epigraph.schema.compiler.CPrettyPrinters._
import com.sumologic.epigraph.schema.parser.SchemaParserDefinition
import com.sumologic.epigraph.schema.parser.psi.SchemaFile
import com.sumologic.epigraph.util.JavaFunction
import org.intellij.grammar.LightPsi
import org.jetbrains.annotations.Nullable

import scala.collection.JavaConversions._
import scala.collection.{GenTraversableOnce, mutable}

object SchemaCompilerMain {

  val paths: Seq[String] = Seq(
    "epi-builtin-types/src/main/schema/epigraph/builtinTypes.esc",
    "epi-schema/src/main/schema/epigraph/schema/names.esc",
    "epi-schema/src/main/schema/epigraph/schema/types.esc",
    "epi-schema/src/main/scala/epigraph/schema/Documented.esc",
    "epi-schema/src/test/schema/example/abstract.esc"
    , "epi-schema-compiler/src/test/schema/example/compilerExamples.esc"
    //"blah"
  )

  val spd: SchemaParserDefinition = new SchemaParserDefinition

  implicit val ctx: CContext = new CContext

  //      import pprint.Config.Colors._
  implicit private val PPConfig = pprint.Config(
    width = 120, colors = pprint.Colors(fansi.Color.Green, fansi.Color.LightBlue)
  )

  def main(args: Array[String]) {

    import CPhase._

    ctx.phase(PARSE)

    val schemaFiles: Seq[SchemaFile] = parseSourceFiles(paths.map(new File(_)))

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
  }

  def parseSourceFiles(files: Seq[File]): Seq[SchemaFile] = {

    val schemaFiles: Seq[SchemaFile] = files.par.flatMap { file =>
      try {
        parseFile(file, spd) match {
          case sf: SchemaFile =>
            Seq(sf)
          case _ =>
            ctx.errors.add(new CError(file.getCanonicalPath, CErrorPosition.NA, "Couldn't parse"))
            Nil
        }
      } catch {
        case ioe: IOException =>
          ctx.errors.add(new CError(file.getCanonicalPath, CErrorPosition.NA, "File not found"))
          Nil
      }
    }.seq

    schemaFiles.foreach { sf => ctx.errors.addAll(ParseErrorsDumper.collectParseErrors(sf)) }

    schemaFiles
  }

  @throws[IOException]
  def parseFile(file: File, parserDefinition: ParserDefinition): PsiFile = {
    val name: String = file.getCanonicalPath
    val text: String = FileUtil.loadFile(file)
    LightPsi.parseFile(name, text, parserDefinition)
  }

  def registerDefinedTypes(cSchemaFiles: Seq[CSchemaFile]): Unit = {
    cSchemaFiles.par foreach { csf =>
      csf.typeDefs foreach { ct =>
        val old: CTypeDef = ctx.typeDefs.putIfAbsent(ct.name, ct)
        if (old != null) ctx.errors.add(
          new CError(
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
          ctx.errors.add(new CError(csf.filename, csf.position(ctr.psi), s"Not found: type '${ctr.name.name}'"))
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

  def handleErrors(exitCode: Int): Unit = { // FIXME it should not exit but return some error code
    if (ctx.errors.nonEmpty) {
      renderErrors(ctx)
      System.exit(exitCode)
    }
  }

  def renderErrors(ctx: CContext): Unit = {
    ctx.errors foreach pprint.pprintln
  }

  def printSchemaFiles(schemaFiles: GenTraversableOnce[CSchemaFile]): Unit = {
    schemaFiles foreach pprint.pprintln
  }

}

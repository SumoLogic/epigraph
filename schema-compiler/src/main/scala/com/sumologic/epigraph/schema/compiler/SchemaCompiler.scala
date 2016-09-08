/* Created by yegor on 6/8/16. */

package com.sumologic.epigraph.schema.compiler

import java.io.{File, IOException}
import java.util
import java.util.Collections

import com.intellij.lang.ParserDefinition
import com.intellij.psi.PsiFile
import com.sumologic.epigraph.schema.compiler.CPrettyPrinters._
import com.sumologic.epigraph.util.JavaFunction
import io.epigraph.schema.parser.SchemaParserDefinition
import io.epigraph.schema.parser.psi.SchemaFile
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

  private val spd = new SchemaParserDefinition

  implicit val ctx: CContext = new CContext

  //      import pprint.Config.Colors._
  implicit private val PPConfig = pprint.Config(
    width = 120, colors = pprint.Colors(fansi.Color.Green, fansi.Color.LightBlue)
  )

  @throws[SchemaCompilerException]("if compilation failed")
  def compile(): CContext = {

    import CPhase._


    // parse schema files

    ctx.phase(PARSE)

    val schemaFiles: Seq[SchemaFile] = parseSourceFiles(sources ++ dependencies)

    handleErrors(1)


    // instantiate compiler schema files and resolve type references

    ctx.phase(RESOLVE_TYPEREFS)

    schemaFiles.map(new CSchemaFile(_)) // compiler schema file adds itself to ctx
    registerDefinedTypes()
    resolveTypeRefs()

    handleErrors(2)


    // apply supplements and compute supertypes //

    ctx.phase(COMPUTE_SUPERTYPES)

    applySupplementingTypeDefs()
    applySupplements() // FIXME track injecting `supplement`s
    computeSupertypes()

    handleErrors(3)


    // verify data types

    ctx.phase(INHERIT_FROM_SUPERTYPES)

    validateTagRefs()
    ctx.anonListTypes.values() foreach (anonListType => anonListType.linearizedParents)

    handleErrors(4)


    //printSchemaFiles(ctx.schemaFiles.values)

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

  def registerDefinedTypes(): Unit = {
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

  //private val AnonListTypeConstructor = JavaFunction[CAnonListTypeName, CAnonListType](new CAnonListType(_))

  private val AnonMapTypeConstructor = JavaFunction[CAnonMapTypeName, CAnonMapType](new CAnonMapType(_))

  def resolveTypeRefs(): Unit = ctx.schemaFiles.values.par foreach { csf =>
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
      //ctr.resolveTo(ctx.anonListTypes.computeIfAbsent(ctr.name, AnonListTypeConstructor))
      case ctr: CAnonMapTypeRef =>
        // FIXME same as above
        ctr.resolveTo(ctx.anonMapTypes.computeIfAbsent(ctr.name, AnonMapTypeConstructor))
    }
  }

  def applySupplementingTypeDefs(): Unit = ctx.typeDefs.elements foreach { typeDef =>
    typeDef.declaredSupplementees foreach { subRef =>
      subRef.resolved.injectedSupertypes.add(typeDef) // TODO capture injector source?
    }
  }

  def applySupplements(): Unit = ctx.schemaFiles.values foreach { csf =>
    csf.supplements foreach { supplement =>
      val sup = supplement.sourceRef.resolved
      supplement.targetRefs foreach (_.resolved.injectedSupertypes.add(sup))
    }
  }

  /** Compute supertypes for all (named and anonymous) collected types */
  def computeSupertypes(): Unit = {
    val visited = mutable.Stack[CTypeDef]()
    ctx.typeDefs.elements foreach { typeDef => typeDef.computeSupertypes(visited); assert(visited.isEmpty) }
    ctx.anonListTypes.values() foreach (anonListType => anonListType.linearizedParents)
    ctx.anonMapTypes.values() foreach (anonMapType => anonMapType.linearizedParents)
  }

  def validateTagRefs(): Unit = ctx.schemaFiles.values foreach { csf =>
    csf.dataTypes foreach { cdt => cdt.effectiveDefaultTagName }
    // TODO: list element, map value, and field value tags?
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
    "builtin-types-schema/src/main/epigraph/epigraph/builtinTypes.esc",
    "java/core/src/test/epigraph/com/example/person.esc",
    "java/core/src/test/epigraph/com/example/user.esc"
//    "schema-schema/src/main/epigraph/epigraph/schema/names.esc",
//    "schema-schema/src/main/epigraph/epigraph/schema/types.esc",
//    "schema-schema/src/main/epigraph/epigraph/schema/Documented.esc",
//    "schema-schema/src/test/epigraph/example/abstract.esc",
//    "schema-compiler/src/test/epigraph/example/compilerExamples.esc"
    //"blah"
  )

}

class SchemaCompilerException(
    message: String,
    val errors: util.Collection[CError],
    cause: Throwable = null
) extends RuntimeException(message, cause)
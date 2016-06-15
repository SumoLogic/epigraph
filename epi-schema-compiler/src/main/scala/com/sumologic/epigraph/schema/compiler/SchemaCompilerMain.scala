/* Created by yegor on 6/8/16. */

package com.sumologic.epigraph.schema.compiler

import java.io.File

import com.sumologic.epigraph.schema.parser.SchemaParserDefinition
import com.sumologic.epigraph.schema.parser.psi.SchemaFile
import org.intellij.grammar.LightPsi
import org.jetbrains.annotations.Nullable

import scala.collection.JavaConversions._


object SchemaCompilerMain {

  val paths: Seq[String] = Seq(
    "epi-builtin-types/src/main/schema/epigraph/builtinTypes.es",
    "epi-schema/src/main/schema/epigraph/schema/names.es",
    "epi-schema/src/main/schema/epigraph/schema/types.es",
    "epi-schema/src/main/scala/epigraph/schema/Documented.es",
    "epi-schema-compiler/src/test/schema/example/compilerExamples.es"
  )

  val spd: SchemaParserDefinition = new SchemaParserDefinition

  implicit val ctx: CContext = new CContext


  def main(args: Array[String]) {

    val files: Seq[File] = paths.map(new File(_))

    val schemaFiles: Seq[SchemaFile] = files.par.flatMap { file =>
      LightPsi.parseFile(file, spd) match {
        case sf: SchemaFile =>
          //println(DebugUtil.psiToString(sf, true, true).trim())
          Seq(sf)
        case _ =>
          ctx.errors.add(new CError(file.getCanonicalPath, CErrorPosition.NA, "Couldn't parse"))
          Nil
      }
    }.seq

    schemaFiles.foreach { sf => ctx.errors.addAll(ParseErrorsDumper.collectParseErrors(sf)) }

    if (ctx.errors.nonEmpty) {
      renderErrors(ctx)
      System.exit(1)
    }

    val cSchemaFiles: Seq[CSchemaFile] = schemaFiles.map(new CSchemaFile(_))

    cSchemaFiles foreach { csf =>
      print("...(" + csf.filename + ":0): ")
      //      import pprint.Config.Colors._
      implicit val PPConfig = pprint.Config(
        width = 120, colors = pprint.Colors(fansi.Color.Green, fansi.Color.LightBlue)
      )
      pprint.pprintln(csf.types)
    }

    cSchemaFiles.par foreach { csf =>
      csf.types foreach { ct =>
        val old: CType = ctx.types.putIfAbsent(ct.name, ct)
        if (old != null) ctx.errors.add(
          new CError(
            csf.filename,
            csf.lnu.pos(ct.name.psi.getTextRange.getStartOffset),
            s"Type '${ct.name.name}' already defined in '${old.csf.filename}'"
          )
        )
      }
    }

    //pprint.pprintln(ctx.types.keys.toSeq)

    cSchemaFiles.par foreach { csf =>
      csf.typerefs foreach { ctr =>
        @Nullable val refType = ctx.types.get(ctr.name)
        if (refType == null) {
          ctx.errors.add(
            new CError(
              csf.filename,
              csf.lnu.pos(ctr.psi.getTextRange.getStartOffset),
              s"Not found: type '${ctr.name.name}'"
            )
          )
        } else {
          ctr.resolveTo(refType)
        }

      }
    }

    if (ctx.errors.nonEmpty) {
      renderErrors(ctx)
      System.exit(1)
    }

  }

  def renderErrors(ctx: CContext): Unit = {
    ctx.errors foreach { cerr =>
      pprint.pprintln(cerr)
    }
  }

}

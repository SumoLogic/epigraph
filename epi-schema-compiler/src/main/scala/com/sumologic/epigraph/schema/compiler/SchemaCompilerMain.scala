/* Created by yegor on 6/8/16. */

package com.sumologic.epigraph.schema.compiler

import java.io.File

import com.sumologic.epigraph.schema.parser.SchemaParserDefinition
import com.sumologic.epigraph.schema.parser.psi.SchemaFile
import org.intellij.grammar.LightPsi

import scala.collection.mutable

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

  val supplements = mutable.LinearSeq()

  val types = mutable.Map[CTypeName, CType]()


  def main(args: Array[String]) {
    val files: Seq[File] = paths.map(new File(_))
    val schemaFiles: Seq[SchemaFile] = files.map { file =>
      LightPsi.parseFile(file, spd) match {
        case sf: SchemaFile => sf
        case _ => throw new RuntimeException(file.getPath)
      }
    }
    schemaFiles foreach { sf =>
      ParseErrorsDumper.printParseErrors(sf)
      print(sf.getName + ": ")
      //println(DebugUtil.psiToString(sf, true, true).trim())

      val csf: CSchemaFile = new CSchemaFile(sf)
      import pprint.Config.Colors._
//      implicit val PPConfig = pprint.Config(colors = pprint.Colors(fansi.Color.Green, fansi.Color.Blue))
      pprint.pprintln(csf.types)
      csf.types.foreach {
//        case rt: CRecordType => pprint.pprintln(rt)
        case mt: CMapType => pprint.pprintln(mt)
        case _ =>
      }

    }

//    println(types.mkString("{\n  ", ",\n  ", "\n}"))


  }

}

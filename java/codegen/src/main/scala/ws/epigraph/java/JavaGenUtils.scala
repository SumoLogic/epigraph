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

/* Created by yegor on 7/12/16. */

package ws.epigraph.java

import java.io.{BufferedWriter, OutputStream, OutputStreamWriter}
import java.nio.charset.{CharsetEncoder, StandardCharsets}
import java.nio.file.{Files, Path, Paths, StandardOpenOption}

import ws.epigraph.compiler.{CDatumType, CDatumTypeApiWrapper, CType, CTypeApiWrapper}
import ws.epigraph.lang.Qn
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.types.{DatumTypeApi, TypeApi}

import scala.collection.JavaConversions._

object JavaGenUtils {
  val builtInPrimitives = Map(
    "epigraph.Integer" -> "java.lang.Integer",
    "epigraph.Long" -> "java.lang.Long",
    "epigraph.Double" -> "java.lang.Double",
    "epigraph.Boolean" -> "java.lang.Boolean",
    "epigraph.String" -> "java.lang.String"
  )

  private val spacesCache = new scala.collection.mutable.HashMap[Int, String]

  val EmptyPath: Path = Paths.get("")

  val topLevelComment = sn""""""

  def spaces(i: Int): String = spacesCache.getOrElseUpdate(i, " " * i)

  def indentButFirstLine(s: String, indent: Int): String = indentButFirstLine(s, spaces(indent))

  def indentButFirstLine(s: String, indent: String): String =
    if (indent.isEmpty || !s.contains('\n')) s
    else s.lines.zipWithIndex.map{ case (l, i) => if (i == 0) l else indent + l }.mkString("\n")

  def indent(s: String, indent: Int): String =
    if (indent == 0) s
    else s.lines.map{ l => spaces(indent) + l }.mkString("\n")


  def fqnToPath(fqn: Qn): Path = if (fqn.isEmpty) EmptyPath else Paths.get(fqn.first, fqn.segments.tail: _*)

  def writeFile(
      root: Path,
      relativeFilePath: Path,
      content: String
  ): Unit = {
    val fullFilePath = root.resolve(relativeFilePath)
    //println(s"Writing to $fullFilePath")
    Files.createDirectories(fullFilePath.getParent)
    writeFile(fullFilePath, content)
  }

  private def writeFile(filePath: Path, content: String): Unit = {
    val encoder: CharsetEncoder = StandardCharsets.UTF_8.newEncoder // TODO figure out what needs to be closed
    val out: OutputStream = Files.newOutputStream(filePath, StandardOpenOption.CREATE_NEW)
    try {
      val writer: BufferedWriter = new BufferedWriter(new OutputStreamWriter(out, encoder))
      try writer.append(content) finally writer.close()
    } finally {
      out.close()
    }
  }

  def rmrf(path: Path, top: Path): Path = {
    //println(s"Removing $path")
    if (Files.exists(path)) {
      checkBounds(path, top)
      if (Files.isDirectory(path)) {
        val stream = Files.newDirectoryStream(path)
        try stream.foreach(rmrf(_, top)) finally stream.close()
      }
      Files.delete/*IfExists*/ (path)
    }
    path
  }

  def checkBounds(path: Path, top: Path): Unit = {
    val rpath = path.toRealPath()
    val rtop = top.toRealPath()
    if (!(rpath.startsWith(rtop) && rpath.getNameCount > rtop.getNameCount))
      throw new IllegalArgumentException(s"out of bounds! $path, $top")
  }

  def move(source: Path, target: Path, top: Path): Unit = {
    if (Files.exists(target)) {
      val tmp = rmrf(target.resolveSibling(target.getFileName.toString + "~old"), top)
      Files.move(target, tmp)
      Files.move(source, target)
      rmrf(tmp, top)
    } else {
      Files.move(source, target)
    }
  }

  def up(name: String): String = Character.toUpperCase(name.charAt(0)) + name.substring(1)

  def lo(name: String): String = Character.toLowerCase(name.charAt(0)) + name.substring(1)

//  def javadocLink(t: CType, currentType: CType): String =
//    s"{@link ${JavaGenNames.lqn(t, currentType)} ${JavaGenNames.ln(t)}}"

  def javadocLink(t: CType, namespace: Qn): String =
    s"{@link ${JavaGenNames.lqn2(t, namespace.toString)} ${JavaGenNames.ln(t)}}"

  def withParents(t: CType, trans: (String) => String = identity): String = {
    t.getLinearizedParentsReversed.map(" " + JavaGenNames.lqn(_, t, trans) + ",").mkString
  }

  def toCType(t: TypeApi): CType = t.asInstanceOf[CTypeApiWrapper].cType

  def toCType(t: DatumTypeApi): CDatumType = t.asInstanceOf[CDatumTypeApiWrapper].cType

//  def ?(arg: AnyRef, ifNotNull: => String, ifNull: => String): String = if (arg ne null) ifNotNull else ifNull

//  def ?(arg: GenTraversableOnce[_], ifNotNull: => String, ifNull: => String): String =
//    if (arg != null && arg.nonEmpty) ifNotNull else ifNull

//  def ?[A >: Null <: AnyRef, B](arg: A, ifNotNull: => B, ifNull: => B): B = if (arg ne null) ifNotNull else ifNull

  def generatedAnnotation(generator: Any): String =
    s"""@javax.annotation.Generated("${ Option(generator.getClass.getCanonicalName).getOrElse(generator.getClass.getName) }")"""
}

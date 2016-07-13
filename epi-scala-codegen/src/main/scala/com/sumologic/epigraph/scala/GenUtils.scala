/* Created by yegor on 7/12/16. */

package com.sumologic.epigraph.scala

import java.io.{BufferedWriter, OutputStream, OutputStreamWriter}
import java.nio.charset.{Charset, CharsetEncoder, StandardCharsets}
import java.nio.file.{Files, Path, Paths, StandardOpenOption}

import com.sumologic.epigraph.schema.parser.Fqn

import scala.collection.JavaConversions._

object GenUtils {

  val EmptyPath: Path = Paths.get("")

  def fqnToPath(fqn: Fqn): Path = if (fqn.isEmpty) EmptyPath else Paths.get(fqn.first, fqn.segments.tail: _*)

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

  def rmrf(path: Path): Path = {
    //println(s"Removing $path")
    if (!path.startsWith("target")) throw new IllegalArgumentException("too scary")
    if (Files.isDirectory(path)) {
      val stream = Files.newDirectoryStream(path)
      try stream.foreach(rmrf) finally stream.close()
    }
    Files.deleteIfExists(path)
    path
  }

  def move(source: Path, target: Path): Unit = {
    if (Files.exists(target)) {
      val tmp = rmrf(target.resolveSibling(target.getFileName.toString + "~old"))
      Files.move(target, tmp)
      Files.move(source, target)
      rmrf(tmp)
    } else {
      Files.move(source, target)
    }
  }

}

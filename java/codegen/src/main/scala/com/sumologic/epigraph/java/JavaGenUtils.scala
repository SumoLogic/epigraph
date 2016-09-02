/* Created by yegor on 7/12/16. */

package com.sumologic.epigraph.java

import java.io.{BufferedWriter, OutputStream, OutputStreamWriter}
import java.nio.charset.{Charset, CharsetEncoder, StandardCharsets}
import java.nio.file.{Files, Path, Paths, StandardOpenOption}

import io.epigraph.lang.Fqn

import scala.collection.JavaConversions._

object JavaGenUtils {

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

}

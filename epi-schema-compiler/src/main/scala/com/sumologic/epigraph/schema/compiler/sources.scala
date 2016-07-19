/* Created by yegor on 7/14/16. */

package com.sumologic.epigraph.schema.compiler

import java.io.{ByteArrayOutputStream, File, IOException, InputStream}
import java.nio.charset.{Charset, StandardCharsets}
import java.nio.file.{Files, Path}
import java.util.jar.{JarEntry, JarFile}

trait Source {

  def name: String

  def text: String

  protected def inputStreamToString(inputStream: InputStream, charset: Charset = StandardCharsets.UTF_8): String = {
    val baos: ByteArrayOutputStream = new ByteArrayOutputStream
    try {
      val buffer: Array[Byte] = new Array[Byte](1024)
      var length: Int = 0
      while ( {
        length = inputStream.read(buffer)
        length != -1
      }) baos.write(buffer, 0, length)

      baos.toString(charset.name)
    } finally {
      inputStream.close()
    }
  }

}

class PathSource(private val path: Path) extends Source {

//  println(s"PathSource: $path")

  override def name: String = path.normalize.toString

  override def text: String = inputStreamToString(Files.newInputStream(path))

}

class FileSource(private val file: File) extends Source {

//  println(s"FileSource: $file")

  override def name: String = file.getCanonicalPath

  override def text: String = inputStreamToString(Files.newInputStream(file.toPath))

}

class JarSource(private val jarFile: JarFile, private val jarEntry: JarEntry) extends Source {

  override val name: String = jarFile.getName + '/' + jarEntry.getName // TODO refine

//  println(s"JarSource: $name")

  @throws[IOException]
  override def text: String = inputStreamToString(jarFile.getInputStream(jarEntry))

}

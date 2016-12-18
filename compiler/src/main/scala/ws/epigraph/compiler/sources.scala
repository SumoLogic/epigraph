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

/* Created by yegor on 7/14/16. */

package ws.epigraph.compiler

import java.io.{ByteArrayOutputStream, File, IOException, InputStream}
import java.nio.charset.{Charset, StandardCharsets}
import java.nio.file.{Files, Path}
import java.util
import java.util.jar.{JarEntry, JarFile}
import java.util.regex.Pattern

trait Source {

  def name: String

  @throws[IOException]
  def text: String

  @throws[IOException]
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

  override def name: String = path.normalize.toString

  //println(getClass.getSimpleName + ": '" + name + "'")

  @throws[IOException]
  override def text: String = inputStreamToString(Files.newInputStream(path))

}

class FileSource(private val file: File) extends Source {

  override def name: String = file.getCanonicalPath

  //println(getClass.getSimpleName + ": '" + name + "'")

  @throws[IOException]
  override def text: String = inputStreamToString(Files.newInputStream(file.toPath))

}

class JarSource(private val jarFile: JarFile, private val jarEntry: JarEntry) extends Source {

  override val name: String = jarFile.getName + "!/" + jarEntry.getName

  //println(getClass.getSimpleName + ": '" + name + "'")

  @throws[IOException]
  override def text: String = inputStreamToString(jarFile.getInputStream(jarEntry))
}

object JarSource {

  import scala.collection.JavaConversions._
  import scala.collection.JavaConverters.asJavaIteratorConverter

  def allFiles(jarFile: JarFile, filenamePattern: Pattern, charset: Charset): util.Iterator[JarSource] =
    jarFile.entries()
    .filter(e => !e.isDirectory && filenamePattern.matcher(e.getName).matches())
    .map(e => new JarSource(jarFile, e))
    .asJava
}

class ResourceSource(private val resourcePath: String) extends Source {

  override val name: String = resourcePath

  //println(getClass.getSimpleName + ": '" + name + "'")

  @throws[IOException]
  override def text: String = {
    var stream = getClass.getResourceAsStream(resourcePath)

    if (stream == null && System.getProperty("junit.mode") == "true") // workaround for tests run from IDEA
      stream = getClass.getResourceAsStream(new File(".").getAbsolutePath + "/src/test/resources" + resourcePath)

    if (stream == null) throw new IOException(s"Resource '$resourcePath' not found")

    inputStreamToString(stream)
  }

}

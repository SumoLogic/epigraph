/* Created by yegor on 7/14/16. */

package ws.epigraph.schema.compiler

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
  override def text: String = inputStreamToString(getClass.getResourceAsStream(resourcePath)) // FIXME NPE

}

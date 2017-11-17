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

package ws.epigraph.java

import ws.epigraph.lang.Qn

import scala.annotation.tailrec

/**
 * Code fragment
 * todo doc
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
case class Fragment(text: String) extends AnyVal {
  // make it a value class?
  // http://docs.scala-lang.org/overviews/core/value-classes.html

  def length: Int = text.length

  def +(other: Fragment): Fragment = Fragment(
    if (text.isEmpty) other.text
    else if (other.text.isEmpty) text
    else text + other.text
  )

//  def +(other: Fragment): Fragment = Fragment(
//    if (text.isEmpty) other.text
//    else if (other.text.isEmpty) text
//    else {
//      if (text.endsWith("\n\n")) text + other.text
//      else if (text.endsWith("\n")) text + "\n" + other.text
//      else text + "\n\n" + other.text
//    }
//  )

  override def toString: String = text

  def interpolate(importsGenerator: List[String] => String = Fragment.javaImportsGenerator): String = {
    val t = interpolateEmptyLines(text)
    t
  }

  private def interpolateEmptyLines(s: String): String = {
    val ml = Fragment.emptyLine.text.length

    @tailrec
    def countNewLinesPrefix(k: String, idx: Int, cur: Int, max: Int): Int =
      if (idx >= k.length) cur
      else if (cur >= max) cur
      else if (k.charAt(idx) == '\n')
        countNewLinesPrefix(k, idx + 1, cur + 1, max)
      else if (k.startsWith(Fragment.emptyLine.text, idx))
        countNewLinesPrefix(k, idx + ml, cur + 2, max)
      else cur

    @tailrec
    def countNewLinesSuffix(k: String, idx: Int, cur: Int, max: Int): Int =
      if (idx <= 0) cur
      else if (cur >= max) cur
      else if (idx - 1 >= 0 && k.charAt(idx - 1) == '\n')
        countNewLinesSuffix(k, idx - 1, cur + 1, max)
      else if (idx - ml >= 0 && k.startsWith(Fragment.emptyLine.text, idx - ml))
        countNewLinesSuffix(k, idx - ml, cur + 2, max)
      else cur

    var t = "\n\n" + s + "\n\n"
    var idx = t.indexOf(Fragment.emptyLine.toString)

    while (idx >= 0) {

      val newLinesBefore = countNewLinesSuffix(t, idx, 0, 2)
      val newLinesAfter = countNewLinesPrefix(t, idx + Fragment.emptyLine.length, 0, 2)
      val numNewLines = Math.max(0, 2 - (newLinesBefore + newLinesAfter))
      val newLines = "\n" * numNewLines

      t = t.substring(0, idx) + newLines + t.substring(idx + ml)
      idx = t.indexOf(Fragment.emptyLine.toString, idx)
    }

    t.substring(2, t.length - 2)
  }
}

object Fragment {
  protected val sign = '\u00a7'

  val imports = Fragment(sign + "imports")

  def import_(short: String, fqn: Qn): Fragment = Fragment(s"${ sign }ref[$short,$fqn]")

  def import_(fqn: Qn): Fragment = {
    val csi = fqn.segments.indexWhere(_.charAt(0).isUpper)

    val ns = if (csi < 0) fqn else if (csi == fqn.size()) Qn.EMPTY else fqn.takeHeadSegments(csi)
    val shortClassName = if (csi < 0) null else if (csi == 0) fqn else fqn.removeHeadSegments(csi)

    import_(shortClassName.toString, fqn)
  }

  val emptyLine = Fragment(sign + "el")

  val javaImportsGenerator: List[String] => String = _.map(i => s"import $i;").mkString("\n")
}

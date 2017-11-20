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
 * todo this won't handle cases like "Map.@Nullalbe Entry"
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
case class Fragment(text: String) extends AnyVal {

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

  def interpolate(
    namespacesInScope: Set[Qn] = Fragment.javaNamespacesInScope,
    importsGenerator: List[String] => String = Fragment.javaImportsGenerator
  ): String = {
    var t = text
    t = interpolateImports(t, namespacesInScope, importsGenerator)
    t = Fragment.interpolateEmptyLines(t) // should be the last one
    t
  }

  private def interpolateImports(
    text: String,
    namespacesInScope: Set[Qn],
    importsGenerator: List[String] => String = Fragment.javaImportsGenerator
  ): String = {
    import scala.util.control.Breaks._

    var curText = text
    var curImportedShortNames: Set[String] = Fragment.getImplicitlyUsedImports(text, namespacesInScope)
    var curImportedFqns: Set[Qn] = Set()

    breakable {
      while (true) {
        val (newText, newImportedShortNames, newImportedFqns) =
          Fragment.resolveImportsOnce(curText, curImportedShortNames, namespacesInScope)

        if (newImportedShortNames.size == curImportedShortNames.size && newText == curText) {
          break
        }

        curText = newText
        curImportedShortNames = newImportedShortNames
        curImportedFqns = newImportedFqns
      }
    }

    val newImportsStmt = importsGenerator.apply(curImportedFqns.map(_.toString).toList.sorted)

    curText.replace(Fragment.imports.text, newImportsStmt)
  }

}

object Fragment {
  protected val sign = '\u00a7'

  val imports = Fragment(sign + "imports")

  def imp(fqn: Qn): Fragment = Fragment(s"${ sign }ref[$fqn]")

  def apply(fqn: Qn): Fragment = imp(fqn)

  def imp(fqn: String): Fragment = imp(Qn.fromDotSeparated(fqn))

  val empty: Fragment = Fragment("")

  val emptyLine: Fragment = Fragment(sign + "el")

  val javaImportsGenerator: List[String] => String = _.map(i => s"import $i;").mkString("\n")

  val javaNamespacesInScope: Set[Qn] = Set(Qn.fromDotSeparated("java.lang"))

  def join(fs: Iterable[Fragment], sep: Fragment): Fragment = Fragment(fs.mkString(sep.toString))

  /////

  private def shortName(fqn: Qn, removeTypeParams: Boolean): String = {
    val csi = fqn.segments.indexWhere(_.charAt(0).isUpper)

    val ns = if (csi < 0 || csi == fqn.size()) Qn.EMPTY else fqn.takeHeadSegments(csi)
    val shortClassName = if (csi < 0) null else if (csi == 0) fqn else fqn.removeHeadSegments(csi)

    if (shortClassName == null || shortClassName.isEmpty)
      throw new IllegalArgumentException(s"Can't determine short name for '$fqn'")

    val res = shortClassName.toString

    lazy val paramsStartAt = Set('[', '<').map(res.indexOf(_)).filter(_ >= 0)
    if (!removeTypeParams || paramsStartAt.isEmpty)
      res
    else
      res.substring(0, paramsStartAt.min)
  }

  private def namespace(fqn: Qn): Qn = {
    val csi = fqn.segments.indexWhere(_.charAt(0).isUpper)
    if (csi < 0 || csi == fqn.size()) Qn.EMPTY else fqn.takeHeadSegments(csi)
  }

  private def interpolateEmptyLines(s: String): String = {
    val ml = emptyLine.text.length

    @tailrec
    def countNewLinesPrefix(k: String, idx: Int, cur: Int, max: Int): Int =
      if (idx >= k.length) cur
      else if (cur >= max) cur
      else if (k.charAt(idx) == '\n')
        countNewLinesPrefix(k, idx + 1, cur + 1, max)
      else if (k.startsWith(emptyLine.text, idx))
        countNewLinesPrefix(k, idx + ml, cur + 2, max)
      else cur

    @tailrec
    def countNewLinesSuffix(k: String, idx: Int, cur: Int, max: Int): Int =
      if (idx <= 0) cur
      else if (cur >= max) cur
      else if (idx - 1 >= 0 && k.charAt(idx - 1) == '\n')
        countNewLinesSuffix(k, idx - 1, cur + 1, max)
      else if (idx - ml >= 0 && k.startsWith(emptyLine.text, idx - ml))
        countNewLinesSuffix(k, idx - ml, cur + 2, max)
      else cur

    var t = "\n\n" + s + "\n\n"
    var idx = t.indexOf(emptyLine.toString)

    while (idx >= 0) {

      val newLinesBefore = countNewLinesSuffix(t, idx, 0, 2)
      val newLinesAfter = countNewLinesPrefix(t, idx + emptyLine.length, 0, 2)
      val numNewLines = Math.max(0, 2 - (newLinesBefore + newLinesAfter))
      val newLines = "\n" * numNewLines

      t = t.substring(0, idx) + newLines + t.substring(idx + ml)
      idx = t.indexOf(emptyLine.toString, idx)
    }

    t.substring(2, t.length - 2)
  }


  private def collectRefs(text: String): Set[Qn] = {
    val refPattern = s"${ sign }ref\\[([^]$sign]+)\\]".r

    (for (m <- refPattern.findAllMatchIn(text)) yield m.group(1)).toSet.map(Qn.fromDotSeparated)
  }

  private def collectPotentialRefs(text: String): Set[Qn] = {
    val refPattern = s"${ sign }ref\\[([^]$sign\\[<]+)".r

    (for (m <- refPattern.findAllMatchIn(text)) yield m.group(1)).toSet.map(Qn.fromDotSeparated)
  }

  private def getImplicitlyUsedImports(text: String, namespacesInScope: Set[Qn]): Set[String] =
    collectPotentialRefs(text)
        .filter(qn => namespacesInScope.contains(namespace(qn)))
        .map(qn => shortName(qn, removeTypeParams = true))

  private def resolveImportsOnce(
    text: String,
    importedShortNames: Set[String],
    namespacesInScope: Set[Qn]
  ): (String, Set[String], Set[Qn]) = {

    def replaceRef(fqn: Qn, to: String, text: String): String = text.replace(imp(fqn).text, to)

    val refs: Set[Qn] = collectRefs(text)
    var processedRefs: Set[Qn] = Set()

    var newImportedShortNames: Set[String] = importedShortNames
    var importedFqns: Set[Qn] = Set()
    var t = text

    // first resolve all implicitly visible names
    for (ref <- refs) {
      val ns = namespace(ref)

      if (namespacesInScope.contains(ns)) {
        val shortWithParams = shortName(ref, removeTypeParams = false)
        val shortWithoutParams = shortName(ref, removeTypeParams = true)

        t = replaceRef(ref, shortWithParams, t)
        newImportedShortNames += shortWithoutParams
        processedRefs += ref
      }
    }

    for (ref <- refs; if !processedRefs.contains(ref)) {
      lazy val shortWithParams = shortName(ref, removeTypeParams = false)
      val shortWithoutParams = shortName(ref, removeTypeParams = true)

      if (!newImportedShortNames.contains(shortWithoutParams)) {
        t = replaceRef(ref, shortWithParams, t)
        importedFqns += ref
        newImportedShortNames += shortWithoutParams
      } else {
        t = replaceRef(ref, ref.toString, t)
      }
    }

    (t, newImportedShortNames, importedFqns)
  }

}

package ws.epigraph.schema.compiler

import com.intellij.psi.PsiElement
import org.jetbrains.annotations.Nullable

import scala.collection.mutable

/**
 * @author <a href="mailto:konstantin.sobolev.com">Konstantin Sobolev</a>
 */
class LineNumberUtil(text: String, tabWidth: Int = 2) {

  private case class Line(number: Int, startOffset: Int, endOffset: Int, text: String)

  private val lines = mutable.MutableList[Line]()

  {
    var offset = 0
    var lineStartOffset = 0
    var line = new StringBuilder
    var lineNumber = 1
    while (offset < text.length) {
      val ch = text.charAt(offset)

      if (ch == '\n') {
        lines += Line(lineNumber, lineStartOffset, offset, line.toString)
        lineStartOffset = offset + 1
        line = new StringBuilder
        lineNumber += 1
      } else {
        line.append(ch)
      }

      offset += 1
    }

    if (line.nonEmpty)
      lines += Line(lineNumber, lineStartOffset, offset, line.toString)
  }

  def pos(@Nullable psi: PsiElement): CErrorPosition =
    if (psi eq null) CErrorPosition.NA else pos(psi.getTextRange.getStartOffset)

  def pos(offset: Int): CErrorPosition = {
    lines.find(_.endOffset >= offset) match {
      case Some(line) => CErrorPosition(line.number, column(line, offset), Some(line.text))
      case None => CErrorPosition.NA
    }
  }

  @Deprecated
  def line(offset: Int): Int = {
    lines.find(_.endOffset >= offset) match {
      case None => -1
      case Some(line) => line.number
    }
  }

  @Deprecated
  def column(offset: Int): Int = {
    lines.find(_.endOffset >= offset) match {
      case None => 0
      case Some(line) => column(line, offset)
    }
  }

  def column(line: Line, offset: Int): Int = {
    val offsetInLine = offset - line.startOffset
    val linePrefix = expandTabs(line.text.substring(0, offsetInLine))
    // FIXME deal with (escape? remove?) other control characters here (or in constructor)?
    val numCrs = linePrefix.count(_ == '\r')
    1 + linePrefix.length - numCrs
  }

  def lineText(offset: Int, expandTabs: Boolean = true): Option[String] = {
    val text = lines.find(_.endOffset >= offset).map(_.text)
    if (expandTabs) text.map(this.expandTabs) else text
  }

  def expandTabs(text: String): String =
    if (!text.contains('\t')) text
    else {
      val line = new StringBuilder
      var columnNumber = 1
      var offset = 0

      while (offset < text.length) {
        val ch = text.charAt(offset)

        if (ch == '\t') {
          val nextTabStop: Int = (columnNumber / tabWidth) * tabWidth + tabWidth + 1
          val numSpaces = nextTabStop - columnNumber
          line.append(" " * numSpaces)
          columnNumber = nextTabStop
        } else {
          line.append(ch)
          columnNumber += 1
        }

        offset += 1
      }

      line.toString()
    }

}

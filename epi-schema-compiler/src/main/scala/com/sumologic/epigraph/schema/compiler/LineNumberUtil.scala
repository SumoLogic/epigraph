package com.sumologic.epigraph.schema.compiler

import scala.collection.mutable

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
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
      line.append(ch)

      if (ch == '\n') {
        lines += Line(lineNumber, lineStartOffset, offset, line.toString)
        lineStartOffset = offset + 1
        line = new StringBuilder
        lineNumber += 1
      }

      offset += 1
    }

    if (line.nonEmpty)
      lines += Line(lineNumber, lineStartOffset, offset, line.toString)
  }

  def line(offset: Int): Int = {
    lines.find(_.endOffset >= offset) match {
      case None => -1
      case Some(line) => line.number
    }
  }

  def column(offset: Int): Int = {
    lines.find(_.endOffset >= offset) match {
      case None => 0
      case Some(line) =>
        val offsetInLine = offset - line.startOffset
        val linePrefix = line.text.substring(0, offsetInLine)
        val numCrs = linePrefix.count(_ == '\r')
        val numTabs = linePrefix.count(_ == '\t')
        1 + linePrefix.length - numCrs - numTabs + numTabs * tabWidth
    }
  }

}
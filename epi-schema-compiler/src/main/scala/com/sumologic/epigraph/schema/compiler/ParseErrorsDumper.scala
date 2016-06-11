package com.sumologic.epigraph.schema.compiler

import com.intellij.psi.{PsiErrorElement, PsiRecursiveElementWalkingVisitor}
import com.sumologic.epigraph.schema.parser.psi.SchemaFile

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
object ParseErrorsDumper {
  // tweak to your liking

  def printParseErrors(file: SchemaFile): Unit = {
    val fileName = file.getName
    lazy val lineNumberUtil = new LineNumberUtil(file.getText, 2)

    val visitor = new PsiRecursiveElementWalkingVisitor() {
      override def visitErrorElement(element: PsiErrorElement): Unit = {
        val errorOffset = element.getTextRange.getStartOffset
        val line = lineNumberUtil.line(errorOffset)
        val column = lineNumberUtil.column(errorOffset)

        println(element.getErrorDescription + " at " + fileName + ':' + line + ':' + column)
      }
    }
  }
}

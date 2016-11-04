package ws.epigraph.schema.compiler

import com.intellij.psi.{PsiErrorElement, PsiRecursiveElementWalkingVisitor}
import ws.epigraph.schema.parser.psi.SchemaFile

import scala.collection.mutable

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
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
    file.accept(visitor)
  }

  def collectParseErrors(sf: SchemaFile, tabWidth: Int = 2): Seq[CError] = {
    val errors = mutable.Buffer[CError]()
    val fileName = sf.getName
    lazy val lineNumberUtil = new LineNumberUtil(sf.getText, tabWidth)

    val visitor = new PsiRecursiveElementWalkingVisitor() {
      override def visitErrorElement(element: PsiErrorElement): Unit = {
        errors += new CError(fileName, lineNumberUtil.pos(element), element.getErrorDescription)
      }
    }
    sf.accept(visitor)
    errors
  }

}

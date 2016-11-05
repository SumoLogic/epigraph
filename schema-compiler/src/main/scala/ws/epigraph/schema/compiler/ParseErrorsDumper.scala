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

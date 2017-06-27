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

package ws.epigraph.compiler

import com.intellij.psi.PsiElement
import ws.epigraph.lang.TextLocation
import ws.epigraph.psi.EpigraphPsiUtil

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
trait ErrorReporter {
  def message(message: String, location: TextLocation, level: CMessageLevel)

  def error(message: String, location: TextLocation): Unit = this.message(message, location, CMessageLevel.Error)
  def error(message: String, psi: PsiElement): Unit = error(message, EpigraphPsiUtil.getLocation(psi))

  def warning(message: String, location: TextLocation): Unit = this.message(message, location, CMessageLevel.Warning)
  def warning(message: String, psi: PsiElement): Unit = warning(message, EpigraphPsiUtil.getLocation(psi))
}

object ErrorReporter {
  private def cErrorPosition(csf: CSchemaFile, location: TextLocation): CMessagePosition =
    if (location == TextLocation.UNKNOWN)
      CMessagePosition.NA
    else if (location.startLine() == location.endLine())
      csf.lnu.pos(location.startOffset(), location.endOffset() - location.startOffset())
    else
      csf.lnu.pos(location.startOffset())

  def reporter(csf: CSchemaFile)(implicit ctx: CContext): ErrorReporter =
    new ErrorReporter {
      override def message(msg: String, location: TextLocation, level: CMessageLevel): Unit = {
        val errorPosition = cErrorPosition(csf, location)
        ctx.errors.add(new CMessage(location.fileName(), errorPosition, msg, level))
      }
    }

  def reporter(csfm: Map[String, CSchemaFile])(implicit ctx: CContext): ErrorReporter =
    new ErrorReporter {
      override def message(msg: String, location: TextLocation, level: CMessageLevel): Unit = {

        val errorPosition: CMessagePosition =
          if (location == TextLocation.UNKNOWN)
            CMessagePosition.NA
          else
            csfm.get(location.fileName()).map(csf => cErrorPosition(csf, location)).getOrElse(CMessagePosition.NA)

        ctx.errors.add(new CMessage(location.fileName(), errorPosition, msg, level))

      }
    }
}

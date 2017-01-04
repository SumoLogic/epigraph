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
  def error(message: String, location: TextLocation)
  def error(message: String, psi: PsiElement): Unit = error(message, EpigraphPsiUtil.getLocation(psi))
}

object ErrorReporter {
  def reporter(csf: CSchemaFile)(implicit ctx: CContext): ErrorReporter =
    new ErrorReporter {
      override def error(message: String, location: TextLocation): Unit = {

        val errorPosition: CErrorPosition =
          if (location == TextLocation.UNKNOWN) CErrorPosition.NA
          else csf.lnu.pos(location.startOffset())


        ctx.errors.add(CError(csf.filename, errorPosition, message))
      }
    }
}

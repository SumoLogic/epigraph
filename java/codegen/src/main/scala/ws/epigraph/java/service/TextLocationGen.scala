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

package ws.epigraph.java.service

import ws.epigraph.lang.TextLocation

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class TextLocationGen(loc: TextLocation) extends ServiceObjectGen[TextLocation](loc) {
  override protected def generateObject(ctx: ServiceGenContext): String =
    if (loc.equals(TextLocation.UNKNOWN)) "TextLocation.UNKNOWN"
    else
      s"new TextLocation(${normalize(loc.startOffset())}, ${normalize(loc.startLine())}, ${normalize(loc.endOffset())}, ${normalize(loc.endLine())}, ${normalize(loc.fileName())})"

  private def normalize(pos: Int): String =
    if (pos == TextLocation.UNKNOWN_POSITION) "TextLocation.UNKNOWN_POSITION" else pos.toString

  private def normalize(filename: String): String =
    if (filename == null) null
    else s""""$filename""""
}

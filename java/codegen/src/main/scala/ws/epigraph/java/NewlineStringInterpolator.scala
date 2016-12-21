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

/* Created by yegor on 7/21/16. */

package ws.epigraph.java

import scala.annotation.tailrec

object NewlineStringInterpolator {

  final class TextToIndent(val s: String)

  def i(s: String) = new TextToIndent(s)

  implicit class NewlineHelper(private val sc: StringContext) extends AnyVal {

    def sn(args: Any*): String = sc.standardInterpolator(
      treatEscapesWithNewline,
      indentTexts(sc, args)
    )

  }

  private def indentTexts(sc: StringContext, args: Seq[Any]): Seq[Any] =
    if (args.exists{ _.isInstanceOf[TextToIndent] }) {

      def getIndent(s: String) = s.lines.toStream.last.length // this is crude, improve as needed

      def indentText(t: TextToIndent, i: Int) = JavaGenUtils.indentButFirstLine(t.s, i)

      val r = sc.parts.zip(args).map{
        case (s: String, t: TextToIndent) => indentText(t, getIndent(s))
        case (_, x) => x
      }
      r
    } else args

  private def treatEscapesWithNewline(str: String): String = {

    val len = str.length

    // replace escapes with given first escape
    def replace(first: Int): String = {

      val sb = new java.lang.StringBuilder

      // append replacement starting at index `i`, with `next` backslash
      @tailrec def loop(i: Int, next: Int): String = {
        if (next >= 0) {
          if (next > i) sb.append(str, i, next)
          var idx = next + 1
          if (idx >= len) throw new StringContext.InvalidEscapeException(str, next)
          val c = str(idx) match {
            case 'b' => '\b'
            case 't' => '\t'
            case 'n' => '\n'
            case 'f' => '\f'
            case 'r' => '\r'
            case '"' => '"'
            case '\'' => '\''
            case '\\' => '\\'
            case o if '0' <= o && o <= '7' =>
              val leadch = str(idx)
              var oct = leadch - '0'
              idx += 1
              if (idx < len && '0' <= str(idx) && str(idx) <= '7') {
                oct = oct * 8 + str(idx) - '0'
                idx += 1
                if (idx < len && leadch <= '3' && '0' <= str(idx) && str(idx) <= '7') {
                  oct = oct * 8 + str(idx) - '0'
                  idx += 1
                }
              }
              idx -= 1   // retreat
              oct.toChar
            case '\n' => -1
            case _ => throw new StringContext.InvalidEscapeException(str, next)
          }
          idx += 1       // advance
          if (c != -1) sb append c
          loop(idx, str.indexOf('\\', idx))
        } else {
          if (i < len) sb.append(str, i, len)
          sb.toString
        }
      }

      loop(0, first)

    }

    str indexOf '\\' match {
      case -1 => str
      case i => replace(i)
    }

  }

}

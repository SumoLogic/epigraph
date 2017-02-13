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

  sealed trait TextInterpolator {
    def interpolate(text: String): String = text
  }

  final class PrefixTextIndent(val arg: String) extends TextInterpolator {
    private def getIndent(prefix: String) = prefix.lines.toStream.last

    override def interpolate(text: String): String = JavaGenUtils.indentButFirstLine(arg, getIndent(text))
  }

  final class FixedTextIndent(val indentSpaces: Int, val arg: String) extends TextInterpolator {
    private def getIndent(prefix: String) = " " * indentSpaces

    override def interpolate(text: String): String = JavaGenUtils.indentButFirstLine(arg, getIndent(text))
  }

  def i(s: String) = new PrefixTextIndent(s)

  def sp(spaces: Int, s: String) = new FixedTextIndent(spaces, s)

  implicit class NewlineHelper(private val sc: StringContext) extends AnyVal {

    def sn(args: Any*): String = sc.standardInterpolator(treatEscapesWithNewline, indentTexts(sc, args))

  }

  private def indentTexts(sc: StringContext, args: Seq[Any]): Seq[Any] =
    if (args.exists{_.isInstanceOf[TextInterpolator]}) {
      sc.parts.zip(args).map{
        case (s: String, t: TextInterpolator) => t.interpolate(s)
        case (_, x) => x
      }
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
            case 's' => -2 //  \s = ensure preceding and following texts are separated by at least two newlines
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
          c match {
            case -2 =>
              val existingNewlines = List(
                sb.length() > 0 && (sb.charAt(sb.length() - 1) == '\n'),
                sb.length() > 1 && (sb.charAt(sb.length() - 2) == '\n'),
                idx < str.length && (str.charAt(idx) == '\n'),
                idx + 1 < str.length && (str.charAt(idx + 1) == '\n')
              ).count(identity[Boolean])

              sb append ("\n" * Math.max(0, 2 - existingNewlines))
            case -1 =>
            case _ => sb append c
          }
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

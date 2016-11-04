/* Created by yegor on 7/21/16. */

package ws.epigraph.scala

import scala.annotation.tailrec

object NewlineStringInterpolator {

  implicit class NewlineHelper(private val sc: StringContext) extends AnyVal {

    def sn(args: Any*): String = sc.standardInterpolator(treatEscapesWithNewline, args)

  }

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

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

package ws.epigraph.compiler

import org.scalatest.{FlatSpec, Matchers}

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class LineNumberUtilSpec extends FlatSpec with Matchers {
  "LineNumberUtil" should "count line and column numbers" in {
    val u = new LineNumberUtil("ab\tc\ndef\r\nhij", 2)

    // 'a'
    u.line(0) should be(1)
    u.column(0) should be(1)

    // 'b'
    u.line(1) should be(1)
    u.column(1) should be(2)

    // '\t'
    u.line(2) should be(1)
    u.column(2) should be(3)

    // 'c'
    u.line(3) should be(1)
    u.column(3) should be(5)

    // '\n'
    u.line(4) should be(1)
    u.column(4) should be(6)

    // 'd'
    u.line(5) should be(2)
    u.column(5) should be(1)

    // 'e'
    u.line(6) should be(2)
    u.column(6) should be(2)

    // 'f'
    u.line(7) should be(2)
    u.column(7) should be(3)

    // '\r'
    u.line(8) should be(2)
    u.column(8) should be(4)

    // '\n'
    u.line(9) should be(2)
    u.column(9) should be(4) // still 4, \r is considered to be zero-width

    // 'h'
    u.line(10) should be(3)
    u.column(10) should be(1)

    // 'i'
    u.line(11) should be(3)
    u.column(11) should be(2)

    // 'j'
    u.line(12) should be(3)
    u.column(12) should be(3)
  }

  it should "correctly expand tabs" in {
    val u = new LineNumberUtil("a\tb", 8)
    u.lineText(1, expandTabs = true) should be (Some("a       b"))
  }
}

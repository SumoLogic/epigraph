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

package ws.epigraph.java

import org.scalatest.{FlatSpec, Matchers}
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.NewlineStringInterpolator.i

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class InterpolatorSpec extends FlatSpec with Matchers {
  "NS Interpolator" should "correctly treat new lines" in {
//    val s = /*@formatter:off*/sn"""\
//foo
//bar
//""" //@formatter:on
//    s shouldEqual "foo\nbar\n"
  }

  it should "insert proper indents" in {
    val s = /*@formatter:off*/sn"""\
foo
  bar
""" //@formatter:on

    val w = /*@formatter:off*/sn"""\
  baz
  ${i(s)}
""" //@formatter:on

    w shouldEqual "  baz\n  foo\n    bar\n"
  }

}

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

package ws.epigraph.java

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpec, Matchers}

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@RunWith(classOf[JUnitRunner])
class FragmentSpec extends FlatSpec with Matchers {
  "Fragment" should "correctly interpolate empty lines" in {

    Fragment.emptyLine.interpolate() shouldEqual ""

    (Fragment.emptyLine + Fragment("foo")).interpolate() shouldEqual "foo"

    (Fragment.emptyLine + Fragment.emptyLine + Fragment("foo")).interpolate() shouldEqual "foo"

    (Fragment("foo") + Fragment.emptyLine).interpolate() shouldEqual "foo"

    (Fragment("foo") + Fragment.emptyLine + Fragment.emptyLine).interpolate() shouldEqual "foo"

    (Fragment("foo") + Fragment.emptyLine + Fragment("\n") + Fragment.emptyLine).interpolate() shouldEqual "foo\n"

    (Fragment("foo") + Fragment.emptyLine + Fragment("bar")).interpolate() shouldEqual "foo\n\nbar"

    (Fragment("foo\n") + Fragment.emptyLine + Fragment("bar")).interpolate() shouldEqual "foo\n\nbar"

    (Fragment("foo\n\n") + Fragment.emptyLine + Fragment("bar")).interpolate() shouldEqual "foo\n\nbar"

    (Fragment("foo") + Fragment.emptyLine + Fragment("\nbar")).interpolate() shouldEqual "foo\n\nbar"

    (Fragment("foo") + Fragment.emptyLine + Fragment("\n\nbar")).interpolate() shouldEqual "foo\n\nbar"

    (Fragment("foo\n") + Fragment.emptyLine + Fragment("\nbar")).interpolate() shouldEqual "foo\n\nbar"

    (Fragment("foo\n") + Fragment.emptyLine + Fragment("\n")).interpolate() shouldEqual "foo\n\n"

    (Fragment("foo") + Fragment.emptyLine + Fragment("\n")).interpolate() shouldEqual "foo\n"

    (Fragment("foo") + Fragment.emptyLine + Fragment("\n\n")).interpolate() shouldEqual "foo\n\n"

    (Fragment("foo") + Fragment.emptyLine + Fragment("\n\n\n")).interpolate() shouldEqual "foo\n\n\n"

    (Fragment("\n") + Fragment.emptyLine + Fragment("\nbar")).interpolate() shouldEqual "\n\nbar"

    (Fragment("\n") + Fragment.emptyLine + Fragment("bar")).interpolate() shouldEqual "\nbar"

    (Fragment("\n\n") + Fragment.emptyLine + Fragment("bar")).interpolate() shouldEqual "\n\nbar"

    (Fragment("\n\n\n") + Fragment.emptyLine + Fragment("bar")).interpolate() shouldEqual "\n\n\nbar"

    (Fragment("foo") + Fragment.emptyLine + Fragment.emptyLine + Fragment("bar")).interpolate() shouldEqual "foo\n\nbar"

    (Fragment("foo") + Fragment.emptyLine + Fragment("\n") + Fragment.emptyLine + Fragment("bar")).interpolate() shouldEqual "foo\n\nbar"
  }
}

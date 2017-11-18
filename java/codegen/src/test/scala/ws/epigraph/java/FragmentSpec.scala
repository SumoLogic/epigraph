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
    import ws.epigraph.java.Fragment.emptyLine

    emptyLine.interpolate() shouldEqual ""
    (emptyLine + Fragment("foo")).interpolate() shouldEqual "foo"
    (emptyLine + emptyLine + Fragment("foo")).interpolate() shouldEqual "foo"
    (Fragment("foo") + emptyLine).interpolate() shouldEqual "foo"
    (Fragment("foo") + emptyLine + emptyLine).interpolate() shouldEqual "foo"
    (Fragment("foo") + emptyLine + Fragment("\n") + emptyLine).interpolate() shouldEqual "foo\n"
    (Fragment("foo") + emptyLine + Fragment("bar")).interpolate() shouldEqual "foo\n\nbar"
    (Fragment("foo\n") + emptyLine + Fragment("bar")).interpolate() shouldEqual "foo\n\nbar"
    (Fragment("foo\n\n") + emptyLine + Fragment("bar")).interpolate() shouldEqual "foo\n\nbar"
    (Fragment("foo") + emptyLine + Fragment("\nbar")).interpolate() shouldEqual "foo\n\nbar"
    (Fragment("foo") + emptyLine + Fragment("\n\nbar")).interpolate() shouldEqual "foo\n\nbar"
    (Fragment("foo\n") + emptyLine + Fragment("\nbar")).interpolate() shouldEqual "foo\n\nbar"
    (Fragment("foo\n") + emptyLine + Fragment("\n")).interpolate() shouldEqual "foo\n\n"
    (Fragment("foo") + emptyLine + Fragment("\n")).interpolate() shouldEqual "foo\n"
    (Fragment("foo") + emptyLine + Fragment("\n\n")).interpolate() shouldEqual "foo\n\n"
    (Fragment("foo") + emptyLine + Fragment("\n\n\n")).interpolate() shouldEqual "foo\n\n\n"
    (Fragment("\n") + emptyLine + Fragment("\nbar")).interpolate() shouldEqual "\n\nbar"
    (Fragment("\n") + emptyLine + Fragment("bar")).interpolate() shouldEqual "\nbar"
    (Fragment("\n\n") + emptyLine + Fragment("bar")).interpolate() shouldEqual "\n\nbar"
    (Fragment("\n\n\n") + emptyLine + Fragment("bar")).interpolate() shouldEqual "\n\n\nbar"
    (Fragment("foo") + emptyLine + emptyLine + Fragment("bar")).interpolate() shouldEqual "foo\n\nbar"
    (Fragment("foo") + emptyLine + Fragment("\n") + emptyLine + Fragment("bar")).interpolate() shouldEqual "foo\n\nbar"
  }

//  it should "create fragments with correct short names" in {
//    Fragment.import_(Qn.fromDotSeparated("Bar")).text shouldEqual "§ref[Bar,Bar]"
//    Fragment.import_(Qn.fromDotSeparated("foo.Bar")).text shouldEqual "§ref[Bar,foo.Bar]"
//    Fragment.import_(Qn.fromDotSeparated("foo.Bar[foo.Baz]")).text shouldEqual "§ref[Bar[foo.Baz],foo.Bar[foo.Baz]]"
//  }

  it should "correctly resolve imports" in {
    import ws.epigraph.java.Fragment.{emptyLine, imp, imports}

    Fragment(
      s"""
         |${ imports }${ emptyLine }hello world
       """.stripMargin.trim
    ).interpolate() shouldEqual "hello world"

    Fragment(
      s"""
         |${ imports }${ emptyLine }${ imp("java.lang.String") }
       """.stripMargin.trim
    ).interpolate() shouldEqual "String"

    Fragment(
      s"""
         |${ imports }${ emptyLine }${ imp("java.lang.String") }
         |${ imp("foo.String") }
       """.stripMargin.trim
    ).interpolate() shouldEqual "String\nfoo.String"

    Fragment(
      s"""
         |${ imports }${ emptyLine }${ imp("foo.String") }
         |${ imp("java.lang.String") }
       """.stripMargin.trim
    ).interpolate() shouldEqual "foo.String\nString"

    Fragment(
      s"""
         |${ imports }${ emptyLine }${ imp(s"java.lang.String<${ imp("foo.String") }>") }
       """.stripMargin.trim
    ).interpolate() shouldEqual "String<foo.String>"

    Fragment(
      s"""
         |${ imports }${ emptyLine }${ imp(s"foo.String<${ imp("java.lang.String") }>") }
       """.stripMargin.trim
    ).interpolate() shouldEqual "foo.String<String>"
  }

}

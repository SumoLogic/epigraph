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
import ws.epigraph.lang.Qn

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@RunWith(classOf[JUnitRunner])
class ImportManagerSpec extends FlatSpec with Matchers {
  "ImportManager" should "correctly handle java.lang" in {
    val im = new ImportManager(Qn.fromDotSeparated("a.b"))
    val o = im.use("java.lang.Object")
    im.close()
    o.toString shouldEqual "Object"
    im.imports shouldBe empty
  }

  it should "prefer same namespace objects to java.lang" in {
    val im = new ImportManager(Qn.fromDotSeparated("a.b"))
    val so = im.use("a.b.Object")
    val o = im.use("java.lang.Object")
    im.close()
    so.toString shouldEqual "Object"
    o.toString shouldEqual "java.lang.Object"
    im.imports shouldBe empty
  }

  it should "resolve conflicting imports" in {
    val im = new ImportManager(Qn.fromDotSeparated("a.b"))
    val foo1 = im.use("x.Foo")
    val foo2 = im.use("y.Foo")
    im.close()
    foo1.toString shouldEqual "Foo"
    foo2.toString shouldEqual "y.Foo"
    im.imports shouldEqual Set("x.Foo")
  }

  it should "resolve imports conflicting with current namespace" in {
    val im = new ImportManager(Qn.fromDotSeparated("a.b"))
    val foo1 = im.use("x.Foo")
    val foo2 = im.use("y.Foo")
    val foo3 = im.use("a.b.Foo")
    im.close()
    foo1.toString shouldEqual "x.Foo"
    foo2.toString shouldEqual "y.Foo"
    foo3.toString shouldEqual "Foo"
    im.imports shouldBe empty
  }
}

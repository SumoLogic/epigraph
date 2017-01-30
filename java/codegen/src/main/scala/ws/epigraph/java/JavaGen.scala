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

/* Created by yegor on 7/11/16. */

package ws.epigraph.java

import java.nio.file.Path

import ws.epigraph.compiler._
import ws.epigraph.java.JavaGenNames._

import scala.collection.JavaConversions._

abstract class JavaGen[From >: Null <: AnyRef](protected val ctx: GenContext) {

  protected def relativeFilePath: Path

  protected def generate: String

  def writeUnder(sourcesRoot: Path): Unit = {
    //System.out.println("Writing to '" + relativeFilePath + "'")
    JavaGenUtils.writeFile(sourcesRoot, relativeFilePath, generate)
  }

  def withParents(t: CType, trans: (String) => String = identity): String = {
    t.getLinearizedParentsReversed.map(" " + lqn(_, t, trans) + ",").mkString
  }

//  def ?(arg: AnyRef, ifNotNull: => String, ifNull: => String): String = if (arg ne null) ifNotNull else ifNull

//  def ?(arg: GenTraversableOnce[_], ifNotNull: => String, ifNull: => String): String =
//    if (arg != null && arg.nonEmpty) ifNotNull else ifNull

//  def ?[A >: Null <: AnyRef, B](arg: A, ifNotNull: => B, ifNull: => B): B = if (arg ne null) ifNotNull else ifNull

}

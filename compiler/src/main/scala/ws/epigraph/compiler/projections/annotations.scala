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

package ws.epigraph.compiler.projections

import ws.epigraph.lang.TextLocation

class Annotation(
  val name: String,
  val value: CDataValue,
  val location: TextLocation
) {

  def canEqual(other: Any): Boolean = other.isInstanceOf[Annotation]

  override def equals(other: Any): Boolean = other match {
    case that: Annotation => (that canEqual this) && name == that.name && value == that.value
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(name, value)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

class Annotations(val entries: Map[String, Annotation]) {
  def this(annotations: Traversable[Annotation]) = this(annotations.map(a => a.name -> a).toMap)

  def get(key: String): Option[Annotation] = entries.get(key)

  def canEqual(other: Any): Boolean = other.isInstanceOf[Annotations]

  override def equals(other: Any): Boolean = other match {
    case that: Annotations => (that canEqual this) && entries == that.entries
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(entries)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

object Annotations {
  val empty: Annotations = new Annotations(Map[String, Annotation]())

  def fromMap(entries: Option[Map[String, Annotation]]): Annotations = entries match {
    case Some(e) => new Annotations(e)
    case None => empty
  }
}
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

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class COpKeyProjection (
  val params: COpParams,
  val annotations: Annotations,
  val location: TextLocation
) {

  def canEqual(other: Any): Boolean = other.isInstanceOf[COpKeyProjection]

  override def equals(other: Any): Boolean = other match {
    case that: COpKeyProjection => (that canEqual this) && params == that.params && annotations == that.annotations
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(params, annotations)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

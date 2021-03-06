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

/* Created by yegor on 6/17/16. */

package ws.epigraph.compiler

object JavaFunction {

  def apply[T, R](function1: T => R): java.util.function.Function[T, R] = new JavaFunction[T, R](function1)

}

private class JavaFunction[T, R](sf: T => R) extends java.util.function.Function[T, R] {

  override def apply(t: T): R = sf.apply(t)

}

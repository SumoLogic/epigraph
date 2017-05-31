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

/* Created by yegor on 7/11/16. */

package ws.epigraph.java

import java.nio.file.Path

trait JavaGen {

  protected def ctx: GenContext

  protected def relativeFilePath: Path

  /** checks if this generator should be invoked. This method may have side effects and should only be called once */
  def shouldRun: Boolean = true

  protected def generate: String

  def children: Iterable[JavaGen] = Iterable()

  def writeUnder(sourcesRoot: Path): Unit = {
//    if (shouldRun) {
      //System.out.println("Writing to '" + relativeFilePath + "'")
      JavaGenUtils.writeFile(sourcesRoot, relativeFilePath, generate)
//    }
  }

  def `NotNull_`: String = if (ctx.java8Annotations) "@NotNull " else ""
  def `Nullable_`: String = if (ctx.java8Annotations) "@Nullable " else ""

}

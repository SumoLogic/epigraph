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

  def relativeFilePath: Path

  def shouldRunStrategy: ShouldRunStrategy = new FileBasedRunStrategy(relativeFilePath, ctx.writtenPaths)

  @throws[TryLaterException]
  protected def generate: String

  def children: Iterable[JavaGen] = Iterable()

  final def writeUnder(sourcesRoot: Path, resourcesRoot: Path): Unit = {
    JavaGenUtils.writeFile(pickRoot(sourcesRoot, resourcesRoot), relativeFilePath, generate)
  }

  protected def pickRoot(sourcesRoot: Path, resourcesRoot: Path): Path = sourcesRoot

  /** Yields "@NotNull " annotation in case java 8 (type use, type parameter) annotations are enabled. */
  def `NotNull_`: String = if (ctx.java8Annotations) "@NotNull " else ""

  /** Yields "@Nullable " annotation in case java 8 (type use, type parameter) annotations are enabled. */
  def `Nullable_`: String = if (ctx.java8Annotations) "@Nullable " else ""

  def description: String = s"${ getClass.getSimpleName }\n  relative path $relativeFilePath"

}

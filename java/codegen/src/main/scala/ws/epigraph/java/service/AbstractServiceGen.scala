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

package ws.epigraph.java.service

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
trait AbstractServiceGen {
  protected def generateNoIndent(ctx: ServiceGenContext): String

  final def generate(ctx: ServiceGenContext): String = generate(0, ctx)

  final def generate(indent: Int, ctx: ServiceGenContext): String = {
    val g = generateNoIndent(ctx)
    if (indent == 0) g
    // assuming current line is already properly indented
    else g.lines.zipWithIndex.map{ case (l, i) => if (i == 0) l else AbstractServiceGen.spaces(indent) }.mkString("\n")
  }
}

object AbstractServiceGen {
  private val spacesCache = new scala.collection.mutable.HashMap[Int, String]

  def spaces(i: Int): String = spacesCache.getOrElseUpdate(i, " " * i)
}

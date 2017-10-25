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

import java.nio.file.Path

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
trait ShouldRunStrategy {
  /**
   * Check if generator should run. If it should: mark as run and return `true`, else return `false`. Should
   * be implemented as an atomic call
   *
   * @return `true` if generator must be called, `false` otherwise
   */
  def checkAndMark: Boolean

  def check: Boolean

  /** unmark generator as run allowing it to be called again */
  def unmark(): Unit
}

object AlwaysRunStrategy extends ShouldRunStrategy {
  override def checkAndMark: Boolean = true

  override def check = true

  override def unmark(): Unit = {}
}

class FileBasedRunStrategy(path: Path, writtenPaths: java.util.Set[Path]) extends ShouldRunStrategy {
  override def checkAndMark: Boolean = writtenPaths.synchronized {
    val res = check
    if (res) { writtenPaths.add(path) }
    res
  }

  override def check: Boolean = writtenPaths.synchronized { !writtenPaths.contains(path) }

  override def unmark(): Unit = writtenPaths.synchronized { writtenPaths.remove(path) }
}

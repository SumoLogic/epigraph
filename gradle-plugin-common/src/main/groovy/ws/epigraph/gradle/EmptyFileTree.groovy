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

package ws.epigraph.gradle

import org.gradle.api.Action
import org.gradle.api.file.FileTree
import org.gradle.api.file.FileVisitDetails
import org.gradle.api.file.FileVisitor
import org.gradle.api.internal.file.AbstractFileCollection
import org.gradle.api.internal.file.FileTreeInternal
import org.gradle.api.tasks.util.PatternFilterable

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class EmptyFileTree extends AbstractFileCollection implements FileTreeInternal {
  public static final FileTree INSTANCE = new EmptyFileTree()

  @Override
  FileTree matching(Closure closure) { return this }

  @Override
  FileTree matching(PatternFilterable patternFilterable) { return this }

  @Override
  FileTree visit(FileVisitor fileVisitor) { return this }

  @Override
  FileTree visit(Closure closure) { return this }

  @Override
  FileTree plus(FileTree fileTree) { return this }

  @Override
  String getDisplayName() { return "-empty-" }

  @Override
  Set<File> getFiles() { return Collections.emptySet() }

  @Override
  void visitTreeOrBackingFile(FileVisitor fileVisitor) { }

  @Override
  FileTree matching(final Action<? super PatternFilterable> action) { return INSTANCE }

  @Override
  FileTree visit(final Action<? super FileVisitDetails> action) { return INSTANCE }

}

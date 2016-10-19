package io.epigraph.gradle

import org.gradle.api.file.FileTree
import org.gradle.api.file.FileVisitor
import org.gradle.api.internal.file.AbstractFileCollection
import org.gradle.api.internal.file.FileTreeInternal
import org.gradle.api.tasks.util.PatternFilterable

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EmptyFileTree extends AbstractFileCollection implements FileTreeInternal {
  public static final FileTree INSTANCE = new EmptyFileTree()

  @Override
  FileTree matching(Closure closure) {
    return this
  }

  @Override
  FileTree matching(PatternFilterable patternFilterable) {
    return this
  }

  @Override
  FileTree visit(FileVisitor fileVisitor) {
    return this
  }

  @Override
  FileTree visit(Closure closure) {
    return this
  }

  @Override
  FileTree plus(FileTree fileTree) {
    return this
  }

  @Override
  String getDisplayName() {
    return "-empty-"
  }

  @Override
  Set<File> getFiles() {
    return Collections.emptySet()
  }

  @Override
  void visitTreeOrBackingFile(FileVisitor fileVisitor) {
  }
}

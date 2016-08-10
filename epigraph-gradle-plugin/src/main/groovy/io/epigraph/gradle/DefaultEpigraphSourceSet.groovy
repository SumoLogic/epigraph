package io.epigraph.gradle

import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.SourceDirectorySetFactory
import org.gradle.util.ConfigureUtil

class DefaultEpigraphSourceSet implements EpigraphSourceSet {
  private final SourceDirectorySet epigraph

  DefaultEpigraphSourceSet(String displayName, SourceDirectorySetFactory sourceDirectorySetFactory) {
    epigraph = sourceDirectorySetFactory.create(displayName + " Epigraph source")
    epigraph.getFilter().include("**/*.esc")
  }

  @Override
  SourceDirectorySet getEpigraph() {
    return epigraph
  }

  @Override
  EpigraphSourceSet epigraph(Closure closure) {
    ConfigureUtil.configure(closure, getEpigraph())
    return this
  }
}

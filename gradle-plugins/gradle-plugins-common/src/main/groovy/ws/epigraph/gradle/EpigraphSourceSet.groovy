package ws.epigraph.gradle

import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.Input

interface EpigraphSourceSet {
  @Input
  SourceDirectorySet getEpigraph()

  EpigraphSourceSet epigraph(Closure closure)
}
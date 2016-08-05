package io.epigraph.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
class CompileSchemaTask extends DefaultTask {
  public static String NAME = "compileEpigraphSchema"

  @TaskAction
  def compileEpigraphSchema() {
    println 'Compiling epigraph schemas'
  }
}

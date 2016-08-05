package io.epigraph.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
class EpigraphPlugin implements Plugin<Project>{
  @Override
  void apply(Project project) {
    project.afterEvaluate {
      project.tasks.create(CompileSchemaTask.NAME, CompileSchemaTask.class)
    }
  }
}

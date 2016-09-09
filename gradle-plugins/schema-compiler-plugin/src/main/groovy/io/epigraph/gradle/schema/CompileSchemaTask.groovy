package io.epigraph.gradle.schema

import io.epigraph.gradle.EpigraphSchemaTask
import org.gradle.api.tasks.ParallelizableTask
import org.gradle.api.tasks.TaskAction

@ParallelizableTask
class CompileSchemaTask extends EpigraphSchemaTask {

  CompileSchemaTask() {
  }

  @TaskAction
  public void run() {
    compileSchemaFiles();
  }
}

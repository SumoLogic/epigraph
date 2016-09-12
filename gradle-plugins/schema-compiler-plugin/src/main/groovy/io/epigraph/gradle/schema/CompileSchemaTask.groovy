package io.epigraph.gradle.schema

import io.epigraph.gradle.EpigraphSchemaTaskBase
import org.gradle.api.GradleException
import org.gradle.api.tasks.ParallelizableTask
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

@ParallelizableTask
class CompileSchemaTask extends SourceTask implements EpigraphSchemaTaskBase {

  CompileSchemaTask() {}

  @TaskAction
  public void run() {
    def context = compileSchemaFiles();
    if (!context.errors().isEmpty())
      throw new GradleException('Epigraph schema compilation failed with errors')
  }
}

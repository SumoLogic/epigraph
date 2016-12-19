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

package ws.epigraph.gradle

import org.gradle.api.GradleException
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.ParallelizableTask
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

import java.nio.file.Files
import java.nio.file.StandardCopyOption

@ParallelizableTask
class CompileTask extends SourceTask implements EpigraphCompileTaskBase {
  private File destinationDir;

  CompileTask() {}

  /**
   * Returns the directory to put verified files into.
   *
   * @return The destination directory.
   */
  @OutputDirectory
  public File getDestinationDir() {
    return destinationDir;
  }

  void setDestinationDir(File destinationDir) {
    this.destinationDir = destinationDir
  }

  @TaskAction
  public void run() {
    def context = compileFiles();

    if (!context.errors().isEmpty())
      throw new GradleException('Schema compilation failed with errors')

    def artifactsDir = new File(destinationDir, EpigraphConstants.ARTIFACTS_PATH_PREFIX).getCanonicalFile()
    artifactsDir.delete()

    // see BaseCompileMojo
    getFileSources().each { fileSource ->
      def sourceFileName = fileSource.name() // this is full path actually
      def sourceFile = new File(sourceFileName)
      def schemaFile = context.schemaFiles().get(sourceFileName)
      assert schemaFile != null

      def relativePath = schemaFile.namespace().fqn().segments.join(File.separator)
      def targetDir = new File(artifactsDir, relativePath)

      targetDir.mkdirs()
      Files.copy(
          sourceFile.toPath(),
          targetDir.toPath().resolve(sourceFile.name),
          StandardCopyOption.REPLACE_EXISTING
      )

//      getLogger().info("Copying ${sourceFile.toPath()} -> ${targetDir.toPath().resolve(sourceFile.name)}")
    }
  }
}

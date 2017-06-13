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

package ws.epigraph.gradle.java

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.ParallelizableTask
import org.gradle.api.tasks.TaskAction
import ws.epigraph.gradle.EmptyFileTree
import ws.epigraph.gradle.EpigraphCompileTaskBase
import ws.epigraph.java.EpigraphJavaGenerator
import ws.epigraph.java.Settings

@ParallelizableTask
class GenerateJavaBindingsTask extends DefaultTask implements EpigraphCompileTaskBase {
  private String sourceSetName
  private File destinationDir

  GenerateJavaBindingsTask() {}

  @TaskAction
  void run() {
    destinationDir.mkdirs()

    def context = compileFiles();
    if (!context.errors().isEmpty())
      throw new GradleException('Schema compilation failed with errors')

    getLogger().info("Generating Java bindings to '${getDestinationDir()}'")

    Server server = project.epigraph.server
    Client client = project.epigraph.client
    boolean java8Annotations = project.epigraph.java8Annotations
    
    def settings = new Settings(
        new Settings.ServerSettings(
            server != null && server.generate,
            server == null ? null : server.services,
            server == null ? null : server.transformers
        ),
        new Settings.ClientSettings(
            client != null && client.generate,
            client == null ? null : client.services
        ),
        java8Annotations
    )

    new EpigraphJavaGenerator(context, getDestinationDir(), settings).generate()
  }

  @Override
  FileTree getSource() {
    return EmptyFileTree.INSTANCE // we only generate from dependencies
  }

  @Internal
  void setSourceSetName(String sourceSetName) {
    this.sourceSetName = sourceSetName
  }

  @OutputDirectory
  File getDestinationDir() {
    if (destinationDir == null) {
      if (sourceSetName == null)
        throw new GradleException('Neither destination dir nor source name is set')

      getLogger().debug('Using default destination dir')
      def generatedSrcDir = new File(project.buildDir, 'generated-src')
      def javaGeneratedSrcDir = new File(generatedSrcDir, 'epigraph.java')
      setDestinationDir(new File(javaGeneratedSrcDir, sourceSetName))
    }

    return destinationDir
  }

  void setDestinationDir(File destinationDir) {
    this.destinationDir = destinationDir
  }
}

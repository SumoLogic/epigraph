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
  private File destinationSourcesDir
  private File destinationResourcesDir

  GenerateJavaBindingsTask() {}

  @TaskAction
  void run() {
    destinationSourcesDir.mkdirs()

    def context = compileFiles()
    if (!context.errors().isEmpty())
      throw new GradleException('Schema compilation failed with errors')

    getLogger().info("Generating Java bindings to '${getDestinationSourcesDir()}'")

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

    new EpigraphJavaGenerator(context, getDestinationSourcesDir(), getDestinationResourcesDir(), settings).generate()
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
  File getDestinationSourcesDir() {
    if (destinationSourcesDir == null) {
      if (sourceSetName == null)
        throw new GradleException('Neither destination sources dir nor source name is set')

      getLogger().debug('Using default destination sources dir')
      def generatedSrcDir = new File(project.buildDir, 'generated-src')
      def javaGeneratedSrcDir = new File(generatedSrcDir, 'epigraph.java')
      setDestinationSourcesDir(new File(javaGeneratedSrcDir, sourceSetName))
    }

    return destinationSourcesDir
  }

  void setDestinationSourcesDir(File dir) {
    this.destinationSourcesDir = dir
  }

  @OutputDirectory
  File getDestinationResourcesDir() {
    if (destinationResourcesDir == null) {
      if (sourceSetName == null)
        throw new GradleException('Neither destination resources dir nor source name is set')

      getLogger().debug('Using default destination resources dir')
      def generatedSrcDir = new File(project.buildDir, 'generated-src')
      def generatedResDir = new File(generatedSrcDir, 'epigraph.resources')
      setDestinationResourcesDir(new File(generatedResDir, sourceSetName))
    }

    return destinationResourcesDir
  }

  void setDestinationResourcesDir(File dir) {
    this.destinationResourcesDir = dir
  }

}

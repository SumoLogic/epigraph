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

import ws.epigraph.gradle.EpigraphPluginConvention
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.PluginManager
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskContainer
import org.gradle.internal.reflect.Instantiator

import javax.inject.Inject

import static ws.epigraph.gradle.GradleUtils.isTestSourceSet

class EpigraphJavaPlugin implements Plugin<ProjectInternal> {
  private final Instantiator instantiator
  private Configuration epigraphConfiguration
  private Configuration epigraphTestConfiguration

  @Inject
  EpigraphJavaPlugin(Instantiator instantiator) {
    this.instantiator = instantiator
  }

  @Override
  void apply(ProjectInternal project) {
    project.extensions.create('epigraph', EpigraphJavaPluginExtension)

    PluginManager pluginManager = project.getPluginManager()

    if (!pluginManager.hasPlugin('java')) {
      project.logger.info('Java plugin not found, applying')
      pluginManager.apply(JavaPlugin.class)
    }

    EpigraphPluginConvention epigraphConvention = new EpigraphPluginConvention(project, instantiator)
    project.getConvention().getPlugins().put(EpigraphPluginConvention.NAME, epigraphConvention)

    epigraphConfiguration = project.configurations.create('epigraph')
    epigraphTestConfiguration = project.configurations.create('testEpigraph')

    configureSourceSets(project)
  }

  private void configureSourceSets(Project project) {
    // http://gradle.1045684.n5.nabble.com/plugins-that-generate-source-code-td1437853.html
    // https://discuss.gradle.org/t/how-can-i-compile-generated-sources/7541/14

    SourceSetContainer sourceSets = project.sourceSets
    TaskContainer tasks = project.tasks

    sourceSets.all { SourceSet sourceSet ->
//      if (!isTestSourceSet(sourceSet)) {
      String taskName = sourceSet.getTaskName('generate', 'EpigraphJavaBindings')
      GenerateJavaBindingsTask task = tasks.create(taskName, GenerateJavaBindingsTask.class)
      task.setDescription("Generate $sourceSet.name Epigraph Java bindings")
      task.setGroup(BasePlugin.BUILD_GROUP)
      task.sourceSetName = sourceSet.name

      def generatedSourcesDir = task.getDestinationSourcesDir()
      task.outputs.dir generatedSourcesDir

      // otherwise idea doesn't recognize generated sources
      sourceSet.getAllSource().srcDir(generatedSourcesDir)

      def generatedResourcesDir = task.getDestinationResourcesDir()
////      sourceSet.output.dir(generatedResourcesDir, builtBy: taskName) // see https://docs.gradle.org/4.1/dsl/org.gradle.api.tasks.SourceSetOutput.html
      sourceSet.resources.srcDirs = [ generatedResourcesDir ] // ?  from https://youtrack.jetbrains.com/issue/IDEA-133399

      SourceTask compileJavaTask = tasks.getByName(sourceSet.getCompileJavaTaskName()) as SourceTask
      if (compileJavaTask != null) {
        compileJavaTask.dependsOn task
        compileJavaTask.source generatedSourcesDir
      }

      def configuration = isTestSourceSet(sourceSet) ? epigraphTestConfiguration : epigraphConfiguration
      task.setConfiguration(configuration)
      task.dependsOn configuration

      if (project.hasProperty('idea')) {
        def ideaModule = project.idea.module
        ideaModule.generatedSourceDirs += generatedSourcesDir
        // no extra steps needed to add resources?
        // see https://youtrack.jetbrains.com/issue/IDEA-133399
      }
//      }
    }
  }
}

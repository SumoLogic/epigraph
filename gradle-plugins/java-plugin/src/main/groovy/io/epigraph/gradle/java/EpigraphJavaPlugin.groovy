package io.epigraph.gradle.java

import io.epigraph.gradle.EpigraphPluginConvention
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

class EpigraphJavaPlugin implements Plugin<ProjectInternal> {
  private final Instantiator instantiator
  private Configuration schemasConfiguration

  @Inject
  EpigraphJavaPlugin(Instantiator instantiator) {
    this.instantiator = instantiator
  }

  @Override
  void apply(ProjectInternal project) {
    PluginManager pluginManager = project.getPluginManager()

    if (!pluginManager.hasPlugin('java')) {
      project.logger.info('Java plugin not found, applying')
      pluginManager.apply(JavaPlugin.class)
    }

    EpigraphPluginConvention epigraphConvention = new EpigraphPluginConvention(project, instantiator)
    project.getConvention().getPlugins().put(EpigraphPluginConvention.NAME, epigraphConvention)

    schemasConfiguration = project.configurations.create('epigraphSchema')

    configureSourceSets(project)
  }

  private void configureSourceSets(Project project) {
    // http://gradle.1045684.n5.nabble.com/plugins-that-generate-source-code-td1437853.html

    SourceSetContainer sourceSets = project.sourceSets
    TaskContainer tasks = project.tasks

    sourceSets.all { SourceSet sourceSet ->
      if (!isTestSourceSet(sourceSet)) {
        String taskName = sourceSet.getTaskName('generate', 'EpigraphJavaBindings')
        GenerateSchemaBindingsTask task = tasks.create(taskName, GenerateSchemaBindingsTask.class)
        task.setDescription("Generate $sourceSet.name Epigraph Java bindings")
        task.setGroup(BasePlugin.BUILD_GROUP)
        task.sourceSetName = sourceSet.name
        def destinationDir = task.getDestinationDir()
        task.outputs.dir destinationDir

        sourceSet.getAllSource().srcDir(destinationDir)

        SourceTask compileJavaTask = tasks.getByName(sourceSet.getCompileJavaTaskName()) as SourceTask
        if (compileJavaTask != null) {
          compileJavaTask.dependsOn task
          compileJavaTask.source destinationDir
        }

        task.setConfiguration(schemasConfiguration)
        task.dependsOn schemasConfiguration

        if (project.hasProperty('idea')) {
          def ideaModule = project.idea.module
          ideaModule.generatedSourceDirs += destinationDir
        }
      }
    }
  }

  private static boolean isTestSourceSet(SourceSet sourceSet) {
    return sourceSet.name == SourceSet.TEST_SOURCE_SET_NAME
  }
}

package io.epigraph.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.SourceDirectorySetFactory
import org.gradle.api.plugins.Convention
import org.gradle.api.tasks.SourceSet

import javax.inject.Inject

class EpigraphPlugin implements Plugin<Project> {
  private final SourceDirectorySetFactory sourceDirectorySetFactory

  @Inject
  public EpigraphPlugin(SourceDirectorySetFactory sourceDirectorySetFactory) {
    this.sourceDirectorySetFactory = sourceDirectorySetFactory
  }

  @Override
  void apply(Project project) {
    project.configurations {
      epigraph
    }

    project.afterEvaluate {
      List<CompileSchemaTask> testCompileTasks = new ArrayList<>()
      List<SourceDirectorySet> nonTestDirectorySets = new ArrayList<>()

      project.sourceSets.all { SourceSet sourceSet ->
        String displayName = sourceSet.getDisplayName()
        Convention sourceSetConvention = sourceSet.convention
        DefaultEpigraphSourceSet epigraphSourceSet = new DefaultEpigraphSourceSet(displayName, sourceDirectorySetFactory)
        sourceSetConvention.getPlugins().put("epigraph", epigraphSourceSet)
        SourceDirectorySet epigraphDirectorySet = epigraphSourceSet.getEpigraph()
        epigraphDirectorySet.srcDir {
          'src/' + sourceSet.getName() + '/epigraph'
        }

        sourceSet.getAllSource().source(epigraphDirectorySet)

        // create compile schema task for this source set
        String taskName = sourceSet.getTaskName("compile", "EpigraphSchema")
        CompileSchemaTask compileSchemaTask = project.getTasks().create(taskName, CompileSchemaTask.class)
        compileSchemaTask.setDescription("Process " + sourceSet.getName() + " Epigraph schemas.")
        compileSchemaTask.setSource(epigraphDirectorySet)

        if (isTestSourceSet(sourceSet))
          testCompileTasks.add(compileSchemaTask)
        else
          nonTestDirectorySets.add(epigraphDirectorySet)
      }

      // add non-test schemas to tests
      testCompileTasks.each {
        List<Object> sources = new ArrayList<>(nonTestDirectorySets)
        sources.add(it.getSource())
        it.setSource(sources)
      }
    }

  }

  private static boolean isTestSourceSet(SourceSet sourceSet) {
    // TODO any better way?
    return sourceSet.name =~ '^(integ)?[Tt]est'
  }
}

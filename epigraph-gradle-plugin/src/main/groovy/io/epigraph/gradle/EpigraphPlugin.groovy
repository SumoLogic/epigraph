package io.epigraph.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.SourceDirectorySetFactory
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.Convention
import org.gradle.api.plugins.PluginManager
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.internal.reflect.Instantiator
import org.gradle.language.base.plugins.LanguageBasePlugin

import javax.inject.Inject

class EpigraphPlugin implements Plugin<ProjectInternal> {
  private final SourceDirectorySetFactory sourceDirectorySetFactory
  private final Instantiator instantiator

  @Inject
  public EpigraphPlugin(SourceDirectorySetFactory sourceDirectorySetFactory, Instantiator instantiator) {
    this.sourceDirectorySetFactory = sourceDirectorySetFactory
    this.instantiator = instantiator
  }

  @Override
  public void apply(ProjectInternal project) {
    PluginManager pluginManager = project.getPluginManager()
    pluginManager.apply(BasePlugin.class)
    pluginManager.apply(LanguageBasePlugin.class)

    EpigraphPluginConvention epigraphConvention = new EpigraphPluginConvention(project, instantiator)
    project.getConvention().getPlugins().put('epigraph', epigraphConvention)

    project.configurations {
      epigraph
    }

    project.afterEvaluate {
      configureSourceSets(project)
    }

  }

  private void configureSourceSets(Project project) {
    SourceSetContainer sourceSets = project.sourceSets
    if (sourceSets.isEmpty()) {
      addDefaultSourceSets(project, sourceSets)
    }

    List<CompileSchemaTask> testCompileTasks = new ArrayList<>()
    List<SourceDirectorySet> nonTestDirectorySets = new ArrayList<>()

    sourceSets.all { SourceSet sourceSet ->
      String displayName = sourceSet.getDisplayName()
      project.getLogger().debug("Epigraph: configuring $displayName")
      Convention sourceSetConvention = sourceSet.convention
      DefaultEpigraphSourceSet epigraphSourceSet = new DefaultEpigraphSourceSet(displayName, sourceDirectorySetFactory)
      sourceSetConvention.getPlugins().put("epigraph", epigraphSourceSet)
      SourceDirectorySet epigraphDirectorySet = epigraphSourceSet.getEpigraph()
      String srcDir = 'src/' + sourceSet.getName() + '/epigraph'
      epigraphDirectorySet.srcDir { srcDir }

      sourceSet.getAllSource().source(epigraphDirectorySet)

      // create compile schema task for this source set
      String taskName = sourceSet.getTaskName("compile", "EpigraphSchema")
      CompileSchemaTask compileSchemaTask = project.getTasks().create(taskName, CompileSchemaTask.class)
      compileSchemaTask.setDescription("Process " + sourceSet.getName() + " Epigraph schemas.")
      compileSchemaTask.setSource(epigraphDirectorySet)
      compileSchemaTask.outputs.dir srcDir // TODO better way to make it incremental? It won't unless output dir is defined

      if (isTestSourceSet(sourceSet))
        testCompileTasks.add(compileSchemaTask)
      else
        nonTestDirectorySets.add(epigraphDirectorySet)

      if (isMainSourceSet(sourceSet))
        project.tasks.getByName('assemble') dependsOn compileSchemaTask // TODO same for test?
    }

    // add non-test schemas to tests
    testCompileTasks.each {
      List<Object> sources = new ArrayList<>(nonTestDirectorySets)
      sources.add(it.getSource())
      it.setSource(sources)
    }
  }

  private static void addDefaultSourceSets(Project project, SourceSetContainer sourceSets) {
    EpigraphPluginConvention epigraphConvention = project.getConvention().getPlugin(EpigraphPluginConvention.class)

//    project.getServices().get(ComponentRegistry.class).setMainComponent(new BuildableJavaComponentImpl(javaConvention));

    SourceSet main = epigraphConvention.getSourceSets().create(SourceSet.MAIN_SOURCE_SET_NAME)
    SourceSet test = epigraphConvention.getSourceSets().create(SourceSet.TEST_SOURCE_SET_NAME)

    sourceSets.add(main)
    sourceSets.add(test)

  }

  private static boolean isMainSourceSet(SourceSet sourceSet) {
    return sourceSet.name == SourceSet.MAIN_SOURCE_SET_NAME
  }

  private static boolean isTestSourceSet(SourceSet sourceSet) {
    return sourceSet.name == SourceSet.TEST_SOURCE_SET_NAME
  }
}

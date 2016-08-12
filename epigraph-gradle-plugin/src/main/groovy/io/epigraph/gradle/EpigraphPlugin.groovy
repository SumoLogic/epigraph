package io.epigraph.gradle

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.XmlProvider
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.SourceDirectorySetFactory
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.Convention
import org.gradle.api.plugins.PluginManager
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.internal.reflect.Instantiator
import org.gradle.language.base.plugins.LanguageBasePlugin

import javax.inject.Inject

class EpigraphPlugin implements Plugin<ProjectInternal> {
  private static final String EPIGRAPH_PACKAGING_TYPE = 'epigraph-schema'

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
//      'default'.extendsFrom epigraph
    }

    project.configurations.default.extendsFrom(project.configurations.epigraph)

//    configureSourceSets(project)

    project.afterEvaluate {
      configureSourceSets(project)
      configurePublishing(project)
    }

    configureIdeaScopes(project)
  }

  private static void configureIdeaScopes(Project project) {
    // see https://youtrack.jetbrains.com/issue/IDEA-137763
    // TODO still doesn't work, IDEA doesn't see dependencies between epigraph modules

    if (project.hasProperty('idea')) {
      def scopes = project.idea.module.scopes

      if (scopes.COMPILE == null) addScope('COMPILE', scopes)
      if (scopes.TEST == null) addScope('TEST', scopes)

      scopes.COMPILE.plus += [project.configurations.epigraph]
      scopes.TEST.plus += [project.configurations.epigraph]

    }
  }

  private static void addScope(String name, LinkedHashMap<String, Map<String, Collection<Configuration>>> scopes) {
    LinkedHashMap<String, Collection<Configuration>> scope = new LinkedHashMap<>()
    scope.put("plus", new ArrayList<Configuration>())
    scope.put("minus", new ArrayList<Configuration>())
    scopes.put(name, scope)
  }


  private void configureSourceSets(Project project) {
    SourceSetContainer sourceSets = project.sourceSets
    if (sourceSets.isEmpty()) {
      addDefaultSourceSets(project, sourceSets)
    }

    TaskContainer tasks = project.getTasks()

    List<CompileSchemaTask> testCompileTasks = new ArrayList<>()
    List<SourceDirectorySet> nonTestDirectorySets = new ArrayList<>()

    sourceSets.all { SourceSet sourceSet ->
      String displayName = sourceSet.getDisplayName()
      project.getLogger().debug("Epigraph: configuring $displayName")
      Convention sourceSetConvention = sourceSet.convention
      DefaultEpigraphSourceSet epigraphSourceSet = new DefaultEpigraphSourceSet(displayName, sourceDirectorySetFactory)
      sourceSetConvention.getPlugins().put('epigraph', epigraphSourceSet)
      SourceDirectorySet epigraphDirectorySet = epigraphSourceSet.getEpigraph()
      String srcDir = "$project.projectDir/src/$sourceSet.name/epigraph"
      epigraphDirectorySet.srcDir { srcDir }

      sourceSet.getAllSource().source(epigraphDirectorySet)

      // create compile schema task for this source set
      String taskName = sourceSet.getTaskName('compile', 'EpigraphSchema')
      CompileSchemaTask compileSchemaTask = tasks.create(taskName, CompileSchemaTask.class)
      compileSchemaTask.setDescription("Process $sourceSet.name Epigraph schemas.")
      compileSchemaTask.setSource(epigraphDirectorySet)
      compileSchemaTask.outputs.dir srcDir // TODO better way to make it incremental? It won't unless output dir is defined

      if (isTestSourceSet(sourceSet)) {
        testCompileTasks.add(compileSchemaTask)
      } else {
        nonTestDirectorySets.add(epigraphDirectorySet)
      }

      configureIdeaModule(project, sourceSet, srcDir)

      // configure assemble task dependency
      if (isMainSourceSet(sourceSet)) {
        tasks.getByName('assemble') dependsOn compileSchemaTask
      }

      createJarTask(project, sourceSet, compileSchemaTask)
    }

    // add non-test schemas to tests
    testCompileTasks.each {
      List<Object> sources = new ArrayList<>(nonTestDirectorySets)
      sources.add(it.getSource())
      it.setSource(sources)
    }
  }

  private static void configureIdeaModule(Project project, SourceSet sourceSet, String srcDir) {
    if (project.hasProperty('idea')) {
      def ideaModule = project.idea.module

      if (isTestSourceSet(sourceSet)) {
        ideaModule.testSourceDirs += new File(srcDir)
      } else {
        ideaModule.sourceDirs += new File(srcDir)
      }
    }
  }

  private static void addDefaultSourceSets(Project project, SourceSetContainer sourceSets) {
    EpigraphPluginConvention epigraphConvention = project.getConvention().getPlugin(EpigraphPluginConvention.class)

    SourceSet main = epigraphConvention.getSourceSets().create(SourceSet.MAIN_SOURCE_SET_NAME)
    SourceSet test = epigraphConvention.getSourceSets().create(SourceSet.TEST_SOURCE_SET_NAME)

    sourceSets.add(main)
    sourceSets.add(test)

  }

  private static void createJarTask(Project project, SourceSet sourceSet, CompileSchemaTask compileSchemaTask) {
    TaskContainer tasks = project.getTasks()
    String jarTaskName = sourceSet.getJarTaskName()

    if (tasks.findByName(jarTaskName) == null) {
      def jarTask = tasks.create(jarTaskName, Jar.class, new Action<Jar>() {
        @Override
        void execute(Jar jar) {
          jar.description = "Assembles a jar archive for $sourceSet.name Epigraph schema"
          jar.group = BasePlugin.BUILD_GROUP
          jar.from(sourceSet.allSource)

          jar.dependsOn(compileSchemaTask)

        }
      })

      if (isMainSourceSet(sourceSet)) {
        tasks.getByName('assemble') dependsOn jarTask
        project.artifacts {
          epigraph jarTask
        }
      }
    }
  }

  private static void configurePublishing(Project project) {
    // TODO only change <packaging> for epigraph jars ?

    project.publishing {
      publications {
        withType(MavenPublication) {
          pom.withXml { XmlProvider xml ->
            def node = xml.asNode()
            def packaging = node.packaging
            if (packaging.isEmpty()) {
              node.appendNode('packaging', EPIGRAPH_PACKAGING_TYPE)
            } else {
              packaging*.setValue(EPIGRAPH_PACKAGING_TYPE)
            }
          }
        }
      }
    }
  }

  private static boolean isMainSourceSet(SourceSet sourceSet) {
    return sourceSet.name == SourceSet.MAIN_SOURCE_SET_NAME
  }

  private static boolean isTestSourceSet(SourceSet sourceSet) {
    return sourceSet.name == SourceSet.TEST_SOURCE_SET_NAME
  }
}

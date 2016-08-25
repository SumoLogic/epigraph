package io.epigraph.gradle

import org.gradle.api.internal.file.FileLookup
import org.gradle.api.internal.file.SourceDirectorySetFactory
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.internal.tasks.DefaultSourceSetContainer
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.internal.reflect.Instantiator

class EpigraphPluginConvention {

  private ProjectInternal project

  private String docsDirName

  private final SourceSetContainer sourceSets

  public EpigraphPluginConvention(ProjectInternal project, Instantiator instantiator) {
    this.project = project
    sourceSets = instantiator.newInstance(
        DefaultSourceSetContainer.class,
        project.getFileResolver(), project.getTasks(), instantiator,
        project.getServices().get(SourceDirectorySetFactory.class))
    docsDirName = "docs"
  }

  /**
   * Configures the source sets of this project.
   *
   * <p>The given closure is executed to configure the {@link SourceSetContainer}. The {@link SourceSetContainer}
   * is passed to the closure as its delegate.
   * <p>
   * See the example below how {@link org.gradle.api.tasks.SourceSet} 'main' is accessed and how the {@link org.gradle.api.file.SourceDirectorySet} 'java'
   * is configured to exclude some package from compilation.
   *
   * <pre autoTested=''>
   * apply plugin: 'java'
   *
   * sourceSets {
   *   main {
   *     java {
   *       exclude 'some/unwanted/package/**'
   *     }
   *   }
   * }
   * </pre>
   *
   * @param closure The closure to execute.
   * @return NamedDomainObjectContainer<org.gradle.api.tasks.SourceSet>
   */
  public Object sourceSets(Closure closure) {
    return sourceSets.configure(closure)
  }

  /**
   * Returns a file pointing to the root directory supposed to be used for all docs.
   */
  public File getDocsDir() {
    return project.getServices().get(FileLookup.class).getFileResolver(project.getBuildDir()).resolve(docsDirName)
  }

  /**
   * The source sets container.
   */
  public SourceSetContainer getSourceSets() {
    return sourceSets
  }

  public ProjectInternal getProject() {
    return project
  }
}

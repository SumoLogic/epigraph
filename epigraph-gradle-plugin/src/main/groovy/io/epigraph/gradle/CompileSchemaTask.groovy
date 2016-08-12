package io.epigraph.gradle

import com.sumologic.epigraph.schema.compiler.*
import org.gradle.api.GradleException
import org.gradle.api.artifacts.Configuration
import org.gradle.api.internal.TaskOutputsInternal
import org.gradle.api.tasks.ParallelizableTask
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

import java.nio.charset.StandardCharsets
import java.util.jar.JarFile
import java.util.regex.Pattern

@ParallelizableTask
class CompileSchemaTask extends SourceTask {
  private static final Pattern SCHEMA_FILENAME_PATTERN = Pattern.compile(".+\\.esc");

  CompileSchemaTask() {
    setGroup('Epigraph')
  }

  @TaskAction
  public void run() {
    Collection<Source> sources = getSources();

    Collection<Source> dependencySources = new ArrayList<>()
    dependencySources.addAll(getDependencySources())
    dependencySources.addAll(getImpliedDependencies())

    compileFiles(sources, dependencySources)
  }

  @Override
  TaskOutputsInternal getOutputs() {
    return super.getOutputs()
  }

  private Collection<Source> getSources() {
    return getSource().files.collect { new FileSource(it) }
  }

  private Collection<Source> getDependencySources() {
    Configuration epigraphConfiguration = project.getConfigurations().getByName('epigraph')
    getLogger().debug("Getting dependencies from $epigraphConfiguration")
    if (epigraphConfiguration == null) return Collections.emptyList()

    Collection<Source> dependencySources = new ArrayList<>()

    epigraphConfiguration.files.each {
      // TODO take charset from build props
      JarSource.allFiles(new JarFile(it), SCHEMA_FILENAME_PATTERN, StandardCharsets.UTF_8).each {
        dependencySources.add(it)
      }
    }

    return dependencySources
  }

  private Collection<Source> getImpliedDependencies() {
    if (getClass().getResource("/epigraph/builtinTypes.esc") != null) {
      return Collections.singletonList(new ResourceSource("/epigraph/builtinTypes.esc")); // TODO use url.openStream?
    }

    return Collections.emptyList();
  }

  private static void compileFiles(Collection<Source> sources, Collection<Source> dependencySources) {
    try {
      SchemaCompiler compiler = new SchemaCompiler(sources, dependencySources)
      compiler.compile()
    } catch (SchemaCompilerException ignored) {
      throw new GradleException("Epigraph schema compilation failed");
    }
  }
}

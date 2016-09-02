package io.epigraph.gradle

import com.sumologic.epigraph.schema.compiler.*
import org.gradle.api.GradleException
import org.gradle.api.artifacts.Configuration
import org.gradle.api.internal.TaskOutputsInternal
import org.gradle.api.tasks.ParallelizableTask
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Internal

import java.nio.charset.StandardCharsets
import java.util.jar.JarFile
import java.util.regex.Pattern

@ParallelizableTask
class CompileSchemaTask extends SourceTask {
  private static final Pattern SCHEMA_FILENAME_PATTERN = Pattern.compile(".+\\.esc");
  private Configuration configuration;

  CompileSchemaTask() {
  }

  @TaskAction
  public void run() {
    Collection<Source> sources = getSources();

    Collection<Source> dependencySources = new ArrayList<>()
    dependencySources.addAll(getDependencySources())
    dependencySources.addAll(getImpliedDependencies())

    compileFiles(sources, dependencySources)
  }

  void setConfiguration(Configuration configuration) {
    this.configuration = configuration
  }

  @Internal
  @Override
  TaskOutputsInternal getOutputs() {
    return super.getOutputs()
  }

  @Internal
  private Collection<Source> getSources() {
    return getSource().files.collect { new FileSource(it) }
  }

  @Internal
  private Collection<Source> getDependencySources() {
    getLogger().debug("Getting dependencies from ${configuration}")
    if (configuration == null) return Collections.emptyList()

    Collection<Source> dependencySources = new ArrayList<>()

    configuration.files.each {
      // TODO take charset from build props
      JarSource.allFiles(new JarFile(it), SCHEMA_FILENAME_PATTERN, StandardCharsets.UTF_8).each {
        dependencySources.add(it)
      }
    }

    return dependencySources
  }

  @Internal
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

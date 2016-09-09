package io.epigraph.gradle

import com.sumologic.epigraph.schema.compiler.*
import org.gradle.api.GradleException
import org.gradle.api.artifacts.Configuration
import org.gradle.api.internal.TaskOutputsInternal
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.ParallelizableTask
import org.gradle.api.tasks.SourceTask

import java.nio.charset.StandardCharsets
import java.util.jar.JarFile
import java.util.regex.Pattern

@ParallelizableTask
abstract class EpigraphSchemaTask extends SourceTask {
  public static final String SCHEMA_EXTENSION = 'esc'
  protected static final Pattern SCHEMA_FILENAME_PATTERN = Pattern.compile(".+\\." + SCHEMA_EXTENSION);
  protected Configuration configuration;

  EpigraphSchemaTask() {}

  void setConfiguration(Configuration configuration) {
    this.configuration = configuration
  }

  protected CContext compileSchemaFiles() {
    Collection<Source> sources = getSources();

    Collection<Source> dependencySources = new ArrayList<>()
    dependencySources.addAll(getDependencySources())
    dependencySources.addAll(getImpliedDependencies())

    return compileFiles(sources, dependencySources)
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
    if (getClass().getResource('/epigraph/builtinTypes.' + SCHEMA_EXTENSION) != null) {
      // TODO use url.openStream?
      return Collections.singletonList(new ResourceSource('/epigraph/builtinTypes.' + SCHEMA_EXTENSION));
    }

    return Collections.emptyList();
  }

  protected static CContext compileFiles(Collection<Source> sources, Collection<Source> dependencySources) {
    try {
      SchemaCompiler compiler = new SchemaCompiler(sources, dependencySources)
      return compiler.compile()
    } catch (SchemaCompilerException e) {
      throw new GradleException('Epigraph schema compilation failed', e);
    }
  }
}

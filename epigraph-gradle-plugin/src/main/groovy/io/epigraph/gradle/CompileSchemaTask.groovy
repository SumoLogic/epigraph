package io.epigraph.gradle

import com.sumologic.epigraph.schema.compiler.*
import org.gradle.api.GradleException
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import scala.Option

import java.nio.charset.StandardCharsets
import java.util.jar.JarFile
import java.util.regex.Pattern

class CompileSchemaTask extends SourceTask {
  private static final Pattern SCHEMA_FILENAME_PATTERN = Pattern.compile(".+\\.esc");

  @TaskAction
  public void run() {
    Collection<Source> sources = getSources();

    Collection<Source> dependencySources = new ArrayList<>()
    dependencySources.addAll(getDependencySources())
    dependencySources.addAll(getImpliedDependencies())

    compileFiles(sources, dependencySources)
  }

  private Collection<Source> getSources() {
    return getSource().files.collect { new FileSource(it) }
  }

  private Collection<Source> getDependencySources() {
    Configuration epigraphConfiguration = project.getConfigurations().getByName('epigraph')
    if (epigraphConfiguration == null) return Collections.emptyList()

    Collection<Source> dependencySources = new ArrayList<>()

    epigraphConfiguration.files.each {
      getLogger().info("Adding dependency sources from " + it.getAbsolutePath())

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

  private void compileFiles(Collection<Source> sources, Collection<Source> dependencySources) {
    try {
      SchemaCompiler compiler = new SchemaCompiler(sources, dependencySources)
      compiler.compile()
    } catch (SchemaCompilerException ignored) {
      for (CError err : compiler.ctx().errors()) {
        final CErrorPosition pos = err.position(); // TODO skip :line:colon, line text, and ^ if NA
        sb.append(err.filename()).append(':').append(pos.line()).append(':').append(pos.column()).append('\n');
        sb.append("Error: ").append(err.message()).append('\n');
        final Option<String> errText = pos.lineText();
        if (errText.nonEmpty()) {
          sb.append(errText.get()).append('\n');
          sb.append(String.format("%" + (pos.column()) + "s", "^").replace(" ", ".")).append('\n');
        }
      }
      getLogger().error(sb.toString())
      throw new GradleException("Epigraph schema compilation failed");
    }
  }
}

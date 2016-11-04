/* Created by yegor on 7/6/16. */

package ws.epigraph.mojo;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Compile Epigraph schema source files (.esc).
 */
@Mojo(
    name = "compile",
    defaultPhase = LifecyclePhase.COMPILE,
    requiresDependencyResolution = ResolutionScope.COMPILE,
    threadSafe = true
)
public class MainCompileMojo extends BaseCompileMojo {

  /**
   * The source directory of Epigraph schema files. This directory is added to the
   * classpath at schema compiling time. All files can therefore be referenced
   * as classpath resources following the directory structure under the
   * source directory.
   */
  @Parameter(defaultValue = "${project.build.sourceDirectory}/../epigraph")
  protected File sourceDirectory;

  /**
   * The directory in which to place compilation output.
   */
  @Parameter(defaultValue = "${project.build.outputDirectory}")
  private File outputDirectory;

  /**
   * A set of Ant-like inclusion patterns used to select files from the source
   * directory for processing. By default, the pattern
   * <code>**&#47;*.esc</code> is used to select epigraph schema files.
   */
  @Parameter
  private String[] includes = new String[]{SCHEMA_FILE_ANT_PATTERN};

  /**
   * A set of Ant-like exclusion patterns used to prevent certain files from
   * being processed. By default, this set is empty such that no files are
   * excluded.
   */
  @Parameter
  private String[] excludes = new String[]{};

  @Override
  protected Collection<? extends String> getSourceRoots(@NotNull MavenProject project) throws IOException {
    project.addCompileSourceRoot(sourceDirectory.getCanonicalPath());
    return project.getCompileSourceRoots();
  }

  @Override
  protected File getOutputDirectory() { return outputDirectory; }

  @Override
  protected String[] includes() { return includes; }

  @Override
  protected String[] excludes() { return excludes; }

  @Override
  protected boolean dependsOnMainOutput() { return false; }

}

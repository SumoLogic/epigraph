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
    name = "testCompile",
    defaultPhase = LifecyclePhase.TEST_COMPILE,
    requiresDependencyResolution = ResolutionScope.TEST,
    threadSafe = true
)
public class TestCompileMojo extends BaseCompileMojo {

  /**
   *
   */
  @Parameter(defaultValue = "${project.build.testSourceDirectory}/../epigraph")
  private File testSourceDirectory;

  /**
   *
   */
  @Parameter(defaultValue = "${project.build.testOutputDirectory}")
  private File testOutputDirectory;

  /**
   * A set of Ant-like inclusion patterns used to select files from the source
   * directory for processing. By default, the pattern
   * <code>**&#47;*.esc</code> is used to select epigraph schema files.
   */
  @Parameter
  private String[] testIncludes = new String[]{SCHEMA_FILE_ANT_PATTERN};

  /**
   * A set of Ant-like exclusion patterns used to prevent certain files from
   * being processed. By default, this set is empty such that no files are
   * excluded.
   */
  @Parameter
  private String[] testExcludes = new String[]{};

  @Override
  protected Collection<? extends String> getSourceRoots(@NotNull MavenProject project) throws IOException {
    project.addTestCompileSourceRoot(testSourceDirectory.getCanonicalPath());
    return project.getTestCompileSourceRoots();
  }

  @Override
  protected File getOutputDirectory() { return testOutputDirectory; }

  @Override
  protected String[] includes() { return testIncludes; }

  @Override
  protected String[] excludes() { return testExcludes; }

  @Override
  protected boolean dependsOnMainOutput() { return true; }

}

/* Created by yegor on 7/6/16. */

package com.sumologic.epigraph.java.mojo;

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
 * Generate Java bindings for Epigraph schema source files (.esc).
 */
@Mojo(
    name = "generate-test-sources", // TODO generateTestSources?
    defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES,
    requiresDependencyResolution = ResolutionScope.TEST,
    threadSafe = true
)
public class TestCodegenMojo extends BaseCodegenMojo {

  /**
   * The source directory of Epigraph schema files. This directory is added to the
   * classpath at schema compiling time. All files can therefore be referenced
   * as classpath resources following the directory structure under the
   * source directory.
   */
  @Parameter(defaultValue = "${project.build.testSourceDirectory}/../epigraph")
  protected File testSourceDirectory;

  /**
   *
   */
  @Parameter(defaultValue = "${project.build.directory}/generated-test-sources/epigraph.java")
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
  protected String[] includes() { return testIncludes; }

  @Override
  protected String[] excludes() { return testExcludes; }

  @Override
  protected Collection<? extends String> getSourceRoots(@NotNull MavenProject project) throws IOException {
    project.addCompileSourceRoot(testSourceDirectory.getCanonicalPath());
    return project.getCompileSourceRoots();
  }

  @Override
  protected boolean dependsOnMainOutput() { return true; }

  @Override
  protected File getOutputDirectory() { return testOutputDirectory; }

  @Override
  protected void addResultsToProject(MavenProject project, String path) { project.addTestCompileSourceRoot(path); }

}

/* Created by yegor on 7/6/16. */

package com.sumologic.epigraph.mojo;

import com.sumologic.epigraph.schema.compiler.Source;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Compile Epigraph schema source files (.esc).
 */
@Mojo(
    name = "test-compile",
    defaultPhase = LifecyclePhase.TEST_COMPILE,
    requiresDependencyResolution = ResolutionScope.TEST,
    threadSafe = true
)
public class TestCompileMojo extends BaseCompileMojo {

  /**
   *
   */
  @Parameter(defaultValue = "${project.basedir}/src/test/epigraph")
  private File testSourceDirectory;

  /**
   *
   */
  @Parameter(defaultValue = "${project.build.directory}/epigraph-tests")
  private File testOutputDirectory;

  /**
   * A set of Ant-like inclusion patterns used to select files from the source
   * directory for processing. By default, the pattern
   * <code>**&#47;*.esc</code> is used to select epigraph schema files.
   */
  @Parameter
  private String[] testIncludes = new String[]{"**/*.esc"};

  /**
   * A set of Ant-like exclusion patterns used to prevent certain files from
   * being processed. By default, this set is empty such that no files are
   * excluded.
   */
  @Parameter
  private String[] testExcludes = new String[]{};

  @Override
  protected File getSourceDirectory() { return testSourceDirectory; }

  @Override
  protected Collection<Source> getDependencySources() throws MojoExecutionException {
    return merge(super.getDependencySources(), getDirectorySources(sourceDirectory));
  }

  private <T> Collection<T> merge(Collection<T>... collections) {
    int size = 0;
    for (Collection<T> collection : collections) size += collection.size();
    Collection<T> merged = new ArrayList<T>(size);
    for (Collection<T> collection : collections) merged.addAll(collection);
    return merged;
  }

  @Override
  protected String[] getIncludes() { return testIncludes; }

  @Override
  protected String[] getExcludes() { return testExcludes; }

}

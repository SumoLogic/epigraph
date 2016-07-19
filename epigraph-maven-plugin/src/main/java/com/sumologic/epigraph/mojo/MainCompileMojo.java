/* Created by yegor on 7/6/16. */

package com.sumologic.epigraph.mojo;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;

/**
 * Compile Epigraph source files (.esc)
 */
@Mojo(
    name = "compile",
    defaultPhase = LifecyclePhase.COMPILE,
    requiresDependencyResolution = ResolutionScope.COMPILE,
    threadSafe = true
)
public class MainCompileMojo extends BaseCompileMojo {

  /**
   * A set of Ant-like inclusion patterns used to select files from the source
   * directory for processing. By default, the pattern
   * <code>**&#47;*.esc</code> is used to select epigraph schema files.
   */
  @Parameter
  private String[] includes = new String[]{"**/*.esc"};

  /**
   * A set of Ant-like exclusion patterns used to prevent certain files from
   * being processed. By default, this set is empty such that no files are
   * excluded.
   */
  @Parameter
  private String[] excludes = new String[]{};

  @Override
  protected File getSourceDirectory() {
    return sourceDirectory;
  }

  @Override
  protected String[] getIncludes() {
    return includes;
  }

  @Override
  protected String[] getExcludes() {
    return excludes;
  }

}

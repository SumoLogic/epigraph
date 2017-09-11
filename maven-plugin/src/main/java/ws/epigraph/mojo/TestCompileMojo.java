/*
 * Copyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
 * Compiles Epigraph Schema test sources (<code>*.epigraph</code>).
 */
@Mojo(
    name = "testCompile",
    defaultPhase = LifecyclePhase.TEST_COMPILE,
    requiresDependencyResolution = ResolutionScope.TEST,
    threadSafe = true
)
public class TestCompileMojo extends BaseCompileMojo {

  /**
   * The source directory of test-scope Epigraph Schema files. This directory is added to the classpath at schema
   * compiling time. All files can therefore be referenced as classpath resources following the directory structure
   * under the source directory.
   */
  @Parameter(defaultValue = "${project.build.testSourceDirectory}/../epigraph")
  private File testSourceDirectory;

  /**
   * The directory in which to place compilation output.
   */
  @Parameter(defaultValue = "${project.build.testOutputDirectory}")
  private File testOutputDirectory;

  /**
   * A set of Ant-like inclusion patterns used to select files from the source directory for processing. By default, the
   * pattern <code>**&#47;*.epigraph</code> is used to select Epigraph Schema files.
   */
  @Parameter
  private String[] testIncludes = {SCHEMA_FILE_ANT_PATTERN};

  /**
   * A set of Ant-like exclusion patterns used to prevent certain files from being processed. By default, this set is
   * empty such that no files are excluded.
   */
  @Parameter
  private String[] testExcludes = {};

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

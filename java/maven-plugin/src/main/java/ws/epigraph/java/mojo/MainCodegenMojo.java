/*
 * Copyright 2017 Sumo Logic
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

package ws.epigraph.java.mojo;

import org.apache.maven.model.Resource;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.java.Settings;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Generate Java bindings for Epigraph Schema source files (.epigraph).
 */
@Mojo(
    name = MainCodegenMojo.MOJO_GOAL_NAME,
    defaultPhase = LifecyclePhase.GENERATE_SOURCES,
    requiresDependencyResolution = ResolutionScope.COMPILE,
    threadSafe = true
)
public class MainCodegenMojo extends BaseCodegenMojo {

  /** The name of this mojo goal. */
  public static final String MOJO_GOAL_NAME = "generate-sources"; // TODO "generateSources"?

  /**
   * The source directory of Epigraph Schema files. This directory is added to the
   * classpath at schema compiling time. All files can therefore be referenced
   * as classpath resources following the directory structure under the
   * source directory.
   */
  @Parameter(defaultValue = "${project.build.sourceDirectory}/../epigraph")
  protected File sourceDirectory;

  /**
   *
   */
  @Parameter(defaultValue = "${project.build.directory}/generated-sources/epigraph.java")
  private File outputDirectory;

  /**
   * A set of Ant-like inclusion patterns used to select files from the source
   * directory for processing. By default, the pattern
   * <code>**&#47;*.epigraph</code> is used to select epigraph Schema files.
   */
  @Parameter
  private String[] includes = {SCHEMA_FILE_ANT_PATTERN};

  /**
   * A set of Ant-like exclusion patterns used to prevent certain files from
   * being processed. By default, this set is empty such that no files are
   * excluded.
   */
  @Parameter
  private String[] excludes = {};

  @Override
  protected String[] includes() { return includes; }

  @Override
  protected String[] excludes() { return excludes; }

  /**
   * Server generator configuration
   */
  @Parameter
  private Server server = null; // keep name in sync with `EpigraphJavaPluginExtension.groovy`

  /**
   * Client generator configuration
   */
  @Parameter
  private Client client = null; // keep name in sync with `EpigraphJavaPluginExtension.groovy`

  @Override
  protected Collection<? extends String> getSourceRoots(@NotNull MavenProject project) throws IOException {
    project.addCompileSourceRoot(sourceDirectory.getCanonicalPath());
    return project.getCompileSourceRoots();
  }

  @Override
  protected boolean dependsOnMainOutput() { return false; }

  @Override
  protected File getOutputDirectory() { return outputDirectory; }

  @Override
  protected void addResultsToProject(String path, Resource resources) {
    project.addCompileSourceRoot(path);
    project.addResource(resources);
  }

  @Override
  protected Settings constructSettings() {
    return new Settings(
        new Settings.ServerSettings(
            server != null && server.generate(),
            server == null || server.services() == null ? null : Arrays.asList(server.services()),
            server == null || server.transformers() == null ? null : Arrays.asList(server.transformers())
        ),
        new Settings.ClientSettings(
            client != null && client.generate(),
            client == null || client.services() == null ? null : Arrays.asList(client.services())
        ),
        java8Annotations
    );
  }

}

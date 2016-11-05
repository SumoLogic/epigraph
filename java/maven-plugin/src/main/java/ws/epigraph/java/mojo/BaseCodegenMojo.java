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

package ws.epigraph.java.mojo;

import ws.epigraph.java.JavaSchemaGenerator;
import ws.epigraph.maven.AbstractCompilingMojo;
import ws.epigraph.schema.compiler.CContext;
import ws.epigraph.schema.compiler.FileSource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Collection;


/**
 * Base for Epigraph Java Codegen Mojos.
 */
public abstract class BaseCodegenMojo extends AbstractCompilingMojo {

  protected void produceOutput(
      @NotNull File outputDirectory,
      @NotNull Collection<? extends FileSource> sources,
      @NotNull CContext ctx
  ) throws MojoExecutionException, MojoFailureException {
    try {
      new JavaSchemaGenerator(ctx, outputDirectory).generate();
      addResultsToProject(project, outputDirectory.getCanonicalPath());
    } catch (IOException e) {
      throw new MojoExecutionException("Error generating sources to " + outputDirectory, e);
    }
  }

  protected abstract void addResultsToProject(MavenProject project, String path);

}

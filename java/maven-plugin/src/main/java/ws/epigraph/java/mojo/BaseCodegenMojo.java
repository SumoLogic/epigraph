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
import org.apache.maven.plugins.annotations.Parameter;
import ws.epigraph.java.EpigraphJavaGenerator;
import ws.epigraph.java.Settings;
import ws.epigraph.maven.AbstractCompilingMojo;
import ws.epigraph.compiler.CContext;
import ws.epigraph.compiler.FileSource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * Base for Epigraph Java Codegen Mojos.
 */
public abstract class BaseCodegenMojo extends AbstractCompilingMojo {

  /** Whether Java 8 annotations should be used in generated code. */
  @Parameter(property = "epigraph.java.java8Annotations", defaultValue = "true")
  protected boolean java8Annotations;

  @Override
  protected void produceOutput(
      @NotNull File outputDirectory,
      @NotNull Collection<? extends FileSource> sources,
      @NotNull CContext ctx
  ) throws MojoExecutionException, MojoFailureException {
    try {
      new EpigraphJavaGenerator(ctx, outputDirectory, constructSettings()).generate();
      String path = outputDirectory.getCanonicalPath();
      addResultsToProject(path, generatedResources(path));
    } catch (IOException e) {
      throw new MojoExecutionException("Error generating sources to " + outputDirectory, e);
    }
  }

  private static Resource generatedResources(String path) {
    Resource resources = new Resource();
    resources.setDirectory(path);
    resources.setExcludes(Collections.singletonList("**/*.java")); // include everything except .java sources
    return resources;
  }

  protected abstract void addResultsToProject(String path, Resource resources);

  protected abstract Settings constructSettings();

}

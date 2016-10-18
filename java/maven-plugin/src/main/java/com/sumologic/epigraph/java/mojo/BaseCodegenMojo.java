/* Created by yegor on 7/6/16. */

package com.sumologic.epigraph.java.mojo;

import com.sumologic.epigraph.java.JavaSchemaGenerator;
import com.sumologic.epigraph.maven.AbstractCompilingMojo;
import com.sumologic.epigraph.schema.compiler.CContext;
import com.sumologic.epigraph.schema.compiler.FileSource;
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

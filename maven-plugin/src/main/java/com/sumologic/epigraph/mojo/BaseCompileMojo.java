/* Created by yegor on 7/6/16. */

package com.sumologic.epigraph.mojo;

import com.sumologic.epigraph.maven.AbstractCompilingMojo;
import com.sumologic.epigraph.schema.compiler.CContext;
import com.sumologic.epigraph.schema.compiler.CSchemaFile;
import com.sumologic.epigraph.schema.compiler.FileSource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.shared.utils.StringUtils;
import org.apache.maven.shared.utils.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Base for Epigraph Compiler Mojos.
 */
public abstract class BaseCompileMojo extends AbstractCompilingMojo {

  protected void produceOutput(
      @NotNull File outputDirectory,
      @NotNull Collection<? extends FileSource> sources,
      @NotNull CContext ctx
  ) throws MojoExecutionException {
    try { // copy source files to namespace-based paths under epigraph artifacts output directory
      File mojoOutputDirectory = new File(getOutputDirectory(), ARTIFACTS_PATH_PREFIX).getCanonicalFile();
      for (FileSource source : sources) {
        String sourceFilename = source.name();
        CSchemaFile schemaFile = ctx.schemaFiles().get(sourceFilename); // schema files are mapped by their source names
        String relativeFilePath = StringUtils.join(schemaFile.namespace().fqn().segments, File.separator);
        FileUtils.copyFileToDirectory(new File(sourceFilename), new File(mojoOutputDirectory, relativeFilePath));
        // TODO there might be collisions (foo/bar.esc and baz/bar.esc moved to the save namespace/bar.esc)...
      }
    } catch (IOException e) {
      throw new MojoExecutionException("Error compiling sources to " + outputDirectory, e);
    }
  }

}

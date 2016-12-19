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

import ws.epigraph.maven.AbstractCompilingMojo;
import ws.epigraph.compiler.CContext;
import ws.epigraph.compiler.CSchemaFile;
import ws.epigraph.compiler.FileSource;
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
        // TODO there might be collisions (foo/bar.epigraph and baz/bar.epigraph moved to the save namespace/bar.epigraph)...
      }
    } catch (IOException e) {
      throw new MojoExecutionException("Error compiling sources to " + outputDirectory, e);
    }
  }

}

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

/* Created by yegor on 10/18/16. */

package ws.epigraph.maven;

import ws.epigraph.compiler.*;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.jetbrains.annotations.NotNull;
import scala.Option;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

/**
 * Base for mojos with epigraph compiler support.
 */
public abstract class AbstractCompilingMojo extends AbstractMojo {

  public static final String SCHEMA_FILE_EXTENSION = "epigraph";

  public static final String SCHEMA_FILE_ANT_PATTERN = "**/*." + SCHEMA_FILE_EXTENSION;

  public static final String ARTIFACTS_PATH_PREFIX = "epigraph$artifacts";

  private static final Pattern SCHEMA_FILE_PATH_PATTERN = Pattern.compile( // TODO use Predicate?
      Pattern.quote(ARTIFACTS_PATH_PREFIX + '/') + ".+\\." + Pattern.quote(SCHEMA_FILE_EXTENSION)
  );

  /**
   * The {@link {MavenProject}.
   */
  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  protected MavenProject project;

  protected abstract Collection<? extends String> getSourceRoots(@NotNull MavenProject project) throws IOException;

  /** e.g. `target/classes` or `target/test-classes` */
  protected abstract File getOutputDirectory();

  protected abstract String[] includes();

  protected abstract String[] excludes();

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      Collection<? extends FileSource> sources = getSources(getSourceDirectories());
      Collection<Source> dependencySources = getDependencySources();
      CContext ctx = compile(sources, dependencySources);
      produceOutput(getOutputDirectory(), sources, ctx);
    } catch (IOException e) {
      throw new MojoExecutionException("Error compiling sources", e);
    }
  }

  private Collection<? extends File> getSourceDirectories() throws IOException {
    Collection<? extends String> sourceRoots = getSourceRoots(project);
    getLog().debug("sourceRoots: " + sourceRoots);
    Collection<File> sourceDirectories = new LinkedHashSet<File>();
    for (String sourceRoot : sourceRoots) {
      File sourceDirectory = new File(sourceRoot);
      if (sourceDirectory.exists()) sourceDirectories.add(sourceDirectory.getCanonicalFile());
      else getLog().debug("Skipping missing source directory '" + sourceRoot + "'");
    }
    getLog().debug("sourceDirectories: " + sourceDirectories);
    return sourceDirectories;
  }

  private Collection<? extends FileSource> getSources(@NotNull Collection<? extends File> sourceDirectories)
      throws IOException {
    Collection<FileSource> sources = new ArrayList<FileSource>();
    for (File sourceDirectory : sourceDirectories) addDirectorySources(sources, sourceDirectory);
    return sources;
  }

  private void addDirectorySources(@NotNull Collection<FileSource> acc, @NotNull File sourceDirectory)
      throws IOException {
    String[] includedFiles = getIncludedFiles(sourceDirectory.getAbsolutePath(), excludes(), includes());
    for (String subpath : includedFiles) acc.add(new FileSource(new File(sourceDirectory, subpath).getCanonicalFile()));
  }

  private String[] getIncludedFiles(String absPath, String[] excludes, String[] includes) {
    FileSetManager fileSetManager = new FileSetManager();
    FileSet fs = new FileSet();
    fs.setDirectory(absPath);
    fs.setFollowSymlinks(false);
    for (String include : includes) fs.addInclude(include);
    for (String exclude : excludes) fs.addExclude(exclude);
    return fileSetManager.getIncludedFiles(fs);
  }

  private Collection<Source> getDependencySources() throws MojoExecutionException, IOException {
    Collection<Source> sources = new ArrayList<Source>();
    for (Artifact artifact : project.getArtifacts()) {
      ArtifactHandler artifactHandler = artifact.getArtifactHandler();
      if (artifactHandler.isAddedToClasspath()) {
        File artifactFile = artifact.getFile();
        // TODO check artifact.getScope(); ???
        if (artifactFile.canRead()) {
          if (artifactFile.isDirectory()) {
            Collection<FileSource> fileSources = new ArrayList<FileSource>();
            addDirectorySources(fileSources, new File(artifactFile, ARTIFACTS_PATH_PREFIX));
            sources.addAll(fileSources);
          } else if ("jar".equals(artifactHandler.getExtension())) {
            addSourcesFromJar(artifactFile, sources);
          } else {
            ; // ignore non-jar artifacts since epigraph is not producing these
          }
        } else {
          getLog().warn("Skipping " + artifact + " - '" + artifactFile + "' is not readable");
        }
      }
    }
    if (dependsOnMainOutput()) {
      Collection<FileSource> fileSources = new ArrayList<FileSource>();
      addDirectorySources(fileSources, new File(project.getBuild().getOutputDirectory(), ARTIFACTS_PATH_PREFIX));
      sources.addAll(fileSources);
    }
    return sources;
  }

  /** Whether to add main phase output to dependencies. */
  protected abstract boolean dependsOnMainOutput();

  private void addSourcesFromJar(@NotNull File file, @NotNull Collection<Source> sources) throws IOException {
    final JarFile jarFile = new JarFile(file);
    Iterator<JarSource> jarSources = JarSource.allFiles(jarFile, SCHEMA_FILE_PATH_PATTERN, Charset.forName("UTF-8"));
    while (jarSources.hasNext()) sources.add(jarSources.next());
  }

  private @NotNull CContext compile(
      Collection<? extends FileSource> sources,
      Collection<? extends Source> dependencySources
  ) throws MojoFailureException {
    // TODO catch and sort compiler exceptions into MojoExecutionException (abnormal) and MojoFailureException (normal failure)
    EpigraphCompiler compiler = new EpigraphCompiler(sources, dependencySources);
    try {
      return compiler.compile();
    } catch (EpigraphCompilerException failure) {
      StringBuilder sb = new StringBuilder();
      for (CError err : compiler.ctx().errors()) {
        CErrorPosition pos = err.position(); // TODO skip :line:colon, line text, and ^ if NA
        sb.append(err.filename()).append(':').append(pos.line()).append(':').append(pos.column()).append('\n');
        sb.append("Error: ").append(err.message()).append('\n');
        Option<String> errText = pos.lineText();
        if (errText.nonEmpty()) {
          sb.append('\177').append(errText.get()).append('\n');
          sb.append('\177').append(String.format("%" + (pos.column()) + "s", "^")).append('\n');
        }
      }
      throw new MojoFailureException(this, "Schema compilation failed", sb.toString());
    }

  }

  protected abstract void produceOutput(
      @NotNull File outputDirectory,
      @NotNull Collection<? extends FileSource> sources,
      @NotNull CContext ctx
  ) throws MojoExecutionException, MojoFailureException;

}

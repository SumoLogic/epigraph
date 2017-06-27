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

package ws.epigraph.scala.mojo;

import ws.epigraph.compiler.*;
import ws.epigraph.scala.ScalaSchemaGenerator;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import scala.Option;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Base for Epigraph Codegen Mojos.
 */
public abstract class BaseCodegenMojo extends AbstractMojo {
  private static final Pattern SCHEMA_FILENAME_PATTERN = Pattern.compile(".+\\.epigraph");

  /**
   * The source directory of Epigraph Schema files. This directory is added to the
   * classpath at schema compiling time. All files can therefore be referenced
   * as classpath resources following the directory structure under the
   * source directory.
   */
  @Parameter(defaultValue = "${project.basedir}/src/main/epigraph")
  protected File sourceDirectory;

  /**
   *
   */
  @Parameter(defaultValue = "${project.build.directory}/generated-sources/epigraph-scala")
  private File outputDirectory;

  /**
   * The {@link {MavenProject}.
   */
  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  protected MavenProject project;

  protected abstract File getSourceDirectory();

  protected abstract String[] getIncludes();

  protected abstract String[] getExcludes();

  protected abstract void addGeneratedSourcesToProject(MavenProject project, String path);

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    Collection<Source> sources = getSources(getSourceDirectory());
    Collection<Source> dependencySources = getDependencySources();
    addImpliedDependencies(dependencySources);

    generateSources(outputDirectory, sources, dependencySources);
    addGeneratedSourcesToProject(project, outputDirectory.getAbsolutePath());
    // TODO? project.addTestCompileSourceRoot(testOutputDirectory.getAbsolutePath());
  }

  private void addImpliedDependencies(Collection<Source> dependencySources) {
    if (getClass().getResource("/epigraph/builtinTypes.epigraph") != null) {
      Source builtinTypes = new ResourceSource("/epigraph/builtinTypes.epigraph"); // TODO use url.openStream?
      dependencySources.add(builtinTypes);
    }
  }

  private Collection<Source> getSources(File sourceDirectory) {
    return getDirectorySources(sourceDirectory);
  }

  protected Collection<Source> getDirectorySources(File sourceDirectory) {
    Collection<Source> sources = new ArrayList<>();
    if (sourceDirectory != null && sourceDirectory.isDirectory()) {
      String[] includedFiles = getIncludedFiles(sourceDirectory.getAbsolutePath(), getExcludes(), getIncludes());
      for (String subpath : includedFiles) sources.add(new FileSource(new File(sourceDirectory, subpath)));
    }
    // TODO? source encoding like: compiler.setOutputCharacterEncoding(project.getProperties().getProperty("project.build.sourceEncoding"));
    return sources;
  }

  protected Collection<Source> getDependencySources() throws MojoExecutionException {
    Collection<Source> sources = new ArrayList<>();
    List<Artifact> epigraphSchemaArtifacts = typedArtifacts(project.getArtifacts(), "epigraph-schema");
    for (Artifact artifact : epigraphSchemaArtifacts) {
      File artifactFile = artifact.getFile();
      try {
        System.out.println("Adding sources from " + artifactFile);
        addSourcesFromJar(artifactFile, sources);
      } catch (IOException e) {
        throw new MojoExecutionException("Error reading artifact " + artifactFile, e);
      }
    }
    return sources;
  }

  private static List<Artifact> typedArtifacts(Set<Artifact> artifacts, String type) {
    return type == null
        ? new ArrayList<>(artifacts)
        : artifacts.stream().filter(artifact -> type.equals(artifact.getType())).collect(Collectors.toList());
  }

  private void addSourcesFromJar(File file, Collection<Source> sources) throws IOException {
    try (JarFile jarFile = new JarFile(file)) {
      // TODO? source encoding like: compiler.setOutputCharacterEncoding(project.getProperties().getProperty("project.build.sourceEncoding"));
      JarSource.allFiles(jarFile, SCHEMA_FILENAME_PATTERN, StandardCharsets.UTF_8).forEachRemaining(sources::add);
    }
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

  private void generateSources(File outputDirectory, Collection<Source> sources, Collection<Source> dependencySources) throws MojoExecutionException, MojoFailureException {
    CContext ctx = compileFiles(outputDirectory, sources, dependencySources);
    try {
      new ScalaSchemaGenerator(ctx, outputDirectory).generate();
    } catch (IOException e) {
      throw new MojoExecutionException("Error generating sources to " + outputDirectory, e);
    }
  }

  private CContext compileFiles(File outputDirectory, Collection<Source> sources, Collection<Source> dependencySources) throws MojoExecutionException, MojoFailureException {
    try {
      return doCompile(outputDirectory, sources, dependencySources);
    } catch (IOException e) {
      throw new MojoExecutionException("Error compiling sources to " + outputDirectory, e);
    }
  }

  // todo duplicates code from AbstractCompilingMojo
  private CContext doCompile(File outputDirectory, Collection<Source> sources, Collection<Source> dependencySources)
      throws IOException, MojoFailureException {
    // TODO catch and sort compiler exceptions into MojoExecutionException (abnormal) and MojoFailureException (normal failure)
    final EpigraphCompiler compiler = new EpigraphCompiler(sources, dependencySources);
    try {
      return compiler.compile();
    } catch (EpigraphCompilerException failure) {
      StringBuilder sb = new StringBuilder();
      for (CMessage err : compiler.ctx().errors()) {
        CMessagePosition pos = err.position(); // TODO skip :line:colon, line text, and ^ if NA
        sb.append(err.filename()).append(':').append(pos.line()).append(':').append(pos.column()).append('\n');


        if (err.level().equals(CMessageLevel.Error$.MODULE$)) {
          sb.append("Error");
        } else if (err.level().equals(CMessageLevel.Warning$.MODULE$)) {
          sb.append("Warning");
        } else {
          sb.append("Message");
        }

        sb.append(": ").append(err.message()).append('\n');
        Option<String> errText = pos.lineText();
        if (errText.nonEmpty()) {
          sb.append('\177').append(errText.get()).append('\n');
          sb.append('\177').append(String.format("%" + (pos.column()) + "s", "^")).append('\n');
        }
      }
      throw new MojoFailureException(this, "Schema compilation failed", sb.toString());
    }
  }

}

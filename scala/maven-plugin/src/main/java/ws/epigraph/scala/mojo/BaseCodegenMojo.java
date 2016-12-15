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

package ws.epigraph.scala.mojo;

import ws.epigraph.compiler.*;
import ws.epigraph.scala.ScalaEdlGenerator;
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

/**
 * Base for Epigraph Codegen Mojos.
 */
public abstract class BaseCodegenMojo extends AbstractMojo {
  private static final Pattern EDL_FILENAME_PATTERN = Pattern.compile(".+\\.epigraph");

  /**
   * The source directory of Epigraph EDL files. This directory is added to the
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
    List<Artifact> epigraphEdlArtifacts = typedArtifacts(project.getArtifacts(), "epigraph-edl");
    for (Artifact artifact : epigraphEdlArtifacts) {
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

  private List<Artifact> typedArtifacts(Set set, String type) {
    List<Artifact> artifacts = new ArrayList<>();
    for (Object object : set) {
      if (object instanceof Artifact) {
        Artifact artifact = (Artifact) object;
        if (type == null || type.equals(artifact.getType())) artifacts.add(artifact);
      }
    }
    return artifacts;
  }

//  private List<Artifact> classifiedArtifacts(Set set, String classifier) {
//    List<Artifact> artifacts = new ArrayList<>();
//    for (Object object : set) {
//      if (object instanceof Artifact) {
//        Artifact artifact = (Artifact) object;
//        if (classifier.equals(artifact.getClassifier())) {
//          artifacts.add(artifact);
//        }
//      }
//    }
//    return artifacts;
//  }

  private void addSourcesFromJar(File file, Collection<Source> sources) throws IOException {
    final JarFile jarFile = new JarFile(file);
    // TODO? source encoding like: compiler.setOutputCharacterEncoding(project.getProperties().getProperty("project.build.sourceEncoding"));
    JarSource.allFiles(jarFile, EDL_FILENAME_PATTERN, StandardCharsets.UTF_8).forEachRemaining(sources::add);
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
      new ScalaEdlGenerator(ctx, outputDirectory).generate();
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

  private CContext doCompile(File outputDirectory, Collection<Source> sources, Collection<Source> dependencySources)
      throws IOException, MojoFailureException {
    // TODO catch and sort compiler exceptions into MojoExecutionException (abnormal) and MojoFailureException (normal failure)
    final EpigraphCompiler compiler = new EpigraphCompiler(sources, dependencySources);
    try {
      return compiler.compile();
    } catch (EpigraphCompilerException failure) {
      StringBuilder sb = new StringBuilder();
      for (CError err : compiler.ctx().errors()) {
        final CErrorPosition pos = err.position(); // TODO skip :line:colon, line text, and ^ if NA
        sb.append(err.filename()).append(':').append(pos.line()).append(':').append(pos.column()).append('\n');
        sb.append("Error: ").append(err.message()).append('\n');
        final Option<String> errText = pos.lineText();
        if (errText.nonEmpty()) {
          sb.append(errText.get()).append('\n');
          sb.append(String.format("%" + (pos.column()) + "s", "^").replace(" ", ".")).append('\n');
        }
      }
      throw new MojoFailureException(this, "EDL compilation failed", sb.toString());
    }
  }

}

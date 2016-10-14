/* Created by yegor on 7/6/16. */

package com.sumologic.epigraph.java.mojo;

import com.sumologic.epigraph.java.JavaSchemaGenerator;
import com.sumologic.epigraph.schema.compiler.*;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

/**
 * Base for Epigraph Java Codegen Mojos.
 */
public abstract class BaseCodegenMojo extends AbstractMojo {

  private static final Pattern SCHEMA_FILENAME_PATTERN = Pattern.compile(".+\\.esc");

  /**
   * The source directory of Epigraph schema files. This directory is added to the
   * classpath at schema compiling time. All files can therefore be referenced
   * as classpath resources following the directory structure under the
   * source directory.
   */
  @Parameter(defaultValue = "${project.basedir}/src/main/epigraph")
  protected File sourceDirectory;

  /**
   *
   */
  @Parameter(defaultValue = "${project.build.directory}/generated-sources/epigraph.java")
  private File outputDirectory;

  /**
   * The @link {MavenProject}.
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
    if (getClass().getResource("/epigraph/builtinTypes.esc") != null) {
      Source builtinTypes = new ResourceSource("/epigraph/builtinTypes.esc"); // TODO use url.openStream?
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
//  Collection<Artifact> epigraphArtifacts = typedArtifacts(project.getArtifacts(), "epigraph-schema");
    Collection<? extends Artifact> epigraphArtifacts = classifiedArtifacts(
        project.getArtifacts(),
        "epigraph-sources",
        "epigraph-test-sources"
    );
    for (Artifact artifact : epigraphArtifacts) {
      File artifactFile = artifact.getFile();
      try {
        getLog().info("Adding sources from " + artifactFile);
        addSourcesFromJar(artifactFile, sources);
      } catch (IOException e) {
        throw new MojoExecutionException("Error reading artifact " + artifactFile, e);
      }
    }
    return sources;
  }

//  private Collection<? extends Artifact> typedArtifacts(Iterable<? extends Artifact> artifacts, String type) {
//    List<Artifact> typedArtifacts = new ArrayList<Artifact>();
//    for (Artifact artifact : artifacts) {
//      if (type == null || type.equals(artifact.getType())) typedArtifacts.add(artifact);
//    }
//    return typedArtifacts;
//  }

  private Collection<? extends Artifact> classifiedArtifacts(
      Iterable<? extends Artifact> artifacts,
      String... classifiers
  ) {
    List<Artifact> classifiedArtifacts = new ArrayList<Artifact>();
    Collection<? extends String> classifierSet = new HashSet<String>(Arrays.asList(classifiers));
    for (Artifact artifact : artifacts) {
      if (classifierSet.contains(artifact.getClassifier())) classifiedArtifacts.add(artifact);
    }
    return classifiedArtifacts;
  }

  private void addSourcesFromJar(File file, Collection<Source> sources) throws IOException {
    final JarFile jarFile = new JarFile(file);
    // TODO? source encoding like: compiler.setOutputCharacterEncoding(project.getProperties().getProperty("project.build.sourceEncoding"));
    JarSource.allFiles(jarFile, SCHEMA_FILENAME_PATTERN, StandardCharsets.UTF_8).forEachRemaining(sources::add);
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

  private void generateSources(File outputDirectory, Collection<Source> sources, Collection<Source> dependencySources)
      throws MojoExecutionException, MojoFailureException {
    CContext ctx = compileFiles(outputDirectory, sources, dependencySources);
    try {
      new JavaSchemaGenerator(ctx, outputDirectory).generate();
    } catch (IOException e) {
      throw new MojoExecutionException("Error generating sources to " + outputDirectory, e);
    }
  }

  private CContext compileFiles(File outputDirectory, Collection<Source> sources, Collection<Source> dependencySources)
      throws MojoExecutionException, MojoFailureException {
    try {
      return doCompile(outputDirectory, sources, dependencySources);
    } catch (IOException e) {
      throw new MojoExecutionException("Error compiling sources to " + outputDirectory, e);
    }
  }

  private CContext doCompile(File outputDirectory, Collection<Source> sources, Collection<Source> dependencySources)
      throws IOException, MojoFailureException {
    // TODO catch and sort compiler exceptions into MojoExecutionException (abnormal) and MojoFailureException (normal failure)
    final SchemaCompiler compiler = new SchemaCompiler(sources, dependencySources);
    try {
      return compiler.compile();
    } catch (SchemaCompilerException failure) {
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

}

/* Created by yegor on 7/6/16. */

package com.sumologic.epigraph.mojo;

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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

/**
 * Base for Epigraph Compiler Mojos.
 */
public abstract class BaseCompileMojo extends AbstractMojo {

  private static final Pattern SCHEMA_FILENAME_PATTERN = Pattern.compile(".+\\.esc"); // TODO use Predicate?

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
  @Parameter(defaultValue = "${project.build.directory}/epigraph-schema")
  private File outputDirectory;

  /**
   * The {@link {MavenProject}.
   */
  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  protected MavenProject project;

  protected abstract File getSourceDirectory();

  protected abstract String[] getIncludes();

  protected abstract String[] getExcludes();

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    Collection<Source> sources = getSources(getSourceDirectory());
    Collection<Source> dependencySources = getDependencySources();
    addImpliedDependencies(dependencySources);

    compileFiles(outputDirectory, sources, dependencySources);
    // TODO? project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
    // TODO? project.addTestCompileSourceRoot(testOutputDirectory.getAbsolutePath());
  }

  private void addImpliedDependencies(Collection<Source> dependencySources) {
    if (getClass().getResource("/epigraph/builtinTypes.esc") != null) {
      Source builtinTypes = new ResourceSource("/epigraph/builtinTypes.esc"); // TODO use url.openStream?
      dependencySources.add(builtinTypes);
    }
  }

  private Collection<Source> getSources(File sourceDirectory) { return getDirectorySources(sourceDirectory); }

  protected Collection<Source> getDirectorySources(File sourceDirectory) {
    Collection<Source> sources = new ArrayList<Source>();
    if (sourceDirectory != null && sourceDirectory.isDirectory()) {
      String[] includedFiles = getIncludedFiles(sourceDirectory.getAbsolutePath(), getExcludes(), getIncludes());
      for (String subpath : includedFiles) sources.add(new FileSource(new File(sourceDirectory, subpath)));
    }
    // TODO? source encoding like: compiler.setOutputCharacterEncoding(project.getProperties().getProperty("project.build.sourceEncoding"));
    return sources;
  }

  protected Collection<Source> getDependencySources() throws MojoExecutionException {
    Collection<Source> sources = new ArrayList<Source>();
    List<Artifact> epigraphSchemaArtifacts = typedArtifacts(project.getArtifacts(), "epigraph-schema");
    for (Artifact artifact : epigraphSchemaArtifacts) {
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

  private List<Artifact> typedArtifacts(Set set, String type) {
    List<Artifact> artifacts = new ArrayList<Artifact>();
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
    Iterator<JarSource> jarSources = JarSource.allFiles(jarFile, SCHEMA_FILENAME_PATTERN, Charset.forName("UTF-8"));
    while (jarSources.hasNext()) { sources.add(jarSources.next()); }
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

  private void compileFiles(File outputDirectory, Collection<Source> sources, Collection<Source> dependencySources)
      throws MojoExecutionException, MojoFailureException {
    try {
      doCompile(outputDirectory, sources, dependencySources);
    } catch (IOException e) {
      throw new MojoExecutionException("Error compiling sources to " + outputDirectory, e);
    }
  }

  private void doCompile(File outputDirectory, Collection<Source> sources, Collection<Source> dependencySources)
      throws IOException, MojoFailureException {
    // TODO catch and sort compiler exceptions into MojoExecutionException (abnormal) and MojoFailureException (normal failure)
    final SchemaCompiler compiler = new SchemaCompiler(sources, dependencySources);
    try {
      CContext ctx = compiler.compile();
    } catch (SchemaCompilerException failure) {
      StringBuilder sb = new StringBuilder();
      for (CError err : compiler.ctx().errors()) {
        final CErrorPosition pos = err.position(); // TODO skip :line:colon, line text, and ^ if NA
        sb.append(err.filename()).append(':').append(pos.line()).append(':').append(pos.column()).append('\n');
        sb.append("Error: ").append(err.message()).append('\n');
        final Option<String> errText = pos.lineText();
        if (errText.nonEmpty()) {
          sb.append('\177').append(errText.get()).append('\n');
          sb.append('\177').append(String.format("%" + (pos.column()) + "s", "^")).append('\n');
        }
      }
      throw new MojoFailureException(this, "Schema compilation failed", sb.toString());
    }

  }

}

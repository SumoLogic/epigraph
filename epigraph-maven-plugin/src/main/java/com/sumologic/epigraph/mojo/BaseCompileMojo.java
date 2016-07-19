package com.sumologic.epigraph.mojo;/* Created by yegor on 7/6/16. */

import com.sumologic.epigraph.schema.compiler.FileSource;
import com.sumologic.epigraph.schema.compiler.JarSource;
import com.sumologic.epigraph.schema.compiler.Source;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;


import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;

/**
 * Base for Epigraph Compiler Mojos.
 */
public abstract class BaseCompileMojo extends AbstractMojo {

  /**
   * The source directory of epigraph files. This directory is added to the
   * classpath at schema compiling time. All files can therefore be referenced
   * as classpath resources following the directory structure under the
   * source directory.
   */
  @Parameter(defaultValue = "${project.basedir}/src/main/epigraph")
  private File sourceDirectory;

  /**
   *
   */
  @Parameter(defaultValue = "${project.build.directory}/epigraph-sources")
  private File outputDirectory;

  /**
   *
   */
  @Parameter(defaultValue = "${project.basedir}/src/test/epigraph")
  private File testSourceDirectory;

  /**
   *
   */
  @Parameter(defaultValue = "${project.build.directory}/test-epigraph-sources")
  private File testOutputDirectory;

  /**
   * A list of files or directories that should be compiled first thus making
   * them importable by subsequently compiled schemas. Note that imported files
   * should not reference each other.
   */
  @Parameter
  protected String[] imports;

  /**
   * A set of Ant-like exclusion patterns used to prevent certain files from
   * being processed. By default, this set is empty such that no files are
   * excluded.
   */
  @Parameter
  protected String[] excludes = new String[0];

  /**
   * A set of Ant-like exclusion patterns used to prevent certain files from
   * being processed. By default, this set is empty such that no files are
   * excluded.
   */
  @Parameter
  protected String[] testExcludes = new String[0];

  /**
   * The {@link {MavenProject}.
   */
  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  protected MavenProject project;

  @Component
  private MavenProjectHelper projectHelper;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {

    final Collection<Source> mainDependencySources = new ArrayList<>(); // TODO split into dependencySources, mainSources, testSources

    final List<Artifact> epigraphArtifacts = typedArtifacts(project.getArtifacts(), "epigraph-schema");

    for (Artifact artifact : epigraphArtifacts) {
      final File artifactFile = artifact.getFile();
      try {
        System.out.println("Adding sources from " + artifactFile);
        addSourcesFromJar(artifactFile, mainDependencySources);
      } catch (IOException e) {
        throw new MojoExecutionException("Error reading artifact " + artifactFile, e);
      }
    }

    boolean hasImports = !mainDependencySources.isEmpty();

    final Collection<Source> testDependencySources = new ArrayList<>(); // TODO populate from test jars

    // TODO find main/test sources
    // TODO find generated sources

    boolean hasTestDir = testSourceDirectory != null && testSourceDirectory.isDirectory();

//    if (!hasSourceDir && !hasTestDir) {
//      throw new MojoExecutionException("neither sourceDirectory: "
//          + sourceDirectory + " or testSourceDirectory: " + testSourceDirectory
//          + " are directories");
//    }

//    if (hasImports) {
//      for (String importedFile : imports) {
//        File file = new File(importedFile);
//        if (file.isDirectory()) {
//          String[] includedFiles = getIncludedFiles(file.getAbsolutePath(), excludes, getIncludes());
//          getLog().info("Importing Directory: " + file.getAbsolutePath());
//          getLog().debug("Importing Directory Files: " + Arrays.toString(includedFiles));
//          compileFiles(includedFiles, file, outputDirectory);
//        } else if (file.isFile()) {
//          getLog().info("Importing File: " + file.getAbsolutePath());
//          compileFiles(new String[]{file.getName()}, file.getParentFile(), outputDirectory);
//        }
//      }
//    }

    Collection<Source> mainSources = new ArrayList<>();

    boolean hasSourceDir = sourceDirectory != null && sourceDirectory.isDirectory();
    if (hasSourceDir) {
      String[] includedFiles = getIncludedFiles(sourceDirectory.getAbsolutePath(), excludes, getIncludes());
      for (String path : includedFiles) mainSources.add(new FileSource(new File(sourceDirectory, path)));
      compileFiles(outputDirectory, mainSources, mainDependencySources);
    }

    if (hasImports || hasSourceDir) {
      project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
    }

    Collection<Source> testSources = new ArrayList<>();

    if (hasTestDir) {
      String[] includedFiles = getIncludedFiles(testSourceDirectory.getAbsolutePath(), testExcludes, getTestIncludes());
      for (String path : includedFiles) testSources.add(new FileSource(new File(testSourceDirectory, path)));
      Collection<Source> testDependencies = merge(mainSources, mainDependencySources, testDependencySources);
      testDependencies.addAll(mainSources);
      compileFiles(outputDirectory, testSources, testDependencies);
      project.addTestCompileSourceRoot(testOutputDirectory.getAbsolutePath());
    }

  }

  private Collection<Source> merge(Collection<Source>... sourcess) {
    int size = 0;
    for (Collection<Source> sources : sourcess) size += sources.size();
    Collection<Source> merged = new ArrayList<>(size);
    for (Collection<Source> sources : sourcess) merged.addAll(sources);
    return merged;
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

  private void addSourcesFromJar(File file, Collection<Source> sources) throws IOException {
    final JarFile jarFile = new JarFile(file);
    try (final Stream<JarEntry> jarEntries = jarFile.stream()) {
      jarEntries.filter(
          jarEntry -> jarEntry.getName().endsWith(".esc") && !jarEntry.isDirectory()
      ).map(
          jarEntry -> new JarSource(jarFile, jarEntry)
      ).forEach(sources::add);
//      jarEntries.forEach(jarEntry -> {
//        if (jarEntry.getName().endsWith(".esc") && !jarEntry.isDirectory()) {
//          sources.add(new JarSource(jarFile, jarEntry));
//        }
//      });
    }
  }

  private String[] getIncludedFiles(String absPath, String[] excludes,
                                    String[] includes) {
    FileSetManager fileSetManager = new FileSetManager();
    FileSet fs = new FileSet();
    fs.setDirectory(absPath);
    fs.setFollowSymlinks(false);

    //exclude imports directory since it has already been compiled.
    if (imports != null) {
      String importExclude = null;

      for (String importFile : this.imports) {
        File file = new File(importFile);

        if (file.isDirectory()) {
          importExclude = file.getName() + "/**";
        } else if (file.isFile()) {
          importExclude = "**/" + file.getName();
        }

        fs.addExclude(importExclude);
      }
    }
    for (String include : includes) {
      fs.addInclude(include);
    }
    for (String exclude : excludes) {
      fs.addExclude(exclude);
    }
    return fileSetManager.getIncludedFiles(fs);
  }

  private void compileFiles(File outputDirectory, Collection<Source> sources, Collection<Source> dependencySources) throws MojoExecutionException, MojoFailureException {
    try {
      doCompile(outputDirectory, sources, dependencySources);
    } catch (IOException e) {
      throw new MojoExecutionException("Error compiling sources to " + outputDirectory, e);
    }
  }

  protected abstract void doCompile(File outputDirectory, Collection<Source> sources, Collection<Source> dependencySources) throws IOException, MojoFailureException;

  protected abstract String[] getIncludes();

  protected abstract String[] getTestIncludes();

}

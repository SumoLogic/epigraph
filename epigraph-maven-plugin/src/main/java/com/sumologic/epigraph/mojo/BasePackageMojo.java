/* Created by yegor on 7/18/16. */

package com.sumologic.epigraph.mojo;

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.jar.JarArchiver;

import java.io.File;

public abstract class BasePackageMojo extends AbstractMojo {

  private static final String[] DEFAULT_INCLUDES = new String[]{"**/*.esc"};

  /**
   * List of files to include. Specified as fileset patterns which are relative to the input directory whose contents
   * is being packaged into the JAR.
   */
  @Parameter
  private String[] includes = DEFAULT_INCLUDES;

  private static final String[] DEFAULT_EXCLUDES = new String[0];

  /**
   * List of files to exclude. Specified as fileset patterns which are relative to the input directory whose contents
   * is being packaged into the JAR.
   */
  @Parameter
  private String[] excludes = DEFAULT_EXCLUDES;

  /**
   * Directory containing the generated JAR.
   */
  @Parameter(defaultValue = "${project.build.directory}", required = true)
  private File outputDirectory;

  /**
   * Name of the generated JAR.
   */
  @Parameter(defaultValue = "${project.build.finalName}", readonly = true)
  private String finalName;

  /**
   * The Jar archiver.
   */
  @Component(role = Archiver.class, hint = "jar")
  private JarArchiver jarArchiver;

  /**
   * The archive configuration to use. See <a href="http://maven.apache.org/shared/maven-archiver/index.html">Maven
   * Archiver Reference</a>.
   */
  @Parameter
  private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

  /**
   * The {@link {MavenProject}.
   */
  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  private MavenProject project;

  /**
   * The {@link MavenSession}.
   */
  @Parameter(defaultValue = "${session}", readonly = true, required = true)
  private MavenSession session;

  /**
   *
   */
  @Component
  private MavenProjectHelper projectHelper;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    File jarFile = createArchive();

    if (hasClassifier()) {
      projectHelper.attachArtifact(project, getType(), getClassifier(), jarFile);
    } else {
      if (projectHasAlreadySetAnArtifact()) {
        throw new MojoExecutionException("You have to use a classifier "
            + "to attach supplemental artifacts to the project instead of replacing them.");
      }
      project.getArtifact().setFile(jarFile);
    }

  }

  /**
   * @return true in case where the classifier is not {@code null} and contains something else than white spaces.
   */
  private boolean hasClassifier() {
    String classifier = getClassifier();
    return !(classifier == null || classifier.trim().isEmpty());
  }

  private boolean projectHasAlreadySetAnArtifact() {
    File projectArtifactFile = project.getArtifact().getFile();
    return projectArtifactFile != null && projectArtifactFile.isFile();
  }

  /**
   * Generates the JAR.
   *
   * @return The instance of File for the created archive file.
   * @throws MojoExecutionException in case of an error.
   */
  private File createArchive()
      throws MojoExecutionException {
    File jarFile = getJarFile(outputDirectory, finalName, getClassifier());

    MavenArchiver archiver = new MavenArchiver();

    archiver.setArchiver(jarArchiver);

    archiver.setOutputFile(jarFile);

    try {
      File contentDirectory = getClassesDirectory();
      if (!contentDirectory.exists()) {
        getLog().warn("JAR will be empty - no content was marked for inclusion!");
      } else {
        archiver.getArchiver().addDirectory(contentDirectory, includes, excludes);
      }

      archiver.createArchive(session, project, archive);

      return jarFile;
    } catch (Exception e) {
      // TODO: improve error handling
      throw new MojoExecutionException("Error assembling JAR", e);
    }
  }

  /**
   * Overload this to produce a jar with another classifier, for example a test-jar.
   *
   * @return get the classifier.
   */
  protected abstract String getClassifier();

  /**
   * Overload this to produce a test-jar, for example.
   *
   * @return return the type.
   */
  protected abstract String getType();

  /**
   * Returns the Jar file to generate, based on an optional classifier.
   *
   * @param basedir         the output directory
   * @param resultFinalName the name of the ear file
   * @param classifier      an optional classifier
   * @return the file to generate
   */
  private File getJarFile(File basedir, String resultFinalName, String classifier) {
    if (basedir == null) {
      throw new IllegalArgumentException("basedir is not allowed to be null");
    }
    if (resultFinalName == null) {
      throw new IllegalArgumentException("finalName is not allowed to be null");
    }

    StringBuilder fileName = new StringBuilder(resultFinalName);

    if (hasClassifier()) {
      fileName.append("-").append(classifier);
    }

    fileName.append(".jar");

    return new File(basedir, fileName.toString());
  }

  /**
   * Return the specific output directory to serve as the root for the archive.
   *
   * @return get classes directory.
   */
  protected abstract File getClassesDirectory();

}

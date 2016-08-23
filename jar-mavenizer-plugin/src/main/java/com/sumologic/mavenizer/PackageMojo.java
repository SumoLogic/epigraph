/* Created by yegor on 7/18/16. */

package com.sumologic.mavenizer;

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;

import java.io.File;

@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true, threadSafe = true)
public class PackageMojo extends AbstractMojo {

  @Parameter(required = true)
  private File sourceJar;

  /**
   *
   */
  @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
  private File classesDirectory;

  /**
   * Directory containing the generated JAR.
   */
  @Parameter(defaultValue = "${project.build.directory}", required = true, readonly = true)
  private File outputDirectory;

  /**
   * File name for the generated JAR.
   */
  @Parameter(defaultValue = "${project.build.finalName}", readonly = true)
  private String finalName;

  /**
   * Classifier to add to the artifact generated. If given, the artifact will be attached as a supplemental artifact.
   * If not given this will create the main artifact which is the default behavior.
   * If you try to do that a second time without using a classifier the build will fail.
   */
  @Parameter
  private String classifier = null;

  @Component
  protected ArchiverManager archiverManager;

  @Component(role = UnArchiver.class, hint = "jar")
  private ZipUnArchiver jarUnArchiver;

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
    jarUnArchiver.setSourceFile(sourceJar);
    jarUnArchiver.setDestDirectory(classesDirectory);
    classesDirectory.mkdirs(); // TODO check it returns `true`?
    getLog().info("Unpacking '"+ sourceJar.getAbsolutePath() + "' to '" + classesDirectory.getAbsolutePath() + "'");
    jarUnArchiver.extract();

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
      File contentDirectory = classesDirectory;
      if (!contentDirectory.exists()) {
        getLog().warn("JAR will be empty - no content was marked for inclusion!");
      } else {
        archiver.getArchiver().addDirectory(contentDirectory);
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
  private String getClassifier() {
    return classifier;
  };

  /**
   * Overload this to produce a test-jar, for example.
   *
   * @return return the type.
   */
  protected String getType() {
    return "jar";
  }

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

}

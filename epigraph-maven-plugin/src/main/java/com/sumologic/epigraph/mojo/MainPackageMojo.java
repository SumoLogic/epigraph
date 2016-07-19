package com.sumologic.epigraph.mojo;/* Created by yegor on 7/18/16. */

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * Package Epigraph source files (.esc)
 */
@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true)
public class MainPackageMojo extends BasePackageMojo {

  /**
   * Directory containing the classes and resource files that should be packaged into the JAR.
   */
  @Parameter(defaultValue = "${project.basedir}/src/main/epigraph"/*${project.build.outputDirectory}"*/, required = true)
  private File classesDirectory = null;

  /**
   * Classifier to add to the artifact generated. If given, the artifact will be attached
   * as a supplemental artifact.
   * If not given this will create the main artifact which is the default behavior.
   * If you try to do that a second time without using a classifier the build will fail.
   */
  @Parameter
  private String classifier = null;

  @Override
  protected File getClassesDirectory() {
    return classesDirectory;
  }

  @Override
  protected String getClassifier() {
    return classifier;
  }

  @Override
  protected String getType() {
    return "epigraph-schema";
  }

}

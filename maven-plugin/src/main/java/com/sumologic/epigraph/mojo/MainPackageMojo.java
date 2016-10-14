/* Created by yegor on 7/18/16. */

package com.sumologic.epigraph.mojo;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * Package Epigraph (.esc) source files
 */
@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true, threadSafe = true)
public class MainPackageMojo extends BasePackageMojo {

  /** Directory containing main epigraph sources that should be packaged into the JAR. */
  @Parameter(defaultValue = "${project.basedir}/src/main/epigraph"/*${project.build.outputDirectory}"?*/, required = true)
  private File classesDirectory = null;

  /**
   * Classifier to add to the artifact generated. If given, the artifact will be attached as a supplemental artifact.
   * If not given this will create the main artifact which is the default behavior.
   * If you try to do that a second time without using a classifier the build will fail.
   */
  @Parameter(defaultValue = "epigraph-sources", required = true)
  private String classifier;

  @Override
  protected File getClassesDirectory() { return classesDirectory; }

  @Override
  protected String getClassifier() { return classifier; }

  @Override
  protected String getType() { return null/*"epigraph-schema"*/; }

}

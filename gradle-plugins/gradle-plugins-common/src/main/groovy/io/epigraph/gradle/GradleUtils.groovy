package io.epigraph.gradle

import org.gradle.api.tasks.SourceSet

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class GradleUtils {
  public static boolean isMainSourceSet(SourceSet sourceSet) {
    return sourceSet.name == SourceSet.MAIN_SOURCE_SET_NAME
  }

  public static boolean isTestSourceSet(SourceSet sourceSet) {
    return sourceSet.name == SourceSet.TEST_SOURCE_SET_NAME
  }
}

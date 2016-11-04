package ws.epigraph.gradle

import java.util.regex.Pattern

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class EpigraphConstants {
  // keep this stuff in sync with maven's AbstractCompilingMojo.java

  public static final String SCHEMA_FILE_EXTENSION = 'esc'
//  public static final Pattern SCHEMA_FILE_NAME_PATTERN = Pattern.compile(".+\\." + SCHEMA_FILE_EXTENSION);

  public static final String ARTIFACTS_PATH_PREFIX = 'epigraph$artifacts';
  public static final Pattern SCHEMA_FILE_PATH_PATTERN = Pattern.compile(
      Pattern.quote(ARTIFACTS_PATH_PREFIX + '/') + '.+\\.' + Pattern.quote(SCHEMA_FILE_EXTENSION)
  );
}

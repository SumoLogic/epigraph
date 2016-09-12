package io.epigraph.gradle

import java.util.regex.Pattern

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class EpigraphSchemaConstants {
  public static final String SCHEMA_EXTENSION = 'esc'
  public static final Pattern SCHEMA_FILENAME_PATTERN = Pattern.compile(".+\\." + SCHEMA_EXTENSION);
}

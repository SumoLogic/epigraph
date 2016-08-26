package io.epigraph.lang.schema.parser;

import com.intellij.openapi.fileTypes.LanguageFileType;
import io.epigraph.lang.parser.Common;
import io.epigraph.lang.schema.SchemaLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaFileType extends LanguageFileType {
  public static final SchemaFileType INSTANCE = new SchemaFileType();
  public static final String DEFAULT_EXTENSION = Common.FILE_EXTENSION;

  protected SchemaFileType() {
    super(SchemaLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public String getName() {
    return "epi_schema";
  }

  @NotNull
  @Override
  public String getDescription() {
    return DEFAULT_EXTENSION;
  }

  @NotNull
  @Override
  public String getDefaultExtension() {
    return DEFAULT_EXTENSION;
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return null;
  }
}

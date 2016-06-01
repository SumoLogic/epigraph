package com.sumologic.epigraph.ideaplugin.schema;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.sumologic.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaFileType extends LanguageFileType {
  public static final SchemaFileType INSTANCE = new SchemaFileType();
  public static final String DEFAULT_EXTENSION = "es"; // TODO change to something less widely used

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
    return "Epigraph Schema";
  }

  @NotNull
  @Override
  public String getDefaultExtension() {
    return DEFAULT_EXTENSION;
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return SchemaPresentationUtil.schemaFileIcon();
  }
}

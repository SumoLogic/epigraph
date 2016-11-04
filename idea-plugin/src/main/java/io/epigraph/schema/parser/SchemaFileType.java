package io.epigraph.schema.parser;

import com.intellij.openapi.fileTypes.LanguageFileType;
import ws.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author <a href="mailto:konstantin.sobolev.com">Konstantin Sobolev</a>
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
    return "epigraph_schema";
  }

  @NotNull
  @Override
  public String getDescription() {
    return Common.DESCRIPTION;
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

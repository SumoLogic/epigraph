package io.epigraph.lang.idl.parser;

import com.intellij.openapi.fileTypes.LanguageFileType;
import io.epigraph.lang.idl.IdlLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class IdlFileType extends LanguageFileType {
  public static final IdlFileType INSTANCE = new IdlFileType();
  public static final String DEFAULT_EXTENSION = "eidl";

  protected IdlFileType() {
    super(IdlLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public String getName() {
    return "epi_idl";
  }

  @NotNull
  @Override
  public String getDescription() {
    return "Epigraph IDL";
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

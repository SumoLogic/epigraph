package io.epigraph.url.parser;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UrlFileType extends LanguageFileType {
  public static final UrlFileType INSTANCE = new UrlFileType();

  protected UrlFileType() { super(UrlLanguage.INSTANCE); }

  @NotNull
  @Override
  public String getName() { return "epigraph_url"; }

  @NotNull
  @Override
  public String getDescription() { return ""; }

  @NotNull
  @Override
  public String getDefaultExtension() { return "eurl"; }

  @Nullable
  @Override
  public Icon getIcon() { return null; }
}

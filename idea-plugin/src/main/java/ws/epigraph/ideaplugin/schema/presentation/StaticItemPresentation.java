package ws.epigraph.ideaplugin.schema.presentation;

import com.intellij.navigation.ItemPresentation;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author <a href="mailto:konstantin.sobolev.com">Konstantin Sobolev</a>
 */
public class StaticItemPresentation implements ItemPresentation {
  @Nullable
  private final String presentableText;
  @Nullable
  private final String locationString;
  @Nullable
  private final Icon icon;

  public StaticItemPresentation(@Nullable String presentableText, @Nullable String locationString, @Nullable Icon icon) {
    this.presentableText = presentableText;
    this.locationString = locationString;
    this.icon = icon;
  }

  @Nullable
  @Override
  public String getPresentableText() {
    return presentableText;
  }

  @Nullable
  @Override
  public String getLocationString() {
    return locationString;
  }

  @Nullable
  @Override
  public Icon getIcon(boolean unused) {
    return icon;
  }
}

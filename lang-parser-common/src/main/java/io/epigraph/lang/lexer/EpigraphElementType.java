package io.epigraph.lang.lexer;

import com.intellij.psi.tree.IElementType;
import io.epigraph.lang.EpigraphLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class EpigraphElementType extends IElementType {
  public EpigraphElementType(@NotNull @NonNls String debugName) {
    super(debugName, EpigraphLanguage.INSTANCE);
  }
}

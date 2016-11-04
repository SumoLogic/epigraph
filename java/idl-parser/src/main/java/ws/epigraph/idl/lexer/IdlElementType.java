package ws.epigraph.idl.lexer;

import com.intellij.psi.tree.IElementType;
import ws.epigraph.idl.parser.IdlLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class IdlElementType extends IElementType {
  public IdlElementType(@NotNull @NonNls String debugName) {
    super(debugName, IdlLanguage.INSTANCE);
  }
}

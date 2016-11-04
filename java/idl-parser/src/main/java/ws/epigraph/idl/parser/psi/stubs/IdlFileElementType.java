package ws.epigraph.idl.parser.psi.stubs;

import com.intellij.psi.tree.IFileElementType;
import ws.epigraph.idl.parser.IdlLanguage;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class IdlFileElementType extends IFileElementType {
  public IdlFileElementType() {
    super("epigraph_idl.FILE", IdlLanguage.INSTANCE);
  }
}

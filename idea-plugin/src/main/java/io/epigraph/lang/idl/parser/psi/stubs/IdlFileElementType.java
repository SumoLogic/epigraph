package io.epigraph.lang.idl.parser.psi.stubs;

import com.intellij.psi.tree.IStubFileElementType;
import io.epigraph.lang.idl.IdlLanguage;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class IdlFileElementType extends IStubFileElementType<IdlFileStub> {
  public IdlFileElementType() {
    super("epigraph_idl.FILE", IdlLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public String getExternalId() {
    return "epigraph_idl.FILE";
  }
}

package io.epigraph.idl;

import io.epigraph.idl.parser.psi.*;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.refs.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class TypeRefs {
  // todo this should only belong to standalone version

  @NotNull
  public static TypeRef fromPsi(@NotNull IdlTypeRef psi) throws PsiProcessingException {
    if (psi instanceof IdlFqnTypeRef) {
      IdlFqnTypeRef fqnTypeRefPsi = (IdlFqnTypeRef) psi;
      return new FqnTypeRef(fqnTypeRefPsi.getFqn().getFqn());
    }

    if (psi instanceof IdlAnonList) {
      IdlAnonList anonListPsi = (IdlAnonList) psi;
      @Nullable IdlValueTypeRef valueTypeRefPsi = anonListPsi.getValueTypeRef();
      if (valueTypeRefPsi == null) throw new PsiProcessingException("List item type not specified", psi);
      return new AnonListRef(fromPsi(valueTypeRefPsi));
    }

    if (psi instanceof IdlAnonMap) {
      IdlAnonMap anonMapPsi = (IdlAnonMap) psi;

      @Nullable IdlTypeRef keyTypeRefPsi = anonMapPsi.getTypeRef();
      if (keyTypeRefPsi == null) throw new PsiProcessingException("Map key type not specified", psi);
      TypeRef keyTypeRef = fromPsi(keyTypeRefPsi);

      @Nullable IdlValueTypeRef valueTypeRefPsi = anonMapPsi.getValueTypeRef();
      if (valueTypeRefPsi == null) throw new PsiProcessingException("Map value type not specified", psi);
      ValueTypeRef valueTypeRef = fromPsi(valueTypeRefPsi);

      return new AnonMapRef(keyTypeRef, valueTypeRef);
    }

    throw new PsiProcessingException("Unknown reference type: " + psi.getClass().getName(), psi);
  }

  @NotNull
  public static ValueTypeRef fromPsi(@NotNull IdlValueTypeRef psi) throws PsiProcessingException {
    @Nullable IdlDefaultOverride defaultOverridePsi = psi.getDefaultOverride();
    return new ValueTypeRef(
        fromPsi(psi.getTypeRef()),
        defaultOverridePsi == null ? null : defaultOverridePsi.getVarTagRef().getQid().getCanonicalName()
    );
  }
}

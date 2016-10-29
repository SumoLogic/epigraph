package io.epigraph.url;

import io.epigraph.url.parser.psi.*;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.refs.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class TypeRefs { // todo create and move to url-psi-parser-common?
  @NotNull
  public static TypeRef fromPsi(@NotNull UrlTypeRef psi) throws PsiProcessingException {
    if (psi instanceof UrlQnTypeRef) {
      UrlQnTypeRef fqnTypeRefPsi = (UrlQnTypeRef) psi;
      return new QnTypeRef(fqnTypeRefPsi.getQn().getQn());
    }

    if (psi instanceof UrlAnonList) {
      UrlAnonList anonListPsi = (UrlAnonList) psi;
      @Nullable UrlValueTypeRef valueTypeRefPsi = anonListPsi.getValueTypeRef();
      if (valueTypeRefPsi == null) throw new PsiProcessingException("List item type not specified", psi);
      return new AnonListRef(fromPsi(valueTypeRefPsi));
    }

    if (psi instanceof UrlAnonMap) {
      UrlAnonMap anonMapPsi = (UrlAnonMap) psi;

      @Nullable UrlTypeRef keyTypeRefPsi = anonMapPsi.getTypeRef();
      if (keyTypeRefPsi == null) throw new PsiProcessingException("Map key type not specified", psi);
      TypeRef keyTypeRef = fromPsi(keyTypeRefPsi);

      @Nullable UrlValueTypeRef valueTypeRefPsi = anonMapPsi.getValueTypeRef();
      if (valueTypeRefPsi == null) throw new PsiProcessingException("Map value type not specified", psi);
      ValueTypeRef valueTypeRef = fromPsi(valueTypeRefPsi);

      return new AnonMapRef(keyTypeRef, valueTypeRef);
    }

    throw new PsiProcessingException("Unknown reference type: " + psi.getClass().getName(), psi);
  }

  @NotNull
  public static ValueTypeRef fromPsi(@NotNull UrlValueTypeRef psi) throws PsiProcessingException {
    @Nullable UrlDefaultOverride defaultOverridePsi = psi.getDefaultOverride();

    @Nullable final String overrideTagName;
    if (defaultOverridePsi == null) overrideTagName = null;
    else {
      @Nullable final UrlVarTagRef varTagRef = defaultOverridePsi.getVarTagRef();
      overrideTagName = varTagRef == null ? null : varTagRef.getQid().getCanonicalName();
    }

    return new ValueTypeRef(fromPsi(psi.getTypeRef()), overrideTagName);
  }
}

package com.sumologic.epigraph.ideaplugin.schema.brains;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaPsiUtil;
import com.sumologic.epigraph.schema.parser.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public abstract class SchemaVarTagReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
  /**
   * host type def
   */
  @NotNull
  private final SchemaTypeDef typeDef;

  @NotNull
  private final String tagName;

  private final ResolveCache.Resolver cachedResolver = ((psiReference, incompleteCode) -> resolveImpl());
  private final ResolveCache.PolyVariantResolver<SchemaVarTagReference> polyVariantResolver =
      (reference, incompleteCode) -> multiResolveImpl();

  public SchemaVarTagReference(@NotNull SchemaTypeDef typeDef, @NotNull PsiElement id) {
    super(id);
    this.typeDef = typeDef;

    TextRange range = SchemaPsiUtil.getQidTextRange(id);
    if (range == null) {
      this.tagName = id.getText();
    } else {
      setRangeInElement(range);
      this.tagName = range.substring(id.getText());
    }
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    return ResolveCache.getInstance(myElement.getProject()).resolveWithCaching(this, cachedResolver, false, false);
  }

  @Nullable
  protected PsiElement resolveImpl() {

    return null;
  }

  @NotNull
  @Override
  public ResolveResult[] multiResolve(boolean incompleteCode) {
    return new ResolveResult[0];
  }

  @NotNull
  protected ResolveResult[] multiResolveImpl() {
    return new ResolveResult[0];
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    return new Object[0];
  }
}

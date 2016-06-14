package com.sumologic.epigraph.ideaplugin.schema.brains;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.PsiElementResult;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.util.IncorrectOperationException;
import com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy.TypeMembers;
import com.sumologic.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import com.sumologic.epigraph.schema.parser.psi.SchemaVarTagDecl;
import com.sumologic.epigraph.schema.parser.psi.SchemaVarTypeDef;
import com.sumologic.epigraph.schema.parser.psi.impl.SchemaElementFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaVarTagReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
  /**
   * host type def
   */
  @NotNull
  private final SchemaVarTypeDef typeDef;

  @NotNull
  private final String tagName;

  private final ResolveCache.Resolver cachedResolver = ((psiReference, incompleteCode) -> resolveImpl());
  private final ResolveCache.PolyVariantResolver<SchemaVarTagReference> polyVariantResolver =
      (reference, incompleteCode) -> multiResolveImpl();

  public SchemaVarTagReference(@NotNull SchemaVarTypeDef typeDef, @NotNull PsiElement id) {
    super(id);
    this.typeDef = typeDef;

//    TextRange range = SchemaPsiUtil.getQidTextRange(id);
//    if (range == null) {
//      this.tagName = id.getText();
//    } else {
//      setRangeInElement(range);
//      this.tagName = range.substring(id.getText());
//    }
    this.tagName = id.getText();
    setRangeInElement(new TextRange(0, tagName.length()));
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    return ResolveCache.getInstance(myElement.getProject()).resolveWithCaching(this, cachedResolver, false, false);
  }

  @Nullable
  protected PsiElement resolveImpl() {
    List<SchemaVarTagDecl> tagDecls = TypeMembers.getVarTagDecls(typeDef, tagName);
    if (tagDecls.size() == 0)
      return null;

    return tagDecls.get(0);
  }

  @NotNull
  @Override
  public ResolveResult[] multiResolve(boolean incompleteCode) {
    return ResolveCache.getInstance(myElement.getProject())
        .resolveWithCaching(this, polyVariantResolver, false, incompleteCode);
  }

  @NotNull
  protected ResolveResult[] multiResolveImpl() {
    List<SchemaVarTagDecl> tagDecls = TypeMembers.getVarTagDecls(typeDef, tagName);
    return tagDecls.stream()
        .map(PsiElementResult::new)
        .toArray(ResolveResult[]::new);
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    List<SchemaVarTagDecl> tagDecls = TypeMembers.getVarTagDecls(typeDef, null);
    return tagDecls.stream()
        .map(varTagDecl ->
            LookupElementBuilder.create(varTagDecl)
                .withIcon(SchemaPresentationUtil.getIcon(varTagDecl))
                .withTypeText(SchemaPresentationUtil.getName(varTagDecl.getVarTypeDef(), true))
        )
        .toArray();
  }

  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    PsiElement oldElement = getElement();
    PsiElement newElement = SchemaElementFactory.createId(oldElement.getProject(), newElementName);
    return oldElement.replace(newElement);
  }
}

/*
 * Copyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.epigraph.ideaplugin.edl.brains;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.PsiElementResult;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.util.IncorrectOperationException;
import ws.epigraph.ideaplugin.edl.brains.hierarchy.TypeMembers;
import ws.epigraph.ideaplugin.edl.presentation.SchemaPresentationUtil;
import ws.epigraph.lang.NamingConventions;
import ws.epigraph.edl.parser.SchemaParserDefinition;
import ws.epigraph.edl.parser.psi.SchemaQid;
import ws.epigraph.edl.parser.psi.SchemaVarTagDecl;
import ws.epigraph.edl.parser.psi.SchemaVarTypeDef;
import ws.epigraph.edl.parser.psi.impl.SchemaElementFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
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

  public SchemaVarTagReference(@NotNull SchemaVarTypeDef typeDef, @NotNull SchemaQid id) {
    super(id);
    this.typeDef = typeDef;

    this.tagName = id.getCanonicalName();
    setRangeInElement(new TextRange(0, id.getName().length()));
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
            LookupElementBuilder.create(getCompletionName(varTagDecl))
                .withIcon(SchemaPresentationUtil.getIcon(varTagDecl))
                .withTypeText(SchemaPresentationUtil.getName(varTagDecl.getVarTypeDef(), true))
        )
        .toArray();
  }

  private String getCompletionName(@NotNull SchemaVarTagDecl varTagDecl) {
    String name = varTagDecl.getQid().getCanonicalName();
    return SchemaParserDefinition.isKeyword(name) ?
        NamingConventions.enquote(name) : name;
  }

  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    PsiElement oldElement = getElement();
    PsiElement newElement = SchemaElementFactory.createId(oldElement.getProject(), newElementName);
    return oldElement.replace(newElement);
  }
}

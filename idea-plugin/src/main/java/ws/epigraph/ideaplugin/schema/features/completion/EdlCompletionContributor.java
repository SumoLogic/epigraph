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

package ws.epigraph.ideaplugin.schema.features.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import ws.epigraph.ideaplugin.schema.brains.hierarchy.TypeMembers;
import ws.epigraph.ideaplugin.schema.psi.EdlPsiUtil;
import ws.epigraph.lang.Qn;
import ws.epigraph.schema.parser.EdlLanguage;
import ws.epigraph.schema.parser.EdlParserDefinition;
import ws.epigraph.schema.parser.psi.*;
import ws.epigraph.schema.parser.psi.impl.EdlPsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static ws.epigraph.schema.lexer.EdlElementTypes.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlCompletionContributor extends CompletionContributor {
  // TODO default` doesn't show up in auto-complete/suggestions after `vartype Foo `

  private static final String[] TOP_LEVEL_COMPLETIONS = {
      "abstract ", "record ", "map", "list", "vartype ", "enum ",
      "integer ", "long ", "double ", "boolean ", "string ",
      "supplement ", "resource "
  };

  // items from TOP_LEVEL_COMPLETIONS that can't follow 'abstract' keyword
  private static final Set<String> TOP_LEVEL_CANT_FOLLOW_ABSTRACT = new HashSet<>(Arrays.asList(
      "abstract ", "vartype ", "enum ", "supplement "
  ));

  // which types can have 'extends' clause
  private static final Set<IElementType> DEFS_SUPPORTING_EXTENDS = new HashSet<>(Arrays.asList(
      S_VAR_TYPE_DEF, S_RECORD_TYPE_DEF, S_LIST_TYPE_DEF, S_MAP_TYPE_DEF, S_PRIMITIVE_TYPE_DEF
  ));

  // which types can have 'meta' clause
  private static final Set<IElementType> DEFS_SUPPORTING_META = new HashSet<>(Arrays.asList(
      S_VAR_TYPE_DEF, S_RECORD_TYPE_DEF, S_LIST_TYPE_DEF, S_MAP_TYPE_DEF, S_ENUM_TYPE_DEF, S_PRIMITIVE_TYPE_DEF
  ));

  // which types can have 'supplements' clause
  private static final Set<IElementType> DEFS_SUPPORTING_SUPPLEMENTS = new HashSet<>(Arrays.asList(
      S_VAR_TYPE_DEF, S_RECORD_TYPE_DEF
  ));

  public EdlCompletionContributor() {
    extend(
        CompletionType.BASIC,
        psiElement().withLanguage(EdlLanguage.INSTANCE),
        new CompletionProvider<CompletionParameters>() {
          @Override
          protected void addCompletions(
              @NotNull CompletionParameters parameters,
              ProcessingContext context,
              @NotNull CompletionResultSet result) {
            PsiElement position = parameters.getPosition();
            completeTopLevelKeywords(position, result);     // complete top-level edl keywords
            completeExtendsKeyword(position, result);       // complete `extends` in type defs
            completeMetaKeyword(position, result);          // complete `meta` in type defs
            completeSupplementsKeyword(position, result);   // complete `supplements` in type defs
            completeWith(position, result);                 // complete `with` inside `supplements`
            completeNewTypeName(
                position,
                result
            );          // complete new type names using unresolved references in current file
            completeOverride(position, result);             // complete `override` in field/tag decls
            completeOverrideMember(position, result);       // complete type name after `override`
            completeDefault(position, result);              // complete `default` keyword in `valueTypeRef`

            completeResourceKeywords(position, result);     // complete top-level keywords for resource defs
            completeOperationKeywords(position, result);    // complete top-level keywords for operation defs
          }
        }
    );
  }

  // TODO rewrite using `PsiElementPattern`. See `RncCompletionData` for examples

  private void completeTopLevelKeywords(@NotNull PsiElement position, @NotNull CompletionResultSet result) {
    // Only complete if this token is first one on the current line?
    // otherwise we encourage something like
    // long Foo string Bar
    // (on the same line)

    PsiElement parent = position.getParent();
    if (parent != null) {
      if (parent instanceof EdlTypeDef || parent instanceof EdlSupplementDef) return;

      PsiElement grandParent = parent.getParent();

      boolean completeTypeDef = false;
      boolean completeImport = false;
      boolean completeNamespace = false;
      boolean afterAbstract = EdlPsiUtil.hasPrevSibling(parent, S_ABSTRACT);

      if (grandParent != null) {
        IElementType grandParentElementType = grandParent.getNode().getElementType();

        if (grandParentElementType == S_DEFS) {
          PsiElement nextParentSibling = EdlPsiUtil.nextNonWhitespaceSibling(parent);
          // don't initiate new type completion if we're followed by anything but a new def, e.g. when we're followed by a {..} dummy block
          completeTypeDef = nextParentSibling == null
                            || nextParentSibling instanceof EdlTypeDefWrapper
                            || nextParentSibling instanceof EdlSupplementDef
                            || nextParentSibling instanceof EdlResourceDef;

        } else if (grandParentElementType == S_IMPORT_STATEMENT) {
          if (!EdlPsiUtil.hasNextSibling(grandParent, S_IMPORT_STATEMENT)) {
            completeTypeDef = true;
          }
          completeImport = true;
        } else if (grandParentElementType == S_NAMESPACE_DECL) {
          // check if we're between 'namespace' and 'import'
          EdlImports edlTypeImports = PsiTreeUtil.getNextSiblingOfType(grandParent, EdlImports.class);
          if (edlTypeImports == null || edlTypeImports.getFirstChild() == null) {
            // we're after 'namespace' not followed by 'import'
            completeTypeDef = true;
          }
          completeImport = true;
        } else if (grandParent instanceof EdlFile /* && !EdlPsiUtil.hasNextSibling(grandParent, S_NAMESPACE_DECL) */) {
          completeNamespace = !EdlPsiUtil.hasPrevSibling(position, S_TYPE_DEF_WRAPPER);
        }
      }

      if (completeTypeDef) {
        for (String topLevelKeyword : TOP_LEVEL_COMPLETIONS) {
          if (afterAbstract && TOP_LEVEL_CANT_FOLLOW_ABSTRACT.contains(topLevelKeyword)) continue;
          result.addElement(LookupElementBuilder.create(topLevelKeyword));
        }
      }

      if (completeImport) {
        result.addElement(LookupElementBuilder.create("import "));
      }

      if (completeNamespace) {
        result.addElement(LookupElementBuilder.create("namespace "));
      }
    }
  }

  private void completeExtendsKeyword(@NotNull PsiElement position, @NotNull CompletionResultSet result) {
    EdlPsiUtil.ElementQualifier extendsPresent = element -> {
      if (element instanceof EdlTypeDef) {
        EdlTypeDef edlTypeDef = (EdlTypeDef) element;
        return edlTypeDef.getExtendsDecl() != null;
      }
      return false;
    };

    completeInnerTypedefKeyword(
        position,
        new EdlPsiUtil.ElementTypeQualifier(DEFS_SUPPORTING_EXTENDS),
        extendsPresent,
        "extends ",
        result
    );
  }

  private void completeMetaKeyword(@NotNull PsiElement position, @NotNull CompletionResultSet result) {
    EdlPsiUtil.ElementQualifier metaOrExtendsPresent = element -> {
      if (element instanceof EdlTypeDef) {
        EdlTypeDef edlTypeDef = (EdlTypeDef) element;
        return edlTypeDef.getMetaDecl() != null || edlTypeDef.getExtendsDecl() != null;
      }
      return false;
    };

    completeInnerTypedefKeyword(
        position,
        new EdlPsiUtil.ElementTypeQualifier(DEFS_SUPPORTING_META),
        metaOrExtendsPresent,
        "meta ",
        result
    );
  }

  private void completeSupplementsKeyword(@NotNull PsiElement position, @NotNull CompletionResultSet result) {
    EdlPsiUtil.ElementQualifier metaOrExtendsOrSupplementsPresent = element -> {
      if (element instanceof EdlTypeDef) {
        EdlTypeDef edlTypeDef = (EdlTypeDef) element;
        if (edlTypeDef.getMetaDecl() != null || edlTypeDef.getExtendsDecl() != null) return true;
        if (edlTypeDef instanceof EdlRecordTypeDef) {
          return edlTypeDef.getSupplementsDecl() != null;
        }
        if (edlTypeDef instanceof EdlVarTypeDef) {
          return edlTypeDef.getSupplementsDecl() != null;
        }
      }
      return false;
    };

    completeInnerTypedefKeyword(
        position,
        new EdlPsiUtil.ElementTypeQualifier(DEFS_SUPPORTING_SUPPLEMENTS),
        metaOrExtendsOrSupplementsPresent,
        "supplements ",
        result
    );
  }

  private void completeInnerTypedefKeyword(
      @NotNull PsiElement position,
      @NotNull EdlPsiUtil.ElementQualifier canHaveQualifier,
      @NotNull EdlPsiUtil.ElementQualifier negativeBeforeQualifier,
      @NotNull String completion,
      @NotNull CompletionResultSet result) {

    PsiElement parent = position.getParent();
    if (parent == null) return;

    // this should be the actual type def wrapper
    PsiElement prevParentSibling = EdlPsiUtil.prevNonWhitespaceSibling(parent);

    if (prevParentSibling instanceof EdlTypeDefWrapper) {
      EdlTypeDefWrapper typeDefWrapper = (EdlTypeDefWrapper) prevParentSibling;
      EdlTypeDef typeDef = typeDefWrapper.getElement();

      if (!canHaveQualifier.qualifies(typeDef)) return;
      if (negativeBeforeQualifier.qualifies(typeDef)) return;

      result.addElement(LookupElementBuilder.create(completion));
    }
  }

  private void completeWith(@NotNull PsiElement position, @NotNull final CompletionResultSet result) {
    PsiElement qid = position.getParent();
    if (!(qid instanceof EdlQid)) return;

    if (PsiTreeUtil.getParentOfType(qid, EdlSupplementDef.class) != null) {
      EdlQnTypeRef qnTypeRef = PsiTreeUtil.getParentOfType(qid, EdlQnTypeRef.class);
      if (qnTypeRef != null) {
        PsiElement prevSibling = EdlPsiUtil.prevNonWhitespaceSibling(qnTypeRef);
        if (prevSibling != null
            && prevSibling.getNode().getElementType() != S_COMMA
            && prevSibling.getNode().getElementType() != S_SUPPLEMENT
            && !EdlPsiUtil.hasPrevSibling(qnTypeRef, S_WITH)) {
          result.addElement(LookupElementBuilder.create("with "));
        }
      }
    }

  }

  private void completeNewTypeName(@NotNull PsiElement position, @NotNull final CompletionResultSet result) {
    // complete things like "record <caret>"
    // use all unresolved type references as candidates. In the future: be more intelligent and filter them by kind,
    // see commented out CompletionTest::testUndefinedTypeNameCompletion2

    // first check we're in the correct position
    PsiElement qid = position.getParent();
    if (!(qid instanceof EdlQid)) return;
    PsiElement parent = qid.getParent();
    if (!(parent instanceof EdlTypeDef)) return;

    PsiElement prevSibling = EdlPsiUtil.prevNonWhitespaceSibling(qid);
    if (prevSibling == null) return;

    IElementType prevSiblingElementType = prevSibling.getNode().getElementType();
    if (!EdlParserDefinition.TYPE_KINDS.contains(prevSiblingElementType)) return;

    PsiElement defs = PsiTreeUtil.getParentOfType(parent, EdlDefs.class);
    assert defs != null;

    // now collect all candidates

    defs.accept(new PsiRecursiveElementVisitor() {
      @Override
      public void visitElement(PsiElement element) {
        super.visitElement(element);

        if (element instanceof EdlQnTypeRef) {
          EdlQnTypeRef typeRef = (EdlQnTypeRef) element;

          Qn qn = typeRef.getQn().getQn();
          // only bother if it's single-segment
          if (qn.size() != 1) return;

          PsiReference reference = EdlPsiImplUtil.getReference(typeRef);
          if (reference == null) return;

          if (reference.resolve() != null) return;

          // now here is the place to look around and be more intelligent

          result.addElement(LookupElementBuilder.create(qn.toString()));
        }

      }
    });

  }

  private void completeOverride(@NotNull PsiElement position, @NotNull final CompletionResultSet result) {
    boolean addOverride = false;

//    PsiElement parent = position.getParent();

    EdlRecordTypeDef recordTypeDef = null;
    EdlVarTypeDef varTypeDef = null;

    PsiElement element = EdlPsiUtil.prevNonWhitespaceSibling(position);

    if (element instanceof EdlTypeDefWrapper && element.getFirstChild() instanceof EdlRecordTypeDef) {
      recordTypeDef = (EdlRecordTypeDef) element.getFirstChild();
    }

    if (element instanceof EdlTypeDefWrapper && element.getFirstChild() instanceof EdlVarTypeDef) {
      varTypeDef = (EdlVarTypeDef) element.getFirstChild();
    }

    if (recordTypeDef != null) {
      List<EdlFieldDecl> fieldDecls = TypeMembers.getFieldDecls(recordTypeDef, null);
      if (!fieldDecls.isEmpty()) addOverride = true;
    }

    if (varTypeDef != null) {
      if (!TypeMembers.getVarTagDecls(varTypeDef, null).isEmpty()) addOverride = true;
    }

    element = PsiTreeUtil.prevVisibleLeaf(position);
    if (!addOverride && element != null) {
      IElementType prevElementType = element.getNode().getElementType();

      EdlFieldDecl prevFieldDecl = PsiTreeUtil.getParentOfType(element, EdlFieldDecl.class);
      recordTypeDef = PsiTreeUtil.getParentOfType(element, EdlRecordTypeDef.class);
      if (recordTypeDef != null && isValid(prevFieldDecl, position)) {
        addOverride = true;
      }

      EdlVarTagDecl prevVarTagDecl = PsiTreeUtil.getParentOfType(element, EdlVarTagDecl.class);
      varTypeDef = PsiTreeUtil.getParentOfType(element, EdlVarTypeDef.class);
      if (varTypeDef != null && isValid(prevVarTagDecl, position)) {
        addOverride = true;
      }
    }

    // last checks: if there's anything to override really?
    if (addOverride && recordTypeDef != null && TypeMembers.getOverridableFields(recordTypeDef).isEmpty())
      addOverride = false;

    if (addOverride && varTypeDef != null && TypeMembers.getOverridableTags(varTypeDef).isEmpty())
      addOverride = false;

    if (addOverride) result.addElement(LookupElementBuilder.create("override "));
  }

  private boolean isValid(@Nullable EdlFieldDecl fieldDecl, @NotNull PsiElement position) {
    if (fieldDecl == null) return true;
    if (fieldDecl.getCurlyRight() != null) return true;
    EdlValueTypeRef valueTypeRef = fieldDecl.getValueTypeRef();
    //noinspection RedundantIfStatement
    if (valueTypeRef != null && !PsiTreeUtil.isAncestor(valueTypeRef, position, false)) return true;
    return false;
  }

  private boolean isValid(@Nullable EdlVarTagDecl tagDecl, @NotNull PsiElement position) {
    if (tagDecl == null) return true;
    if (tagDecl.getCurlyRight() != null) return true;
    EdlTypeRef typeRef = tagDecl.getTypeRef();
    //noinspection RedundantIfStatement
    if (typeRef != null && !PsiTreeUtil.isAncestor(typeRef, position, false)) return true;
    return false;
  }

  private void completeOverrideMember(@NotNull PsiElement position, @NotNull final CompletionResultSet result) {
    EdlRecordTypeDef recordTypeDef = null;
    EdlVarTypeDef varTypeDef = null;

    PsiElement element = PsiTreeUtil.prevVisibleLeaf(position);
    if (element == null || element.getNode().getElementType() != S_OVERRIDE) return;


//
//    element = EdlPsiUtil.prevNonWhitespaceSibling(element);
//
//    if (element instanceof EdlTypeDefWrapper && element.getFirstChild() instanceof EdlRecordTypeDef) {
//      recordTypeDef = (EdlRecordTypeDef) element.getFirstChild();
//    }
//
//    if (element instanceof EdlTypeDefWrapper && element.getFirstChild() instanceof EdlVarTypeDef) {
//      varTypeDef = (EdlVarTypeDef) element.getFirstChild();
//    }

    PsiElement elementParent = element.getParent();
    if (elementParent == null) return;

    recordTypeDef = PsiTreeUtil.getParentOfType(element, EdlRecordTypeDef.class);
    if (recordTypeDef == null && elementParent.getNode().getElementType() == S_DEFS) {
      recordTypeDef = PsiTreeUtil.getParentOfType(PsiTreeUtil.prevVisibleLeaf(element), EdlRecordTypeDef.class);
    }

    varTypeDef = PsiTreeUtil.getParentOfType(element, EdlVarTypeDef.class);
    if (varTypeDef == null && elementParent.getNode().getElementType() == S_DEFS) {
      varTypeDef = PsiTreeUtil.getParentOfType(PsiTreeUtil.prevVisibleLeaf(element), EdlVarTypeDef.class);
    }

    if (recordTypeDef != null) {
      List<EdlFieldDecl> overrideableFields = TypeMembers.getOverridableFields(recordTypeDef);
      for (EdlFieldDecl fieldDecl : overrideableFields) {
        result.addElement(LookupElementBuilder.create(fieldDecl));
      }
    }

    if (varTypeDef != null) {
      List<EdlVarTagDecl> overrideableTags = TypeMembers.getOverridableTags(varTypeDef);
      for (EdlVarTagDecl varTagDecl : overrideableTags) {
        result.addElement(LookupElementBuilder.create(varTagDecl));
      }
    }
  }

  private void completeDefault(@NotNull PsiElement position, @NotNull final CompletionResultSet result) {
    EdlValueTypeRef valueTypeRef = PsiTreeUtil.getParentOfType(position, EdlValueTypeRef.class);
    // there is a value type ref and no 'default' in it yet
    if (valueTypeRef == null) {
      PsiElement prevVisibleLeaf = PsiTreeUtil.prevVisibleLeaf(position);
      if (prevVisibleLeaf != null) {
        EdlFieldDecl fieldDecl = PsiTreeUtil.getParentOfType(prevVisibleLeaf, EdlFieldDecl.class);
        if (fieldDecl != null) {
          completeDefault(fieldDecl.getValueTypeRef(), result);
        }
      }
    } else {
      completeDefault(valueTypeRef, result);
    }
  }

  private void completeDefault(@Nullable EdlValueTypeRef valueTypeRef, @NotNull CompletionResultSet result) {
    if (valueTypeRef != null && valueTypeRef.getDefaultOverride() == null) {
      EdlTypeRef typeRef = valueTypeRef.getTypeRef();
      // type ref is an QN type ref (not an anon list or map)
      if (typeRef instanceof EdlQnTypeRef) {
        // resolve it and ensure it points to a var type
        EdlQnTypeRef qnTypeRef = (EdlQnTypeRef) typeRef;
        EdlTypeDef typeDef = qnTypeRef.resolve();
        if (typeDef instanceof EdlVarTypeDef) {
          PsiElement prevSibling = EdlPsiUtil.prevNonWhitespaceSibling(valueTypeRef);
          if (prevSibling != null && prevSibling.getNode().getElementType() == S_COLON) {
            result.addElement(LookupElementBuilder.create("default "));
          }
        }
      }
    }
  }

  // service declarations ----------------------------------------------------------------------------------------------

  private void completeResourceKeywords(@NotNull PsiElement position, @NotNull CompletionResultSet result) {
    // todo
  }

  private void completeOperationKeywords(@NotNull PsiElement position, @NotNull CompletionResultSet result) {
    // todo
  }

}

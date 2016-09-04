package com.sumologic.epigraph.ideaplugin.schema.features.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy.TypeMembers;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaPsiUtil;
import io.epigraph.lang.Fqn;
import io.epigraph.schema.parser.SchemaLanguage;
import io.epigraph.schema.parser.SchemaParserDefinition;
import io.epigraph.schema.parser.psi.*;
import io.epigraph.schema.parser.psi.impl.SchemaPsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static io.epigraph.schema.lexer.SchemaElementTypes.*;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaCompletionContributor extends CompletionContributor {
  // TODO default` doesn't show up in auto-complete/suggestions after `vartype Foo `

  private static final String[] TOP_LEVEL_COMPLETIONS = new String[]{
      "polymorphic ", "abstract ",
      "record ", "map", "list", "vartype ", "enum ",
      "integer ", "long ", "double ", "boolean ", "string ",
      "supplement "
  };

  // items from TOP_LEVEL_COMPLETIONS that can't follow 'polymorphic' keyword
  private static final Set<String> TOP_LEVEL_CANT_FOLLOW_POLY = new HashSet<>(Arrays.asList(
      "polymorphic ", "abstract ", "vartype ", "enum ", "supplement "
  ));

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

  public SchemaCompletionContributor() {
    extend(
        CompletionType.BASIC,
        psiElement().withLanguage(SchemaLanguage.INSTANCE),
        new CompletionProvider<CompletionParameters>() {
          @Override
          protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
            PsiElement position = parameters.getPosition();
            completeTopLevelKeywords(position, result);     // complete top-level schema keywords
            completeExtendsKeyword(position, result);       // complete `extends` in type defs
            completeMetaKeyword(position, result);          // complete `meta` in type defs
            completeSupplementsKeyword(position, result);   // complete `supplements` in type defs
            completeWith(position, result);                 // complete `with` inside `supplements`
            completeNewTypeName(position, result);          // complete new type names using unresolved references in current file
            completeOverride(position, result);             // complete `override` in field/tag decls
            completeOverrideMember(position, result);       // complete type name after `override`
            completePolymorphic(position, result);          // complete `polymorphic` keyword in `valueTypeRef`
            completeDefault(position, result);              // complete `default` keyword in `valueTypeRef`
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
      IElementType parentElementType = parent.getNode().getElementType();
      if (parent instanceof SchemaTypeDef || parent instanceof SchemaSupplementDef) return;

      PsiElement grandParent = parent.getParent();

      boolean completeTypeDef = false;
      boolean completeImport = false;
      boolean completeNamespace = false;
      boolean afterPolymorphic = SchemaPsiUtil.hasPrevSibling(parent, S_POLYMORPHIC);
      boolean afterAbstract = SchemaPsiUtil.hasPrevSibling(parent, S_ABSTRACT);

      if (grandParent != null) {
        IElementType grandParentElementType = grandParent.getNode().getElementType();

        if (grandParentElementType == S_DEFS) {
          PsiElement nextParentSibling = SchemaPsiUtil.nextNonWhitespaceSibling(parent);
          // don't initiate new type completion if we're followed by anything but a new def, e.g. when we're followed by a {..} dummy block
          completeTypeDef = nextParentSibling == null
              || nextParentSibling instanceof SchemaTypeDefWrapper
              || nextParentSibling instanceof SchemaSupplementDef;

        } else if (grandParentElementType == S_IMPORT_STATEMENT) {
          if (!SchemaPsiUtil.hasNextSibling(grandParent, S_IMPORT_STATEMENT)) {
            completeTypeDef = true;
          }
          completeImport = true;
        } else if (grandParentElementType == S_NAMESPACE_DECL) {
          // check if we're between 'namespace' and 'import'
          SchemaImports schemaImports = PsiTreeUtil.getNextSiblingOfType(grandParent, SchemaImports.class);
          if (schemaImports == null || schemaImports.getFirstChild() == null) {
            // we're after 'namespace' not followed by 'import'
            completeTypeDef = true;
          }
          completeImport = true;
        } else if (grandParent instanceof SchemaFile /* && !SchemaPsiUtil.hasNextSibling(grandParent, S_NAMESPACE_DECL) */) {
          completeNamespace = !SchemaPsiUtil.hasPrevSibling(position, S_TYPE_DEF_WRAPPER);
        }
      }

      if (completeTypeDef) {
        for (String topLevelKeyword : TOP_LEVEL_COMPLETIONS) {
          if (afterPolymorphic && TOP_LEVEL_CANT_FOLLOW_POLY.contains(topLevelKeyword)) continue;
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
    SchemaPsiUtil.ElementQualifier extendsPresent = element -> {
      if (element instanceof SchemaTypeDef) {
        SchemaTypeDef schemaTypeDef = (SchemaTypeDef) element;
        return schemaTypeDef.getExtendsDecl() != null;
      }
      return false;
    };

    completeInnerTypedefKeyword(
        position,
        new SchemaPsiUtil.ElementTypeQualifier(DEFS_SUPPORTING_EXTENDS),
        extendsPresent,
        "extends ",
        result);
  }

  private void completeMetaKeyword(@NotNull PsiElement position, @NotNull CompletionResultSet result) {
    SchemaPsiUtil.ElementQualifier metaOrExtendsPresent = element -> {
      if (element instanceof SchemaTypeDef) {
        SchemaTypeDef schemaTypeDef = (SchemaTypeDef) element;
        return schemaTypeDef.getMetaDecl() != null || schemaTypeDef.getExtendsDecl() != null;
      }
      return false;
    };

    completeInnerTypedefKeyword(
        position,
        new SchemaPsiUtil.ElementTypeQualifier(DEFS_SUPPORTING_META),
        metaOrExtendsPresent,
        "meta ",
        result);
  }

  private void completeSupplementsKeyword(@NotNull PsiElement position, @NotNull CompletionResultSet result) {
    SchemaPsiUtil.ElementQualifier metaOrExtendsOrSupplementsPresent = element -> {
      if (element instanceof SchemaTypeDef) {
        SchemaTypeDef schemaTypeDef = (SchemaTypeDef) element;
        if (schemaTypeDef.getMetaDecl() != null || schemaTypeDef.getExtendsDecl() != null) return true;
        if (schemaTypeDef instanceof SchemaRecordTypeDef) {
          return schemaTypeDef.getSupplementsDecl() != null;
        }
        if (schemaTypeDef instanceof SchemaVarTypeDef) {
          return schemaTypeDef.getSupplementsDecl() != null;
        }
      }
      return false;
    };

    completeInnerTypedefKeyword(
        position,
        new SchemaPsiUtil.ElementTypeQualifier(DEFS_SUPPORTING_SUPPLEMENTS),
        metaOrExtendsOrSupplementsPresent,
        "supplements ",
        result);
  }

  private void completeInnerTypedefKeyword(@NotNull PsiElement position,
                                           @NotNull SchemaPsiUtil.ElementQualifier canHaveQualifier,
                                           @NotNull SchemaPsiUtil.ElementQualifier negativeBeforeQualifier,
                                           @NotNull String completion,
                                           @NotNull CompletionResultSet result) {

    PsiElement parent = position.getParent();
    if (parent == null) return;

    // this should be the actual type def wrapper
    PsiElement prevParentSibling = SchemaPsiUtil.prevNonWhitespaceSibling(parent);

    if (prevParentSibling instanceof SchemaTypeDefWrapper) {
      SchemaTypeDefWrapper typeDefWrapper = (SchemaTypeDefWrapper) prevParentSibling;
      SchemaTypeDef typeDef = typeDefWrapper.getElement();

      if (!canHaveQualifier.qualifies(typeDef)) return;
      if (negativeBeforeQualifier.qualifies(typeDef)) return;

      result.addElement(LookupElementBuilder.create(completion));
    }
  }

  private void completeWith(@NotNull PsiElement position, @NotNull final CompletionResultSet result) {
    PsiElement qid = position.getParent();
    if (!(qid instanceof SchemaQid)) return;

    if (PsiTreeUtil.getParentOfType(qid, SchemaSupplementDef.class) != null) {
      SchemaFqnTypeRef fqnTypeRef = PsiTreeUtil.getParentOfType(qid, SchemaFqnTypeRef.class);
      if (fqnTypeRef != null) {
        PsiElement prevSibling = SchemaPsiUtil.prevNonWhitespaceSibling(fqnTypeRef);
        if (prevSibling != null
            && prevSibling.getNode().getElementType() != S_COMMA
            && prevSibling.getNode().getElementType() != S_SUPPLEMENT
            && !SchemaPsiUtil.hasPrevSibling(fqnTypeRef, S_WITH)) {
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
    if (!(qid instanceof SchemaQid)) return;
    PsiElement parent = qid.getParent();
    if (!(parent instanceof SchemaTypeDef)) return;

    PsiElement prevSibling = SchemaPsiUtil.prevNonWhitespaceSibling(qid);
    if (prevSibling == null) return;

    IElementType prevSiblingElementType = prevSibling.getNode().getElementType();
    if (!SchemaParserDefinition.TYPE_KINDS.contains(prevSiblingElementType)) return;

    PsiElement defs = PsiTreeUtil.getParentOfType(parent, SchemaDefs.class);
    assert defs != null;

    // now collect all candidates

    defs.accept(new PsiRecursiveElementVisitor() {
      @Override
      public void visitElement(PsiElement element) {
        super.visitElement(element);

        if (element instanceof SchemaFqnTypeRef) {
          SchemaFqnTypeRef typeRef = (SchemaFqnTypeRef) element;

          Fqn fqn = typeRef.getFqn().getFqn();
          // only bother if it's single-segment
          if (fqn.size() != 1) return;

          PsiReference reference = SchemaPsiImplUtil.getReference(typeRef);
          if (reference == null) return;

          if (reference.resolve() != null) return;

          // now here is the place to look around and be more intelligent

          result.addElement(LookupElementBuilder.create(fqn.toString()));
        }

      }
    });

  }

  private void completeOverride(@NotNull PsiElement position, @NotNull final CompletionResultSet result) {
    boolean addOverride = false;

//    PsiElement parent = position.getParent();

    SchemaRecordTypeDef recordTypeDef = null;
    SchemaVarTypeDef varTypeDef = null;

    PsiElement element = SchemaPsiUtil.prevNonWhitespaceSibling(position);

    if (element instanceof SchemaTypeDefWrapper && element.getFirstChild() instanceof SchemaRecordTypeDef) {
      recordTypeDef = (SchemaRecordTypeDef) element.getFirstChild();
    }

    if (element instanceof SchemaTypeDefWrapper && element.getFirstChild() instanceof SchemaVarTypeDef) {
      varTypeDef = (SchemaVarTypeDef) element.getFirstChild();
    }

    if (recordTypeDef != null) {
      List<SchemaFieldDecl> fieldDecls = TypeMembers.getFieldDecls(recordTypeDef, null);
      if (!fieldDecls.isEmpty()) addOverride = true;
    }

    if (varTypeDef != null) {
      if (!TypeMembers.getVarTagDecls(varTypeDef, null).isEmpty()) addOverride = true;
    }

    element = PsiTreeUtil.prevVisibleLeaf(position);
    if (!addOverride && element != null) {
      IElementType prevElementType = element.getNode().getElementType();

      SchemaFieldDecl prevFieldDecl = PsiTreeUtil.getParentOfType(element, SchemaFieldDecl.class);
      recordTypeDef = PsiTreeUtil.getParentOfType(element, SchemaRecordTypeDef.class);
      if (recordTypeDef != null && isValid(prevFieldDecl, position)) {
        addOverride = true;
      }

      SchemaVarTagDecl prevVarTagDecl = PsiTreeUtil.getParentOfType(element, SchemaVarTagDecl.class);
      varTypeDef = PsiTreeUtil.getParentOfType(element, SchemaVarTypeDef.class);
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

  private boolean isValid(@Nullable SchemaFieldDecl fieldDecl, @NotNull PsiElement position) {
    if (fieldDecl == null) return true;
    if (fieldDecl.getCurlyRight() != null) return true;
    SchemaValueTypeRef valueTypeRef = fieldDecl.getValueTypeRef();
    //noinspection RedundantIfStatement
    if (valueTypeRef != null && !PsiTreeUtil.isAncestor(valueTypeRef, position, false)) return true;
    return false;
  }

  private boolean isValid(@Nullable SchemaVarTagDecl tagDecl, @NotNull PsiElement position) {
    if (tagDecl == null) return true;
    if (tagDecl.getCurlyRight() != null) return true;
    SchemaTypeRef typeRef = tagDecl.getTypeRef();
    //noinspection RedundantIfStatement
    if (typeRef != null && !PsiTreeUtil.isAncestor(typeRef, position, false)) return true;
    return false;
  }

  private void completeOverrideMember(@NotNull PsiElement position, @NotNull final CompletionResultSet result) {
    SchemaRecordTypeDef recordTypeDef = null;
    SchemaVarTypeDef varTypeDef = null;

    PsiElement element = PsiTreeUtil.prevVisibleLeaf(position);
    if (element == null || element.getNode().getElementType() != S_OVERRIDE) return;


//
//    element = SchemaPsiUtil.prevNonWhitespaceSibling(element);
//
//    if (element instanceof SchemaTypeDefWrapper && element.getFirstChild() instanceof SchemaRecordTypeDef) {
//      recordTypeDef = (SchemaRecordTypeDef) element.getFirstChild();
//    }
//
//    if (element instanceof SchemaTypeDefWrapper && element.getFirstChild() instanceof SchemaVarTypeDef) {
//      varTypeDef = (SchemaVarTypeDef) element.getFirstChild();
//    }

    PsiElement elementParent = element.getParent();
    if (elementParent == null) return;

    recordTypeDef = PsiTreeUtil.getParentOfType(element, SchemaRecordTypeDef.class);
    if (recordTypeDef == null && elementParent.getNode().getElementType() == S_DEFS) {
      recordTypeDef = PsiTreeUtil.getParentOfType(PsiTreeUtil.prevVisibleLeaf(element), SchemaRecordTypeDef.class);
    }

    varTypeDef = PsiTreeUtil.getParentOfType(element, SchemaVarTypeDef.class);
    if (varTypeDef == null && elementParent.getNode().getElementType() == S_DEFS) {
      varTypeDef = PsiTreeUtil.getParentOfType(PsiTreeUtil.prevVisibleLeaf(element), SchemaVarTypeDef.class);
    }

    if (recordTypeDef != null) {
      List<SchemaFieldDecl> overrideableFields = TypeMembers.getOverridableFields(recordTypeDef);
      for (SchemaFieldDecl fieldDecl : overrideableFields) {
        result.addElement(LookupElementBuilder.create(fieldDecl));
      }
    }

    if (varTypeDef != null) {
      List<SchemaVarTagDecl> overrideableTags = TypeMembers.getOverridableTags(varTypeDef);
      for (SchemaVarTagDecl varTagDecl : overrideableTags) {
        result.addElement(LookupElementBuilder.create(varTagDecl));
      }
    }
  }

  private void completePolymorphic(@NotNull PsiElement position, @NotNull final CompletionResultSet result) {
    // todo support for anon lists and maps

    SchemaValueTypeRef valueTypeRef = PsiTreeUtil.getParentOfType(position, SchemaValueTypeRef.class);
    if (valueTypeRef != null && valueTypeRef.getPolymorphic() == null) {
      PsiElement prevSibling = PsiTreeUtil.prevVisibleLeaf(position);
      if (prevSibling != null && prevSibling.getNode().getElementType() == S_COLON) {
        result.addElement(LookupElementBuilder.create("polymorphic "));
      }
    } else {
      PsiElement prevVisibleLeaf = PsiTreeUtil.prevVisibleLeaf(position);
      if (prevVisibleLeaf != null) {
        if (PsiTreeUtil.getParentOfType(prevVisibleLeaf, SchemaRecordTypeDef.class) != null) {
          IElementType prevLeafType = prevVisibleLeaf.getNode().getElementType();
          if (prevLeafType == S_COLON) {
            result.addElement(LookupElementBuilder.create("polymorphic "));
          }
        }
      }
    }
  }

  private void completeDefault(@NotNull PsiElement position, @NotNull final CompletionResultSet result) {
    SchemaValueTypeRef valueTypeRef = PsiTreeUtil.getParentOfType(position, SchemaValueTypeRef.class);
    // there is a value type ref and no 'default' in it yet
    if (valueTypeRef != null) {
      completeDefault(valueTypeRef, result);
    } else {
      PsiElement prevVisibleLeaf = PsiTreeUtil.prevVisibleLeaf(position);
      if (prevVisibleLeaf != null) {
        SchemaFieldDecl fieldDecl = PsiTreeUtil.getParentOfType(prevVisibleLeaf, SchemaFieldDecl.class);
        if (fieldDecl != null) {
          completeDefault(fieldDecl.getValueTypeRef(), result);
        }
      }
    }
  }

  private void completeDefault(@Nullable SchemaValueTypeRef valueTypeRef, @NotNull CompletionResultSet result) {
    if (valueTypeRef != null && valueTypeRef.getDefaultOverride() == null) {
      SchemaTypeRef typeRef = valueTypeRef.getTypeRef();
      // type ref is an FQN type ref (not an anon list or map)
      if (typeRef instanceof SchemaFqnTypeRef) {
        // resolve it and ensure it points to a var type
        SchemaFqnTypeRef fqnTypeRef = (SchemaFqnTypeRef) typeRef;
        SchemaTypeDef typeDef = fqnTypeRef.resolve();
        if (typeDef instanceof SchemaVarTypeDef) {
          PsiElement prevSibling = SchemaPsiUtil.prevNonWhitespaceSibling(valueTypeRef);
          if (prevSibling != null && prevSibling.getNode().getElementType() == S_COLON) {
            result.addElement(LookupElementBuilder.create("default "));
          }
        }
      }
    }
  }

}

package com.sumologic.epigraph.ideaplugin.schema.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.sumologic.epigraph.ideaplugin.schema.SchemaLanguage;
import com.sumologic.epigraph.ideaplugin.schema.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaElementTypes.*;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaCompletionContributor extends CompletionContributor {
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

  public SchemaCompletionContributor() {
    extend(
        CompletionType.BASIC,
        PlatformPatterns.psiElement().withLanguage(SchemaLanguage.INSTANCE),
        new CompletionProvider<CompletionParameters>() {
          @Override
          protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
            PsiElement position = parameters.getPosition();
            completeTopLevelKeywords(position, result);
            completeExtendsKeyword(position, result);
          }
        }
    );
  }

  private void completeTopLevelKeywords(@NotNull PsiElement position, @NotNull CompletionResultSet result) {
    // Only complete if this token is first one on the current line?
    // otherwise we encourage something like
    // long Foo string Bar
    // (on the same line)

    PsiElement parent = position.getParent();
    if (parent != null) {
      if (parent instanceof SchemaTypeDef) return;

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
          completeTypeDef = nextParentSibling == null || nextParentSibling instanceof SchemaTypeDef;
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
          completeNamespace = true;
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

  // TODO logic for meta/supplements is very similar.. reuse?
  private void completeExtendsKeyword(@NotNull PsiElement position, @NotNull CompletionResultSet result) {
    PsiElement parent = position.getParent();
    if (parent != null) {
      boolean doComplete = false;

      PsiElement grandParent = parent.getParent();

      if (grandParent != null && grandParent.getNode().getElementType() == S_DEFS) {

        // only care about record or multi type def
        PsiElement prevParentSibling = SchemaPsiUtil.prevNonWhitespaceSibling(parent);
        if (prevParentSibling instanceof SchemaRecordTypeDef) {
          SchemaRecordTypeDef recordTypeDef = (SchemaRecordTypeDef) prevParentSibling;
          if (recordTypeDef.getExtendsDecl() != null) return;
        } else if (prevParentSibling instanceof SchemaVarTypeDef) {
          SchemaVarTypeDef varTypeDef = (SchemaVarTypeDef) prevParentSibling;
          if (varTypeDef.getExtendsDecl() != null) return;
        } else return;

        PsiElement nextParentSibling = SchemaPsiUtil.nextNonWhitespaceSibling(parent);
        if (nextParentSibling == null) doComplete = true;
        else if (nextParentSibling instanceof SchemaTypeDef) doComplete = true;
        else if (nextParentSibling.getNode().getElementType().equals(GeneratedParserUtilBase.DUMMY_BLOCK)) {
          doComplete = !SchemaPsiUtil.hasChildOfType(nextParentSibling, S_EXTENDS);
        }

      }

      if (doComplete)
        result.addElement(LookupElementBuilder.create("extends "));
    }
  }
}

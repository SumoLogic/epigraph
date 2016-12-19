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

package ws.epigraph.ideaplugin.schema.features;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.CustomFoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.SourceTreeToPsiMap;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.containers.ContainerUtil;
import ws.epigraph.schema.parser.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

import static ws.epigraph.schema.lexer.EdlElementTypes.S_BLOCK_COMMENT;
import static ws.epigraph.schema.lexer.EdlElementTypes.S_COMMENT;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlFoldingBuilder extends CustomFoldingBuilder implements DumbAware {

  private static String getPlaceholderText(PsiElement element) {
    // todo namespace
    if (element instanceof EdlTypeDef) {
      return "{...}";
    } else if (element instanceof PsiFile) {
      return "/.../";
    } else if (element instanceof PsiComment) {
      if (((PsiComment) element).getTokenType() == S_BLOCK_COMMENT)
        return "/*..*/";
      else
        return "//...";
    }
    return "...";
  }

  private TextRange getRangeToFold(PsiElement element) {
    if (element instanceof SyntheticElement) {
      return null;
    }

    if (element instanceof EdlTypeDef) {
      @Nullable
      PsiElement body = null;

      // TODO add a mixin with getBody?
      if (element instanceof EdlVarTypeDef) {
        body = ((EdlVarTypeDef) element).getVarTypeBody();
      } else if (element instanceof EdlRecordTypeDef) {
        body = ((EdlRecordTypeDef) element).getRecordTypeBody();
      } else if (element instanceof EdlPrimitiveTypeDef) {
        body = ((EdlPrimitiveTypeDef) element).getPrimitiveTypeBody();
      } else if (element instanceof EdlListTypeDef) {
        body = ((EdlListTypeDef) element).getListTypeBody();
      } else if (element instanceof EdlMapTypeDef) {
        body = ((EdlMapTypeDef) element).getMapTypeBody();
      } else if (element instanceof EdlEnumTypeDef) {
        body = ((EdlEnumTypeDef) element).getEnumTypeBody();
      }

      return body == null ? null : body.getTextRange();
    }

    return null;
  }

  private boolean addToFold(List<FoldingDescriptor> list, PsiElement elementToFold, Document document, boolean allowOneLiners) {
    PsiUtilCore.ensureValid(elementToFold);
    TextRange range = getRangeToFold(elementToFold);
    return range != null && addFoldRegion(list, elementToFold, document, allowOneLiners, range);
  }

  private static boolean addFoldRegion(final List<FoldingDescriptor> list, final PsiElement elementToFold, final Document document,
                                       final boolean allowOneLiners,
                                       final TextRange range) {
    final TextRange fileRange = elementToFold.getContainingFile().getTextRange();
    if (range.equals(fileRange)) return false;

//    LOG.assertTrue(range.getStartOffset() >= 0 && range.getEndOffset() <= fileRange.getEndOffset());
    // PSI getElement text ranges may be invalid because of reparse exception (see, for example, IDEA-10617)
    if (range.getStartOffset() < 0 || range.getEndOffset() > fileRange.getEndOffset()) {
      return false;
    }
    if (!allowOneLiners) { // TODO: do we need this setting?
      int startLine = document.getLineNumber(range.getStartOffset());
      int endLine = document.getLineNumber(range.getEndOffset() - 1);
      if (startLine < endLine && range.getLength() > 1) {
        list.add(new FoldingDescriptor(elementToFold, range));
        return true;
      }
      return false;
    } else {
      if (range.getLength() > getPlaceholderText(elementToFold).length()) {
        list.add(new FoldingDescriptor(elementToFold, range));
        return true;
      }
      return false;
    }
  }


  // Taken from JavaFoldingBase

  /**
   * We want to allow to fold subsequent single line comments like
   * <pre>
   *     // this is comment line 1
   *     // this is comment line 2
   * </pre>
   *
   * @param comment           comment to check
   * @param processedComments set that contains already processed elements. It is necessary because we process all elements of
   *                          the PSI tree, hence, this method may be called for both comments from the example above. However,
   *                          we want to create fold region during the first comment processing, put second comment to it and
   *                          skip processing when current method is called for the second getElement
   * @param foldElements      fold descriptors holder to store newly created descriptor (if any)
   */
  private static void addCommentFolds(@NotNull PsiComment comment, @NotNull Set<PsiElement> processedComments,
                                      @NotNull List<FoldingDescriptor> foldElements) {
    if (processedComments.contains(comment) || comment.getTokenType() != S_COMMENT) {
      return;
    }

    PsiElement end = null;
    boolean containsCustomRegionMarker = isCustomRegionElement(comment);
    for (PsiElement current = comment.getNextSibling(); current != null; current = current.getNextSibling()) {
      ASTNode node = current.getNode();
      if (node == null) {
        break;
      }
      IElementType elementType = node.getElementType();
      if (elementType == S_COMMENT) {
        end = current;
        // We don't want to process, say, the second comment in case of three subsequent comments when it's being examined
        // during all elements traversal. I.e. we expect to start from the first comment and grab as many subsequent
        // comments as possible during the single iteration.
        processedComments.add(current);
        containsCustomRegionMarker |= isCustomRegionElement(current);
        continue;
      }
      if (elementType == TokenType.WHITE_SPACE) {
        continue;
      }
      break;
    }

    if (end != null && !containsCustomRegionMarker) {
      foldElements.add(
          new FoldingDescriptor(comment, new TextRange(comment.getTextRange().getStartOffset(), end.getTextRange().getEndOffset()))
      );
    }
  }

  @Override
  protected void buildLanguageFoldRegions(@NotNull List<FoldingDescriptor> descriptors, @NotNull PsiElement root, @NotNull Document document, boolean quick) {
    if (!(root instanceof EdlFile))
      return;

    EdlFile file = (EdlFile) root;

    EdlDefs defs = file.getDefs();
    if (defs != null) {
      for (EdlTypeDefWrapper typeDefWrapper : defs.getTypeDefWrapperList()) {
        addToFold(descriptors, typeDefWrapper.getElement(), document, true);
      }
    }

    // TODO(low) fold {} around custom params

    if (!quick) {
      final Set<PsiElement> seenComments = ContainerUtil.newHashSet();

      PsiTreeUtil.processElements(file, element -> {
        if (element.getNode().getElementType().equals(S_BLOCK_COMMENT)) {
          descriptors.add(new FoldingDescriptor(element, element.getTextRange()));
        } else if (element.getNode().getElementType().equals(S_COMMENT)) {
          addCommentFolds((PsiComment) element, seenComments, descriptors);
        }
        return true;
      });
    }
  }

  @Override
  protected String getLanguagePlaceholderText(@NotNull ASTNode node, @NotNull TextRange range) {
    return getPlaceholderText(SourceTreeToPsiMap.treeElementToPsi(node));
  }

  @Override
  protected boolean isRegionCollapsedByDefault(@NotNull ASTNode node) {
    return false;
  }
}

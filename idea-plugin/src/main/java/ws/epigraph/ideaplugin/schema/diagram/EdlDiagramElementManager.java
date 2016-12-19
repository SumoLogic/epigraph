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

package ws.epigraph.ideaplugin.schema.diagram;

import com.intellij.diagram.AbstractDiagramElementManager;
import com.intellij.diagram.presentation.DiagramState;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.ui.SimpleColoredText;
import ws.epigraph.ideaplugin.schema.presentation.EdlPresentationUtil;
import ws.epigraph.schema.parser.psi.*;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlDiagramElementManager extends AbstractDiagramElementManager<PsiNamedElement> {
  @Nullable
  @Override
  public PsiNamedElement findInDataContext(DataContext context) {
    PsiFile file = LangDataKeys.PSI_FILE.getData(context);
    return file instanceof EdlFile ? file : null;
  }

  @Override
  public boolean isAcceptableAsNode(Object o) {
    return o instanceof EdlTypeDef || o instanceof EdlFile;
  }

  @Nullable
  @Override
  public String getElementTitle(PsiNamedElement psiNamedElement) {
    return psiNamedElement.getName();
  }

  @Nullable
  @Override
  public SimpleColoredText getItemName(Object o, DiagramState diagramState) {
    if (o instanceof PsiNamedElement) {
      PsiNamedElement namedElement = (PsiNamedElement) o;

      final String itemName, itemType;

      if (namedElement instanceof EdlFieldDecl) {
        EdlFieldDecl fieldDecl = (EdlFieldDecl) namedElement;

        itemName = fieldDecl.getQid().getCanonicalName();
        EdlValueTypeRef valueTypeRef = fieldDecl.getValueTypeRef();

        itemType = valueTypeRef == null ? null : valueTypeRef.getTypeRef().getText();
      } else if (namedElement instanceof EdlVarTagDecl) {
        EdlVarTagDecl tagDecl = (EdlVarTagDecl) namedElement;

        itemName = tagDecl.getQid().getCanonicalName();
        EdlTypeRef typeRef = tagDecl.getTypeRef();

        itemType = typeRef == null ? null : typeRef.getText();
      } else {
        itemName = namedElement.getName();
        if (itemName == null) return null;

        itemType = null;
      }

      SimpleColoredText res = new SimpleColoredText(itemName, DEFAULT_TITLE_ATTR);
      if (itemType != null) res.append(" : " + itemType, DEFAULT_TEXT_ATTR);

      return res;
    }

    return null;
  }

  @Override
  public Object[] getNodeItems(PsiNamedElement parent) {
    List<Object> res = new ArrayList<>();

    if (parent instanceof EdlVarTypeDef) {
      EdlVarTypeDef varTypeDef = (EdlVarTypeDef) parent;
      EdlVarTypeBody body = varTypeDef.getVarTypeBody();
      if (body != null) {
        res.addAll(body.getVarTagDeclList());
        res.addAll(body.getAnnotationList());
      }
    }

    if (parent instanceof EdlRecordTypeDef) {
      EdlRecordTypeDef recordTypeDef = (EdlRecordTypeDef) parent;
      EdlRecordTypeBody body = recordTypeDef.getRecordTypeBody();
      if (body != null) {
        res.addAll(body.getFieldDeclList());
        res.addAll(body.getAnnotationList());
      }
    }

    if (parent instanceof EdlMapTypeDef) {
      EdlMapTypeDef mapTypeDef = (EdlMapTypeDef) parent;
      EdlMapTypeBody body = mapTypeDef.getMapTypeBody();
      if (body != null) {
        res.addAll(body.getAnnotationList());
      }
    }

    if (parent instanceof EdlListTypeDef) {
      EdlListTypeDef listTypeDef = (EdlListTypeDef) parent;
      EdlListTypeBody body = listTypeDef.getListTypeBody();
      if (body != null) {
        res.addAll(body.getAnnotationList());
      }
    }

    if (parent instanceof EdlEnumTypeDef) {
      EdlEnumTypeDef enumTypeDef = (EdlEnumTypeDef) parent;
      EdlEnumTypeBody body = enumTypeDef.getEnumTypeBody();
      if (body != null) {
        res.addAll(body.getEnumMemberDeclList());
        res.addAll(body.getAnnotationList());
      }
    }

    if (parent instanceof EdlPrimitiveTypeDef) {
      EdlPrimitiveTypeDef primitiveTypeDef = (EdlPrimitiveTypeDef) parent;
      EdlPrimitiveTypeBody body = primitiveTypeDef.getPrimitiveTypeBody();
      if (body != null) {
        res.addAll(body.getAnnotationList());
      }
    }

    return res.toArray();
  }

  @Override
  public Icon getItemIcon(Object element, DiagramState presentation) {
    // todo make them Iconable instead?

    if (element instanceof EdlVarTagDecl)
      return EdlPresentationUtil.TAG_ICON;

    if (element instanceof EdlFieldDecl)
      return EdlPresentationUtil.FIELD_ICON;

    if (element instanceof EdlEnumMemberDecl)
      return EdlPresentationUtil.ENUM_MEMBER_ICON;

    if (element instanceof EdlAnnotation)
      return EdlPresentationUtil.ANNOTATION_ICON;

    return super.getItemIcon(element, presentation);
  }

  @Override
  public String getNodeTooltip(PsiNamedElement psiNamedElement) {
    return null;
  }
}

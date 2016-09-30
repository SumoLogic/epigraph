package com.sumologic.epigraph.ideaplugin.schema.diagram;

import com.intellij.diagram.AbstractDiagramElementManager;
import com.intellij.diagram.presentation.DiagramState;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.ui.SimpleColoredText;
import com.sumologic.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import io.epigraph.schema.parser.psi.*;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaDiagramElementManager extends AbstractDiagramElementManager<PsiNamedElement> {
  @Nullable
  @Override
  public PsiNamedElement findInDataContext(DataContext context) {
    PsiFile file = LangDataKeys.PSI_FILE.getData(context);
    return file instanceof SchemaFile ? file : null;
  }

  @Override
  public boolean isAcceptableAsNode(Object o) {
    return o instanceof SchemaTypeDef || o instanceof SchemaFile;
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

      if (namedElement instanceof SchemaFieldDecl) {
        SchemaFieldDecl fieldDecl = (SchemaFieldDecl) namedElement;

        itemName = fieldDecl.getQid().getCanonicalName();
        SchemaValueTypeRef valueTypeRef = fieldDecl.getValueTypeRef();

        itemType = valueTypeRef == null ? null : valueTypeRef.getTypeRef().getText();
      } else if (namedElement instanceof SchemaVarTagDecl) {
        SchemaVarTagDecl tagDecl = (SchemaVarTagDecl) namedElement;

        itemName = tagDecl.getQid().getCanonicalName();
        SchemaTypeRef typeRef = tagDecl.getTypeRef();

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

    if (parent instanceof SchemaVarTypeDef) {
      SchemaVarTypeDef varTypeDef = (SchemaVarTypeDef) parent;
      SchemaVarTypeBody body = varTypeDef.getVarTypeBody();
      if (body != null) {
        res.addAll(body.getVarTagDeclList());
        res.addAll(body.getAnnotationsList());
      }
    }

    if (parent instanceof SchemaRecordTypeDef) {
      SchemaRecordTypeDef recordTypeDef = (SchemaRecordTypeDef) parent;
      SchemaRecordTypeBody body = recordTypeDef.getRecordTypeBody();
      if (body != null) {
        res.addAll(body.getFieldDeclList());
        res.addAll(body.getAnnotationsList());
      }
    }

    if (parent instanceof SchemaMapTypeDef) {
      SchemaMapTypeDef mapTypeDef = (SchemaMapTypeDef) parent;
      SchemaMapTypeBody body = mapTypeDef.getMapTypeBody();
      if (body != null) {
        res.addAll(body.getAnnotationsList());
      }
    }

    if (parent instanceof SchemaListTypeDef) {
      SchemaListTypeDef listTypeDef = (SchemaListTypeDef) parent;
      SchemaListTypeBody body = listTypeDef.getListTypeBody();
      if (body != null) {
        res.addAll(body.getAnnotationsList());
      }
    }

    if (parent instanceof SchemaEnumTypeDef) {
      SchemaEnumTypeDef enumTypeDef = (SchemaEnumTypeDef) parent;
      SchemaEnumTypeBody body = enumTypeDef.getEnumTypeBody();
      if (body != null) {
        res.addAll(body.getEnumMemberDeclList());
        res.addAll(body.getAnnotationsList());
      }
    }

    if (parent instanceof SchemaPrimitiveTypeDef) {
      SchemaPrimitiveTypeDef primitiveTypeDef = (SchemaPrimitiveTypeDef) parent;
      SchemaPrimitiveTypeBody body = primitiveTypeDef.getPrimitiveTypeBody();
      if (body != null) {
        res.addAll(body.getAnnotationsList());
      }
    }

    return res.toArray();
  }

  @Override
  public Icon getItemIcon(Object element, DiagramState presentation) {
    // todo make them Iconable instead?

    if (element instanceof SchemaVarTagDecl)
      return SchemaPresentationUtil.TAG_ICON;

    if (element instanceof SchemaFieldDecl)
      return SchemaPresentationUtil.FIELD_ICON;

    if (element instanceof SchemaEnumMemberDecl)
      return SchemaPresentationUtil.ENUM_MEMBER_ICON;

    if (element instanceof SchemaAnnotation)
      return SchemaPresentationUtil.ANNOTATION_ICON;

    return super.getItemIcon(element, presentation);
  }

  @Override
  public String getNodeTooltip(PsiNamedElement psiNamedElement) {
    return null;
  }
}

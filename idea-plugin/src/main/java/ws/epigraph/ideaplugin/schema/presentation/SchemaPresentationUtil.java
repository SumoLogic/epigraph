/*
 * Copyright 2017 Sumo Logic
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

package ws.epigraph.ideaplugin.schema.presentation;

import com.intellij.icons.AllIcons;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiInvalidElementAccessException;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IconUtil;
import ws.epigraph.ideaplugin.schema.brains.NamespaceManager;
import ws.epigraph.ideaplugin.schema.brains.VirtualFileUtil;
import ws.epigraph.lang.Qn;
import ws.epigraph.schema.parser.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class SchemaPresentationUtil {
  private static final String OVERLAY = "§";
  // TODO own icons!
  private static Icon SCHEMA_FILE_ICON;
  private static Icon SCHEMA_OUTSIDE_SOURCE_FILE_ICON;

  private static Icon TYPE_ICON;
  private static Icon RECORD_TYPE_ICON;
  private static Icon ENUM_TYPE_ICON;
  private static Icon ENTITY_TYPE_ICON;
  private static Icon SUPPLEMENT_ICON;

  public static final Icon PARENT_TYPES_GUTTER_ICON = AllIcons.Gutter.OverridingMethod;
  public static final Icon CHILD_TYPES_GUTTER_ICON = AllIcons.Gutter.OverridenMethod;
  public static final Icon SUPPLEMENTS_GUTTER_ICON = AllIcons.Gutter.ExtAnnotation;

  public static final Icon OVERRIDEN_FIELD_GUTTER_ICON = AllIcons.Gutter.OverridenMethod;
  public static final Icon OVERRIDING_FIELD_GUTTER_ICON = AllIcons.Gutter.OverridingMethod;
  public static final Icon OVERRIDEN_TAG_GUTTER_ICON = AllIcons.Gutter.ImplementedMethod;
  public static final Icon OVERRIDING_TAG_GUTTER_ICON = AllIcons.Gutter.ImplementingMethod;

  public static final Icon TAG_ICON = AllIcons.Nodes.Function;
  public static final Icon FIELD_ICON = AllIcons.Nodes.Field;
  public static final Icon ANNOTATION_ICON = AllIcons.Nodes.Annotationtype;
  public static final Icon ENUM_MEMBER_ICON = AllIcons.Nodes.Property;

  private SchemaPresentationUtil() {}

  // need lazy init on overlayed icons to avoid start-up deadlocks
  public static Icon schemaFileIcon() {
    // this still deadlocks as it tries to create an action

//    if (SCHEMA_FILE_ICON == null) SCHEMA_FILE_ICON = addOverlay(AllIcons.FileTypes.Custom);
//    return SCHEMA_FILE_ICON;

    return AllIcons.FileTypes.Custom;
  }

  public static Icon schemaOutsideSourceFileIcon() {
    if (SCHEMA_OUTSIDE_SOURCE_FILE_ICON == null)
      SCHEMA_OUTSIDE_SOURCE_FILE_ICON = addOverlay(AllIcons.FileTypes.Unknown);
    return SCHEMA_OUTSIDE_SOURCE_FILE_ICON;
  }

  // causes deadlocks?
  private static Icon addOverlay(@NotNull Icon i) { return IconUtil.addText(i, OVERLAY); }
//  private static Icon addOverlay(@NotNull Icon i) { return i; }

  @Nullable
  public static String getName(@NotNull PsiNamedElement element, boolean qualified) {
    if (element instanceof SchemaQnSegment) {
      SchemaQnSegment qnSegment = (SchemaQnSegment) element;
      return qnSegment.getQn().toString();
    }

    String shortName = element.getName();
    if (shortName == null) return null;

    if (qualified) {
      Qn namespace;
      if (element instanceof SchemaTypeDef) {
        SchemaTypeDef typeDef = (SchemaTypeDef) element;
        namespace = typeDef.getNamespace();
      } else namespace = NamespaceManager.getNamespace(element);
      return namespace == null ? shortName : namespace.append(shortName).toString();
    } else return shortName;
  }

  @Nullable
  public static Icon getIcon(@NotNull PsiElement element) {
    if (element instanceof SchemaFile) return SCHEMA_FILE_ICON;

    if (element instanceof SchemaRecordTypeDef) {
      if (RECORD_TYPE_ICON == null) RECORD_TYPE_ICON = addOverlay(AllIcons.Nodes.Class);
      return RECORD_TYPE_ICON;
    }
    if (element instanceof SchemaEnumTypeDef) {
      if (ENUM_TYPE_ICON == null) ENUM_TYPE_ICON = addOverlay(AllIcons.Nodes.Enum);
      return ENUM_TYPE_ICON;
    }
    if (element instanceof SchemaEntityTypeDef) {
      if (ENTITY_TYPE_ICON == null) ENTITY_TYPE_ICON = addOverlay(AllIcons.Nodes.Interface);
      return ENTITY_TYPE_ICON;
    }

    if (element instanceof SchemaTypeDef) {
      if (TYPE_ICON == null) TYPE_ICON = addOverlay(AllIcons.Nodes.Class);
      return TYPE_ICON;
    }

    if (element instanceof SchemaSupplementDef) {
      if (SUPPLEMENT_ICON == null) SUPPLEMENT_ICON = addOverlay(AllIcons.Nodes.Aspect);
      return SUPPLEMENT_ICON;
    }

    if (element instanceof SchemaAnnotation) return ANNOTATION_ICON;
    if (element instanceof SchemaFieldDecl) return FIELD_ICON;
    if (element instanceof SchemaEnumMemberDecl) return ENUM_MEMBER_ICON;
    if (element instanceof SchemaEntityTagDecl) return TAG_ICON;

    // TODO icons for all!
    return null;
  }

  @NotNull
  private static String getPresentableText(@NotNull PsiElement element, boolean structureView) {
    if (element instanceof SchemaFile) {
      return VirtualFileUtil.getOriginalVirtualFile((SchemaFile) element).getPresentableName();
    }

    if (element instanceof SchemaTypeDef) {
      SchemaTypeDef schemaTypeDef = (SchemaTypeDef) element;

      PsiElement id = schemaTypeDef.getNameIdentifier();
      return id == null ? "" : id.getText();
    }

    if (element instanceof SchemaAnnotation)
      return ((SchemaAnnotation) element).getQid().getName();

    if (element instanceof SchemaFieldDecl) {
      SchemaFieldDecl schemaFieldDecl = (SchemaFieldDecl) element;
      String name = schemaFieldDecl.getQid().getName();

      if (structureView) return name;

      SchemaRecordTypeDef recordTypeDef = PsiTreeUtil.getParentOfType(element, SchemaRecordTypeDef.class);
      String typeName = recordTypeDef == null ? null : recordTypeDef.getName();
      typeName = typeName == null ? "???" : typeName;

      return typeName + '.' + name;
    }

    if (element instanceof SchemaEntityTagDecl) {
      SchemaEntityTagDecl varTagDecl = (SchemaEntityTagDecl) element;
      String name = varTagDecl.getQid().getName();

      if (structureView) return name;

      SchemaEntityTypeDef entityTypeDef = PsiTreeUtil.getParentOfType(element, SchemaEntityTypeDef.class);
      String entityTypeName = entityTypeDef == null ? null : entityTypeDef.getName();
      entityTypeName = entityTypeName == null ? "???" : entityTypeName;

      return entityTypeName + '.' + name;
    }

    if (element instanceof SchemaEnumMemberDecl) {
      SchemaEnumMemberDecl schemaEnumMemberDecl = (SchemaEnumMemberDecl) element;
      return schemaEnumMemberDecl.getQid().getName();
    }

    if (element instanceof SchemaSupplementDef) {
      SchemaSupplementDef schemaSupplementDef = (SchemaSupplementDef) element;

      StringBuilder name = new StringBuilder();

      List<SchemaQnTypeRef> qnTypeRef = schemaSupplementDef.supplementedRefs();
      for (SchemaQnTypeRef typeRef : qnTypeRef) {
        if (name.length() > 0) name.append(", ");
        name.append(typeRef.getQn().getQn().toString());
      }
      name.append(" with ");
      SchemaTypeDef source = schemaSupplementDef.source();
      name.append(source == null ? "???" : source.getName());

      return "supplement " + name;
    }

    return "(getPresentableText) Unknown element: " + element.getClass();
  }

  @NotNull
  public static ItemPresentation getPresentation(@NotNull PsiElement element, boolean structureView) {
    final String presentableTest = getPresentableText(element, structureView);
    final String location = getNamespaceString(element, true);
    final Icon icon = getIcon(element);

    return new StaticItemPresentation(presentableTest, location, icon);
  }

  @NotNull
  public static String getNamespaceString(@NotNull PsiElement element, boolean inParens) {
    Qn namespace;

    if (element instanceof SchemaTypeDef) {
      SchemaTypeDef typeDef = (SchemaTypeDef) element;
      namespace = typeDef.getNamespace();
    } else {
      namespace = NamespaceManager.getNamespace(element);
    }

    if (namespace != null) return inParens ? "(" + namespace.toString() + ')' : namespace.toString();

    try {
      PsiFile containingFile = element.getContainingFile();
      if (containingFile == null) return "[invalid]";
      return inParens ? "(" + containingFile.getName() + ')' : containingFile.getName();
    } catch (PsiInvalidElementAccessException e) {
      return "[invalid]"; // file not available
    }
  }

  @NotNull
  public static String getType(@NotNull PsiElement element) {
    if (element instanceof SchemaListTypeDef) return "List type";
    if (element instanceof SchemaPrimitiveTypeDef) return "Primitive type";
    if (element instanceof SchemaEnumTypeDef) return "Enum type";
    if (element instanceof SchemaEntityTypeDef) return "Entity type";
    if (element instanceof SchemaRecordTypeDef) return "Record type";
    if (element instanceof SchemaMapTypeDef) return "Map type";
    if (element instanceof SchemaQnSegment) return "Namespace";
    if (element instanceof SchemaEntityTagDecl) return "Var tag";
    if (element instanceof SchemaFieldDecl) return "Record field";
    if (element instanceof SchemaEntityTagRef)
      return "Entity tag reference";

    return "(getType) Unknown element: " + element;
  }

  @NotNull
  public static String psiToString(@NotNull PsiElement element) {
    return element.getClass().getSimpleName() + "(" + element.getNode().getElementType().toString() + ")";
  }

}

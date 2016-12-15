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

package ws.epigraph.ideaplugin.edl.presentation;

import com.intellij.icons.AllIcons;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiInvalidElementAccessException;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IconUtil;
import ws.epigraph.ideaplugin.edl.brains.NamespaceManager;
import ws.epigraph.ideaplugin.edl.brains.VirtualFileUtil;
import ws.epigraph.lang.Qn;
import ws.epigraph.edl.parser.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class EdlPresentationUtil {
  private static final String OVERLAY = "§";
  // TODO own icons!
  private static Icon EDL_FILE_ICON;
  private static Icon EDL_OUTSIDE_SOURCE_FILE_ICON;

  private static Icon TYPE_ICON;
  private static Icon RECORD_TYPE_ICON;
  private static Icon ENUM_TYPE_ICON;
  private static Icon VAR_TYPE_ICON;
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

  private EdlPresentationUtil() {}

  // need lazy init on overlayed icons to avoid start-up deadlocks
  public static Icon edlFileIcon() {
    // this still deadlocks as it tries to create an action

//    if (EDL_FILE_ICON == null) EDL_FILE_ICON = addOverlay(AllIcons.FileTypes.Custom);
//    return EDL_FILE_ICON;

    return AllIcons.FileTypes.Custom;
  }

  public static Icon edlOutsideSourceFileIcon() {
    if (EDL_OUTSIDE_SOURCE_FILE_ICON == null)
      EDL_OUTSIDE_SOURCE_FILE_ICON = addOverlay(AllIcons.FileTypes.Unknown);
    return EDL_OUTSIDE_SOURCE_FILE_ICON;
  }

  // causes deadlocks?
  private static Icon addOverlay(@NotNull Icon i) { return IconUtil.addText(i, OVERLAY); }
//  private static Icon addOverlay(@NotNull Icon i) { return i; }

  @Nullable
  public static String getName(@NotNull PsiNamedElement element, boolean qualified) {
    if (element instanceof EdlQnSegment) {
      EdlQnSegment qnSegment = (EdlQnSegment) element;
      return qnSegment.getQn().toString();
    }

    String shortName = element.getName();
    if (shortName == null) return null;

    if (qualified) {
      Qn namespace;
      if (element instanceof EdlTypeDef) {
        EdlTypeDef typeDef = (EdlTypeDef) element;
        namespace = typeDef.getNamespace();
      } else namespace = NamespaceManager.getNamespace(element);
      return namespace == null ? shortName : namespace.append(shortName).toString();
    } else return shortName;
  }

  @Nullable
  public static Icon getIcon(@NotNull PsiElement element) {
    if (element instanceof EdlFile) return EDL_FILE_ICON;

    if (element instanceof EdlRecordTypeDef) {
      if (RECORD_TYPE_ICON == null) RECORD_TYPE_ICON = addOverlay(AllIcons.Nodes.Class);
      return RECORD_TYPE_ICON;
    }
    if (element instanceof EdlEnumTypeDef) {
      if (ENUM_TYPE_ICON == null) ENUM_TYPE_ICON = addOverlay(AllIcons.Nodes.Enum);
      return ENUM_TYPE_ICON;
    }
    if (element instanceof EdlVarTypeDef) {
      if (VAR_TYPE_ICON == null) VAR_TYPE_ICON = addOverlay(AllIcons.Nodes.Interface);
      return VAR_TYPE_ICON;
    }

    if (element instanceof EdlTypeDef) {
      if (TYPE_ICON == null) TYPE_ICON = addOverlay(AllIcons.Nodes.Class);
      return TYPE_ICON;
    }

    if (element instanceof EdlSupplementDef) {
      if (SUPPLEMENT_ICON == null) SUPPLEMENT_ICON = addOverlay(AllIcons.Nodes.Aspect);
      return SUPPLEMENT_ICON;
    }

    if (element instanceof EdlAnnotation) return ANNOTATION_ICON;
    if (element instanceof EdlFieldDecl) return FIELD_ICON;
    if (element instanceof EdlEnumMemberDecl) return ENUM_MEMBER_ICON;
    if (element instanceof EdlVarTagDecl) return TAG_ICON;

    // TODO icons for all!
    return null;
  }

  @NotNull
  private static String getPresentableText(@NotNull PsiElement element, boolean structureView) {
    if (element instanceof EdlFile) {
      return VirtualFileUtil.getOriginalVirtualFile((EdlFile) element).getPresentableName();
    }

    if (element instanceof EdlTypeDef) {
      EdlTypeDef edlTypeDef = (EdlTypeDef) element;

      PsiElement id = edlTypeDef.getNameIdentifier();
      return id == null ? "" : id.getText();
    }

    if (element instanceof EdlAnnotation)
      return ((EdlAnnotation) element).getQid().getName();

    if (element instanceof EdlFieldDecl) {
      EdlFieldDecl edlFieldDecl = (EdlFieldDecl) element;
      String name = edlFieldDecl.getQid().getName();

      if (structureView) return name;

      EdlRecordTypeDef recordTypeDef = PsiTreeUtil.getParentOfType(element, EdlRecordTypeDef.class);
      String typeName = recordTypeDef == null ? null : recordTypeDef.getName();
      typeName = typeName == null ? "???" : typeName;

      return typeName + '.' + name;
    }

    if (element instanceof EdlVarTagDecl) {
      EdlVarTagDecl varTagDecl = (EdlVarTagDecl) element;
      String name = varTagDecl.getQid().getName();

      if (structureView) return name;

      EdlVarTypeDef varTypeDef = PsiTreeUtil.getParentOfType(element, EdlVarTypeDef.class);
      String varTypeName = varTypeDef == null ? null : varTypeDef.getName();
      varTypeName = varTypeName == null ? "???" : varTypeName;

      return varTypeName + '.' + name;
    }

    if (element instanceof EdlEnumMemberDecl) {
      EdlEnumMemberDecl edlEnumMemberDecl = (EdlEnumMemberDecl) element;
      return edlEnumMemberDecl.getQid().getName();
    }

    if (element instanceof EdlSupplementDef) {
      EdlSupplementDef edlSupplementDef = (EdlSupplementDef) element;

      StringBuilder name = new StringBuilder();

      List<EdlQnTypeRef> qnTypeRef = edlSupplementDef.supplementedRefs();
      for (EdlQnTypeRef typeRef : qnTypeRef) {
        if (name.length() > 0) name.append(", ");
        name.append(typeRef.getQn().getQn().toString());
      }
      name.append(" with ");
      EdlTypeDef source = edlSupplementDef.source();
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

    if (element instanceof EdlTypeDef) {
      EdlTypeDef typeDef = (EdlTypeDef) element;
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
    if (element instanceof EdlListTypeDef) return "List type";
    if (element instanceof EdlPrimitiveTypeDef) return "Primitive type";
    if (element instanceof EdlEnumTypeDef) return "Enum type";
    if (element instanceof EdlVarTypeDef) return "Var type";
    if (element instanceof EdlRecordTypeDef) return "Record type";
    if (element instanceof EdlMapTypeDef) return "Map type";
    if (element instanceof EdlQnSegment) return "Namespace";
    if (element instanceof EdlVarTagDecl) return "Var tag";
    if (element instanceof EdlFieldDecl) return "Record field";
    if (element instanceof EdlVarTagRef)
      return "Var tag reference";

    return "(getType) Unknown element: " + element;
  }

  @NotNull
  public static String psiToString(@NotNull PsiElement element) {
    return element.getClass().getSimpleName() + "(" + element.getNode().getElementType().toString() + ")";
  }

}

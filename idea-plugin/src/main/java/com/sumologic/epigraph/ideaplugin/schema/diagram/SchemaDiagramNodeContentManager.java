package com.sumologic.epigraph.ideaplugin.schema.diagram;

import com.intellij.diagram.AbstractDiagramNodeContentManager;
import com.intellij.diagram.DiagramCategory;
import com.intellij.diagram.presentation.DiagramState;
import com.sumologic.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import io.epigraph.schema.parser.psi.SchemaCustomParam;
import io.epigraph.schema.parser.psi.SchemaEnumMemberDecl;
import io.epigraph.schema.parser.psi.SchemaFieldDecl;
import io.epigraph.schema.parser.psi.SchemaVarTagDecl;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaDiagramNodeContentManager extends AbstractDiagramNodeContentManager {
  static final DiagramCategory TAGS = new DiagramCategory("Tags", SchemaPresentationUtil.TAG_ICON, true);
  static final DiagramCategory FIELDS = new DiagramCategory("Fields", SchemaPresentationUtil.FIELD_ICON, true);
  static final DiagramCategory CUSTOM_PROPERTIES =
      new DiagramCategory("Custom properties", SchemaPresentationUtil.CUSTOM_PROPERTY_ICON, true);
  static final DiagramCategory ENUM_MEMBERS =
      new DiagramCategory("Enum members", SchemaPresentationUtil.ENUM_MEMBER_ICON, true);

  static final DiagramCategory[] CATEGORIES = new DiagramCategory[]{TAGS, FIELDS, CUSTOM_PROPERTIES, ENUM_MEMBERS};

  @SuppressWarnings("RedundantIfStatement")
  @Override
  public boolean isInCategory(Object o, DiagramCategory category, DiagramState diagramState) {
    if (o instanceof SchemaVarTagDecl && category == TAGS) return true;
    if (o instanceof SchemaFieldDecl && category == FIELDS) return true;
    if (o instanceof SchemaCustomParam && category == CUSTOM_PROPERTIES) return true;
    if (o instanceof SchemaEnumMemberDecl && category == ENUM_MEMBERS) return true;

    return false;
  }

  @Override
  public DiagramCategory[] getContentCategories() { return CATEGORIES; }
}

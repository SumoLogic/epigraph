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

package ws.epigraph.ideaplugin.edl.diagram;

import com.intellij.diagram.AbstractDiagramNodeContentManager;
import com.intellij.diagram.DiagramCategory;
import com.intellij.diagram.presentation.DiagramState;
import ws.epigraph.ideaplugin.edl.presentation.SchemaPresentationUtil;
import ws.epigraph.edl.parser.psi.SchemaAnnotation;
import ws.epigraph.edl.parser.psi.SchemaEnumMemberDecl;
import ws.epigraph.edl.parser.psi.SchemaFieldDecl;
import ws.epigraph.edl.parser.psi.SchemaVarTagDecl;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaDiagramNodeContentManager extends AbstractDiagramNodeContentManager {
  static final DiagramCategory TAGS = new DiagramCategory("Tags", SchemaPresentationUtil.TAG_ICON, true);
  static final DiagramCategory FIELDS = new DiagramCategory("Fields", SchemaPresentationUtil.FIELD_ICON, true);
  static final DiagramCategory ANNOTATIONS =
      new DiagramCategory("Annotations", SchemaPresentationUtil.ANNOTATION_ICON, true);
  static final DiagramCategory ENUM_MEMBERS =
      new DiagramCategory("Enum members", SchemaPresentationUtil.ENUM_MEMBER_ICON, true);

  static final DiagramCategory[] CATEGORIES = new DiagramCategory[]{TAGS, FIELDS, ANNOTATIONS, ENUM_MEMBERS};

  @SuppressWarnings("RedundantIfStatement")
  @Override
  public boolean isInCategory(Object o, DiagramCategory category, DiagramState diagramState) {
    if (o instanceof SchemaVarTagDecl && category == TAGS) return true;
    if (o instanceof SchemaFieldDecl && category == FIELDS) return true;
    if (o instanceof SchemaAnnotation && category == ANNOTATIONS) return true;
    if (o instanceof SchemaEnumMemberDecl && category == ENUM_MEMBERS) return true;

    return false;
  }

  @Override
  public DiagramCategory[] getContentCategories() { return CATEGORIES; }
}

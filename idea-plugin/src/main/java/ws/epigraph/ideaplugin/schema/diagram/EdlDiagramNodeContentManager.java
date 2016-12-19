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

import com.intellij.diagram.AbstractDiagramNodeContentManager;
import com.intellij.diagram.DiagramCategory;
import com.intellij.diagram.presentation.DiagramState;
import ws.epigraph.ideaplugin.schema.presentation.EdlPresentationUtil;
import ws.epigraph.schema.parser.psi.EdlAnnotation;
import ws.epigraph.schema.parser.psi.EdlEnumMemberDecl;
import ws.epigraph.schema.parser.psi.EdlFieldDecl;
import ws.epigraph.schema.parser.psi.EdlVarTagDecl;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlDiagramNodeContentManager extends AbstractDiagramNodeContentManager {
  static final DiagramCategory TAGS = new DiagramCategory("Tags", EdlPresentationUtil.TAG_ICON, true);
  static final DiagramCategory FIELDS = new DiagramCategory("Fields", EdlPresentationUtil.FIELD_ICON, true);
  static final DiagramCategory ANNOTATIONS =
      new DiagramCategory("Annotations", EdlPresentationUtil.ANNOTATION_ICON, true);
  static final DiagramCategory ENUM_MEMBERS =
      new DiagramCategory("Enum members", EdlPresentationUtil.ENUM_MEMBER_ICON, true);

  static final DiagramCategory[] CATEGORIES = new DiagramCategory[]{TAGS, FIELDS, ANNOTATIONS, ENUM_MEMBERS};

  @SuppressWarnings("RedundantIfStatement")
  @Override
  public boolean isInCategory(Object o, DiagramCategory category, DiagramState diagramState) {
    if (o instanceof EdlVarTagDecl && category == TAGS) return true;
    if (o instanceof EdlFieldDecl && category == FIELDS) return true;
    if (o instanceof EdlAnnotation && category == ANNOTATIONS) return true;
    if (o instanceof EdlEnumMemberDecl && category == ENUM_MEMBERS) return true;

    return false;
  }

  @Override
  public DiagramCategory[] getContentCategories() { return CATEGORIES; }
}

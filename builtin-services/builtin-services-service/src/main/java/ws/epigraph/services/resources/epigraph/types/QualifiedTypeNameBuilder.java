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

package ws.epigraph.services.resources.epigraph.types;

import epigraph.schema.NameString;
import epigraph.schema.NameString_List;
import epigraph.schema.QualifiedTypeName;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.lang.Qn;
import ws.epigraph.services.resources.epigraph.projections.output.typenameprojection._normalized.qualifiedtypename.OutputQualifiedTypeNameProjection;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class QualifiedTypeNameBuilder {
  private QualifiedTypeNameBuilder() {}

  public static @NotNull QualifiedTypeName buildQualifiedTypeName(
      @NotNull ws.epigraph.names.QualifiedTypeName qtn,
      @NotNull OutputQualifiedTypeNameProjection projection) {

    QualifiedTypeName.Builder builder = QualifiedTypeName.create();

    if (projection.string() != null)
      builder.setString(NameString.create(qtn.toString()));

    if (projection.segments() != null) {
      final NameString_List.Builder segmentsBuilder = NameString_List.create();

      final Qn qn = qtn.toFqn();

      for (int i = 0; i < qn.size(); i++) {
        segmentsBuilder.add(NameString.create(qn.segments[i]));
      }

      builder.setSegments(segmentsBuilder);
    }

    return builder;
  }
}

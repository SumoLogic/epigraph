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

import epigraph.schema.*;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.lang.Qn;
import ws.epigraph.services.resources.epigraph.projections.output.datatypenameprojection.OutputDataTypeNameProjection;
import ws.epigraph.services.resources.epigraph.projections.output.typenameprojection.OutputTypeNameProjectionProjection;
import ws.epigraph.services.resources.epigraph.projections.output.typenameprojection._normalized.anonlisttypename.OutputAnonListTypeNameProjection;
import ws.epigraph.services.resources.epigraph.projections.output.typenameprojection._normalized.anonmaptypename.OutputAnonMapTypeNameProjection;
import ws.epigraph.services.resources.epigraph.projections.output.typenameprojection._normalized.anonmaptypename.record.OutputAnonMapTypeNameRecordProjection;
import ws.epigraph.services.resources.epigraph.projections.output.typenameprojection._normalized.qualifiedtypename.OutputQualifiedTypeNameProjection;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class TypeNameBuilder {
  private TypeNameBuilder() {}

  public static @NotNull TypeName buildTypeName(
      @NotNull ws.epigraph.names.TypeName typeName,
      @NotNull OutputTypeNameProjectionProjection projection) {

    // todo string representation should be built here

    if (typeName instanceof ws.epigraph.names.QualifiedTypeName) {
      ws.epigraph.names.QualifiedTypeName qualifiedTypeName = (ws.epigraph.names.QualifiedTypeName) typeName;
      return buildQualifiedTypeName(qualifiedTypeName, projection.normalizedFor_qualifiedTypeName());
    }

    if (typeName instanceof ws.epigraph.names.AnonMapTypeName) {
      ws.epigraph.names.AnonMapTypeName anonMapTypeName = (ws.epigraph.names.AnonMapTypeName) typeName;
      return buildAnonMapTypeName(anonMapTypeName, projection.normalizedFor_anonMapTypeName());
    }

    if (typeName instanceof ws.epigraph.names.AnonListTypeName) {
      ws.epigraph.names.AnonListTypeName anonListTypeName = (ws.epigraph.names.AnonListTypeName) typeName;
      return buildAnonListTypeName(anonListTypeName, projection.normalizedFor_anonListTypeName());
    }

    throw new IllegalArgumentException("Unsupported type name: " + typeName.getClass().getName());
  }

  public static @NotNull QualifiedTypeName buildQualifiedTypeName(
      @NotNull ws.epigraph.names.QualifiedTypeName typeName,
      @NotNull OutputQualifiedTypeNameProjection projection) {

    QualifiedTypeName.Builder builder = QualifiedTypeName.create();

    if (projection.string() != null)
      builder.setString(NameString.create(typeName.toString()));

    if (projection.segments() != null) {
      final NameString_List.Builder segmentsBuilder = NameString_List.create();

      final Qn qn = typeName.toFqn();

      for (int i = 0; i < qn.size(); i++) {
        segmentsBuilder.add(NameString.create(qn.segments[i]));
      }

      builder.setSegments(segmentsBuilder);
    }

    return builder;
  }

  public static @NotNull AnonListTypeName buildAnonListTypeName(
      @NotNull ws.epigraph.names.AnonListTypeName typeName,
      @NotNull OutputAnonListTypeNameProjection projection) {

    AnonListTypeName.Builder builder = AnonListTypeName.create();

    if (projection.string() != null)
      builder.setString(NameString.create(typeName.toString()));

    final OutputDataTypeNameProjection elementTypeNameProjection = projection.elementTypeName();
    if (elementTypeNameProjection != null)
      builder.setElementTypeName(buildDataTypeName(typeName.elementTypeName, elementTypeNameProjection));

    return builder;
  }

  public static @NotNull AnonMapTypeName buildAnonMapTypeName(
      @NotNull ws.epigraph.names.AnonMapTypeName typeName,
      @NotNull OutputAnonMapTypeNameProjection projection) {

    AnonMapTypeName.Builder builder = AnonMapTypeName.create();

    if (projection.string() != null)
      builder.setString(NameString.create(typeName.toString()));

    final OutputAnonMapTypeNameRecordProjection recordProjection = projection.record();
    if (recordProjection != null) {
      AnonMapTypeNameRecord.Builder recordBuilder = AnonMapTypeNameRecord.create();

      final OutputTypeNameProjectionProjection keyProjection = recordProjection.keyTypeName();
      if (keyProjection != null)
        recordBuilder.setKeyTypeName(buildTypeName(typeName.keyTypeName, keyProjection));

      final OutputDataTypeNameProjection valueProjection = recordProjection.valueTypeName();
      if (valueProjection != null)
        recordBuilder.setValueTypeName(buildDataTypeName(typeName.valueTypeName, valueProjection));

      builder.setRecord(recordBuilder);
    }

    return builder;
  }

  public static @NotNull DataTypeName buildDataTypeName(
      @NotNull ws.epigraph.names.DataTypeName dataTypeName,
      @NotNull OutputDataTypeNameProjection projection) {

    DataTypeName.Builder builder = DataTypeName.create();

    if (projection.retroTagName() != null)
      builder.setRetroTagName(dataTypeName.defaultTagName == null
                              ? null
                              : TagName.create().setString(NameString.create(dataTypeName.defaultTagName)));

    final OutputTypeNameProjectionProjection typeNameProjection = projection.typeName();
    if (typeNameProjection != null)
      builder.setTypeName(buildTypeName(dataTypeName.typeName, typeNameProjection));

    return builder;
  }
}

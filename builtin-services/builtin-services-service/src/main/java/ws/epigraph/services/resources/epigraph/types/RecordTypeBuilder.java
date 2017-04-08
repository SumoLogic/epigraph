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

import epigraph.schema.Field_List;
import epigraph.schema.RecordType;
import epigraph.schema.RecordType_List;
import epigraph.schema.Type_;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.services.resources.epigraph.projections.output.fieldprojection.OutputField_Projection;
import ws.epigraph.services.resources.epigraph.projections.output.typenameprojection._normalized.qualifiedtypename.OutputQualifiedTypeNameProjection;
import ws.epigraph.services.resources.epigraph.projections.output.typeprojection._normalized.datumtype._normalized.recordtype.OutputRecordTypeProjection;
import ws.epigraph.services.resources.epigraph.projections.output.typeprojection._normalized.datumtype._normalized.recordtype.declaredfields.OutputField_ListProjection;
import ws.epigraph.services.resources.epigraph.projections.output.typeprojection._normalized.datumtype._normalized.recordtype.supertypes.OutputRecordType_ListProjection;
import ws.epigraph.types.FieldApi;
import ws.epigraph.types.RecordTypeApi;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class RecordTypeBuilder {
  private RecordTypeBuilder() {}

  public static @NotNull RecordType buildRecordType(
      @NotNull RecordTypeApi type,
      @NotNull OutputRecordTypeProjection projection,
      @NotNull TypeBuilder.Context context) {

    final TypeBuilder.Context.Key key = new TypeBuilder.Context.Key(type, projection._raw());
    final Type_ v = context.visited.get(key);
    if (v != null)
      return (RecordType) v;

    RecordType.Builder builder = RecordType.create();
    context.visited.put(key, builder);

    // name
    final OutputQualifiedTypeNameProjection nameProjection = projection.name();
    if (nameProjection != null)
      builder.setName(TypeNameBuilder.buildQualifiedTypeName(type.name(), nameProjection));

    // supertypes
    final OutputRecordType_ListProjection supertypesProjection = projection.supertypes();
    if (supertypesProjection != null) {
      final OutputRecordTypeProjection supertypeProjection = supertypesProjection.itemsProjection();

      RecordType_List.Builder supertypesBuilder = RecordType_List.create();

      for (final RecordTypeApi supertype : type.supertypes())
        supertypesBuilder.add(buildRecordType(supertype, supertypeProjection, context));

      builder.setSupertypes(supertypesBuilder);
    }

    // declared fields
    final OutputField_ListProjection fieldsProjection = projection.declaredFields();
    if (fieldsProjection != null) {
      final OutputField_Projection fieldProjection = fieldsProjection.itemsProjection();

      Field_List.Builder fieldsBuilder = Field_List.create();
      for (final FieldApi field : type.immediateFields())
        fieldsBuilder.add(FieldBuilder.buildField(field, fieldProjection, context));

      builder.setDeclaredFields(fieldsBuilder);
    }

    return builder;
  }
}

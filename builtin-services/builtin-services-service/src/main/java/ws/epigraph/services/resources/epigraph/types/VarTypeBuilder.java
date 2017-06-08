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

import epigraph.schema.Tag_List;
import epigraph.schema.VarType;
import epigraph.schema.VarType_List;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.services._resources.epigraph.projections.output.tagprojection.OutputTag_Projection;
import ws.epigraph.services._resources.epigraph.projections.output.typenameprojection._normalized.qualifiedtypename.OutputQualifiedTypeNameProjection;
import ws.epigraph.services._resources.epigraph.projections.output.typeprojection._normalized.vartype.OutputVarTypeProjection;
import ws.epigraph.services._resources.epigraph.projections.output.typeprojection._normalized.vartype.supertypes.OutputVarType_ListProjection;
import ws.epigraph.services._resources.epigraph.projections.output.typeprojection._normalized.vartype.tags.OutputTag_ListProjection;
import ws.epigraph.types.EntityTypeApi;
import ws.epigraph.types.TagApi;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class VarTypeBuilder {
  private VarTypeBuilder() {}

  public static @NotNull VarType buildVarType(
      @NotNull EntityTypeApi type,
      @NotNull OutputVarTypeProjection projection,
      @NotNull TypeBuilder.Context context) {

    VarType.Builder builder = VarType.create();

    // name
    final OutputQualifiedTypeNameProjection nameProjection = projection.name();
    if (nameProjection != null) {
      builder.setName(
          TypeNameBuilder.buildQualifiedTypeName(type.name(), nameProjection)
      );
    }

    // supertypes
    final OutputVarType_ListProjection supertypesProjection = projection.supertypes();
    if (supertypesProjection != null) {
      final OutputVarTypeProjection supertypeProjection = supertypesProjection.itemsProjection();

      VarType_List.Builder supertypesBuilder = VarType_List.create();

      for (final EntityTypeApi supertype : type.supertypes())
        supertypesBuilder.add(buildVarType(supertype, supertypeProjection, context));

      builder.setSupertypes(supertypesBuilder);
    }

    // tags
    final OutputTag_ListProjection tagsProjection = projection.tags();
    if (tagsProjection != null) {
      final OutputTag_Projection tagProjection = tagsProjection.itemsProjection();

      Tag_List.Builder tagsBuilder = Tag_List.create();
      for (final TagApi tag : type.tags())
        tagsBuilder.add(TagBuilder.buildTag(tag, tagProjection, context));

      builder.setTags(tagsBuilder);
    }

    return builder;
  }

}

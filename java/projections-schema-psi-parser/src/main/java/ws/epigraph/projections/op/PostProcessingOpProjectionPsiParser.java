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

package ws.epigraph.projections.op;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.parser.psi.SchemaOpEntityProjection;
import ws.epigraph.schema.parser.psi.SchemaOpFieldProjection;
import ws.epigraph.schema.parser.psi.SchemaOpModelProjection;
import ws.epigraph.schema.parser.psi.SchemaOpUnnamedOrRefEntityProjection;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.types.DatumTypeApi;
import ws.epigraph.util.Tuple2;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class PostProcessingOpProjectionPsiParser implements OpProjectionPsiParser {
  // keep in sync with PostProcessingReqProjectionsPsiParser

  private final @Nullable OpProjectionTraversal traversal;
  private final @Nullable OpProjectionTransformer transformer;

  public PostProcessingOpProjectionPsiParser(
      @Nullable OpProjectionTraversal traversal,
      @Nullable OpProjectionTransformer transformer) {

    this.traversal = traversal;
    this.transformer = transformer;
  }

  @Override
  public @NotNull OpProjection<?, ?> parseProjection(
      final @NotNull DataTypeApi dataType,
      final boolean flagged,
      final @NotNull SchemaOpEntityProjection psi,
      final @NotNull TypesResolver typesResolver,
      final @NotNull OpPsiProcessingContext context) throws PsiProcessingException {

    OpProjection<?, ?> res =
        OpBasicProjectionPsiParser.parseProjection(dataType, flagged, psi, typesResolver, context);

    return processProjection(res, context);
  }

  @Override
  public @NotNull OpFieldProjection parseFieldProjection(
      final @NotNull DataTypeApi fieldType,
      final boolean flagged,
      final @NotNull SchemaOpFieldProjection psi,
      final @NotNull TypesResolver resolver,
      final @NotNull OpPsiProcessingContext context) throws PsiProcessingException {

    OpFieldProjection res =
        OpBasicProjectionPsiParser.parseFieldProjection(fieldType, flagged, psi, resolver, context);

    OpProjection<?, ?> transformedEp = processProjection(res.projection(), context);

    return new OpFieldProjection(transformedEp, res.location());
  }

  @Override
  public @NotNull OpProjection<?, ?> parseUnnamedOrRefProjection(
      final @NotNull DataTypeApi dataType,
      final boolean flagged,
      final @NotNull SchemaOpUnnamedOrRefEntityProjection psi,
      final @NotNull TypesResolver typesResolver,
      final @NotNull OpPsiProcessingContext context) throws PsiProcessingException {

    OpProjection<?, ?> res =
        OpBasicProjectionPsiParser.parseUnnamedOrRefProjection(
            dataType,
            flagged,
            psi,
            null,
            typesResolver,
            context
        );

    return processProjection(res, context);
  }

  @Override
  public @NotNull OpModelProjection<?, ?, ?, ?> parseModelProjection(
      final @NotNull DatumTypeApi type,
      final boolean flagged,
      final @NotNull SchemaOpModelProjection psi,
      final @NotNull TypesResolver typesResolver,
      final @NotNull OpPsiProcessingContext context) throws PsiProcessingException {

    OpModelProjection<?, ?, ?, ?> res =
        OpBasicProjectionPsiParser.parseModelProjection(type, flagged, psi, typesResolver, context);

    return processProjection(res, context).asModelProjection();
  }

  private @NotNull OpProjection<?, ?> processProjection(
      @NotNull OpProjection<?, ?> p,
      @NotNull OpPsiProcessingContext context) {

    if (traversal != null) {
      traversal.traverse(p);
    }

    if (transformer == null)
      return p;
    else {
      Tuple2<OpProjection<?, ?>, Map<OpProjection<?, ?>, OpProjection<?, ?>>> tuple2 = transformer.transform(p, null);

      OpProjection<?, ?> transformedMp = tuple2._1;
      Map<OpProjection<?, ?>, OpProjection<?, ?>> transformationMap = tuple2._2;

      context.referenceContext().transform(transformationMap);
      return transformedMp;
    }
  }

}

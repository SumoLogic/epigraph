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

package ws.epigraph.url.projections.req;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.op.OpFieldProjection;
import ws.epigraph.projections.op.OpProjection;
import ws.epigraph.projections.req.*;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.url.parser.psi.UrlReqComaEntityProjection;
import ws.epigraph.url.parser.psi.UrlReqTrunkEntityProjection;
import ws.epigraph.url.parser.psi.UrlReqTrunkFieldProjection;
import ws.epigraph.util.Tuple2;

import java.util.Map;

/**
 * Psi parser that calls {@link ReqBasicProjectionPsiParser} to build basic structure, then
 * optionally applies supplied traversal and transformer to it.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class PostProcessingReqProjectionPsiParser implements ReqProjectionPsiParser {
  // keep in sync with PostProcessingOpProjectionsPsiParser

  private final @Nullable ReqProjectionTraversal traversal;
  private final @Nullable ReqProjectionTransformer transformer;
  private final @NotNull DefaultReqProjectionConstructor defaultProjectionConstructor;

  public PostProcessingReqProjectionPsiParser(
      @Nullable ReqProjectionTraversal traversal,
      @Nullable ReqProjectionTransformer transformer,
      @NotNull DefaultReqProjectionConstructor defaultReqProjectionConstructor) {

    this.traversal = traversal;
    this.transformer = transformer;
    defaultProjectionConstructor = defaultReqProjectionConstructor;
  }

  @Override
  public @NotNull StepsAndProjection<ReqProjection<?, ?>> parseTrunkProjection(
      @NotNull DataTypeApi dataType,
      boolean flagged,
      @NotNull OpProjection<?, ?> op,
      @NotNull UrlReqTrunkEntityProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    @NotNull StepsAndProjection<ReqProjection<?, ?>> stepsAndProjection =
        new ReqBasicProjectionPsiParser(defaultProjectionConstructor, context).parseTrunkProjection(
            dataType, flagged, op, psi, resolver
        );

    return processProjection(stepsAndProjection, op, context);
  }

  @Override
  public StepsAndProjection<ReqProjection<?, ?>> parseComaProjection(
      @NotNull DataTypeApi dataType,
      boolean flagged,
      @NotNull OpProjection<?, ?> op,
      @NotNull UrlReqComaEntityProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    @NotNull StepsAndProjection<ReqProjection<?, ?>> stepsAndProjection =
        new ReqBasicProjectionPsiParser(defaultProjectionConstructor, context).parseComaProjection(
            dataType, flagged, op, psi, resolver
        );

    return processProjection(stepsAndProjection, op, context);
  }

  @Override
  public @NotNull ReqProjection<?, ?> createDefaultProjection(
      @NotNull DataTypeApi type,
      OpProjection<?, ?> op,
      boolean required,
      @NotNull TypesResolver resolver,
      @NotNull TextLocation location,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    return defaultProjectionConstructor.createDefaultProjection(
        type, op, required, null, resolver, location, context
    );
  }

  @Override
  public @NotNull StepsAndProjection<ReqFieldProjection> parseTrunkFieldProjection(
      @NotNull DataTypeApi fieldType,
      boolean flagged,
      @NotNull OpFieldProjection op,
      @NotNull UrlReqTrunkFieldProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    StepsAndProjection<ReqFieldProjection> stepsAndProjection =
        new ReqBasicProjectionPsiParser(defaultProjectionConstructor, context).parseTrunkFieldProjection(
            fieldType, flagged, op, psi, resolver
        );

    ReqFieldProjection fieldProjection = stepsAndProjection.projection();
    ReqProjection<?, ?> ep = fieldProjection.projection();

    ReqProjection<?, ?> transformedEp = processProjection(ep, op.projection(), context);

    return new StepsAndProjection<>(
        stepsAndProjection.pathSteps(),
        new ReqFieldProjection(
            transformedEp,
            fieldProjection.location()
        )
    );
  }

  private @NotNull StepsAndProjection<ReqProjection<?, ?>> processProjection(
      @NotNull StepsAndProjection<ReqProjection<?, ?>> s,
      @NotNull OpProjection<?, ?> op,
      @NotNull ReqPsiProcessingContext context) {

    return new StepsAndProjection<>(
        s.pathSteps(),
        processProjection(
            s.projection(),
            op,
            context
        )
    );
  }

  private @NotNull ReqProjection<?, ?> processProjection(
      @NotNull ReqProjection<?, ?> ep,
      @NotNull OpProjection<?, ?> op,
      @NotNull ReqPsiProcessingContext context) {

    if (traversal != null) {
      traversal.traverse(ep, op);
    }

    if (transformer == null)
      return ep;
    else {
      Tuple2<ReqProjection<?, ?>, Map<ReqProjection<?, ?>, ReqProjection<?, ?>>> tuple2 =
          transformer.transform(ep, null);

      ReqProjection<?, ?> transformedEp = tuple2._1;
      Map<ReqProjection<?, ?>, ReqProjection<?, ?>> transformationMap = tuple2._2;

      context.referenceContext().transform(transformationMap);
      return transformedEp;
    }
  }
}

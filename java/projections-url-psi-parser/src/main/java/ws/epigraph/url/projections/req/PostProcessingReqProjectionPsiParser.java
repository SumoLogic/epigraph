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
import ws.epigraph.projections.op.OpEntityProjection;
import ws.epigraph.projections.op.OpFieldProjection;
import ws.epigraph.projections.req.ReqEntityProjection;
import ws.epigraph.projections.req.ReqFieldProjection;
import ws.epigraph.projections.req.ReqProjectionTraversal;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.url.parser.psi.UrlReqComaEntityProjection;
import ws.epigraph.url.parser.psi.UrlReqTrunkEntityProjection;
import ws.epigraph.url.parser.psi.UrlReqTrunkFieldProjection;
import ws.epigraph.util.Tuple2;

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
  public @NotNull StepsAndProjection<ReqEntityProjection> parseTrunkEntityProjection(
      @NotNull DataTypeApi dataType,
      boolean flagged,
      @NotNull OpEntityProjection op,
      @NotNull UrlReqTrunkEntityProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    StepsAndProjection<ReqEntityProjection> stepsAndProjection = ReqBasicProjectionPsiParser.parseTrunkEntityProjection(
        dataType, flagged, op, psi, resolver, defaultProjectionConstructor, context
    );

    return processEntityProjection(stepsAndProjection, op, context);
  }

  @Override
  public @NotNull StepsAndProjection<ReqEntityProjection> parseComaEntityProjection(
      @NotNull DataTypeApi dataType,
      boolean flagged,
      @NotNull OpEntityProjection op,
      @NotNull UrlReqComaEntityProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    StepsAndProjection<ReqEntityProjection> stepsAndProjection = ReqBasicProjectionPsiParser.parseComaEntityProjection(
        dataType, flagged, op, psi, resolver, defaultProjectionConstructor, context
    );

    return processEntityProjection(stepsAndProjection, op, context);
  }

  @Override
  public ReqEntityProjection createDefaultEntityProjection(
      @NotNull DataTypeApi type,
      @NotNull OpEntityProjection op,
      boolean required,
      @NotNull TypesResolver resolver,
      @NotNull TextLocation location,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    return defaultProjectionConstructor.createDefaultEntityProjection(
        type, op, required, resolver,location, context
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

    StepsAndProjection<ReqFieldProjection> stepsAndProjection = ReqBasicProjectionPsiParser.parseTrunkFieldProjection(
        fieldType, flagged, op, psi, resolver, defaultProjectionConstructor, context
    );

    ReqFieldProjection fieldProjection = stepsAndProjection.projection();
    ReqEntityProjection ep = fieldProjection.entityProjection();

    ReqEntityProjection transformedEp = processEntityProjection(ep, op.entityProjection(), context);

    return new StepsAndProjection<>(
        stepsAndProjection.pathSteps(),
        new ReqFieldProjection(
            transformedEp,
            fieldProjection.location()
        )
    );
  }

  private @NotNull StepsAndProjection<ReqEntityProjection> processEntityProjection(
      @NotNull StepsAndProjection<ReqEntityProjection> s,
      @NotNull OpEntityProjection op,
      @NotNull ReqPsiProcessingContext context) {

    return new StepsAndProjection<>(
        s.pathSteps(),
        processEntityProjection(
            s.projection(),
            op,
            context
        )
    );
  }

  private @NotNull ReqEntityProjection processEntityProjection(
      @NotNull ReqEntityProjection ep,
      @NotNull OpEntityProjection op,
      @NotNull ReqPsiProcessingContext context) {

    if (traversal != null) {
      traversal.traverse(ep, op);
    }

    if (transformer == null)
      return ep;
    else {
      Tuple2<ReqEntityProjection, ReqProjectionTransformationMap> tuple2 = transformer.transform(ep, null);
      ReqEntityProjection transformedEp = tuple2._1;
      ReqProjectionTransformationMap transformationMap = tuple2._2;

      context.referenceContext().transform(transformationMap);
      return transformedEp;
    }
  }
}

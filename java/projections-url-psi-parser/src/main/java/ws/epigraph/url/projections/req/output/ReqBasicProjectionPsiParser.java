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

package ws.epigraph.url.projections.req.output;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Datum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.ProjectionsParsingUtil;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.op.*;
import ws.epigraph.projections.req.*;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypeRef;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.*;
import ws.epigraph.url.TypeRefs;
import ws.epigraph.url.parser.psi.*;
import ws.epigraph.url.projections.UrlProjectionsPsiParserUtil;
import ws.epigraph.url.projections.req.ReqPsiProcessingContext;

import java.util.*;

import static ws.epigraph.projections.ProjectionsParsingUtil.*;
import static ws.epigraph.url.projections.UrlProjectionsPsiParserUtil.*;

/**
 * Basic projections parser that does no further post-processing.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class ReqBasicProjectionPsiParser {
  private ReqBasicProjectionPsiParser() {}

  // trunk var ================================================

  public static @NotNull StepsAndProjection<ReqEntityProjection> parseTrunkEntityProjection(
      @NotNull DataTypeApi dataType,
      boolean flagged,
      @NotNull OpEntityProjection op,
      @NotNull UrlReqTrunkEntityProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    final @Nullable UrlReqNamedTrunkEntityProjection namedEntityProjection = psi.getReqNamedTrunkEntityProjection();
    if (namedEntityProjection == null) {
      final @Nullable UrlReqUnnamedOrRefTrunkEntityProjection unnamedOrRefEntityProjection =
          psi.getReqUnnamedOrRefTrunkEntityProjection();

      if (unnamedOrRefEntityProjection == null)
        throw new PsiProcessingException(
            "Incomplete var projection definition",
            psi,
            context
        );

      return parseUnnamedOrRefTrunkEntityProjection(
          dataType,
          flagged,
          op,
          unnamedOrRefEntityProjection,
          resolver,
          context
      );
    } else {
      // named var projection
      final String projectionName = namedEntityProjection.getQid().getCanonicalName();

      final @Nullable UrlReqUnnamedOrRefTrunkEntityProjection unnamedOrRefEntityProjection =
          namedEntityProjection.getReqUnnamedOrRefTrunkEntityProjection();

      if (unnamedOrRefEntityProjection == null)
        throw new PsiProcessingException(
            String.format("Incomplete var projection '%s' definition", projectionName),
            psi,
            context
        );

      final ReqEntityProjection reference = context.referenceContext()
          .entityReference(dataType.type(), projectionName, false, EpigraphPsiUtil.getLocation(psi));

      final @NotNull StepsAndProjection<ReqEntityProjection> stepsAndProjection =
          parseUnnamedOrRefTrunkEntityProjection(
              dataType,
              flagged,
              op,
              unnamedOrRefEntityProjection,
              resolver,
              context
          );

      context.referenceContext()
          .resolveEntityRef(
              projectionName,
              stepsAndProjection.projection(),
              EpigraphPsiUtil.getLocation(unnamedOrRefEntityProjection)
          );

      final Queue<OpEntityProjection> unverifiedOps = context.unverifiedRefOps(projectionName);
      while (unverifiedOps != null && !unverifiedOps.isEmpty()) {
        final OpEntityProjection unverifiedOp = unverifiedOps.poll();
        context.addVerifiedRefOp(projectionName, unverifiedOp);

        parseUnnamedOrRefTrunkEntityProjection(
            dataType,
            flagged,
            unverifiedOp,
            unnamedOrRefEntityProjection,
            resolver,
            context
        );
      }

      return new StepsAndProjection<>(
          stepsAndProjection.pathSteps(),
          reference
      );
    }

  }

  public static @NotNull StepsAndProjection<ReqEntityProjection> parseUnnamedOrRefTrunkEntityProjection(
      final @NotNull DataTypeApi dataType,
      final boolean flagged,
      final @NotNull OpEntityProjection op,
      final @NotNull UrlReqUnnamedOrRefTrunkEntityProjection psi,
      final @NotNull TypesResolver resolver,
      final @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    final UrlReqTrunkEntityProjectionRef varProjectionRef = psi.getReqTrunkEntityProjectionRef();
    if (varProjectionRef == null) {
      // usual var projection
      final UrlReqUnnamedTrunkEntityProjection unnamedEntityProjection = psi.getReqUnnamedTrunkEntityProjection();
      if (unnamedEntityProjection == null)
        throw new PsiProcessingException("Incomplete var projection definition", psi, context.messages());
      else {
        return parseUnnamedTrunkEntityProjection(
            dataType,
            flagged,
            op,
            unnamedEntityProjection,
            resolver,
            context
        );
      }
    } else {
      // var projection reference
      throw new PsiProcessingException("References are not allowed in path", psi, context);
    }
  }

  public static @NotNull StepsAndProjection<ReqEntityProjection> parseUnnamedTrunkEntityProjection(
      @NotNull DataTypeApi dataType,
      boolean flagged,
      @NotNull OpEntityProjection op,
      @NotNull UrlReqUnnamedTrunkEntityProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    final TypeApi type = dataType.type();
    final LinkedHashMap<String, ReqTagProjectionEntry> tagProjections;
    final int steps;
    final boolean parenthesized;

    @Nullable UrlReqTrunkSingleTagProjection singleTagProjectionPsi = psi.getReqTrunkSingleTagProjection();
    final @NotNull TypesResolver subResolver = addTypeNamespace(dataType.type(), typesResolver);

    if (psi.getReqStarTagProjection() != null) {
      steps = 0;
      parenthesized = true;

      tagProjections = new LinkedHashMap<>();
      addStarTags(op, tagProjections, context, psi);
    } else if (singleTagProjectionPsi != null) {
      // try to improve error reporting: singleTagProjectionPsi may be empty
      TextLocation tagLocation = getSingleTagLocation(singleTagProjectionPsi);

      tagProjections = new LinkedHashMap<>();

      final ReqModelProjection<?, ?, ?> parsedModelProjection;
      final @Nullable UrlTagName tagNamePsi = singleTagProjectionPsi.getTagName();

      @Nullable TagApi tag = findTag(dataType, getTagName(tagNamePsi), op, tagLocation, context);

      if (tag == null) {
        // no retro of self tag found
        // will be OK in only one case: if singleTagProjectionPsi is empty
        // we want to treat `( bestFriend )` as `( bestFriend:() )`
        if (singleTagProjectionPsi.getText().trim().isEmpty()) {
          // tagProjections stay empty
          parenthesized = false;
          steps = 0;
        } else {
          throw noRetroTagError(dataType, tagLocation, context);
        }

      } else {

        @NotNull OpTagProjectionEntry opTagProjection =
            getTagProjection(tag.name(), op, tagLocation, context);

        @NotNull OpModelProjection<?, ?, ?, ?> opModelProjection = opTagProjection.projection();
        @NotNull UrlReqTrunkModelProjection modelProjectionPsi =
            singleTagProjectionPsi.getReqTrunkModelProjection();

        StepsAndProjection<? extends ReqModelProjection<?, ?, ?>> stepsAndProjection = parseTrunkModelProjection(
            opModelProjection,
            singleTagProjectionPsi.getPlus() != null,
            parseReqParams(
                singleTagProjectionPsi.getReqParamList(),
                opModelProjection.params(),
                subResolver,
                singleTagProjectionPsi,
                context
            ),
            parseAnnotations(singleTagProjectionPsi.getReqAnnotationList(), context),
            parseModelMetaProjection(
                opModelProjection,
                singleTagProjectionPsi.getReqModelMeta(),
                subResolver,
                context
            ),
            modelProjectionPsi,
            subResolver,
            context
        );

        parsedModelProjection = stepsAndProjection.projection();
        steps = stepsAndProjection.pathSteps() + 1;

        tagProjections.put(
            tag.name(),
            new ReqTagProjectionEntry(
                tag,
                parsedModelProjection,
                tagLocation
            )
        );

        parenthesized = false;
      }

    } else {
      @Nullable UrlReqComaMultiTagProjection multiTagProjection = psi.getReqComaMultiTagProjection();
      assert multiTagProjection != null;
      tagProjections = parseComaMultiTagProjection(dataType, op, multiTagProjection, subResolver, context);
      steps = 0;
      parenthesized = true;
    }

    final List<ReqEntityProjection> tails =
        parseTails(dataType, flagged, op, psi.getReqEntityPolymorphicTail(), subResolver, context);

    try {
      return new StepsAndProjection<>(
          steps,
          new ReqEntityProjection(
              type,
              flagged,
              tagProjections,
              parenthesized,
              tails,
              EpigraphPsiUtil.getLocation(psi)
          )
      );
    } catch (Exception e) {
      throw new PsiProcessingException(e, psi, context);
    }
  }

  private static @NotNull TextLocation
  getSingleTagLocation(final @NotNull UrlReqTrunkSingleTagProjection singleTagProjectionPsi) {
    final UrlTagName tagName = singleTagProjectionPsi.getTagName();
    if (tagName != null) return EpigraphPsiUtil.getLocation(tagName);
    PsiElement tagLocation = singleTagProjectionPsi;
    if (tagLocation.getText().isEmpty()) {
      final @Nullable UrlReqComaFieldProjection fieldProjectionPsi =
          PsiTreeUtil.getParentOfType(tagLocation, UrlReqComaFieldProjection.class);
      if (fieldProjectionPsi == null) {
        final @Nullable UrlReqTrunkRecordModelProjection recordProjectionPsi =
            PsiTreeUtil.getParentOfType(tagLocation, UrlReqTrunkRecordModelProjection.class);
        if (recordProjectionPsi != null)
          tagLocation = recordProjectionPsi.getQid();
      } else {
        tagLocation = fieldProjectionPsi.getQid();
      }
    }
    return EpigraphPsiUtil.getLocation(tagLocation);
  }

  private static void addStarTags(
      final @NotNull OpEntityProjection op,
      final @NotNull LinkedHashMap<String, ReqTagProjectionEntry> tagProjections,
      final @NotNull ReqPsiProcessingContext context,
      final @NotNull PsiElement locationPsi) throws PsiProcessingException {

    TextLocation location = EpigraphPsiUtil.getLocation(locationPsi);

    for (final Map.Entry<String, OpTagProjectionEntry> entry : op.tagProjections().entrySet()) {
      final TagApi tag = entry.getValue().tag();
      final OpModelProjection<?, ?, ?, ?> opModelProjection = entry.getValue().projection();

      tagProjections.put(
          entry.getKey(),
          new ReqTagProjectionEntry(
              tag,
              createDefaultModelProjection(
                  tag.type(),
                  false,
                  opModelProjection,
                  ReqParams.EMPTY,
                  Directives.EMPTY,
                  location,
                  context
              ),
              location
          )
      );
    }
  }

  // coma var ================================================

  public static @NotNull StepsAndProjection<ReqEntityProjection> parseComaEntityProjection(
      @NotNull DataTypeApi dataType,
      boolean flagged,
      @NotNull OpEntityProjection op,
      @NotNull UrlReqComaEntityProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    final UrlReqNamedComaEntityProjection namedEntityProjection = psi.getReqNamedComaEntityProjection();
    if (namedEntityProjection == null) {
      final UrlReqUnnamedOrRefComaEntityProjection unnamedOrRefEntityProjection =
          psi.getReqUnnamedOrRefComaEntityProjection();

      if (unnamedOrRefEntityProjection == null)
        throw new PsiProcessingException(
            "Incomplete var projection definition",
            psi,
            context
        );

      return parseUnnamedOrRefComaEntityProjection(
          dataType,
          flagged,
          op,
          unnamedOrRefEntityProjection,
          resolver,
          context
      );
    } else {
      // named var projection
      final String projectionName = namedEntityProjection.getQid().getCanonicalName();

      final @Nullable UrlReqUnnamedOrRefComaEntityProjection unnamedOrRefEntityProjection =
          namedEntityProjection.getReqUnnamedOrRefComaEntityProjection();

      if (unnamedOrRefEntityProjection == null)
        throw new PsiProcessingException(
            String.format("Incomplete var projection '%s' definition", projectionName),
            psi,
            context
        );

      final ReqEntityProjection reference = context.referenceContext()
          .entityReference(dataType.type(), projectionName, false, EpigraphPsiUtil.getLocation(psi));

      final StepsAndProjection<ReqEntityProjection> stepsAndProjection = parseUnnamedOrRefComaEntityProjection(
          dataType,
          flagged,
          op,
          unnamedOrRefEntityProjection,
          resolver,
          context
      );

      context.referenceContext()
          .resolveEntityRef(
              projectionName,
              stepsAndProjection.projection(),
              EpigraphPsiUtil.getLocation(unnamedOrRefEntityProjection)
          );

      final Queue<OpEntityProjection> unverifiedOps = context.unverifiedRefOps(projectionName);
      while (unverifiedOps != null && !unverifiedOps.isEmpty()) {
        final OpEntityProjection unverifiedOp = unverifiedOps.poll();
        context.addVerifiedRefOp(projectionName, unverifiedOp);

        parseUnnamedOrRefComaEntityProjection(
            dataType,
            flagged,
            unverifiedOp,
            unnamedOrRefEntityProjection,
            resolver,
            context
        );
      }

      return new StepsAndProjection<>(
          stepsAndProjection.pathSteps(),
          reference
      );
    }

  }

  public static StepsAndProjection<ReqEntityProjection> parseUnnamedOrRefComaEntityProjection(
      final @NotNull DataTypeApi dataType,
      boolean flagged,
      final @NotNull OpEntityProjection op,
      final @NotNull UrlReqUnnamedOrRefComaEntityProjection psi,
      final @NotNull TypesResolver resolver,
      final @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    final UrlReqComaEntityProjectionRef varProjectionRef = psi.getReqComaEntityProjectionRef();
    if (varProjectionRef == null) {
      // usual var projection
      final UrlReqUnnamedComaEntityProjection unnamedEntityProjection = psi.getReqUnnamedComaEntityProjection();
      if (unnamedEntityProjection == null)
        throw new PsiProcessingException("Incomplete var projection definition", psi, context.messages());
      else {
        return parseUnnamedComaEntityProjection(
            dataType,
            flagged,
            op,
            unnamedEntityProjection,
            resolver,
            context
        );
      }
    } else {
      // var projection reference
      final UrlQid varProjectionRefPsi = varProjectionRef.getQid();
      if (varProjectionRefPsi == null)
        throw new PsiProcessingException(
            "Incomplete var projection definition: name not specified",
            psi,
            context.messages()
        );

      final String referenceName = varProjectionRefPsi.getCanonicalName();

      final Collection<OpEntityProjection> verifiedOps = context.verifiedRefOps(referenceName);
      if (verifiedOps == null || !verifiedOps.contains(op))
        context.addUnverifiedRefOp(referenceName, op);

      return new StepsAndProjection<>(
          0,
          context.referenceContext()
              .entityReference(dataType.type(), referenceName, true, EpigraphPsiUtil.getLocation(psi))
      );
    }

  }

  public static StepsAndProjection<ReqEntityProjection> parseUnnamedComaEntityProjection(
      @NotNull DataTypeApi dataType,
      boolean flagged,
      @NotNull OpEntityProjection op,
      @NotNull UrlReqUnnamedComaEntityProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    final TypeApi type = dataType.type();
    final LinkedHashMap<String, ReqTagProjectionEntry> tagProjections;
    final boolean parenthesized;

    final @NotNull TypesResolver subResolver = addTypeNamespace(dataType.type(), typesResolver);
    @Nullable UrlReqComaSingleTagProjection singleTagProjectionPsi = psi.getReqComaSingleTagProjection();

    if (psi.getReqStarTagProjection() != null) {
      parenthesized = true;

      tagProjections = new LinkedHashMap<>();
      addStarTags(op, tagProjections, context, psi);
    } else if (singleTagProjectionPsi != null) {
      tagProjections = new LinkedHashMap<>();

      TextLocation tagLocation = getSingleTagLocation(singleTagProjectionPsi);
      TagApi tag = ProjectionsParsingUtil.findTag(
          dataType,
          UrlProjectionsPsiParserUtil.getTagName(singleTagProjectionPsi.getTagName()),
          op,
          tagLocation,
          context
      );

      if (tag == null && !singleTagProjectionPsi.getText().isEmpty()) {
        // can't deduce the tag but there's a projection specified for it
        raiseNoTagsError(dataType, op, singleTagProjectionPsi, context);
      }

      if (tag != null) {
        @NotNull OpTagProjectionEntry opTagProjection =
            getTagProjection(tag.name(), op, EpigraphPsiUtil.getLocation(singleTagProjectionPsi), context);

        @NotNull OpModelProjection<?, ?, ?, ?> opModelProjection = opTagProjection.projection();

        @NotNull UrlReqComaModelProjection modelProjectionPsi =
            singleTagProjectionPsi.getReqComaModelProjection();

        final ReqModelProjection<?, ?, ?> parsedModelProjection = parseComaModelProjection(
            ReqModelProjection.class,
            opModelProjection,
            singleTagProjectionPsi.getPlus() != null,
            parseReqParams(
                singleTagProjectionPsi.getReqParamList(),
                opModelProjection.params(),
                subResolver,
                singleTagProjectionPsi,
                context
            ),
            parseAnnotations(singleTagProjectionPsi.getReqAnnotationList(), context),
            parseModelMetaProjection(
                opModelProjection,
                singleTagProjectionPsi.getReqModelMeta(),
                subResolver,
                context
            ),
            modelProjectionPsi, subResolver, context
        );

        tagProjections.put(
            tag.name(),
            new ReqTagProjectionEntry(
                tag,
                parsedModelProjection,
                EpigraphPsiUtil.getLocation(singleTagProjectionPsi)
            )
        );
      }

      parenthesized = false;

    } else {
      @Nullable UrlReqComaMultiTagProjection multiTagProjection = psi.getReqComaMultiTagProjection();
      assert multiTagProjection != null;
      tagProjections = parseComaMultiTagProjection(dataType, op, multiTagProjection, subResolver, context);
      parenthesized = true;
    }

    final List<ReqEntityProjection> tails =
        parseTails(dataType, flagged, op, psi.getReqEntityPolymorphicTail(), subResolver, context);

    try {
      return new StepsAndProjection<>(
          0,
          new ReqEntityProjection(
              type,
              flagged,
              tagProjections,
              parenthesized,
              tails,
              EpigraphPsiUtil.getLocation(psi)
          )
      );
    } catch (Exception e) {
      throw new PsiProcessingException(e.toString(), psi, context);
    }
  }

  private static @NotNull TextLocation getSingleTagLocation(final @NotNull UrlReqComaSingleTagProjection singleTagProjectionPsi) {
    final UrlTagName tagName = singleTagProjectionPsi.getTagName();
    if (tagName != null) return EpigraphPsiUtil.getLocation(tagName);
    PsiElement tagLocation = singleTagProjectionPsi;
    if (tagLocation.getText().isEmpty()) {
      final @Nullable UrlReqComaFieldProjection fieldProjectionPsi =
          PsiTreeUtil.getParentOfType(tagLocation, UrlReqComaFieldProjection.class);
      if (fieldProjectionPsi != null) {
        tagLocation = fieldProjectionPsi.getQid();
      }
    }
    return EpigraphPsiUtil.getLocation(tagLocation);
  }

  private static LinkedHashMap<String, ReqTagProjectionEntry> parseComaMultiTagProjection(
      @NotNull DataTypeApi dataType,
      @NotNull OpEntityProjection op,
      @NotNull UrlReqComaMultiTagProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    if (!(dataType.type().equals(op.type())))
      throw new PsiProcessingException(
          String.format("Inconsistent arguments. data type: '%s', op type: '%s'", dataType.name(), op.type().name()),
          psi,
          context
      );

    final @NotNull TypesResolver subResolver = addTypeNamespace(dataType.type(), typesResolver);

    final LinkedHashMap<String, ReqTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    // parse list of tags
    @NotNull Iterable<UrlReqComaMultiTagProjectionItem> tagProjectionPsiList =
        psi.getReqComaMultiTagProjectionItemList();

    for (UrlReqComaMultiTagProjectionItem tagProjectionPsi : tagProjectionPsiList) {
      try {
        @NotNull TagApi tag = getTag(
            dataType,
            getTagName(tagProjectionPsi.getTagName()),
            op,
            EpigraphPsiUtil.getLocation(tagProjectionPsi),
            context
        );

        @NotNull OpTagProjectionEntry opTag =
            getTagProjection(tag.name(), op, EpigraphPsiUtil.getLocation(tagProjectionPsi), context);

        OpModelProjection<?, ?, ?, ?> opTagProjection = opTag.projection();

        @NotNull UrlReqComaModelProjection modelProjection = tagProjectionPsi.getReqComaModelProjection();

        final ReqModelProjection<?, ?, ?> parsedModelProjection = parseComaModelProjection(
            ReqModelProjection.class,
            opTagProjection,
            tagProjectionPsi.getPlus() != null,
            parseReqParams(
                tagProjectionPsi.getReqParamList(),
                opTagProjection.params(),
                subResolver,
                tagProjectionPsi,
                context
            ),
            parseAnnotations(tagProjectionPsi.getReqAnnotationList(), context),
            parseModelMetaProjection(opTagProjection, tagProjectionPsi.getReqModelMeta(), subResolver, context),
            modelProjection, subResolver, context
        );

        tagProjections.put(
            tag.name(),
            new ReqTagProjectionEntry(
                tag,
                parsedModelProjection,
                EpigraphPsiUtil.getLocation(tagProjectionPsi)
            )
        );
      } catch (PsiProcessingException e) { context.addException(e); }
    }

    return tagProjections;
  }

  private static List<ReqEntityProjection> parseTails(
      @NotNull DataTypeApi dataType,
      boolean flagged,
      @NotNull OpEntityProjection op,
      @Nullable UrlReqEntityPolymorphicTail psi,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    final List<ReqEntityProjection> tails;

    @NotNull TypesResolver subResolver = addTypeNamespace(dataType.type(), typesResolver);

    if (psi == null) tails = null;
    else {
      tails = new ArrayList<>();

      @Nullable UrlReqEntitySingleTail singleTail = psi.getReqEntitySingleTail();
      if (singleTail == null) {
        @Nullable UrlReqEntityMultiTail multiTail = psi.getReqEntityMultiTail();
        assert multiTail != null;
        TypeApi prevTailType = null;

        for (UrlReqEntityMultiTailItem tailItem : multiTail.getReqEntityMultiTailItemList()) {
          try {
            if (prevTailType != null)
              subResolver = addTypeNamespace(prevTailType, typesResolver);

            @NotNull UrlTypeRef tailTypeRef = tailItem.getTypeRef();
            @NotNull UrlReqComaEntityProjection psiTailProjection = tailItem.getReqComaEntityProjection();
            @NotNull ReqEntityProjection tailProjection =
                buildTailProjection(dataType, flagged, op, tailTypeRef, psiTailProjection, subResolver, context);
            tails.add(tailProjection);

            prevTailType = tailProjection.type();
          } catch (PsiProcessingException e) { context.addException(e); }
        }
      } else {
        @NotNull UrlTypeRef tailTypeRef = singleTail.getTypeRef();
        @NotNull UrlReqComaEntityProjection psiTailProjection = singleTail.getReqComaEntityProjection();
        @NotNull ReqEntityProjection tailProjection =
            buildTailProjection(dataType, flagged, op, tailTypeRef, psiTailProjection, subResolver, context);
        tails.add(tailProjection);
      }

    }

    return tails;
  }


  @Contract("_, null, _, _ -> null")
  private static @Nullable ReqModelProjection<?, ?, ?> parseModelMetaProjection(
      @NotNull OpModelProjection<?, ?, ?, ?> op,
      @Nullable UrlReqModelMeta modelMetaPsi,
      @NotNull TypesResolver resolver,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    if (modelMetaPsi == null) return null;

    OpModelProjection<?, ?, ?, ?> metaOp = op.metaProjection();

    if (metaOp == null) {
      context.addError(
          String.format("Meta projection not supported on type '%s'", op.type().name()),
          modelMetaPsi
      );
      return null;
    }

    // no params/annotations/meta on meta for now

    UrlReqComaModelProjection modelProjectionPsi = modelMetaPsi.getReqComaModelProjection();
    if (modelProjectionPsi == null) {
      context.addError("Incomplete meta projection", modelMetaPsi);
      return null;
    }

    return parseComaModelProjection(
        ReqModelProjection.class,
        metaOp,
        modelMetaPsi.getPlus() != null,
        ReqParams.EMPTY,
        Directives.EMPTY,
        null,
        modelProjectionPsi,
        addTypeNamespace(metaOp.type(), resolver),
        context
    );
  }

  private static ReqEntityProjection buildTailProjection(
      @NotNull DataTypeApi dataType,
      boolean flagged,
      @NotNull OpEntityProjection op,
      @NotNull UrlTypeRef tailTypeRefPsi,
      @NotNull UrlReqComaEntityProjection tailProjectionPsi,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    @NotNull TypeRef tailTypeRef = TypeRefs.fromPsi(tailTypeRefPsi, context);
    @NotNull EntityTypeApi tailType = getEntityType(tailTypeRef, typesResolver, tailTypeRefPsi, context);

    checkEntityTailType(tailType, dataType, tailTypeRefPsi, context);
    @NotNull OpEntityProjection opTail = ProjectionsParsingUtil.getTail(op, tailType, tailTypeRefPsi, context);

    return parseComaEntityProjection(
        tailType.dataType(dataType.retroTag()),
        flagged, // todo allow flags on tails
        opTail,
        tailProjectionPsi,
        typesResolver,
        context
    ).projection();
  }

  /**
   * Creates default var projection with explicitly specified list of tags
   */
  private static ReqEntityProjection createDefaultEntityProjection(
      @NotNull TypeApi type,
      @NotNull Iterable<TagApi> tags,
      @NotNull OpEntityProjection op,
      boolean required,
      @NotNull TextLocation location,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    LinkedHashMap<String, ReqTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    for (TagApi tag : tags) {
      final OpTagProjectionEntry opOutputTagProjection = op.tagProjections().get(tag.name());
      if (opOutputTagProjection != null) {
        tagProjections.put(
            tag.name(),
            new ReqTagProjectionEntry(
                tag,
                createDefaultModelProjection(
                    tag.type(),
                    required,
                    opOutputTagProjection.projection(),
                    ReqParams.EMPTY,
                    Directives.EMPTY,
                    location,
                    context
                ),
                location
            )
        );
      }
    }

    return new ReqEntityProjection(
        type,
        false,
        tagProjections,
        op.parenthesized() || tagProjections.size() != 1, null,
        location
    );
  }

  public static @NotNull ReqEntityProjection createDefaultEntityProjection(
      @NotNull DataTypeApi type,
      @NotNull OpEntityProjection op,
      boolean required,
      @NotNull TextLocation location,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    @Nullable TagApi defaultTag = ProjectionsParsingUtil.findTag(type, null, op, location, context);
    Iterable<TagApi> tags = defaultTag == null ?
                            Collections.emptyList() :
                            Collections.singletonList(defaultTag);
    return createDefaultEntityProjection(type.type(), tags, op, required, location, context);
  }

  @SuppressWarnings("unchecked")
  private static @NotNull StepsAndProjection<? extends ReqModelProjection<?, ?, ?>> parseTrunkModelProjection(
      @NotNull OpModelProjection<?, ?, ?, ?> op,
      boolean required,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @Nullable ReqModelProjection<?, ?, ?> metaProjection,
      @NotNull UrlReqTrunkModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    return (StepsAndProjection<? extends ReqModelProjection<?, ?, ?>>) parseTrunkModelProjection(
        ReqModelProjection.class,
        op,
        required,
        params,
        directives,
        metaProjection,
        psi,
        typesResolver,
        context
    );
  }

  @SuppressWarnings("unchecked")
  private static <MP extends ReqModelProjection<?, ?, ?>>
  @NotNull StepsAndProjection<? extends MP> parseTrunkModelProjection(
      @NotNull Class<MP> modelClass,
      @NotNull OpModelProjection<?, ?, ?, ?> op,
      boolean required,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @Nullable ReqModelProjection<?, ?, ?> metaProjection,
      @NotNull UrlReqTrunkModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    final @NotNull TypesResolver subResolver = addTypeNamespace(op.type(), typesResolver);

    //noinspection SwitchStatementWithoutDefaultBranch,EnumSwitchStatementWhichMissesCases
    switch (op.type().kind()) {
      case RECORD:
        assert modelClass.isAssignableFrom(ReqRecordModelProjection.class);
        @Nullable
        UrlReqTrunkRecordModelProjection trunkRecordProjectionPsi = psi.getReqTrunkRecordModelProjection();

        if (trunkRecordProjectionPsi == null) {
          checkModelPsi(psi, TypeKind.RECORD, context);
          break;
        } else {
          return (StepsAndProjection<? extends MP>) parseTrunkRecordModelProjection(
              (OpRecordModelProjection) op,
              required,
              params,
              directives,
              metaProjection,
              parseModelTails(
                  ReqRecordModelProjection.class,
                  op,
                  psi.getReqModelPolymorphicTail(),
                  subResolver,
                  context
              ),
              trunkRecordProjectionPsi,
              subResolver,
              context
          );
        }

      case MAP:
        assert modelClass.isAssignableFrom(ReqMapModelProjection.class);
        @Nullable
        UrlReqTrunkMapModelProjection trunkMapProjectionPsi = psi.getReqTrunkMapModelProjection();

        if (trunkMapProjectionPsi == null) {
          checkModelPsi(psi, TypeKind.MAP, context);
          break;
        } else {
          return (StepsAndProjection<? extends MP>) parseTrunkMapModelProjection(
              (OpMapModelProjection) op,
              required,
              params,
              directives,
              metaProjection,
              parseModelTails(
                  ReqMapModelProjection.class,
                  op,
                  psi.getReqModelPolymorphicTail(),
                  subResolver,
                  context
              ),
              trunkMapProjectionPsi,
              subResolver,
              context
          );
        }
    }

    // end of path
    return
        new StepsAndProjection<>(
            0,
            parseComaModelProjection(
                modelClass,
                op,
                required,
                params,
                directives,
                metaProjection,
                psi,
                subResolver,
                context
            )
        );

  }

  private static void checkModelPsi(
      @NotNull UrlReqTrunkModelProjection psi,
      @NotNull TypeKind expectedKind,
      @NotNull ReqPsiProcessingContext context) {

    TypeKind actualKind = null;

    if (psi.getReqTrunkRecordModelProjection() != null) actualKind = TypeKind.RECORD;
    else if (psi.getReqTrunkMapModelProjection() != null) actualKind = TypeKind.MAP;

    if (actualKind != null && actualKind != expectedKind)
      context.addError(
          String.format(
              "Expected '%s', got '%s' model kind",
              expectedKind,
              actualKind
          ), psi
      );
  }

  @SuppressWarnings("unchecked")
  private static <MP extends ReqModelProjection<?, ?, ?>> @NotNull MP parseComaModelProjection(
      @NotNull Class<MP> modelClass,
      @NotNull OpModelProjection<?, ?, ?, ?> op,
      boolean required,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @Nullable ReqModelProjection<?, ?, ?> metaProjection,
      @NotNull UrlReqComaModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    DatumTypeApi model = op.type();
    final @NotNull TypesResolver subResolver = addTypeNamespace(model, typesResolver);

    switch (model.kind()) {
      case RECORD:
        assert modelClass.isAssignableFrom(ReqRecordModelProjection.class);
        ensureModelKind(findProjectionKind(psi), TypeKind.RECORD, psi, context);

        final OpRecordModelProjection opRecord = (OpRecordModelProjection) op;

        @Nullable UrlReqComaRecordModelProjection recordModelProjectionPsi =
            psi.getReqComaRecordModelProjection();

        if (recordModelProjectionPsi == null) {
          checkModelPsi(psi, TypeKind.RECORD, context);
          return (MP) createDefaultModelProjection(
              model,
              required,
              opRecord,
              params,
              directives,
              EpigraphPsiUtil.getLocation(psi),
              context
          );
        }

        return (MP) parseComaRecordModelProjection(
            opRecord,
            required,
            params,
            directives,
            metaProjection,
            parseModelTails(
                ReqRecordModelProjection.class,
                op,
                psi.getReqModelPolymorphicTail(),
                subResolver,
                context
            ),
            recordModelProjectionPsi,
            subResolver,
            context
        );

      case MAP:
        assert modelClass.isAssignableFrom(ReqMapModelProjection.class);
        ensureModelKind(findProjectionKind(psi), TypeKind.MAP, psi, context);

        final OpMapModelProjection opMap = (OpMapModelProjection) op;
        @Nullable UrlReqComaMapModelProjection mapModelProjectionPsi = psi.getReqComaMapModelProjection();

        if (mapModelProjectionPsi == null) {
          checkModelPsi(psi, TypeKind.MAP, context);
          return (MP) createDefaultModelProjection(
              model,
              required,
              opMap,
              params,
              directives,
              EpigraphPsiUtil.getLocation(psi),
              context
          );
        }

        return (MP) parseComaMapModelProjection(
            opMap,
            required,
            params,
            directives,
            metaProjection,
            parseModelTails(
                ReqMapModelProjection.class,
                op,
                psi.getReqModelPolymorphicTail(),
                subResolver,
                context
            ),
            mapModelProjectionPsi,
            subResolver,
            context
        );

      case LIST:
        assert modelClass.isAssignableFrom(ReqListModelProjection.class);
        ensureModelKind(findProjectionKind(psi), TypeKind.LIST, psi, context);

        final OpListModelProjection opList = (OpListModelProjection) op;
        @Nullable UrlReqComaListModelProjection listModelProjectionPsi =
            psi.getReqComaListModelProjection();

        if (listModelProjectionPsi == null) {
          checkModelPsi(psi, TypeKind.LIST, context);
          return (MP) createDefaultModelProjection(
              model,
              required,
              opList,
              params,
              directives,
              EpigraphPsiUtil.getLocation(psi),
              context
          );
        }

        return (MP) parseListModelProjection(
            opList,
            required,
            params,
            directives,
            metaProjection,
            parseModelTails(
                ReqListModelProjection.class,
                op,
                psi.getReqModelPolymorphicTail(),
                subResolver,
                context
            ),
            listModelProjectionPsi,
            subResolver,
            context
        );

      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + model.kind(), psi, context);

      case PRIMITIVE:
        assert modelClass.isAssignableFrom(ReqPrimitiveModelProjection.class);
        return (MP) parsePrimitiveModelProjection(
            (PrimitiveTypeApi) model,
            required,
            params,
            directives,
            metaProjection,
            parseModelTails(
                ReqPrimitiveModelProjection.class,
                op,
                psi.getReqModelPolymorphicTail(),
                subResolver,
                context
            ),
            psi
        );

      case ENTITY:
        throw new PsiProcessingException("Unsupported type kind: " + model.kind(), psi, context);

      default:
        throw new PsiProcessingException("Unknown type kind: " + model.kind(), psi, context);
    }
  }

  private static void checkModelPsi(
      @NotNull UrlReqComaModelProjection psi,
      @NotNull TypeKind expectedKind,
      @NotNull ReqPsiProcessingContext context) {

    TypeKind actualKind = null;

    if (psi.getReqComaRecordModelProjection() != null) actualKind = TypeKind.RECORD;
    else if (psi.getReqComaMapModelProjection() != null) actualKind = TypeKind.MAP;
    else if (psi.getReqComaListModelProjection() != null) actualKind = TypeKind.LIST;

    if (actualKind != null && actualKind != expectedKind)
      context.addError(
          String.format(
              "Expected '%s', got '%s' model kind",
              expectedKind,
              actualKind
          ), psi
      );
  }

  @Contract("_, _, null, _, _ -> null")
  private static <MP extends ReqModelProjection<?, ?, ?>>
  @Nullable List<MP> parseModelTails(
      @NotNull Class<MP> modelClass,
      @NotNull OpModelProjection<?, ?, ?, ?> op,
      @Nullable UrlReqModelPolymorphicTail tailPsi,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    if (tailPsi == null) return null;
    else {
      List<MP> tails = new ArrayList<>();

      final UrlReqModelSingleTail singleTailPsi = tailPsi.getReqModelSingleTail();
      if (singleTailPsi == null) {
        final UrlReqModelMultiTail multiTailPsi = tailPsi.getReqModelMultiTail();
        assert multiTailPsi != null;
        for (UrlReqModelMultiTailItem tailItemPsi : multiTailPsi.getReqModelMultiTailItemList()) {
          try {
            tails.add(
                buildModelTailProjection(
                    modelClass,
                    op,
                    tailItemPsi.getPlus() != null,
                    tailItemPsi.getTypeRef(),
                    tailItemPsi.getReqComaModelProjection(),
                    tailItemPsi,
                    tailItemPsi.getReqParamList(),
                    tailItemPsi.getReqAnnotationList(),
                    tailItemPsi.getReqModelMeta(),
                    typesResolver,
                    context
                )
            );
          } catch (PsiProcessingException e) { context.addException(e); }
        }
      } else {
        tails.add(
            buildModelTailProjection(
                modelClass,
                op,
                singleTailPsi.getPlus() != null,
                singleTailPsi.getTypeRef(),
                singleTailPsi.getReqComaModelProjection(),
                singleTailPsi,
                singleTailPsi.getReqParamList(),
                singleTailPsi.getReqAnnotationList(),
                singleTailPsi.getReqModelMeta(),
                typesResolver,
                context
            )
        );
      }
      return tails;
    }
  }

  private static <MP extends ReqModelProjection<?, ?, ?>>
  @NotNull MP buildModelTailProjection(
      @NotNull Class<MP> modelClass,
      @NotNull OpModelProjection<?, ?, ?, ?> op,
      boolean required,
      @NotNull UrlTypeRef tailTypeRefPsi,
      @NotNull UrlReqComaModelProjection modelProjectionPsi,
      @NotNull PsiElement paramsLocationPsi,
      @NotNull List<UrlReqParam> modelParamsList,
      @NotNull List<UrlReqAnnotation> modelAnnotationsList,
      @Nullable UrlReqModelMeta modelMetaPsi,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    @NotNull TypeRef tailTypeRef = TypeRefs.fromPsi(tailTypeRefPsi, context);
    @NotNull DatumTypeApi tailType = getDatumType(tailTypeRef, typesResolver, tailTypeRefPsi, context);

    final OpModelProjection<?, ?, ?, ?> opTail =
        ProjectionsParsingUtil.getTail(op, tailType, tailTypeRefPsi, context);

    return parseComaModelProjection(
        modelClass,
        opTail,
        required,
        parseReqParams(modelParamsList, op.params(), typesResolver, paramsLocationPsi, context),
        parseAnnotations(modelAnnotationsList, context),
        parseModelMetaProjection(
            op,
            modelMetaPsi,
            typesResolver,
            context
        ),
        modelProjectionPsi,
        typesResolver,
        context
    );
  }

  private static @Nullable TypeKind findProjectionKind(@NotNull UrlReqComaModelProjection psi) {
    if (psi.getReqComaRecordModelProjection() != null) return TypeKind.RECORD;
    if (psi.getReqComaMapModelProjection() != null) return TypeKind.MAP;
    if (psi.getReqComaListModelProjection() != null) return TypeKind.LIST;
    return null;
  }

  private static @NotNull ReqModelProjection<?, ?, ?> createDefaultModelProjection(
      @NotNull DatumTypeApi type,
      boolean required,
      @NotNull OpModelProjection<?, ?, ?, ?> op,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @NotNull TextLocation location,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        OpRecordModelProjection opRecord = (OpRecordModelProjection) op;
        final Map<String, OpFieldProjectionEntry> opFields = opRecord.fieldProjections();

        final @NotNull Map<String, ReqFieldProjectionEntry> fields;

        if (opFields.isEmpty()) {
          fields = Collections.emptyMap();
        } else {
          fields = new LinkedHashMap<>();

//          for (Map.Entry<String, OpOutputFieldProjectionEntry> entry : opFields.entrySet()) {
//            final OpOutputFieldProjectionEntry opFieldProjectionEntry = entry.getValue();
//            @NotNull final OpOutputFieldProjection opFieldProjection = opFieldProjectionEntry.projection();

//            if (opFieldProjection.includeInDefault()) {
//              final String fieldName = entry.getKey();
//              final Field field = opFieldProjectionEntry.field();
//
//              fields.put(
//                  fieldName,
//                  new ReqOutputFieldProjectionEntry(
//                      field,
//                      new ReqOutputFieldProjection(
//                          ReqParams.EMPTY,
//                          Annotations.EMPTY,
//                          createDefaultEntityProjection(
//                              field.dataType().type,
//                              opFieldProjection.projection(),
//                              false,
//                              locationPsi,
//                              context
//                          ),
//                          false,
//                          TextLocation.UNKNOWN
//                      ),
//                      TextLocation.UNKNOWN
//                  )
//              );
//            }
//          }
        }

        return new ReqRecordModelProjection(
            (RecordTypeApi) type,
            required,
            params,
            directives,
            null,
            fields,
            null,
            location
        );
      case MAP:
        OpMapModelProjection opMap = (OpMapModelProjection) op;

        if (opMap.keyProjection().presence() == AbstractOpKeyPresence.REQUIRED)
          throw new PsiProcessingException(
              String.format("Can't build default projection for '%s': keys are required", type.name()),
              location,
              context
          );

        MapTypeApi mapType = (MapTypeApi) type;
        final ReqEntityProjection valueEntityProjection = createDefaultEntityProjection(
            mapType.valueType(),
            opMap.itemsProjection(),
            required,
            location,
            context
        );

        return new ReqMapModelProjection(
            mapType,
            required,
            params,
            directives,
            null,
            null,
            false,
            valueEntityProjection,
            null,
            location
        );
      case LIST:
        OpListModelProjection opList = (OpListModelProjection) op;
        ListTypeApi listType = (ListTypeApi) type;

        final ReqEntityProjection itemEntityProjection = createDefaultEntityProjection(
            listType.elementType(),
            opList.itemsProjection(),
            required,
            location,
            context
        );

        return new ReqListModelProjection(
            listType,
            required,
            params,
            directives,
            null,
            itemEntityProjection,
            null,
            location
        );
      case ENTITY:
        throw new PsiProcessingException(
            "Was expecting to get datum model kind, got: " + type.kind(),
            location,
            context
        );
      case ENUM:
        // todo
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), location, context);
      case PRIMITIVE:
        return new ReqPrimitiveModelProjection(
            (PrimitiveTypeApi) type,
            required,
            params,
            directives,
            null,
            null,
            location
        );
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), location, context);
    }
  }

  private static @NotNull StepsAndProjection<ReqRecordModelProjection> parseTrunkRecordModelProjection(
      @NotNull OpRecordModelProjection op,
      boolean required,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @Nullable ReqModelProjection<?, ?, ?> metaProjection,
      @Nullable List<ReqRecordModelProjection> tails,
      @NotNull UrlReqTrunkRecordModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    Map<String, OpFieldProjectionEntry> opFields = op.fieldProjections();
    final String fieldName = psi.getQid().getCanonicalName();

    if (opFields.isEmpty())
      throw new PsiProcessingException("No fields are supported by the operation", psi.getQid(), context);

    OpFieldProjectionEntry opFieldProjectionEntry = opFields.get(fieldName);
    if (opFieldProjectionEntry == null) {
      throw new PsiProcessingException(
          String.format(
              "Field '%s' is not supported, supported fields: (%s)",
              fieldName,
              ProjectionUtils.listFields(opFields.keySet())
          ),
          psi.getQid(),
          context
      );
    }

    final @NotNull FieldApi field = opFieldProjectionEntry.field();
    final @NotNull OpFieldProjection opFieldProjection = opFieldProjectionEntry.fieldProjection();

    @NotNull DataTypeApi fieldType = field.dataType();

    @Nullable UrlReqTrunkFieldProjection fieldProjectionPsi = psi.getReqTrunkFieldProjection();
    boolean fieldFlagged = psi.getPlus() != null;

    @Nullable LinkedHashMap<String, ReqFieldProjectionEntry> fieldProjections = new LinkedHashMap<>();
    final int steps;

    final @NotNull TextLocation fieldLocation = EpigraphPsiUtil.getLocation(psi);

    if (fieldProjectionPsi == null) {
      @Nullable TagApi defaultFieldTag = fieldType.retroTag();
      if (defaultFieldTag == null)
        throw new PsiProcessingException(String.format(
            "Can't construct default projection for field '%s', as it's type '%s' has no default tag",
            fieldName,
            fieldType.name()
        ), psi, context);

      @NotNull ReqEntityProjection varProjection = createDefaultEntityProjection(
          fieldType,
          opFieldProjection.entityProjection(),
          fieldFlagged,
          EpigraphPsiUtil.getLocation(psi),
          context
      );

      fieldProjections.put(
          fieldName,
          new ReqFieldProjectionEntry(
              field,
              new ReqFieldProjection(
//                  ReqParams.EMPTY,
//                  Annotations.EMPTY,
                  varProjection,
//                  fieldRequired,
                  fieldLocation
              ),
              fieldLocation
          )
      );

      // first step = our field, second step = default var. default var projection is a trunk projection,
      // default model projection is a coma projection
      steps = 2;
    } else {
      @NotNull StepsAndProjection<ReqFieldProjection> fieldStepsAndProjection =
          parseTrunkFieldProjection(
              fieldType,
              fieldFlagged,
              opFieldProjection,
              fieldProjectionPsi,
              resolver,
              context
          );

      fieldProjections.put(
          fieldName,
          new ReqFieldProjectionEntry(
              field,
              fieldStepsAndProjection.projection(),
              EpigraphPsiUtil.getLocation(fieldProjectionPsi)
          )
      );

      steps = fieldStepsAndProjection.pathSteps();
    }

    return new StepsAndProjection<>(
        steps,
        new ReqRecordModelProjection(
            op.type(),
            required,
            params,
            directives,
            metaProjection,
            fieldProjections,
            tails,
            fieldLocation
        )
    );
  }

  public static @NotNull StepsAndProjection<ReqFieldProjection> parseTrunkFieldProjection(
      @NotNull DataTypeApi fieldType,
      boolean flagged,
      @NotNull OpFieldProjection op,
      @NotNull UrlReqTrunkFieldProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    return parseTrunkFieldProjection(
        fieldType,
        flagged,  /*op.params(), */
        op.entityProjection(),
        psi,
        resolver,
        context
    );
  }

  private static StepsAndProjection<ReqFieldProjection> parseTrunkFieldProjection(
      @NotNull DataTypeApi fieldType,
      boolean flagged,
      //      @Nullable OpParams opParams,
      @NotNull OpEntityProjection opEntityProjection,
      @NotNull UrlReqTrunkFieldProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqPsiProcessingContext context
  ) throws PsiProcessingException {

    @Nullable UrlReqTrunkEntityProjection psiEntityProjection = psi.getReqTrunkEntityProjection();
    StepsAndProjection<ReqEntityProjection> stepsAndProjection =
        parseTrunkEntityProjection(fieldType, flagged, opEntityProjection, psiEntityProjection, resolver, context);

    final ReqEntityProjection varProjection = stepsAndProjection.projection();
    final int steps = stepsAndProjection.pathSteps() + 1;

    ProjectionsParsingUtil.verifyData(fieldType, varProjection, psi, context);

    return new StepsAndProjection<>(
        steps,
        new ReqFieldProjection(
//            parseReqParams(psi.getReqParamList(), opParams, resolver, context),
//            parseAnnotations(psi.getReqAnnotationList(), context),
            varProjection,
//            flagged,
            EpigraphPsiUtil.getLocation(psi)
        )
    );
  }

  private static @NotNull ReqRecordModelProjection parseComaRecordModelProjection(
      @NotNull OpRecordModelProjection op,
      boolean required,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @Nullable ReqModelProjection<?, ?, ?> metaProjection,
      @Nullable List<ReqRecordModelProjection> tails,
      @NotNull UrlReqComaRecordModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    LinkedHashMap<String, ReqFieldProjectionEntry> fieldProjections = new LinkedHashMap<>();
    @NotNull Iterable<UrlReqComaFieldProjection> psiFieldProjections = psi.getReqComaFieldProjectionList();

    Map<String, OpFieldProjectionEntry> opFields = op.fieldProjections();

    if (psi.getReqAll() == null) {
      for (UrlReqComaFieldProjection fieldProjectionPsi : psiFieldProjections) {
        final String fieldName = fieldProjectionPsi.getQid().getCanonicalName();

        @Nullable OpFieldProjectionEntry opFieldProjectionEntry = opFields.get(fieldName);

        if (opFieldProjectionEntry == null)
          context.addError(
              String.format(
                  "Field '%s' is not supported, supported fields: (%s)",
                  fieldName,
                  ProjectionUtils.listFields(opFields.keySet())
              ),
              fieldProjectionPsi
          );
        else {
          try {
            final @NotNull FieldApi field = opFieldProjectionEntry.field();
            final @NotNull OpFieldProjection opFieldProjection = opFieldProjectionEntry.fieldProjection();
            final boolean fieldFlagged = fieldProjectionPsi.getPlus() != null;

//            ReqParams fieldParams =
//                parseReqParams(fieldProjectionPsi.getReqParamList(), opFieldProjection.params(), resolver, context);
//
//            Annotations fieldAnnotations = parseAnnotations(fieldProjectionPsi.getReqAnnotationList(), context);

            @Nullable UrlReqComaEntityProjection psiEntityProjection =
                fieldProjectionPsi.getReqComaEntityProjection();
            @NotNull ReqEntityProjection varProjection =
                parseComaEntityProjection(
                    field.dataType(),
                    fieldFlagged,
                    opFieldProjection.entityProjection(),
                    psiEntityProjection,
                    resolver,
                    context
                ).projection();

            final @NotNull TextLocation fieldLocation = EpigraphPsiUtil.getLocation(fieldProjectionPsi);

            fieldProjections.put(
                fieldName,
                new ReqFieldProjectionEntry(
                    field,
                    new ReqFieldProjection(
//                        fieldParams,
//                        fieldAnnotations,
                        varProjection,
//                        fieldFlagged,
                        fieldLocation
                    ),
                    fieldLocation
                )
            );
          } catch (PsiProcessingException e) { context.addException(e); }
        }
      }
    } else {
      TextLocation location = EpigraphPsiUtil.getLocation(psi.getReqAll());

      for (final Map.Entry<String, OpFieldProjectionEntry> entry : opFields.entrySet()) {
        final FieldApi field = entry.getValue().field();
        final @NotNull OpFieldProjection opFieldProjection = entry.getValue().fieldProjection();

        fieldProjections.put(
            entry.getKey(),
            new ReqFieldProjectionEntry(
                field,
                new ReqFieldProjection(
//                    ReqParams.EMPTY,
//                    Annotations.EMPTY,
                    createDefaultEntityProjection(
                        field.dataType(),
                        opFieldProjection.entityProjection(),
                        false,
                        EpigraphPsiUtil.getLocation(psi.getReqAll()),
                        context
                    ),
//                    false,
                    location
                ),
                location
            )
        );
      }

    }

    return new ReqRecordModelProjection(
        op.type(),
        required,
        params,
        directives,
        metaProjection,
        fieldProjections,
        tails,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  private static @NotNull StepsAndProjection<ReqMapModelProjection> parseTrunkMapModelProjection(
      @NotNull OpMapModelProjection op,
      boolean required,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @Nullable ReqModelProjection<?, ?, ?> metaProjection,
      @Nullable List<ReqMapModelProjection> tails,
      @NotNull UrlReqTrunkMapModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    if (op.keyProjection().presence() == AbstractOpKeyPresence.FORBIDDEN)
      throw new PsiProcessingException("Map keys are forbidden", psi.getDatum(), context);

    @NotNull UrlDatum valuePsi = psi.getDatum();
    @Nullable Datum keyValue =
        getDatum(valuePsi, op.type().keyType(), resolver, "Error processing map key: ", context);
    if (keyValue == null) throw new PsiProcessingException("Null keys are not allowed", valuePsi, context);

    ReqKeyProjection keyProjection = new ReqKeyProjection(
        keyValue,
        parseReqParams(psi.getReqParamList(), op.keyProjection().params(), resolver, psi, context),
        parseAnnotations(psi.getReqAnnotationList(), context),
        EpigraphPsiUtil.getLocation(psi)
    );

    final int steps;
    final ReqEntityProjection valueProjection;

    @Nullable UrlReqTrunkEntityProjection valueProjectionPsi = psi.getReqTrunkEntityProjection();
    StepsAndProjection<ReqEntityProjection> stepsAndProjection =
        parseTrunkEntityProjection(
            op.type().valueType(),
            psi.getPlus() != null,
            op.itemsProjection(),
            valueProjectionPsi,
            resolver,
            context
        );

    valueProjection = stepsAndProjection.projection();
    steps = stepsAndProjection.pathSteps() + 1;

    return new StepsAndProjection<>(
        steps,
        new ReqMapModelProjection(
            op.type(),
            required,
            params,
            directives,
            metaProjection,
            Collections.singletonList(keyProjection),
            psi.getPlus() != null,
            valueProjection,
            tails,
            EpigraphPsiUtil.getLocation(psi)
        )
    );
  }

  private static @NotNull ReqMapModelProjection parseComaMapModelProjection(
      @NotNull OpMapModelProjection op,
      boolean flagged,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @Nullable ReqModelProjection<?, ?, ?> metaProjection,
      @Nullable List<ReqMapModelProjection> tails,
      @NotNull UrlReqComaMapModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    @NotNull UrlReqComaKeysProjection keysProjectionPsi = psi.getReqComaKeysProjection();

    final @NotNull OpKeyProjection opKeyProjection = op.keyProjection();
    final List<ReqKeyProjection> keyProjections;

    if (keysProjectionPsi.getStar() == null) {
      final Collection<UrlReqComaKeyProjection> keyProjectionsPsi =
          keysProjectionPsi.getReqComaKeyProjectionList();

      final int keysSize = keyProjectionsPsi.size();

      if (opKeyProjection.presence() == AbstractOpKeyPresence.FORBIDDEN) {
        if (keysSize > 0) context.addError("Map keys are forbidden", keysProjectionPsi);
        keyProjections = null;
      } else {
        keyProjections = new ArrayList<>(keysSize);
        for (UrlReqComaKeyProjection keyProjectionPsi : keyProjectionsPsi) {

          try {
            @NotNull UrlDatum valuePsi = keyProjectionPsi.getDatum();
            @Nullable Datum keyValue =
                getDatum(valuePsi, op.type().keyType(), resolver, "Error processing map key: ", context);

            if (keyValue == null) context.addError("Null keys are not allowed", valuePsi);
            else {
              keyProjections.add(
                  new ReqKeyProjection(
                      keyValue,
                      parseReqParams(
                          keyProjectionPsi.getReqParamList(),
                          opKeyProjection.params(),
                          resolver,
                          keyProjectionPsi,
                          context
                      ),
                      parseAnnotations(keyProjectionPsi.getReqAnnotationList(), context),
                      EpigraphPsiUtil.getLocation(keyProjectionPsi)
                  )
              );
            }

          } catch (PsiProcessingException e) { context.addException(e); }
        }
      }
    } else {
      if (opKeyProjection.presence() == AbstractOpKeyPresence.REQUIRED)
        context.addError("Map keys are required", keysProjectionPsi.getStar());

      keyProjections = null;
    }

    @Nullable UrlReqComaEntityProjection valueProjectionPsi = psi.getReqComaEntityProjection();
    final @NotNull ReqEntityProjection valueProjection;
    if (valueProjectionPsi == null) {
      valueProjection = createDefaultEntityProjection(
          op.type().valueType(),
          op.itemsProjection(),
          psi.getPlus() != null,
          EpigraphPsiUtil.getLocation(psi),
          context
      );
    } else {
      valueProjection = parseComaEntityProjection(
          op.type().valueType(),
          psi.getPlus() != null,
          op.itemsProjection(),
          valueProjectionPsi,
          resolver,
          context
      ).projection();
    }


    return new ReqMapModelProjection(
        op.type(),
        flagged,
        params,
        directives,
        metaProjection,
        keyProjections,
        psi.getPlus() != null,
        valueProjection,
        tails,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  private static @NotNull ReqListModelProjection parseListModelProjection(
      @NotNull OpListModelProjection op,
      boolean flagged,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @Nullable ReqModelProjection<?, ?, ?> metaProjection,
      @Nullable List<ReqListModelProjection> tails,
      @NotNull UrlReqComaListModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException {

    ReqEntityProjection itemsProjection;
    @Nullable UrlReqComaEntityProjection reqOutputEntityProjectionPsi = psi.getReqComaEntityProjection();
    if (reqOutputEntityProjectionPsi == null)
      itemsProjection =
          createDefaultEntityProjection(
              op.type().elementType(),
              op.itemsProjection(),
              true,
              EpigraphPsiUtil.getLocation(psi),
              context
          );
    else
      itemsProjection =
          parseComaEntityProjection(
              op.type().elementType(),
              psi.getPlus() != null,
              op.itemsProjection(),
              reqOutputEntityProjectionPsi,
              resolver,
              context
          ).projection();


    return new ReqListModelProjection(
        op.type(),
        flagged,
        params,
        directives,
        metaProjection,
        itemsProjection,
        tails,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  private static @NotNull ReqPrimitiveModelProjection parsePrimitiveModelProjection(
      @NotNull PrimitiveTypeApi type,
      boolean required,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @Nullable ReqModelProjection<?, ?, ?> metaProjection,
      @Nullable List<ReqPrimitiveModelProjection> tails,
      @NotNull PsiElement locationPsi) {

    return new ReqPrimitiveModelProjection(
        type,
        required,
        params,
        directives,
        metaProjection,
        tails,
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

}

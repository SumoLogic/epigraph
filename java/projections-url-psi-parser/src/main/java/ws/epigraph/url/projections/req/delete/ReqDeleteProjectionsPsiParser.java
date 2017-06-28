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

package ws.epigraph.url.projections.req.delete;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Datum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.req.Directives;
import ws.epigraph.projections.ProjectionsParsingUtil;
import ws.epigraph.projections.op.delete.*;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.projections.req.delete.*;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypeRef;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.*;
import ws.epigraph.url.TypeRefs;
import ws.epigraph.url.parser.psi.*;
import ws.epigraph.url.projections.UrlProjectionsPsiParserUtil;

import java.util.*;

import static ws.epigraph.projections.ProjectionsParsingUtil.*;
import static ws.epigraph.url.projections.UrlProjectionsPsiParserUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class ReqDeleteProjectionsPsiParser {

  private ReqDeleteProjectionsPsiParser() {}

  public static @NotNull ReqDeleteVarProjection parseVarProjection(
      @NotNull DataTypeApi dataType,
      @NotNull OpDeleteVarProjection op,
      @NotNull UrlReqDeleteVarProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqDeletePsiProcessingContext context) throws PsiProcessingException {

    final UrlReqDeleteNamedVarProjection namedVarProjection = psi.getReqDeleteNamedVarProjection();
    if (namedVarProjection == null) {
      final UrlReqDeleteUnnamedOrRefVarProjection unnamedOrRefVarProjection =
          psi.getReqDeleteUnnamedOrRefVarProjection();

      if (unnamedOrRefVarProjection == null)
        throw new PsiProcessingException(
            "Incomplete var projection definition",
            psi,
            context.messages()
        );

      return parseUnnamedOrRefVarProjection(
          dataType,
          op,
          unnamedOrRefVarProjection,
          resolver,
          context
      );
    } else {
      // named var projection
      final String projectionName = namedVarProjection.getQid().getCanonicalName();

      final @Nullable UrlReqDeleteUnnamedOrRefVarProjection unnamedOrRefVarProjection =
          namedVarProjection.getReqDeleteUnnamedOrRefVarProjection();

      if (unnamedOrRefVarProjection == null)
        throw new PsiProcessingException(
            String.format("Incomplete var projection '%s' definition", projectionName),
            psi,
            context.messages()
        );

      final ReqDeleteVarProjection reference = context.varReferenceContext()
          .varReference(dataType.type(), projectionName, false, EpigraphPsiUtil.getLocation(psi));

      final ReqDeleteVarProjection value = parseUnnamedOrRefVarProjection(
          dataType,
          op,
          unnamedOrRefVarProjection,
          resolver,
          context
      );

      context.varReferenceContext()
          .resolveEntityRef(projectionName, value, EpigraphPsiUtil.getLocation(unnamedOrRefVarProjection));

      final Queue<OpDeleteVarProjection> unverifiedOps = context.unverifiedRefOps(projectionName);
      while (unverifiedOps != null && !unverifiedOps.isEmpty()) {
        final OpDeleteVarProjection unverifiedOp = unverifiedOps.poll();
        context.addVerifiedRefOp(projectionName, unverifiedOp);

        parseUnnamedOrRefVarProjection(
            dataType,
            unverifiedOp,
            unnamedOrRefVarProjection,
            resolver,
            context
        );
      }

      return reference;
    }

  }

  public static @NotNull ReqDeleteVarProjection parseUnnamedOrRefVarProjection(
      final @NotNull DataTypeApi dataType,
      final @NotNull OpDeleteVarProjection op,
      final @NotNull UrlReqDeleteUnnamedOrRefVarProjection psi,
      final @NotNull TypesResolver resolver,
      final @NotNull ReqDeletePsiProcessingContext context) throws PsiProcessingException {

    final UrlReqDeleteVarProjectionRef varProjectionRef = psi.getReqDeleteVarProjectionRef();
    if (varProjectionRef == null) {
      // usual var projection
      final UrlReqDeleteUnnamedVarProjection unnamedVarProjection = psi.getReqDeleteUnnamedVarProjection();
      if (unnamedVarProjection == null)
        throw new PsiProcessingException("Incomplete var projection definition", psi, context.messages());
      else {
        return parseUnnamedVarProjection(
            dataType,
            op,
            unnamedVarProjection,
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

      final Collection<OpDeleteVarProjection> verifiedOps = context.verifiedRefOps(referenceName);
      if (verifiedOps == null || !verifiedOps.contains(op))
        context.addUnverifiedRefOp(referenceName, op);

      return context.varReferenceContext()
          .varReference(dataType.type(), referenceName, true, EpigraphPsiUtil.getLocation(psi));
    }

  }

  public static @NotNull ReqDeleteVarProjection parseUnnamedVarProjection(
      @NotNull DataTypeApi dataType,
      @NotNull OpDeleteVarProjection op,
      @NotNull UrlReqDeleteUnnamedVarProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqDeletePsiProcessingContext context) throws PsiProcessingException {

    final TypeApi type = dataType.type();
    final LinkedHashMap<String, ReqDeleteTagProjectionEntry> tagProjections;

    @Nullable UrlReqDeleteSingleTagProjection singleTagProjectionPsi = psi.getReqDeleteSingleTagProjection();
    final @NotNull TypesResolver subResolver = addTypeNamespace(dataType.type(), resolver);

    if (singleTagProjectionPsi == null) {
      @Nullable UrlReqDeleteMultiTagProjection multiTagProjection = psi.getReqDeleteMultiTagProjection();
      assert multiTagProjection != null;
      tagProjections = parseMultiTagProjection(dataType, op, multiTagProjection, subResolver, context);
    } else {
      // try to improve error reporting: singleTagProjectionPsi may be empty
      PsiElement tagLocation = getSingleTagLocation(singleTagProjectionPsi);

      // todo better location: containing field/key/element, once it's available from the context
      if (!op.canDelete() && isEmpty(singleTagProjectionPsi))
        context.addError(String.format("'%s' entity can't be deleted", dataType.name().toString()), tagLocation);

      tagProjections = new LinkedHashMap<>();

      final @Nullable UrlTagName tagNamePsi = singleTagProjectionPsi.getTagName();
      TagApi tag = findTagOrSelfTag(type, tagNamePsi, op, tagLocation, context);
      if (tag != null || !singleTagProjectionPsi.getText().isEmpty()) {
        if (tag == null) tag = getTagOrSelfTag(type, null, op, tagLocation, context); // will throw proper error

        @NotNull OpDeleteTagProjectionEntry opTagProjection =
            getTagProjection(tag.name(), op, tagLocation, context);

        @NotNull OpDeleteModelProjection<?, ?, ?> opModelProjection = opTagProjection.projection();
        @NotNull UrlReqDeleteModelProjection modelProjectionPsi = singleTagProjectionPsi.getReqDeleteModelProjection();

        final ReqDeleteModelProjection<?, ?, ?> parsedModelProjection = parseModelProjection(
            opModelProjection,
            parseReqParams(
                singleTagProjectionPsi.getReqParamList(),
                opModelProjection.params(),
                subResolver,
                singleTagProjectionPsi,
                context
            ),
            parseAnnotations(singleTagProjectionPsi.getReqAnnotationList(), context),
            modelProjectionPsi,
            subResolver,
            context
        );

        tagProjections.put(
            tag.name(),
            new ReqDeleteTagProjectionEntry(
                tag,
                parsedModelProjection,
                EpigraphPsiUtil.getLocation(tagLocation)
            )
        );
      }
    }

    final List<ReqDeleteVarProjection> tails =
        parseTails(dataType, op, psi.getReqDeleteVarPolymorphicTail(), subResolver, context);

    try {
      return new ReqDeleteVarProjection(
          type,
          tagProjections,
          singleTagProjectionPsi == null || tagProjections.size() != 1,
          tails,
          EpigraphPsiUtil.getLocation(psi)
      );
    } catch (RuntimeException e) {
      throw new PsiProcessingException(e, psi, context);
    }
  }

  private static boolean isEmpty(@NotNull UrlReqDeleteSingleTagProjection singleTagProjectionPsi) {
    if (singleTagProjectionPsi.getTagName() != null) return false;
    final UrlReqDeleteModelProjection modelProjectionPsi = singleTagProjectionPsi.getReqDeleteModelProjection();
    return modelProjectionPsi.getReqDeleteListModelProjection() == null &&
           modelProjectionPsi.getReqDeleteMapModelProjection() == null &&
           modelProjectionPsi.getReqDeleteModelPolymorphicTail() == null &&
           modelProjectionPsi.getReqDeleteRecordModelProjection() == null;
  }

  private static @NotNull PsiElement getSingleTagLocation(final @NotNull UrlReqDeleteSingleTagProjection singleTagProjectionPsi) {
    final UrlTagName tagName = singleTagProjectionPsi.getTagName();
    if (tagName != null) return tagName;
    PsiElement tagLocation = singleTagProjectionPsi;
    if (tagLocation.getText().isEmpty()) {
      final @Nullable UrlReqDeleteFieldProjectionEntry fieldProjectionPsi =
          PsiTreeUtil.getParentOfType(tagLocation, UrlReqDeleteFieldProjectionEntry.class);
      if (fieldProjectionPsi != null) {
        tagLocation = fieldProjectionPsi.getQid();
      }
    }
    return tagLocation;
  }

  private static @NotNull LinkedHashMap<String, ReqDeleteTagProjectionEntry> parseMultiTagProjection(
      @NotNull DataTypeApi dataType,
      @NotNull OpDeleteVarProjection op,
      @NotNull UrlReqDeleteMultiTagProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqDeletePsiProcessingContext context) throws PsiProcessingException {

    if (!(dataType.type().equals(op.type())))
      throw new PsiProcessingException(
          String.format("Inconsistent arguments. data type: '%s', op type: '%s'", dataType.name(), op.type().name()),
          psi,
          context
      );

    final @NotNull TypesResolver subResolver = addTypeNamespace(dataType.type(), typesResolver);

    final LinkedHashMap<String, ReqDeleteTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    // parse list of tags
    @NotNull Iterable<UrlReqDeleteMultiTagProjectionItem> tagProjectionPsiList =
        psi.getReqDeleteMultiTagProjectionItemList();

    for (UrlReqDeleteMultiTagProjectionItem tagProjectionPsi : tagProjectionPsiList) {
      try {
        @NotNull TagApi tag =
            UrlProjectionsPsiParserUtil.getTag(tagProjectionPsi.getTagName(), op, tagProjectionPsi, context);
        @NotNull OpDeleteTagProjectionEntry opTag = getTagProjection(tag.name(), op, tagProjectionPsi, context);

        OpDeleteModelProjection<?, ?, ?> opTagProjection = opTag.projection();

        @NotNull UrlReqDeleteModelProjection modelProjection = tagProjectionPsi.getReqDeleteModelProjection();

        final ReqDeleteModelProjection<?, ?, ?> parsedModelProjection = parseModelProjection(
            opTagProjection,
            parseReqParams(
                tagProjectionPsi.getReqParamList(),
                opTagProjection.params(),
                subResolver,
                tagProjectionPsi,
                context
            ),
            parseAnnotations(tagProjectionPsi.getReqAnnotationList(), context),
            modelProjection, subResolver, context
        );

        tagProjections.put(
            tag.name(),
            new ReqDeleteTagProjectionEntry(
                tag,
                parsedModelProjection,
                EpigraphPsiUtil.getLocation(tagProjectionPsi)
            )
        );
      } catch (PsiProcessingException e) {
        context.addException(e);
      }
    }

    return tagProjections;
  }

  private static @NotNull ReqDeleteVarProjection getDefaultVarProjection(
      final @NotNull TypeApi type,
      final @NotNull PsiElement psi) {
    return new ReqDeleteVarProjection(
        type,
        Collections.emptyMap(),
        true,
        null,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  private static @Nullable List<ReqDeleteVarProjection> parseTails(
      @NotNull DataTypeApi dataType,
      @NotNull OpDeleteVarProjection op,
      @Nullable UrlReqDeleteVarPolymorphicTail tailPsi,
      @NotNull TypesResolver resolver,
      @NotNull ReqDeletePsiProcessingContext context) throws PsiProcessingException {

    final List<ReqDeleteVarProjection> tails;

    @NotNull TypesResolver subResolver = addTypeNamespace(dataType.type(), resolver);

    if (tailPsi == null) tails = null;
    else {
      tails = new ArrayList<>();

      @Nullable UrlReqDeleteVarSingleTail singleTail = tailPsi.getReqDeleteVarSingleTail();
      if (singleTail == null) {
        @Nullable UrlReqDeleteVarMultiTail multiTail = tailPsi.getReqDeleteVarMultiTail();
        assert multiTail != null;
        TypeApi prevTailType = null;

        for (UrlReqDeleteVarMultiTailItem tailItem : multiTail.getReqDeleteVarMultiTailItemList()) {
          try {
            if (prevTailType != null)
              subResolver = addTypeNamespace(prevTailType, resolver);

            @NotNull UrlTypeRef tailTypeRef = tailItem.getTypeRef();
            @NotNull UrlReqDeleteVarProjection psiTailProjection = tailItem.getReqDeleteVarProjection();
            @NotNull ReqDeleteVarProjection tailProjection =
                buildTailProjection(dataType, op, tailTypeRef, psiTailProjection, subResolver, context);
            tails.add(tailProjection);

            prevTailType = tailProjection.type();
          } catch (PsiProcessingException e) {
            context.addException(e);
          }
        }
      } else {
        @NotNull UrlTypeRef tailTypeRef = singleTail.getTypeRef();
        @NotNull UrlReqDeleteVarProjection psiTailProjection = singleTail.getReqDeleteVarProjection();
        @NotNull ReqDeleteVarProjection tailProjection =
            buildTailProjection(dataType, op, tailTypeRef, psiTailProjection, subResolver, context);
        tails.add(tailProjection);
      }

    }

    return tails;
  }

  private static @NotNull ReqDeleteVarProjection buildTailProjection(
      @NotNull DataTypeApi dataType,
      @NotNull OpDeleteVarProjection op,
      @NotNull UrlTypeRef tailTypeRefPsi,
      @NotNull UrlReqDeleteVarProjection tailProjectionPsi,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqDeletePsiProcessingContext context) throws PsiProcessingException {

    @NotNull TypeRef tailTypeRef = TypeRefs.fromPsi(tailTypeRefPsi, context);
    @NotNull EntityTypeApi tailType = getEntityType(tailTypeRef, typesResolver, tailTypeRefPsi, context);

    checkEntityTailType(tailType, dataType, tailTypeRefPsi, context);
    @NotNull OpDeleteVarProjection opTail = ProjectionsParsingUtil.getTail(op, tailType, tailTypeRefPsi, context);

    return parseVarProjection(
        tailType.dataType(dataType.defaultTag()),
        opTail,
        tailProjectionPsi,
        typesResolver,
        context
    );
  }

  public static @NotNull ReqDeleteModelProjection<?, ?, ?> parseModelProjection(
      @NotNull OpDeleteModelProjection<?, ?, ?> op,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @NotNull UrlReqDeleteModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqDeletePsiProcessingContext context) throws PsiProcessingException {

    return parseModelProjection(
        ReqDeleteModelProjection.class,
        op,
        params,
        directives,
        psi,
        resolver,
        context
    );

  }

  @SuppressWarnings("unchecked")
  private static <MP extends ReqDeleteModelProjection<?, ?, ?>> @NotNull MP parseModelProjection(
      @NotNull Class<MP> modelClass,
      @NotNull OpDeleteModelProjection<?, ?, ?> op,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @NotNull UrlReqDeleteModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqDeletePsiProcessingContext context) throws PsiProcessingException {

    DatumTypeApi model = op.type();
    final @NotNull TypesResolver subResolver = addTypeNamespace(model, resolver);

    switch (model.kind()) {
      case RECORD:
        assert modelClass.isAssignableFrom(ReqDeleteRecordModelProjection.class);
        ensureModelKind(findProjectionKind(psi), TypeKind.RECORD, psi, context);

        final OpDeleteRecordModelProjection opRecord = (OpDeleteRecordModelProjection) op;

        @Nullable UrlReqDeleteRecordModelProjection recordModelProjectionPsi =
            psi.getReqDeleteRecordModelProjection();

        if (recordModelProjectionPsi == null) {
          checkModelPsi(psi, TypeKind.RECORD, context);
          return (MP) createDefaultModelProjection(model, opRecord, params, directives, psi, context);
        }

        return (MP) parseRecordModelProjection(
            opRecord,
            params,
            directives,
            parseModelTails(
                ReqDeleteRecordModelProjection.class,
                op,
                psi.getReqDeleteModelPolymorphicTail(),
                subResolver,
                context
            ),
            recordModelProjectionPsi,
            subResolver,
            context
        );

      case MAP:
        assert modelClass.isAssignableFrom(ReqDeleteMapModelProjection.class);
        ensureModelKind(findProjectionKind(psi), TypeKind.MAP, psi, context);

        final OpDeleteMapModelProjection opMap = (OpDeleteMapModelProjection) op;
        @Nullable UrlReqDeleteMapModelProjection mapModelProjectionPsi = psi.getReqDeleteMapModelProjection();

        if (mapModelProjectionPsi == null) {
          checkModelPsi(psi, TypeKind.MAP, context);
          return (MP) createDefaultModelProjection(model, opMap, params, directives, psi, context);
        }

        return (MP) parseMapModelProjection(
            opMap,
            params,
            directives,
            parseModelTails(
                ReqDeleteMapModelProjection.class,
                op,
                psi.getReqDeleteModelPolymorphicTail(),
                subResolver,
                context
            ),
            mapModelProjectionPsi,
            subResolver,
            context
        );

      case LIST:
        assert modelClass.isAssignableFrom(ReqDeleteListModelProjection.class);
        ensureModelKind(findProjectionKind(psi), TypeKind.LIST, psi, context);

        final OpDeleteListModelProjection opList = (OpDeleteListModelProjection) op;
        @Nullable UrlReqDeleteListModelProjection listModelProjectionPsi =
            psi.getReqDeleteListModelProjection();

        if (listModelProjectionPsi == null) {
          checkModelPsi(psi, TypeKind.LIST, context);
          return (MP) createDefaultModelProjection(model, opList, params, directives, psi, context);
        }

        return (MP) parseListModelProjection(
            opList,
            params,
            directives,
            parseModelTails(
                ReqDeleteListModelProjection.class,
                op,
                psi.getReqDeleteModelPolymorphicTail(),
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
        assert modelClass.isAssignableFrom(ReqDeletePrimitiveModelProjection.class);
        return (MP) parsePrimitiveModelProjection(
            (OpDeletePrimitiveModelProjection) op,
            params,
            directives,
            parseModelTails(
                ReqDeletePrimitiveModelProjection.class,
                op,
                psi.getReqDeleteModelPolymorphicTail(),
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
      @NotNull UrlReqDeleteModelProjection psi,
      @NotNull TypeKind expectedKind,
      @NotNull ReqDeletePsiProcessingContext context) {

    TypeKind actualKind = null;

    if (psi.getReqDeleteRecordModelProjection() != null) actualKind = TypeKind.RECORD;
    else if (psi.getReqDeleteMapModelProjection() != null) actualKind = TypeKind.MAP;
    else if (psi.getReqDeleteListModelProjection() != null) actualKind = TypeKind.LIST;

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
  private static <MP extends ReqDeleteModelProjection<?, ?, ?>>
  @Nullable List<MP> parseModelTails(
      @NotNull Class<MP> modelClass,
      @NotNull OpDeleteModelProjection<?, ?, ?> op,
      @Nullable UrlReqDeleteModelPolymorphicTail tailPsi,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqDeletePsiProcessingContext context) throws PsiProcessingException {

    if (tailPsi == null) return null;
    else {
      List<MP> tails = new ArrayList<>();

      final UrlReqDeleteModelSingleTail singleTailPsi = tailPsi.getReqDeleteModelSingleTail();
      if (singleTailPsi == null) {
        final UrlReqDeleteModelMultiTail multiTailPsi = tailPsi.getReqDeleteModelMultiTail();
        assert multiTailPsi != null;
        for (UrlReqDeleteModelMultiTailItem tailItemPsi : multiTailPsi.getReqDeleteModelMultiTailItemList()) {
          try {
            tails.add(
                buildModelTailProjection(
                    modelClass,
                    op,
                    tailItemPsi.getTypeRef(),
                    tailItemPsi.getReqDeleteModelProjection(),
                    tailItemPsi,
                    tailItemPsi.getReqParamList(),
                    tailItemPsi.getReqAnnotationList(),
                    typesResolver,
                    context
                )
            );
          } catch (PsiProcessingException e) {
            context.addException(e);
          }
        }
      } else {
        tails.add(
            buildModelTailProjection(
                modelClass,
                op,
                singleTailPsi.getTypeRef(),
                singleTailPsi.getReqDeleteModelProjection(),
                singleTailPsi,
                singleTailPsi.getReqParamList(),
                singleTailPsi.getReqAnnotationList(),
                typesResolver,
                context
            )
        );
      }
      return tails;
    }
  }

  private static <MP extends ReqDeleteModelProjection<?, ?, ?>>
  @NotNull MP buildModelTailProjection(
      @NotNull Class<MP> modelClass,
      @NotNull OpDeleteModelProjection<?, ?, ?> op,
      @NotNull UrlTypeRef tailTypeRefPsi,
      @NotNull UrlReqDeleteModelProjection modelProjectionPsi,
      @NotNull PsiElement paramsLocationPsi,
      @NotNull List<UrlReqParam> modelParamsList,
      @NotNull List<UrlReqAnnotation> modelAnnotationsList,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqDeletePsiProcessingContext context) throws PsiProcessingException {

    @NotNull TypeRef tailTypeRef = TypeRefs.fromPsi(tailTypeRefPsi, context);
    @NotNull DatumTypeApi tailType = getDatumType(tailTypeRef, typesResolver, tailTypeRefPsi, context);

    final @NotNull OpDeleteModelProjection<?, ?, ?> opTail =
        ProjectionsParsingUtil.getTail(op, tailType, tailTypeRefPsi, context);

    return parseModelProjection(
        modelClass,
        opTail,
        parseReqParams(modelParamsList, op.params(), typesResolver, paramsLocationPsi, context),
        parseAnnotations(modelAnnotationsList, context),
        modelProjectionPsi,
        typesResolver,
        context
    );
  }

  private static @NotNull ReqDeleteModelProjection<?, ?, ?> createDefaultModelProjection(
      @NotNull DatumTypeApi type,
      @NotNull OpDeleteModelProjection<?, ?, ?> op,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @NotNull PsiElement locationPsi,
      @NotNull ReqDeletePsiProcessingContext context) throws PsiProcessingException {

    @NotNull TextLocation location = EpigraphPsiUtil.getLocation(locationPsi);

    switch (type.kind()) {
      case RECORD:
        OpDeleteRecordModelProjection opRecord = (OpDeleteRecordModelProjection) op;
        final Map<String, OpDeleteFieldProjectionEntry> opFields = opRecord.fieldProjections();

        final @NotNull Map<String, ReqDeleteFieldProjectionEntry> fields;

        fields = opFields.isEmpty() ? Collections.emptyMap() : new LinkedHashMap<>();

        return new ReqDeleteRecordModelProjection(
            (RecordTypeApi) type,
            params,
            directives,
            fields,
            null,
            location
        );
      case MAP:
        OpDeleteMapModelProjection opMap = (OpDeleteMapModelProjection) op;

        MapTypeApi mapType = (MapTypeApi) type;
        final ReqDeleteVarProjection valueVarProjection = createDefaultVarProjection(
            mapType.valueType(),
            opMap.itemsProjection(),
            locationPsi,
            context
        );

        return new ReqDeleteMapModelProjection(
            mapType,
            params,
            directives,
            null,
            valueVarProjection,
            null,
            location
        );
      case LIST:
        OpDeleteListModelProjection opList = (OpDeleteListModelProjection) op;
        ListTypeApi listType = (ListTypeApi) type;

        final ReqDeleteVarProjection itemVarProjection = createDefaultVarProjection(
            listType.elementType(),
            opList.itemsProjection(),
            locationPsi,
            context
        );

        return new ReqDeleteListModelProjection(
            listType,
            params,
            directives,
            itemVarProjection,
            null,
            location
        );
      case ENTITY:
        throw new PsiProcessingException(
            "Was expecting to get datum model kind, got: " + type.kind(),
            locationPsi,
            context
        );
      case ENUM:
        // todo
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), locationPsi, context);
      case PRIMITIVE:
        return new ReqDeletePrimitiveModelProjection(
            (PrimitiveTypeApi) type,
            params,
            directives,
            null,
            location
        );
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), locationPsi, context);
    }
  }

  private static @NotNull ReqDeleteVarProjection createDefaultVarProjection(
      @NotNull DataTypeApi type,
      @NotNull OpDeleteVarProjection op,
      @NotNull PsiElement locationPsi,
      @NotNull ReqDeletePsiProcessingContext context) throws PsiProcessingException {

    return createDefaultVarProjection(type.type(), op, locationPsi, context);
  }

  private static @NotNull ReqDeleteVarProjection createDefaultVarProjection(
      @NotNull TypeApi type,
      @NotNull OpDeleteVarProjection op,
      @NotNull PsiElement locationPsi,
      @NotNull ReqDeletePsiProcessingContext context) throws PsiProcessingException {

    @Nullable TagApi defaultTag = findSelfTag(type, op, locationPsi, context);
    List<TagApi> tags = defaultTag == null ?
                        Collections.emptyList() :
                        Collections.singletonList(defaultTag);
    return createDefaultVarProjection(type, tags, op, locationPsi, context);
  }

  /**
   * Creates default var projection with explicitly specified list of tags
   */
  private static ReqDeleteVarProjection createDefaultVarProjection(
      @NotNull TypeApi type,
      @NotNull Iterable<TagApi> tags,
      @NotNull OpDeleteVarProjection op,
      @NotNull PsiElement locationPsi,
      @NotNull ReqDeletePsiProcessingContext context) throws PsiProcessingException {

    LinkedHashMap<String, ReqDeleteTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    for (TagApi tag : tags) {
      final OpDeleteTagProjectionEntry opDeleteTagProjection = op.tagProjections().get(tag.name());
      if (opDeleteTagProjection != null) {
        tagProjections.put(
            tag.name(),
            new ReqDeleteTagProjectionEntry(
                tag,
                createDefaultModelProjection(
                    tag.type(),
                    opDeleteTagProjection.projection(),
                    ReqParams.EMPTY,
                    Directives.EMPTY,
                    locationPsi,
                    context
                ),
                EpigraphPsiUtil.getLocation(locationPsi)
            )
        );
      }
    }

    return new ReqDeleteVarProjection(
        type,
        tagProjections,
        op.parenthesized() || tagProjections.size() != 1,
        null,
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

  private static @Nullable TypeKind findProjectionKind(@NotNull UrlReqDeleteModelProjection psi) {
    if (psi.getReqDeleteRecordModelProjection() != null) return TypeKind.RECORD;
    if (psi.getReqDeleteMapModelProjection() != null) return TypeKind.MAP;
    if (psi.getReqDeleteListModelProjection() != null) return TypeKind.LIST;
    return null;
  }

  public static @NotNull ReqDeleteRecordModelProjection parseRecordModelProjection(
      @NotNull OpDeleteRecordModelProjection op,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @Nullable List<ReqDeleteRecordModelProjection> tails,
      @NotNull UrlReqDeleteRecordModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqDeletePsiProcessingContext context) {

    final Map<String, ReqDeleteFieldProjectionEntry> fieldProjections = new LinkedHashMap<>();

    for (final UrlReqDeleteFieldProjectionEntry entryPsi : psi.getReqDeleteFieldProjectionEntryList()) {
      final @NotNull String fieldName = entryPsi.getQid().getCanonicalName();

      final @Nullable OpDeleteFieldProjectionEntry opFieldProjectionEntry = op.fieldProjection(fieldName);
      if (opFieldProjectionEntry == null) {
        context.addError(
            String.format(
                "Field '%s' is not supported by the operation. Supported fields: {%s}",
                fieldName,
                String.join(", ", op.fieldProjections().keySet())
            ), entryPsi.getQid()
        );
      } else {
        try {
          final FieldApi field = opFieldProjectionEntry.field();
          final @NotNull OpDeleteFieldProjection opFieldProjection = opFieldProjectionEntry.fieldProjection();
          final @NotNull UrlReqDeleteFieldProjection fieldProjectionPsi = entryPsi.getReqDeleteFieldProjection();
          final @NotNull DataTypeApi fieldType = field.dataType();

          final ReqDeleteFieldProjection fieldProjection =
              parseFieldProjection(
                  fieldType,
                  opFieldProjection,
                  fieldProjectionPsi,
                  resolver,
                  context
              );

          fieldProjections.put(
              fieldName,
              new ReqDeleteFieldProjectionEntry(
                  field,
                  fieldProjection,
                  EpigraphPsiUtil.getLocation(fieldProjectionPsi)
              )
          );
        } catch (PsiProcessingException e) {
          context.addException(e);
        }
      }
    }

    return new ReqDeleteRecordModelProjection(
        op.type(),
        params,
        directives,
        fieldProjections,
        tails,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull ReqDeleteFieldProjection parseFieldProjection(
      final DataTypeApi fieldType,
      final @NotNull OpDeleteFieldProjection op,
      final @NotNull UrlReqDeleteFieldProjection psi,
      final @NotNull TypesResolver resolver, final @NotNull ReqDeletePsiProcessingContext context)
      throws PsiProcessingException {

//    ReqParams fieldParams = parseReqParams(psi.getReqParamList(), op.params(), resolver, context);

//    Annotations fieldAnnotations = parseAnnotations(psi.getReqAnnotationList(), context);

    @NotNull UrlReqDeleteVarProjection psiVarProjection = psi.getReqDeleteVarProjection();
    @NotNull ReqDeleteVarProjection varProjection =
        parseVarProjection(
            fieldType,
            op.varProjection(),
            psiVarProjection,
            resolver,
            context
        );

    ProjectionsParsingUtil.verifyData(fieldType, varProjection, psi, context);

    return new ReqDeleteFieldProjection(
//        fieldParams,
//        fieldAnnotations,
        varProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull ReqDeleteMapModelProjection parseMapModelProjection(
      @NotNull OpDeleteMapModelProjection op,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @Nullable List<ReqDeleteMapModelProjection> tails,
      @NotNull UrlReqDeleteMapModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqDeletePsiProcessingContext context) throws PsiProcessingException {


    final List<ReqDeleteKeyProjection> keyProjections;
    if (psi.getReqDeleteKeysProjection().getStar() == null) {
      final @NotNull List<UrlReqDeleteKeyProjection> keyProjectionsPsi =
          psi.getReqDeleteKeysProjection().getReqDeleteKeyProjectionList();

      keyProjections = new ArrayList<>(keyProjectionsPsi.size());

      for (final UrlReqDeleteKeyProjection keyProjectionPsi : keyProjectionsPsi) {
        try {
          final @NotNull UrlDatum keyValuePsi = keyProjectionPsi.getDatum();
          final @Nullable Datum keyValue =
              getDatum(keyValuePsi, op.type().keyType(), resolver, "Error processing map key:", context);

          if (keyValue == null) context.addError("Null keys are not allowed", keyValuePsi);
          else {
            keyProjections.add(
                new ReqDeleteKeyProjection(
                    keyValue,
                    parseReqParams(
                        keyProjectionPsi.getReqParamList(),
                        op.keyProjection().params(),
                        resolver,
                        keyProjectionPsi,
                        context
                    ),
                    parseAnnotations(keyProjectionPsi.getReqAnnotationList(), context),
                    EpigraphPsiUtil.getLocation(keyProjectionPsi)
                )
            );
          }
        } catch (PsiProcessingException e) {
          context.addException(e);
        }
      }
    } else keyProjections = null;

    final @Nullable UrlReqDeleteVarProjection elementsVarProjectionPsi = psi.getReqDeleteVarProjection();
    final @NotNull ReqDeleteVarProjection elementsVarProjection;
    if (elementsVarProjectionPsi == null) {
      final @NotNull TypeApi type = op.type().valueType().type();
      elementsVarProjection = getDefaultVarProjection(type, psi);
    } else {
      elementsVarProjection = parseVarProjection(
          op.type().valueType(),
          op.itemsProjection(),
          elementsVarProjectionPsi,
          resolver,
          context
      );
    }

    return new ReqDeleteMapModelProjection(
        op.type(),
        params,
        directives,
        keyProjections,
        elementsVarProjection,
        tails,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull ReqDeleteListModelProjection parseListModelProjection(
      @NotNull OpDeleteListModelProjection op,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @Nullable List<ReqDeleteListModelProjection> tails,
      @NotNull UrlReqDeleteListModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqDeletePsiProcessingContext context) throws PsiProcessingException {

    final @Nullable UrlReqDeleteVarProjection elementsVarProjectionPsi = psi.getReqDeleteVarProjection();

    final @NotNull ReqDeleteVarProjection elementsVarProjection;
    if (elementsVarProjectionPsi == null) {
      final @NotNull TypeApi type = op.type().elementType().type();
      elementsVarProjection = getDefaultVarProjection(type, psi);
    } else {
      elementsVarProjection = parseVarProjection(
          op.type().elementType(),
          op.itemsProjection(),
          elementsVarProjectionPsi,
          resolver,
          context
      );
    }

    return new ReqDeleteListModelProjection(
        op.type(),
        params,
        directives,
        elementsVarProjection,
        tails,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull ReqDeletePrimitiveModelProjection parsePrimitiveModelProjection(
      @NotNull OpDeletePrimitiveModelProjection op,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @Nullable List<ReqDeletePrimitiveModelProjection> tails,
      @NotNull PsiElement locationPsi) {

    return new ReqDeletePrimitiveModelProjection(
        op.type(),
        params,
        directives,
        tails,
        EpigraphPsiUtil.getLocation(locationPsi)
    );

  }
}

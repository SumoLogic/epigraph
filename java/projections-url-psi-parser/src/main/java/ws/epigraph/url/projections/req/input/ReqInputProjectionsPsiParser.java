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

package ws.epigraph.url.projections.req.input;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Datum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ProjectionsParsingUtil;
import ws.epigraph.projections.op.OpKeyPresence;
import ws.epigraph.projections.op.input.*;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.projections.req.input.*;
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
public final class ReqInputProjectionsPsiParser {

  private ReqInputProjectionsPsiParser() {}

  public static @NotNull ReqInputVarProjection parseVarProjection(
      @NotNull DataTypeApi dataType,
      @NotNull OpInputVarProjection op,
      @NotNull UrlReqInputVarProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqInputPsiProcessingContext context) throws PsiProcessingException {

    final UrlReqInputNamedVarProjection namedVarProjection = psi.getReqInputNamedVarProjection();
    if (namedVarProjection == null) {
      final UrlReqInputUnnamedOrRefVarProjection unnamedOrRefVarProjection =
          psi.getReqInputUnnamedOrRefVarProjection();

      if (unnamedOrRefVarProjection == null)
        throw new PsiProcessingException(
            "Incomplete var projection definition",
            psi,
            context.errors()
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

      final @Nullable UrlReqInputUnnamedOrRefVarProjection unnamedOrRefVarProjection =
          namedVarProjection.getReqInputUnnamedOrRefVarProjection();

      if (unnamedOrRefVarProjection == null)
        throw new PsiProcessingException(
            String.format("Incomplete var projection '%s' definition", projectionName),
            psi,
            context.errors()
        );

      final ReqInputVarProjection reference = context.varReferenceContext()
          .varReference(dataType.type(), projectionName, false, EpigraphPsiUtil.getLocation(psi));

      final ReqInputVarProjection value = parseUnnamedOrRefVarProjection(
          dataType,
          op,
          unnamedOrRefVarProjection,
          resolver,
          context
      );

      context.varReferenceContext()
          .resolve(projectionName, value, EpigraphPsiUtil.getLocation(unnamedOrRefVarProjection), context);

      final Queue<OpInputVarProjection> unverifiedOps = context.unverifiedRefOps(projectionName);
      while (unverifiedOps != null && !unverifiedOps.isEmpty()) {
        final OpInputVarProjection unverifiedOp = unverifiedOps.poll();
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

  public static @NotNull ReqInputVarProjection parseUnnamedOrRefVarProjection(
      final @NotNull DataTypeApi dataType,
      final @NotNull OpInputVarProjection op,
      final @NotNull UrlReqInputUnnamedOrRefVarProjection psi,
      final @NotNull TypesResolver resolver,
      final @NotNull ReqInputPsiProcessingContext context) throws PsiProcessingException {

    final UrlReqInputVarProjectionRef varProjectionRef = psi.getReqInputVarProjectionRef();
    if (varProjectionRef == null) {
      // usual var projection
      final UrlReqInputUnnamedVarProjection unnamedVarProjection = psi.getReqInputUnnamedVarProjection();
      if (unnamedVarProjection == null)
        throw new PsiProcessingException("Incomplete var projection definition", psi, context.errors());
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
            context.errors()
        );

      final String referenceName = varProjectionRefPsi.getCanonicalName();

      final Collection<OpInputVarProjection> verifiedOps = context.verifiedRefOps(referenceName);
      if (verifiedOps == null || !verifiedOps.contains(op))
        context.addUnverifiedRefOp(referenceName, op);

      return context.varReferenceContext()
          .varReference(dataType.type(), referenceName, true, EpigraphPsiUtil.getLocation(psi));
    }

  }

  public static @NotNull ReqInputVarProjection parseUnnamedVarProjection(
      @NotNull DataTypeApi dataType,
      @NotNull OpInputVarProjection op,
      @NotNull UrlReqInputUnnamedVarProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqInputPsiProcessingContext context) throws PsiProcessingException {

    final TypeApi type = dataType.type();
    final LinkedHashMap<String, ReqInputTagProjectionEntry> tagProjections;

    @Nullable UrlReqInputSingleTagProjection singleTagProjectionPsi = psi.getReqInputSingleTagProjection();
    final @NotNull TypesResolver subResolver = addTypeNamespace(dataType.type(), resolver);

    if (singleTagProjectionPsi == null) {
      @Nullable UrlReqInputMultiTagProjection multiTagProjection = psi.getReqInputMultiTagProjection();
      assert multiTagProjection != null;
      tagProjections = parseMultiTagProjection(dataType, op, multiTagProjection, subResolver, context);
    } else {
      // try to improve error reporting: singleTagProjectionPsi may be empty
      PsiElement tagLocation = getSingleTagLocation(singleTagProjectionPsi);
      tagProjections = new LinkedHashMap<>();
      final @Nullable UrlTagName tagNamePsi = singleTagProjectionPsi.getTagName();

      TagApi tag = findTagOrSelfTag(type, tagNamePsi, op, tagLocation, context);
      if (tag != null || !singleTagProjectionPsi.getText().isEmpty()) {
        if (tag == null) tag = getTagOrSelfTag(type, null, op, tagLocation, context); // will throw proper error

        @NotNull OpInputTagProjectionEntry opTagProjection = getTagProjection(tag.name(), op, tagLocation, context);

        @NotNull OpInputModelProjection<?, ?, ?, ?> opModelProjection = opTagProjection.projection();
        @NotNull UrlReqInputModelProjection modelProjectionPsi = singleTagProjectionPsi.getReqInputModelProjection();

        final ReqInputModelProjection<?, ?, ?> parsedModelProjection = parseModelProjection(
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
            new ReqInputTagProjectionEntry(
                tag,
                parsedModelProjection,
                EpigraphPsiUtil.getLocation(tagLocation)
            )
        );

      }
    }

    // check that all required tags are present
    for (final Map.Entry<String, OpInputTagProjectionEntry> entry : op.tagProjections().entrySet()) {
      if (entry.getValue().projection().required() && !tagProjections.containsKey(entry.getKey()))
        context.addError(String.format("Required tag '%s' is missing", entry.getKey()), psi);
    }

    final List<ReqInputVarProjection> tails =
        parseTails(dataType, op, psi.getReqInputVarPolymorphicTail(), subResolver, context);

    try {
      return new ReqInputVarProjection(
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

  private static @NotNull PsiElement getSingleTagLocation(final @NotNull UrlReqInputSingleTagProjection singleTagProjectionPsi) {
    final UrlTagName tagName = singleTagProjectionPsi.getTagName();
    if (tagName != null) return tagName;
    PsiElement tagLocation = singleTagProjectionPsi;
    if (tagLocation.getText().isEmpty()) {
      final @Nullable UrlReqInputFieldProjectionEntry fieldProjectionPsi =
          PsiTreeUtil.getParentOfType(tagLocation, UrlReqInputFieldProjectionEntry.class);
      if (fieldProjectionPsi != null) {
        tagLocation = fieldProjectionPsi.getQid();
      }
    }
    return tagLocation;
  }

  private static @NotNull LinkedHashMap<String, ReqInputTagProjectionEntry> parseMultiTagProjection(
      @NotNull DataTypeApi dataType,
      @NotNull OpInputVarProjection op,
      @NotNull UrlReqInputMultiTagProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqInputPsiProcessingContext context) throws PsiProcessingException {

    if (!(dataType.type().equals(op.type())))
      throw new PsiProcessingException(
          String.format("Inconsistent arguments. data type: '%s', op type: '%s'", dataType.name(), op.type().name()),
          psi,
          context
      );

    final @NotNull TypesResolver subResolver = addTypeNamespace(dataType.type(), typesResolver);

    final LinkedHashMap<String, ReqInputTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    // parse list of tags
    @NotNull Iterable<UrlReqInputMultiTagProjectionItem> tagProjectionPsiList =
        psi.getReqInputMultiTagProjectionItemList();

    for (UrlReqInputMultiTagProjectionItem tagProjectionPsi : tagProjectionPsiList) {
      try {
        @NotNull TagApi tag =
            UrlProjectionsPsiParserUtil.getTag(tagProjectionPsi.getTagName(), op, tagProjectionPsi, context);
        @NotNull OpInputTagProjectionEntry opTag = getTagProjection(tag.name(), op, tagProjectionPsi, context);

        OpInputModelProjection<?, ?, ?, ?> opTagProjection = opTag.projection();

        @NotNull UrlReqInputModelProjection modelProjection = tagProjectionPsi.getReqInputModelProjection();

        final ReqInputModelProjection<?, ?, ?> parsedModelProjection = parseModelProjection(
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
            new ReqInputTagProjectionEntry(
                tag,
                parsedModelProjection,
                EpigraphPsiUtil.getLocation(tagProjectionPsi)
            )
        );
      } catch (PsiProcessingException e) {
        context.setErrors(e.errors());
      }
    }

    return tagProjections;
  }

  private static @NotNull ReqInputVarProjection getDefaultVarProjection(
      final @NotNull TypeApi type,
      final @NotNull PsiElement psi) {
    return new ReqInputVarProjection(
        type,
        Collections.emptyMap(),
        true,
        null,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  private static @Nullable List<ReqInputVarProjection> parseTails(
      @NotNull DataTypeApi dataType,
      @NotNull OpInputVarProjection op,
      @Nullable UrlReqInputVarPolymorphicTail tailPsi,
      @NotNull TypesResolver resolver,
      @NotNull ReqInputPsiProcessingContext context) throws PsiProcessingException {

    final List<ReqInputVarProjection> tails;

    @NotNull TypesResolver subResolver = addTypeNamespace(dataType.type(), resolver);

    if (tailPsi == null) tails = null;
    else {
      tails = new ArrayList<>();

      @Nullable UrlReqInputVarSingleTail singleTail = tailPsi.getReqInputVarSingleTail();
      if (singleTail == null) {
        @Nullable UrlReqInputVarMultiTail multiTail = tailPsi.getReqInputVarMultiTail();
        assert multiTail != null;
        TypeApi prevTailType = null;

        for (UrlReqInputVarMultiTailItem tailItem : multiTail.getReqInputVarMultiTailItemList()) {
          try {
            if (prevTailType != null)
              subResolver = addTypeNamespace(prevTailType, resolver);

            @NotNull UrlTypeRef tailTypeRef = tailItem.getTypeRef();
            @NotNull UrlReqInputVarProjection psiTailProjection = tailItem.getReqInputVarProjection();
            @NotNull ReqInputVarProjection tailProjection =
                buildTailProjection(dataType, op, tailTypeRef, psiTailProjection, subResolver, context);
            tails.add(tailProjection);

            prevTailType = tailProjection.type();
          } catch (PsiProcessingException e) {
            context.addException(e);
          }
        }
      } else {
        @NotNull UrlTypeRef tailTypeRef = singleTail.getTypeRef();
        @NotNull UrlReqInputVarProjection psiTailProjection = singleTail.getReqInputVarProjection();
        @NotNull ReqInputVarProjection tailProjection =
            buildTailProjection(dataType, op, tailTypeRef, psiTailProjection, subResolver, context);
        tails.add(tailProjection);
      }

    }

    return tails;
  }

  private static @NotNull ReqInputVarProjection buildTailProjection(
      @NotNull DataTypeApi dataType,
      @NotNull OpInputVarProjection op,
      @NotNull UrlTypeRef tailTypeRefPsi,
      @NotNull UrlReqInputVarProjection tailProjectionPsi,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqInputPsiProcessingContext context) throws PsiProcessingException {

    @NotNull TypeRef tailTypeRef = TypeRefs.fromPsi(tailTypeRefPsi, context);
    @NotNull UnionTypeApi tailType = getUnionType(tailTypeRef, typesResolver, tailTypeRefPsi, context);

    checkTailType(tailType, dataType, tailTypeRefPsi, context);
    @NotNull OpInputVarProjection opTail = ProjectionsParsingUtil.getTail(op, tailType, tailTypeRefPsi, context);

    return parseVarProjection(
        tailType.dataType(dataType.defaultTag()),
        opTail,
        tailProjectionPsi,
        typesResolver,
        context
    );
  }

  public static @NotNull ReqInputModelProjection<?, ?, ?> parseModelProjection(
      @NotNull OpInputModelProjection<?, ?, ?, ?> op,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull UrlReqInputModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqInputPsiProcessingContext context) throws PsiProcessingException {

    return parseModelProjection(
        ReqInputModelProjection.class,
        op,
        params,
        annotations,
        psi,
        resolver,
        context
    );

  }

  @SuppressWarnings("unchecked")
  private static <MP extends ReqInputModelProjection<?, ?, ?>>
  @NotNull MP parseModelProjection(
      @NotNull Class<MP> modelClass,
      @NotNull OpInputModelProjection<?, ?, ?, ?> op,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull UrlReqInputModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqInputPsiProcessingContext context) throws PsiProcessingException {

    DatumTypeApi model = op.type();
    final @NotNull TypesResolver subResolver = addTypeNamespace(model, resolver);

    switch (model.kind()) {
      case RECORD:
        assert modelClass.isAssignableFrom(ReqInputRecordModelProjection.class);
        ensureModelKind(findProjectionKind(psi), TypeKind.RECORD, psi, context);

        final OpInputRecordModelProjection opRecord = (OpInputRecordModelProjection) op;

        @Nullable UrlReqInputRecordModelProjection recordModelProjectionPsi =
            psi.getReqInputRecordModelProjection();

        if (recordModelProjectionPsi == null) {
          if (op.required())
            context.addError(String.format("'%s' projection is required", model.name()), psi);
          checkModelPsi(psi, TypeKind.RECORD, context);
          return (MP) createDefaultModelProjection(model, opRecord, params, annotations, psi, context);
        }

        return (MP) parseRecordModelProjection(
            opRecord,
            params,
            annotations,
            parseModelTails(
                ReqInputRecordModelProjection.class,
                op,
                psi.getReqInputModelPolymorphicTail(),
                subResolver,
                context
            ),
            recordModelProjectionPsi,
            subResolver,
            context
        );

      case MAP:
        assert modelClass.isAssignableFrom(ReqInputMapModelProjection.class);
        ensureModelKind(findProjectionKind(psi), TypeKind.MAP, psi, context);

        final OpInputMapModelProjection opMap = (OpInputMapModelProjection) op;
        @Nullable UrlReqInputMapModelProjection mapModelProjectionPsi = psi.getReqInputMapModelProjection();

        if (mapModelProjectionPsi == null) {
          if (op.required())
            context.addError(String.format("'%s' projection is required", model.name()), psi);
          checkModelPsi(psi, TypeKind.MAP, context);
          return (MP) createDefaultModelProjection(model, opMap, params, annotations, psi, context);
        }

        return (MP) parseMapModelProjection(
            opMap,
            params,
            annotations,
            parseModelTails(
                ReqInputMapModelProjection.class,
                op,
                psi.getReqInputModelPolymorphicTail(),
                subResolver,
                context
            ),
            mapModelProjectionPsi,
            subResolver,
            context
        );

      case LIST:
        assert modelClass.isAssignableFrom(ReqInputListModelProjection.class);
        ensureModelKind(findProjectionKind(psi), TypeKind.LIST, psi, context);

        final OpInputListModelProjection opList = (OpInputListModelProjection) op;
        @Nullable UrlReqInputListModelProjection listModelProjectionPsi =
            psi.getReqInputListModelProjection();

        if (listModelProjectionPsi == null) {
          if (op.required())
            context.addError(String.format("'%s' projection is required", model.name()), psi);
          checkModelPsi(psi, TypeKind.LIST, context);
          return (MP) createDefaultModelProjection(model, opList, params, annotations, psi, context);
        }

        return (MP) parseListModelProjection(
            opList,
            params,
            annotations,
            parseModelTails(
                ReqInputListModelProjection.class,
                op,
                psi.getReqInputModelPolymorphicTail(),
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
        assert modelClass.isAssignableFrom(ReqInputPrimitiveModelProjection.class);

        return (MP) parsePrimitiveModelProjection(
            (OpInputPrimitiveModelProjection) op,
            params,
            annotations,
            parseModelTails(
                ReqInputPrimitiveModelProjection.class,
                op,
                psi.getReqInputModelPolymorphicTail(),
                subResolver,
                context
            ),
            psi
        );

      case UNION:
        throw new PsiProcessingException("Unsupported type kind: " + model.kind(), psi, context);

      default:
        throw new PsiProcessingException("Unknown type kind: " + model.kind(), psi, context);
    }

  }

  private static void checkModelPsi(
      @NotNull UrlReqInputModelProjection psi,
      @NotNull TypeKind expectedKind,
      @NotNull ReqInputPsiProcessingContext context) {

    TypeKind actualKind = null;

    if (psi.getReqInputRecordModelProjection() != null) actualKind = TypeKind.RECORD;
    else if (psi.getReqInputMapModelProjection() != null) actualKind = TypeKind.MAP;
    else if (psi.getReqInputListModelProjection() != null) actualKind = TypeKind.LIST;

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
  private static <MP extends ReqInputModelProjection<?, ?, ?>>
  @Nullable List<MP> parseModelTails(
      @NotNull Class<MP> modelClass,
      @NotNull OpInputModelProjection<?, ?, ?, ?> op,
      @Nullable UrlReqInputModelPolymorphicTail tailPsi,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqInputPsiProcessingContext context) throws PsiProcessingException {

    if (tailPsi == null) return null;
    else {
      List<MP> tails = new ArrayList<>();

      final UrlReqInputModelSingleTail singleTailPsi = tailPsi.getReqInputModelSingleTail();
      if (singleTailPsi == null) {
        final UrlReqInputModelMultiTail multiTailPsi = tailPsi.getReqInputModelMultiTail();
        assert multiTailPsi != null;
        for (UrlReqInputModelMultiTailItem tailItemPsi : multiTailPsi.getReqInputModelMultiTailItemList()) {
          try {
            tails.add(
                buildModelTailProjection(
                    modelClass,
                    op,
                    tailItemPsi.getTypeRef(),
                    tailItemPsi.getReqInputModelProjection(),
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
                singleTailPsi.getReqInputModelProjection(),
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

  private static <MP extends ReqInputModelProjection<?, ?, ?>>
  @NotNull MP buildModelTailProjection(
      @NotNull Class<MP> modelClass,
      @NotNull OpInputModelProjection<?, ?, ?, ?> op,
      @NotNull UrlTypeRef tailTypeRefPsi,
      @NotNull UrlReqInputModelProjection modelProjectionPsi,
      @NotNull PsiElement paramsLocationPsi,
      @NotNull List<UrlReqParam> modelParamsList,
      @NotNull List<UrlReqAnnotation> modelAnnotationsList,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqInputPsiProcessingContext context) throws PsiProcessingException {

    @NotNull TypeRef tailTypeRef = TypeRefs.fromPsi(tailTypeRefPsi, context);
    @NotNull DatumTypeApi tailType = getDatumType(tailTypeRef, typesResolver, tailTypeRefPsi, context);

    final @NotNull OpInputModelProjection<?, ?, ?, ?> opTail =
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

  private static @NotNull ReqInputModelProjection<?, ?, ?> createDefaultModelProjection(
      @NotNull DatumTypeApi type,
      @NotNull OpInputModelProjection<?, ?, ?, ?> op,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull PsiElement locationPsi,
      @NotNull ReqInputPsiProcessingContext context) throws PsiProcessingException {

    @NotNull TextLocation location = EpigraphPsiUtil.getLocation(locationPsi);

    switch (type.kind()) {
      case RECORD:
        OpInputRecordModelProjection opRecord = (OpInputRecordModelProjection) op;
        final Map<String, OpInputFieldProjectionEntry> opFields = opRecord.fieldProjections();

        final @NotNull Map<String, ReqInputFieldProjectionEntry> fields;

        fields = opFields.isEmpty() ? Collections.emptyMap() : new LinkedHashMap<>();

        return new ReqInputRecordModelProjection(
            (RecordTypeApi) type,
            params,
            annotations,
            fields,
            null,
            location
        );
      case MAP:
        OpInputMapModelProjection opMap = (OpInputMapModelProjection) op;

        if (opMap.keyProjection().presence() == OpKeyPresence.REQUIRED)
          throw new PsiProcessingException(
              String.format("Can't build default projection for '%s': keys are required", type.name()),
              locationPsi,
              context
          );

        MapTypeApi mapType = (MapTypeApi) type;
        final ReqInputVarProjection valueVarProjection = createDefaultVarProjection(
            mapType.valueType(),
            opMap.itemsProjection(),
            locationPsi,
            context
        );

        return new ReqInputMapModelProjection(
            mapType,
            params,
            annotations,
            Collections.emptyList(),
            valueVarProjection,
            null,
            location
        );
      case LIST:
        OpInputListModelProjection opList = (OpInputListModelProjection) op;
        ListTypeApi listType = (ListTypeApi) type;

        final ReqInputVarProjection itemVarProjection = createDefaultVarProjection(
            listType.elementType(),
            opList.itemsProjection(),
            locationPsi,
            context
        );

        return new ReqInputListModelProjection(
            listType,
            params,
            annotations,
            itemVarProjection,
            null,
            location
        );
      case UNION:
        throw new PsiProcessingException(
            "Was expecting to get datum model kind, got: " + type.kind(),
            locationPsi,
            context
        );
      case ENUM:
        // todo
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), locationPsi, context);
      case PRIMITIVE:
        return new ReqInputPrimitiveModelProjection(
            (PrimitiveTypeApi) type,
            params,
            annotations,
            null,
            location
        );
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), locationPsi, context);
    }
  }

  private static @NotNull ReqInputVarProjection createDefaultVarProjection(
      @NotNull DataTypeApi type,
      @NotNull OpInputVarProjection op,
      @NotNull PsiElement locationPsi,
      @NotNull ReqInputPsiProcessingContext context) throws PsiProcessingException {

    return createDefaultVarProjection(type.type(), op, locationPsi, context);
  }

  private static @NotNull ReqInputVarProjection createDefaultVarProjection(
      @NotNull TypeApi type,
      @NotNull OpInputVarProjection op,
      @NotNull PsiElement locationPsi,
      @NotNull ReqInputPsiProcessingContext context) throws PsiProcessingException {

    @Nullable TagApi defaultTag = findSelfTag(type, op, locationPsi, context);
    List<TagApi> tags = defaultTag == null ?
                        Collections.emptyList() :
                        Collections.singletonList(defaultTag);
    return createDefaultVarProjection(type, tags, op, locationPsi, context);
  }

  /**
   * Creates default var projection with explicitly specified list of tags
   */
  private static ReqInputVarProjection createDefaultVarProjection(
      @NotNull TypeApi type,
      @NotNull Iterable<TagApi> tags,
      @NotNull OpInputVarProjection op,
      @NotNull PsiElement locationPsi,
      @NotNull ReqInputPsiProcessingContext context) throws PsiProcessingException {

    LinkedHashMap<String, ReqInputTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    for (TagApi tag : tags) {
      final OpInputTagProjectionEntry opInputTagProjection = op.tagProjections().get(tag.name());
      if (opInputTagProjection != null) {
        tagProjections.put(
            tag.name(),
            new ReqInputTagProjectionEntry(
                tag,
                createDefaultModelProjection(
                    tag.type(),
                    opInputTagProjection.projection(),
                    ReqParams.EMPTY,
                    Annotations.EMPTY,
                    locationPsi,
                    context
                ),
                EpigraphPsiUtil.getLocation(locationPsi)
            )
        );
      }
    }

    return new ReqInputVarProjection(
        type,
        tagProjections,
        op.parenthesized() || tagProjections.size() != 1,
        null,
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

  private static @Nullable TypeKind findProjectionKind(@NotNull UrlReqInputModelProjection psi) {
    if (psi.getReqInputRecordModelProjection() != null) return TypeKind.RECORD;
    if (psi.getReqInputMapModelProjection() != null) return TypeKind.MAP;
    if (psi.getReqInputListModelProjection() != null) return TypeKind.LIST;
    return null;
  }

  public static @NotNull ReqInputRecordModelProjection parseRecordModelProjection(
      @NotNull OpInputRecordModelProjection op,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @Nullable List<ReqInputRecordModelProjection> tails,
      @NotNull UrlReqInputRecordModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqInputPsiProcessingContext context) {

    final Map<String, ReqInputFieldProjectionEntry> fieldProjections = new LinkedHashMap<>();

    for (final UrlReqInputFieldProjectionEntry entryPsi : psi.getReqInputFieldProjectionEntryList()) {
      final @NotNull String fieldName = entryPsi.getQid().getCanonicalName();

      final @Nullable OpInputFieldProjectionEntry opFieldProjectionEntry = op.fieldProjection(fieldName);
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
          final @NotNull OpInputFieldProjection opFieldProjection = opFieldProjectionEntry.fieldProjection();
          final @NotNull UrlReqInputFieldProjection fieldProjectionPsi = entryPsi.getReqInputFieldProjection();
          final @NotNull DataTypeApi fieldType = field.dataType();

          final ReqInputFieldProjection fieldProjection =
              parseFieldProjection(
                  fieldType,
                  opFieldProjection,
                  fieldProjectionPsi,
                  resolver,
                  context
              );

          fieldProjections.put(
              fieldName,
              new ReqInputFieldProjectionEntry(
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

    // check that all required fields are specified
    for (final Map.Entry<String, OpInputFieldProjectionEntry> entry : op.fieldProjections().entrySet()) {
      if (entry.getValue().fieldProjection().required() && !fieldProjections.containsKey(entry.getKey())) {
        context.addError(String.format("Required field '%s' is missing", entry.getKey()), psi);
      }
    }

    return new ReqInputRecordModelProjection(
        op.type(),
        params,
        annotations,
        fieldProjections,
        tails,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull ReqInputFieldProjection parseFieldProjection(
      final DataTypeApi fieldType,
      final @NotNull OpInputFieldProjection op,
      final @NotNull UrlReqInputFieldProjection psi,
      final @NotNull TypesResolver resolver, final @NotNull ReqInputPsiProcessingContext context)
      throws PsiProcessingException {

//    ReqParams fieldParams = parseReqParams(psi.getReqParamList(), op.params(), resolver, context);

//    Annotations fieldAnnotations = parseAnnotations(psi.getReqAnnotationList(), context);

    @NotNull UrlReqInputVarProjection psiVarProjection = psi.getReqInputVarProjection();
    @NotNull ReqInputVarProjection varProjection =
        parseVarProjection(
            fieldType,
            op.varProjection(),
            psiVarProjection,
            resolver,
            context
        );

    ProjectionsParsingUtil.verifyData(fieldType, varProjection, psi, context);

    return new ReqInputFieldProjection(
//        fieldParams,
//        fieldAnnotations,
        varProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull ReqInputMapModelProjection parseMapModelProjection(
      @NotNull OpInputMapModelProjection op,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @Nullable List<ReqInputMapModelProjection> tails,
      @NotNull UrlReqInputMapModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqInputPsiProcessingContext context) throws PsiProcessingException {

    final UrlReqInputKeysProjection keysProjectionPsi = psi.getReqInputKeysProjection();

    final OpInputKeyProjection opKeyProjection = op.keyProjection();
    final List<ReqInputKeyProjection> keyProjections;

    if (keysProjectionPsi.getStar() == null) {
      if (opKeyProjection.presence() == OpKeyPresence.FORBIDDEN)
        throw new PsiProcessingException("Map keys are forbidden", keysProjectionPsi, context);

      final @NotNull Collection<UrlReqInputKeyProjection> keyProjectionsPsi =
          keysProjectionPsi.getReqInputKeyProjectionList();

      final int keysSize = keyProjectionsPsi.size();


      if (opKeyProjection.presence() == OpKeyPresence.FORBIDDEN) {
        if (keysSize > 0) context.addError("Map keys are forbidden", keysProjectionPsi);
        keyProjections = null;
      } else {
        keyProjections = new ArrayList<>(keysSize);

        for (final UrlReqInputKeyProjection keyProjectionPsi : keyProjectionsPsi) {
          try {
            final @NotNull UrlDatum keyValuePsi = keyProjectionPsi.getDatum();
            final @Nullable Datum keyValue =
                getDatum(keyValuePsi, op.type().keyType(), resolver, "Error processing map key:", context);

            if (keyValue == null)
              context.addError("Null keys are not allowed", keyValuePsi);
            else {
              keyProjections.add(
                  new ReqInputKeyProjection(
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
      if (opKeyProjection.presence() == OpKeyPresence.REQUIRED)
        context.addError("Map keys are required", keysProjectionPsi.getStar());

      keyProjections = null;
    }

    final @Nullable UrlReqInputVarProjection elementsVarProjectionPsi = psi.getReqInputVarProjection();
    final @NotNull ReqInputVarProjection elementsVarProjection;
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

    return new ReqInputMapModelProjection(
        op.type(),
        params,
        annotations,
        keyProjections,
        elementsVarProjection,
        tails,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull ReqInputListModelProjection parseListModelProjection(
      @NotNull OpInputListModelProjection op,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @Nullable List<ReqInputListModelProjection> tails,
      @NotNull UrlReqInputListModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqInputPsiProcessingContext context) throws PsiProcessingException {

    final @Nullable UrlReqInputVarProjection elementsVarProjectionPsi = psi.getReqInputVarProjection();

    final @NotNull ReqInputVarProjection elementsVarProjection;
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

    return new ReqInputListModelProjection(
        op.type(),
        params,
        annotations,
        elementsVarProjection,
        tails,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull ReqInputPrimitiveModelProjection parsePrimitiveModelProjection(
      @NotNull OpInputPrimitiveModelProjection op,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @Nullable List<ReqInputPrimitiveModelProjection> tails,
      @NotNull PsiElement locationPsi) {

    return new ReqInputPrimitiveModelProjection(
        op.type(),
        params,
        annotations,
        tails,
        EpigraphPsiUtil.getLocation(locationPsi)
    );

  }
}

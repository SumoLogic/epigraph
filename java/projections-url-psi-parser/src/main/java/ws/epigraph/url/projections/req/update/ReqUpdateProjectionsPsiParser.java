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

package ws.epigraph.url.projections.req.update;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Datum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ProjectionsParsingUtil;
import ws.epigraph.projections.op.input.*;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.projections.req.update.*;
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
public final class ReqUpdateProjectionsPsiParser {

  private ReqUpdateProjectionsPsiParser() {}
  
  public static @NotNull ReqUpdateVarProjection parseVarProjection(
      @NotNull DataTypeApi dataType,
      @NotNull OpInputVarProjection op,
      @NotNull UrlReqUpdateVarProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqUpdatePsiProcessingContext context) throws PsiProcessingException {

    final UrlReqUpdateNamedVarProjection namedVarProjection = psi.getReqUpdateNamedVarProjection();
    if (namedVarProjection == null) {
      final UrlReqUpdateUnnamedOrRefVarProjection unnamedOrRefVarProjection =
          psi.getReqUpdateUnnamedOrRefVarProjection();

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

      final @Nullable UrlReqUpdateUnnamedOrRefVarProjection unnamedOrRefVarProjection =
          namedVarProjection.getReqUpdateUnnamedOrRefVarProjection();

      if (unnamedOrRefVarProjection == null)
        throw new PsiProcessingException(
            String.format("Incomplete var projection '%s' definition", projectionName),
            psi,
            context.errors()
        );

      final ReqUpdateVarProjection reference = context.varReferenceContext()
          .varReference(dataType.type(), projectionName, false, EpigraphPsiUtil.getLocation(psi));

      final ReqUpdateVarProjection value = parseUnnamedOrRefVarProjection(
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

  public static @NotNull ReqUpdateVarProjection parseUnnamedOrRefVarProjection(
      final @NotNull DataTypeApi dataType,
      final @NotNull OpInputVarProjection op,
      final @NotNull UrlReqUpdateUnnamedOrRefVarProjection psi,
      final @NotNull TypesResolver resolver,
      final @NotNull ReqUpdatePsiProcessingContext context) throws PsiProcessingException {

    final UrlReqUpdateVarProjectionRef varProjectionRef = psi.getReqUpdateVarProjectionRef();
    if (varProjectionRef == null) {
      // usual var projection
      final UrlReqUpdateUnnamedVarProjection unnamedVarProjection = psi.getReqUpdateUnnamedVarProjection();
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

  public static @NotNull ReqUpdateVarProjection parseUnnamedVarProjection(
      @NotNull DataTypeApi dataType,
      @NotNull OpInputVarProjection op,
      @NotNull UrlReqUpdateUnnamedVarProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqUpdatePsiProcessingContext context) throws PsiProcessingException {


    final TypeApi type = dataType.type();
    final LinkedHashMap<String, ReqUpdateTagProjectionEntry> tagProjections;

    @Nullable UrlReqUpdateSingleTagProjection singleTagProjectionPsi = psi.getReqUpdateSingleTagProjection();
    final @NotNull TypesResolver subResolver = addTypeNamespace(dataType.type(), resolver);

    if (singleTagProjectionPsi == null) {
      @Nullable UrlReqUpdateMultiTagProjection multiTagProjection = psi.getReqUpdateMultiTagProjection();
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
        @NotNull OpInputTagProjectionEntry opTagProjection =
            getTagProjection(tag.name(), op, tagLocation, context);

        @NotNull OpInputModelProjection<?, ?, ?, ?> opModelProjection = opTagProjection.projection();
        @NotNull UrlReqUpdateModelProjection modelProjectionPsi = singleTagProjectionPsi.getReqUpdateModelProjection();

        final ReqUpdateModelProjection<?, ?, ?> parsedModelProjection = parseModelProjection(
            opModelProjection,
            singleTagProjectionPsi.getPlus() != null,
            parseReqParams(singleTagProjectionPsi.getReqParamList(), opModelProjection.params(), subResolver, context),
            parseAnnotations(singleTagProjectionPsi.getReqAnnotationList(), context),
            modelProjectionPsi,
            subResolver,
            context
        );

        tagProjections.put(
            tag.name(),
            new ReqUpdateTagProjectionEntry(
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
        context.addError(
            String.format("Required tag '%s' is missing", entry.getKey()), psi
        );
    }

    final List<ReqUpdateVarProjection> tails =
        parseTails(dataType, op, psi.getReqUpdateVarPolymorphicTail(), subResolver, context);

    try {
      return new ReqUpdateVarProjection(
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

  private static @NotNull PsiElement getSingleTagLocation(final @NotNull UrlReqUpdateSingleTagProjection singleTagProjectionPsi) {
    PsiElement tagLocation = singleTagProjectionPsi;
    if (tagLocation.getText().isEmpty()) {
      final @Nullable UrlReqUpdateFieldProjectionEntry fieldProjectionPsi =
          PsiTreeUtil.getParentOfType(tagLocation, UrlReqUpdateFieldProjectionEntry.class);
      if (fieldProjectionPsi != null) {
        tagLocation = fieldProjectionPsi.getQid();
      }
    }
    return tagLocation;
  }

  private static @NotNull LinkedHashMap<String, ReqUpdateTagProjectionEntry> parseMultiTagProjection(
      @NotNull DataTypeApi dataType,
      @NotNull OpInputVarProjection op,
      @NotNull UrlReqUpdateMultiTagProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqUpdatePsiProcessingContext context) throws PsiProcessingException {

    if (!(dataType.type().equals(op.type())))
      throw new PsiProcessingException(
          String.format("Inconsistent arguments. data type: '%s', op type: '%s'", dataType.name(), op.type().name()),
          psi,
          context
      );

    final @NotNull TypesResolver subResolver = addTypeNamespace(dataType.type(), typesResolver);

    final LinkedHashMap<String, ReqUpdateTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    // parse list of tags
    @NotNull Iterable<UrlReqUpdateMultiTagProjectionItem> tagProjectionPsiList =
        psi.getReqUpdateMultiTagProjectionItemList();

    for (UrlReqUpdateMultiTagProjectionItem tagProjectionPsi : tagProjectionPsiList) {
      try {
        @NotNull TagApi tag =
            UrlProjectionsPsiParserUtil.getTag(tagProjectionPsi.getTagName(), op, tagProjectionPsi, context);
        @NotNull OpInputTagProjectionEntry opTag = getTagProjection(tag.name(), op, tagProjectionPsi, context);

        OpInputModelProjection<?, ?, ?, ?> opTagProjection = opTag.projection();

        @NotNull UrlReqUpdateModelProjection modelProjection = tagProjectionPsi.getReqUpdateModelProjection();

        final ReqUpdateModelProjection<?, ?, ?> parsedModelProjection = parseModelProjection(
            opTagProjection,
            tagProjectionPsi.getPlus() != null,
            parseReqParams(tagProjectionPsi.getReqParamList(), opTagProjection.params(), subResolver, context),
            parseAnnotations(tagProjectionPsi.getReqAnnotationList(), context),
            modelProjection, subResolver, context
        );

        tagProjections.put(
            tag.name(),
            new ReqUpdateTagProjectionEntry(
                tag,
                parsedModelProjection,
                EpigraphPsiUtil.getLocation(tagProjectionPsi)
            )
        );
      } catch (PsiProcessingException e) { context.addException(e); }
    }

    return tagProjections;
  }

  private static @NotNull ReqUpdateVarProjection getDefaultVarProjection(
      final @NotNull TypeApi type,
      final @NotNull PsiElement psi) {
    return new ReqUpdateVarProjection(
        type,
        Collections.emptyMap(),
        true,
        null,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  private static @Nullable List<ReqUpdateVarProjection> parseTails(
      @NotNull DataTypeApi dataType,
      @NotNull OpInputVarProjection op,
      @Nullable UrlReqUpdateVarPolymorphicTail tailPsi,
      @NotNull TypesResolver resolver,
      @NotNull ReqUpdatePsiProcessingContext context) throws PsiProcessingException {

    final List<ReqUpdateVarProjection> tails;

    @NotNull TypesResolver subResolver = addTypeNamespace(dataType.type(), resolver);

    if (tailPsi == null) tails = null;
    else {
      tails = new ArrayList<>();

      @Nullable UrlReqUpdateVarSingleTail singleTail = tailPsi.getReqUpdateVarSingleTail();
      if (singleTail == null) {
        @Nullable UrlReqUpdateVarMultiTail multiTail = tailPsi.getReqUpdateVarMultiTail();
        assert multiTail != null;
        TypeApi prevTailType = null;

        for (UrlReqUpdateVarMultiTailItem tailItem : multiTail.getReqUpdateVarMultiTailItemList()) {
          try {
            if (prevTailType != null)
              subResolver = addTypeNamespace(prevTailType, resolver);

            @NotNull UrlTypeRef tailTypeRef = tailItem.getTypeRef();
            @NotNull UrlReqUpdateVarProjection psiTailProjection = tailItem.getReqUpdateVarProjection();
            @NotNull ReqUpdateVarProjection tailProjection =
                buildTailProjection(dataType, op, tailTypeRef, psiTailProjection, subResolver, context);
            tails.add(tailProjection);

            prevTailType = tailProjection.type();
          } catch (PsiProcessingException e) { context.addException(e); }
        }
      } else {
        @NotNull UrlTypeRef tailTypeRef = singleTail.getTypeRef();
        @NotNull UrlReqUpdateVarProjection psiTailProjection = singleTail.getReqUpdateVarProjection();
        @NotNull ReqUpdateVarProjection tailProjection =
            buildTailProjection(dataType, op, tailTypeRef, psiTailProjection, subResolver, context);
        tails.add(tailProjection);
      }

    }

    return tails;
  }

  private static @NotNull ReqUpdateVarProjection buildTailProjection(
      @NotNull DataTypeApi dataType,
      @NotNull OpInputVarProjection op,
      @NotNull UrlTypeRef tailTypeRefPsi,
      @NotNull UrlReqUpdateVarProjection tailProjectionPsi,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqUpdatePsiProcessingContext context) throws PsiProcessingException {

    @NotNull TypeRef tailTypeRef = TypeRefs.fromPsi(tailTypeRefPsi, context);
    @NotNull UnionTypeApi tailType = getUnionType(tailTypeRef, typesResolver, tailTypeRefPsi, context);

    @Nullable OpInputVarProjection opTail = ProjectionsParsingUtil.getTail(op, tailType, tailTypeRefPsi, context);

    return parseVarProjection(
        tailType.dataType(dataType.defaultTag()),
        opTail,
        tailProjectionPsi,
        typesResolver,
        context
    );
  }

  public static @NotNull ReqUpdateModelProjection<?, ?, ?> parseModelProjection(
      @NotNull OpInputModelProjection<?, ?, ?, ?> op,
      boolean update,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull UrlReqUpdateModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqUpdatePsiProcessingContext context) throws PsiProcessingException {

    return parseModelProjection(
        ReqUpdateModelProjection.class,
        op,
        update,
        params,
        annotations,
        psi,
        resolver,
        context
    );

  }

  @SuppressWarnings("unchecked")
  public static <MP extends ReqUpdateModelProjection<?, ?, ?>> @NotNull MP parseModelProjection(
      @NotNull Class<MP> modelClass,
      @NotNull OpInputModelProjection<?, ?, ?, ?> op,
      boolean update,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull UrlReqUpdateModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqUpdatePsiProcessingContext context) throws PsiProcessingException {

    DatumTypeApi model = op.type();
    final @NotNull TypesResolver subResolver = addTypeNamespace(model, resolver);

    switch (model.kind()) {
      case RECORD:
        assert modelClass.isAssignableFrom(ReqUpdateRecordModelProjection.class);
        final OpInputRecordModelProjection opRecord = (OpInputRecordModelProjection) op;

        @Nullable UrlReqUpdateRecordModelProjection recordModelProjectionPsi =
            psi.getReqUpdateRecordModelProjection();

        if (recordModelProjectionPsi == null) {
          checkModelPsi(psi, TypeKind.RECORD, context);
          return (MP) createDefaultModelProjection(model, update, opRecord, params, annotations, psi, context);
        }

        ensureModelKind(findProjectionKind(psi), TypeKind.RECORD, psi, context);

        return (MP) parseRecordModelProjection(
            opRecord,
            update,
            params,
            annotations,
            parseModelTails(
                ReqUpdateRecordModelProjection.class,
                op,
                psi.getReqUpdateModelPolymorphicTail(),
                subResolver,
                context
            ),
            recordModelProjectionPsi,
            subResolver,
            context
        );

      case MAP:
        assert modelClass.isAssignableFrom(ReqUpdateMapModelProjection.class);
        final OpInputMapModelProjection opMap = (OpInputMapModelProjection) op;
        @Nullable UrlReqUpdateMapModelProjection mapModelProjectionPsi = psi.getReqUpdateMapModelProjection();

        if (mapModelProjectionPsi == null) {
          checkModelPsi(psi, TypeKind.MAP, context);
          return (MP) createDefaultModelProjection(model, update, opMap, params, annotations, psi, context);
        }

        ensureModelKind(findProjectionKind(psi), TypeKind.MAP, psi, context);

        return (MP) parseMapModelProjection(
            opMap,
            update,
            params,
            annotations,
            parseModelTails(
                ReqUpdateMapModelProjection.class,
                op,
                psi.getReqUpdateModelPolymorphicTail(),
                subResolver,
                context
            ),
            mapModelProjectionPsi,
            subResolver,
            context
        );

      case LIST:
        assert modelClass.isAssignableFrom(ReqUpdateListModelProjection.class);
        final OpInputListModelProjection opList = (OpInputListModelProjection) op;
        @Nullable UrlReqUpdateListModelProjection listModelProjectionPsi =
            psi.getReqUpdateListModelProjection();

        if (listModelProjectionPsi == null) {
          checkModelPsi(psi, TypeKind.LIST, context);
          return (MP) createDefaultModelProjection(model, update, opList, params, annotations, psi, context);
        }

        ensureModelKind(findProjectionKind(psi), TypeKind.LIST, psi, context);

        return (MP) parseListModelProjection(
            opList,
            update,
            params,
            annotations,
            parseModelTails(
                ReqUpdateListModelProjection.class,
                op,
                psi.getReqUpdateModelPolymorphicTail(),
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
        assert modelClass.isAssignableFrom(ReqUpdatePrimitiveModelProjection.class);
        return (MP) parsePrimitiveModelProjection(
            (OpInputPrimitiveModelProjection) op,
            params,
            annotations,
            parseModelTails(
                ReqUpdatePrimitiveModelProjection.class,
                op,
                psi.getReqUpdateModelPolymorphicTail(),
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
      @NotNull UrlReqUpdateModelProjection psi,
      @NotNull TypeKind expectedKind,
      @NotNull ReqUpdatePsiProcessingContext context) {

    TypeKind actualKind = null;

    if (psi.getReqUpdateRecordModelProjection() != null) actualKind = TypeKind.RECORD;
    else if (psi.getReqUpdateMapModelProjection() != null) actualKind = TypeKind.MAP;
    else if (psi.getReqUpdateListModelProjection() != null) actualKind = TypeKind.LIST;

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
  private static <MP extends ReqUpdateModelProjection<?, ?, ?>>
  @Nullable List<MP> parseModelTails(
      @NotNull Class<MP> modelClass,
      @NotNull OpInputModelProjection<?, ?, ?, ?> op,
      @Nullable UrlReqUpdateModelPolymorphicTail tailPsi,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqUpdatePsiProcessingContext context) throws PsiProcessingException {

    if (tailPsi == null) return null;
    else {
      List<MP> tails = new ArrayList<>();

      final UrlReqUpdateModelSingleTail singleTailPsi = tailPsi.getReqUpdateModelSingleTail();
      if (singleTailPsi == null) {
        final UrlReqUpdateModelMultiTail multiTailPsi = tailPsi.getReqUpdateModelMultiTail();
        assert multiTailPsi != null;
        for (UrlReqUpdateModelMultiTailItem tailItemPsi : multiTailPsi.getReqUpdateModelMultiTailItemList()) {
          try {
            tails.add(
                buildModelTailProjection(
                    modelClass,
                    op,
                    tailItemPsi.getPlus() != null,
                    tailItemPsi.getTypeRef(),
                    tailItemPsi.getReqUpdateModelProjection(),
                    tailItemPsi.getReqParamList(),
                    tailItemPsi.getReqAnnotationList(),
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
                singleTailPsi.getReqUpdateModelProjection(),
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

  private static <MP extends ReqUpdateModelProjection<?, ?, ?>>
  @NotNull MP buildModelTailProjection(
      @NotNull Class<MP> modelClass,
      @NotNull OpInputModelProjection<?, ?, ?, ?> op,
      boolean update,
      @NotNull UrlTypeRef tailTypeRefPsi,
      @NotNull UrlReqUpdateModelProjection modelProjectionPsi,
      @NotNull List<UrlReqParam> modelParamsList,
      @NotNull List<UrlReqAnnotation> modelAnnotationsList,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqUpdatePsiProcessingContext context) throws PsiProcessingException {

    @NotNull TypeRef tailTypeRef = TypeRefs.fromPsi(tailTypeRefPsi, context);
    @NotNull DatumTypeApi tailType = getDatumType(tailTypeRef, typesResolver, tailTypeRefPsi, context);

    final @NotNull OpInputModelProjection<?, ?, ?, ?> opTail =
        ProjectionsParsingUtil.getTail(op, tailType, tailTypeRefPsi, context);

    return parseModelProjection(
        modelClass,
        opTail,
        update,
        parseReqParams(modelParamsList, op.params(), typesResolver, context),
        parseAnnotations(modelAnnotationsList, context),
        modelProjectionPsi,
        typesResolver,
        context
    );
  }

  private static @NotNull ReqUpdateModelProjection<?, ?, ?> createDefaultModelProjection(
      @NotNull DatumTypeApi type,
      boolean update,
      @NotNull OpInputModelProjection<?, ?, ?, ?> op,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull PsiElement locationPsi,
      @NotNull ReqUpdatePsiProcessingContext context) throws PsiProcessingException {

    @NotNull TextLocation location = EpigraphPsiUtil.getLocation(locationPsi);

    switch (type.kind()) {
      case RECORD:
        OpInputRecordModelProjection opRecord = (OpInputRecordModelProjection) op;
        final Map<String, OpInputFieldProjectionEntry> opFields = opRecord.fieldProjections();

        final @NotNull Map<String, ReqUpdateFieldProjectionEntry> fields;

        fields = opFields.isEmpty() ? Collections.emptyMap() : new LinkedHashMap<>();

        return new ReqUpdateRecordModelProjection(
            (RecordTypeApi) type,
            update,
            params,
            annotations,
            fields,
            null,
            location
        );
      case MAP:
        OpInputMapModelProjection opMap = (OpInputMapModelProjection) op;

        MapTypeApi mapType = (MapTypeApi) type;
        final ReqUpdateVarProjection valueVarProjection = createDefaultVarProjection(
            mapType.valueType(),
            opMap.itemsProjection(),
            update,
            locationPsi,
            context
        );

        return new ReqUpdateMapModelProjection(
            mapType,
            update,
            params,
            annotations,
            false,
            Collections.emptyList(),
            valueVarProjection,
            null,
            location
        );
      case LIST:
        OpInputListModelProjection opList = (OpInputListModelProjection) op;
        ListTypeApi listType = (ListTypeApi) type;

        final ReqUpdateVarProjection itemVarProjection = createDefaultVarProjection(
            listType.elementType(),
            opList.itemsProjection(),
            update,
            locationPsi,
            context
        );

        return new ReqUpdateListModelProjection(
            listType,
            update,
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
        return new ReqUpdatePrimitiveModelProjection(
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

  private static @NotNull ReqUpdateVarProjection createDefaultVarProjection(
      @NotNull DataTypeApi type,
      @NotNull OpInputVarProjection op,
      boolean required,
      @NotNull PsiElement locationPsi,
      @NotNull ReqUpdatePsiProcessingContext context) throws PsiProcessingException {

    return createDefaultVarProjection(type.type(), op, required, locationPsi, context);
  }

  private static @NotNull ReqUpdateVarProjection createDefaultVarProjection(
      @NotNull TypeApi type,
      @NotNull OpInputVarProjection op,
      boolean required,
      @NotNull PsiElement locationPsi,
      @NotNull ReqUpdatePsiProcessingContext context) throws PsiProcessingException {

    @Nullable TagApi defaultTag = findSelfTag(type, op, locationPsi, context);
    List<TagApi> tags = defaultTag == null ?
                        Collections.emptyList() :
                        Collections.singletonList(defaultTag);
    return createDefaultVarProjection(type, tags, op, required, locationPsi, context);
  }

  /**
   * Creates default var projection with explicitly specified list of tags
   */
  private static ReqUpdateVarProjection createDefaultVarProjection(
      @NotNull TypeApi type,
      @NotNull Iterable<TagApi> tags,
      @NotNull OpInputVarProjection op,
      boolean required,
      @NotNull PsiElement locationPsi,
      @NotNull ReqUpdatePsiProcessingContext context) throws PsiProcessingException {

    LinkedHashMap<String, ReqUpdateTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    for (TagApi tag : tags) {
      final OpInputTagProjectionEntry opInputTagProjection = op.tagProjections().get(tag.name());
      if (opInputTagProjection != null) {
        tagProjections.put(
            tag.name(),
            new ReqUpdateTagProjectionEntry(
                tag,
                createDefaultModelProjection(
                    tag.type(),
                    required,
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

    return new ReqUpdateVarProjection(
        type,
        tagProjections,
        op.parenthesized() || tagProjections.size() != 1,
        null,
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

  private static @Nullable TypeKind findProjectionKind(@NotNull UrlReqUpdateModelProjection psi) {
    if (psi.getReqUpdateRecordModelProjection() != null) return TypeKind.RECORD;
    if (psi.getReqUpdateMapModelProjection() != null) return TypeKind.MAP;
    if (psi.getReqUpdateListModelProjection() != null) return TypeKind.LIST;
    return null;
  }

  public static @NotNull ReqUpdateRecordModelProjection parseRecordModelProjection(
      @NotNull OpInputRecordModelProjection op,
      boolean update,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @Nullable List<ReqUpdateRecordModelProjection> tails,
      @NotNull UrlReqUpdateRecordModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqUpdatePsiProcessingContext context) {

    final Map<String, ReqUpdateFieldProjectionEntry> fieldProjections = new LinkedHashMap<>();

    for (final UrlReqUpdateFieldProjectionEntry entryPsi : psi.getReqUpdateFieldProjectionEntryList()) {
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
          final @NotNull UrlReqUpdateFieldProjection fieldProjectionPsi = entryPsi.getReqUpdateFieldProjection();
          final @NotNull DataTypeApi fieldType = field.dataType();

          final ReqUpdateFieldProjection fieldProjection =
              parseFieldProjection(
                  fieldType,
                  entryPsi.getPlus() != null,
                  opFieldProjection,
                  fieldProjectionPsi,
                  resolver,
                  context
              );

          fieldProjections.put(
              fieldName,
              new ReqUpdateFieldProjectionEntry(
                  field,
                  fieldProjection,
                  EpigraphPsiUtil.getLocation(fieldProjectionPsi)
              )
          );
        } catch (PsiProcessingException e) { context.addException(e); }
      }
    }

    // check that all required fields are specified
    for (final Map.Entry<String, OpInputFieldProjectionEntry> entry : op.fieldProjections().entrySet()) {
      if (entry.getValue().fieldProjection().required() && !fieldProjections.containsKey(entry.getKey())) {
        context.addError(String.format("Required field '%s' is missing", entry.getKey()), psi);
      }
    }

    return new ReqUpdateRecordModelProjection(
        op.type(),
        update,
        params,
        annotations,
        fieldProjections,
        tails,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull ReqUpdateFieldProjection parseFieldProjection(
      final DataTypeApi fieldType,
      boolean update,
      final @NotNull OpInputFieldProjection op,
      final @NotNull UrlReqUpdateFieldProjection psi,
      final @NotNull TypesResolver resolver, final @NotNull ReqUpdatePsiProcessingContext context)
      throws PsiProcessingException {

//    ReqParams fieldParams = parseReqParams(psi.getReqParamList(), op.params(), resolver, context);

//    Annotations fieldAnnotations = parseAnnotations(psi.getReqAnnotationList(), context);

    @NotNull UrlReqUpdateVarProjection psiVarProjection = psi.getReqUpdateVarProjection();
    @NotNull ReqUpdateVarProjection varProjection =
        parseVarProjection(
            fieldType,
            op.varProjection(),
            psiVarProjection,
            resolver,
            context
        );

    ProjectionsParsingUtil.verifyData(fieldType, varProjection, psi, context);

    return new ReqUpdateFieldProjection(
//        fieldParams,
//        fieldAnnotations,
        varProjection,
        update,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull ReqUpdateMapModelProjection parseMapModelProjection(
      @NotNull OpInputMapModelProjection op,
      boolean update,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @Nullable List<ReqUpdateMapModelProjection> tails,
      @NotNull UrlReqUpdateMapModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqUpdatePsiProcessingContext context) throws PsiProcessingException {

    final @NotNull MapTypeApi model = op.type();

    final @NotNull Collection<UrlReqUpdateKeyProjection> keysPsi =
        psi.getReqUpdateKeysProjection().getReqUpdateKeyProjectionList();

    List<ReqUpdateKeyProjection> keys = new ArrayList<>(keysPsi.size());

    for (final UrlReqUpdateKeyProjection keyPsi : keysPsi) {
      try {
        final @Nullable Datum keyValue =
            getDatum(keyPsi.getDatum(), model.keyType(), resolver, "Error processing map key: ", context);

        if (keyValue == null) {
          context.addError("Null keys are not allowed", keyPsi);
        } else {
          keys.add(
              new ReqUpdateKeyProjection(
                  keyValue,
                  parseReqParams(keyPsi.getReqParamList(), op.keyProjection().params(), resolver, context),
                  parseAnnotations(keyPsi.getReqAnnotationList(), context),
                  EpigraphPsiUtil.getLocation(keyPsi)
              )
          );
        }
      } catch (PsiProcessingException e) { context.addException(e); }
    }

    final @Nullable UrlReqUpdateVarProjection elementsVarProjectionPsi = psi.getReqUpdateVarProjection();
    final @NotNull ReqUpdateVarProjection elementsVarProjection;

    if (elementsVarProjectionPsi == null) {
      final @NotNull TypeApi type = model.valueType().type();
      elementsVarProjection = getDefaultVarProjection(type, psi);
    } else {
      elementsVarProjection = parseVarProjection(
          model.valueType(),
          op.itemsProjection(),
          elementsVarProjectionPsi,
          resolver,
          context
      );
    }

    return new ReqUpdateMapModelProjection(
        model,
        update,
        params,
        annotations,
        psi.getReqUpdateKeysProjection().getPlus() != null,
        keys,
        elementsVarProjection,
        tails,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull ReqUpdateListModelProjection parseListModelProjection(
      @NotNull OpInputListModelProjection op,
      boolean update,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @Nullable List<ReqUpdateListModelProjection> tails,
      @NotNull UrlReqUpdateListModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqUpdatePsiProcessingContext context) throws PsiProcessingException {

    final @Nullable UrlReqUpdateVarProjection elementsVarProjectionPsi = psi.getReqUpdateVarProjection();

    final @NotNull ReqUpdateVarProjection elementsVarProjection;
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

    return new ReqUpdateListModelProjection(
        op.type(),
        update,
        params,
        annotations,
        elementsVarProjection,
        tails,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull ReqUpdatePrimitiveModelProjection parsePrimitiveModelProjection(
      @NotNull OpInputPrimitiveModelProjection op,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @Nullable List<ReqUpdatePrimitiveModelProjection> tails,
      @NotNull PsiElement locationPsi) {

    return new ReqUpdatePrimitiveModelProjection(
        op.type(),
        params,
        annotations,
        tails,
        EpigraphPsiUtil.getLocation(locationPsi)
    );

  }
}

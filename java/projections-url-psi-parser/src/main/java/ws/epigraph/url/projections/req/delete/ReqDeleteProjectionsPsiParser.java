/*
 * Copyright 2016 Sumo Logic
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Datum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.op.delete.*;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.projections.req.delete.*;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypeRef;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.*;
import ws.epigraph.url.TypeRefs;
import ws.epigraph.url.parser.psi.*;

import java.util.*;

import static ws.epigraph.projections.ProjectionsParsingUtil.*;
import static ws.epigraph.url.projections.UrlProjectionsPsiParserUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqDeleteProjectionsPsiParser {

  @NotNull
  public static ReqDeleteVarProjection parseVarProjection(
      @NotNull DataType dataType,
      @NotNull OpDeleteVarProjection op,
      @NotNull UrlReqDeleteVarProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {


    final Type type = dataType.type;
    final LinkedHashMap<String, ReqDeleteTagProjectionEntry> tagProjections;

    @Nullable UrlReqDeleteSingleTagProjection singleTagProjectionPsi = psi.getReqDeleteSingleTagProjection();
    @NotNull final TypesResolver subResolver = addTypeNamespace(dataType.type, resolver);

    if (singleTagProjectionPsi != null) {
      // try to improve error reporting: singleTagProjectionPsi may be empty
      PsiElement tagLocation = getSingleTagLocation(singleTagProjectionPsi);

      tagProjections = new LinkedHashMap<>();

      final ReqDeleteModelProjection<?, ?> parsedModelProjection;
      @Nullable final UrlTagName tagNamePsi = singleTagProjectionPsi.getTagName();

      @NotNull final Type.Tag tag;

      tag = findTagOrDefaultTag(type, tagNamePsi, op, tagLocation, errors);
      @NotNull OpDeleteTagProjectionEntry opTagProjection =
          findTagProjection(tag.name(), op, tagLocation, errors);

      @NotNull OpDeleteModelProjection<?, ?> opModelProjection = opTagProjection.projection();
      @NotNull UrlReqDeleteModelProjection modelProjectionPsi = singleTagProjectionPsi.getReqDeleteModelProjection();

      parsedModelProjection = parseModelProjection(
          opModelProjection,
          parseReqParams(singleTagProjectionPsi.getReqParamList(), opModelProjection.params(), subResolver, errors),
          parseAnnotations(singleTagProjectionPsi.getReqAnnotationList(), errors),
          modelProjectionPsi,
          subResolver,
          errors
      );

      tagProjections.put(
          tag.name(),
          new ReqDeleteTagProjectionEntry(
              tag,
              parsedModelProjection,
              EpigraphPsiUtil.getLocation(tagLocation)
          )
      );

    } else {
      @Nullable UrlReqDeleteMultiTagProjection multiTagProjection = psi.getReqDeleteMultiTagProjection();
      assert multiTagProjection != null;
      tagProjections = parseMultiTagProjection(dataType, op, multiTagProjection, subResolver, errors);
    }

    final List<ReqDeleteVarProjection> tails =
        parseTails(dataType, op, psi.getReqDeleteVarPolymorphicTail(), subResolver, errors);

    return
        new ReqDeleteVarProjection(type, tagProjections, tails, EpigraphPsiUtil.getLocation(psi));
  }

  @NotNull
  private static PsiElement getSingleTagLocation(@NotNull final UrlReqDeleteSingleTagProjection singleTagProjectionPsi) {
    PsiElement tagLocation = singleTagProjectionPsi;
    if (tagLocation.getText().length() == 0) {
      @Nullable final UrlReqDeleteFieldProjectionEntry fieldProjectionPsi =
          PsiTreeUtil.getParentOfType(tagLocation, UrlReqDeleteFieldProjectionEntry.class);
      if (fieldProjectionPsi != null) {
        tagLocation = fieldProjectionPsi.getQid();
      }
    }
    return tagLocation;
  }

  @NotNull
  private static LinkedHashMap<String, ReqDeleteTagProjectionEntry> parseMultiTagProjection(
      @NotNull DataType dataType,
      @NotNull OpDeleteVarProjection op,
      @NotNull UrlReqDeleteMultiTagProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    if (!(dataType.type.equals(op.type())))
      throw new PsiProcessingException(
          String.format("Inconsistent arguments. data type: '%s', op type: '%s'", dataType.name, op.type().name()),
          psi,
          errors
      );

    @NotNull final TypesResolver subResolver = addTypeNamespace(dataType.type, typesResolver);

    final LinkedHashMap<String, ReqDeleteTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    // parse list of tags
    @NotNull List<UrlReqDeleteMultiTagProjectionItem> tagProjectionPsiList =
        psi.getReqDeleteMultiTagProjectionItemList();

    for (UrlReqDeleteMultiTagProjectionItem tagProjectionPsi : tagProjectionPsiList) {
      try {
        @NotNull Type.Tag tag = findTag(tagProjectionPsi.getTagName(), op, tagProjectionPsi, errors);
        @NotNull OpDeleteTagProjectionEntry opTag = findTagProjection(tag.name(), op, tagProjectionPsi, errors);

        OpDeleteModelProjection<?, ?> opTagProjection = opTag.projection();

        final ReqDeleteModelProjection<?, ?> parsedModelProjection;

        @NotNull UrlReqDeleteModelProjection modelProjection = tagProjectionPsi.getReqDeleteModelProjection();

        parsedModelProjection = parseModelProjection(
            opTagProjection,
            parseReqParams(tagProjectionPsi.getReqParamList(), opTagProjection.params(), subResolver, errors),
            parseAnnotations(tagProjectionPsi.getReqAnnotationList(), errors),
            modelProjection, subResolver, errors
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
        errors.add(e.toError());
      }
    }

    return tagProjections;
  }

  @NotNull
  private static ReqDeleteVarProjection getDefaultVarProjection(
      @NotNull final Type type,
      final @NotNull PsiElement psi) {
    return new ReqDeleteVarProjection(
        type,
        Collections.emptyMap(),
        null,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @Nullable
  private static List<ReqDeleteVarProjection> parseTails(
      @NotNull DataType dataType,
      @NotNull OpDeleteVarProjection op,
      @Nullable UrlReqDeleteVarPolymorphicTail tailPsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final List<ReqDeleteVarProjection> tails;

    @NotNull TypesResolver subResolver = addTypeNamespace(dataType.type, resolver);

    if (tailPsi != null) {

      tails = new ArrayList<>();

      @Nullable UrlReqDeleteVarSingleTail singleTail = tailPsi.getReqDeleteVarSingleTail();
      if (singleTail != null) {
        @NotNull UrlTypeRef tailTypeRef = singleTail.getTypeRef();
        @NotNull UrlReqDeleteVarProjection psiTailProjection = singleTail.getReqDeleteVarProjection();
        @NotNull ReqDeleteVarProjection tailProjection =
            buildTailProjection(dataType, op, tailTypeRef, psiTailProjection, subResolver, errors);
        tails.add(tailProjection);
      } else {
        @Nullable UrlReqDeleteVarMultiTail multiTail = tailPsi.getReqDeleteVarMultiTail();
        assert multiTail != null;
        Type prevTailType = null;

        for (UrlReqDeleteVarMultiTailItem tailItem : multiTail.getReqDeleteVarMultiTailItemList()) {
          try {
            if (prevTailType != null)
              subResolver = addTypeNamespace(prevTailType, resolver);

            @NotNull UrlTypeRef tailTypeRef = tailItem.getTypeRef();
            @NotNull UrlReqDeleteVarProjection psiTailProjection = tailItem.getReqDeleteVarProjection();
            @NotNull ReqDeleteVarProjection tailProjection =
                buildTailProjection(dataType, op, tailTypeRef, psiTailProjection, subResolver, errors);
            tails.add(tailProjection);

            prevTailType = tailProjection.type();
          } catch (PsiProcessingException e) {
            errors.add(e.toError());
          }
        }
      }

    } else tails = null;

    return tails;
  }

  @NotNull
  private static ReqDeleteVarProjection buildTailProjection(
      @NotNull DataType dataType,
      @NotNull OpDeleteVarProjection op,
      @NotNull UrlTypeRef tailTypeRefPsi,
      @NotNull UrlReqDeleteVarProjection tailProjectionPsi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull TypeRef tailTypeRef = TypeRefs.fromPsi(tailTypeRefPsi, errors);
    @NotNull Type tailType = getType(tailTypeRef, typesResolver, tailTypeRefPsi, errors);

    @Nullable OpDeleteVarProjection opTail = mergeOpTails(op, tailType);
    if (opTail == null)
      throw new PsiProcessingException(
          String.format("Polymorphic tail for type '%s' is not supported", tailType.name()),
          tailProjectionPsi,
          errors
      );

    return parseVarProjection(
        new DataType(tailType, dataType.defaultTag),
        opTail,
        tailProjectionPsi,
        typesResolver,
        errors
    );
  }

  @Nullable
  private static OpDeleteVarProjection mergeOpTails(@NotNull OpDeleteVarProjection op, @NotNull Type tailType) {
    List<OpDeleteVarProjection> opTails = op.polymorphicTails();
    if (opTails == null) return null;
    // TODO a deep merge of op projections wrt to tailType is needed here, probably moved into a separate class
    // we simply look for the first fully matching tail for now
    // algo should be: DFS on tails, look for exact match on tailType
    // if found: merge all op tails up the stack into one mega-op-var-projection: squash all tags/fields/params together. Should be OK since they all are supertypes of tailType
    // else null

    for (OpDeleteVarProjection opTail : opTails) {
      if (opTail.type().equals(tailType)) return opTail;
    }

    return null;
  }

  @NotNull
  public static ReqDeleteModelProjection<?, ?> parseModelProjection(
      @NotNull OpDeleteModelProjection<?, ?> op,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull UrlReqDeleteModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    DatumType model = op.model();
    @NotNull final TypesResolver subResolver = addTypeNamespace(model, resolver);

    switch (model.kind()) {
      case RECORD:
        final OpDeleteRecordModelProjection opRecord = (OpDeleteRecordModelProjection) op;

        @Nullable UrlReqDeleteRecordModelProjection recordModelProjectionPsi =
            psi.getReqDeleteRecordModelProjection();

        if (recordModelProjectionPsi == null)
          return createDefaultModelProjection(model, opRecord, params, annotations, psi, errors);

        ensureModelKind(findProjectionKind(psi), TypeKind.RECORD, psi, errors);

        return parseRecordModelProjection(
            opRecord,
            params,
            annotations,
            recordModelProjectionPsi,
            subResolver,
            errors
        );

      case MAP:
        final OpDeleteMapModelProjection opMap = (OpDeleteMapModelProjection) op;
        @Nullable UrlReqDeleteMapModelProjection mapModelProjectionPsi = psi.getReqDeleteMapModelProjection();

        if (mapModelProjectionPsi == null)
          return createDefaultModelProjection(model, opMap, params, annotations, psi, errors);

        ensureModelKind(findProjectionKind(psi), TypeKind.MAP, psi, errors);

        return parseMapModelProjection(
            opMap,
            params,
            annotations,
            mapModelProjectionPsi,
            subResolver,
            errors
        );

      case LIST:
        final OpDeleteListModelProjection opList = (OpDeleteListModelProjection) op;
        @Nullable UrlReqDeleteListModelProjection listModelProjectionPsi =
            psi.getReqDeleteListModelProjection();

        if (listModelProjectionPsi == null)
          return createDefaultModelProjection(model, opList, params, annotations, psi, errors);

        ensureModelKind(findProjectionKind(psi), TypeKind.LIST, psi, errors);

        return parseListModelProjection(
            opList,
            params,
            annotations,
            listModelProjectionPsi,
            subResolver,
            errors
        );

      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + model.kind(), psi, errors);

      case PRIMITIVE:
        return parsePrimitiveModelProjection(
            (OpDeletePrimitiveModelProjection) op,
            params,
            annotations,
            psi
        );

      case UNION:
        throw new PsiProcessingException("Unsupported type kind: " + model.kind(), psi, errors);

      default:
        throw new PsiProcessingException("Unknown type kind: " + model.kind(), psi, errors);
    }

  }

  @NotNull
  private static ReqDeleteModelProjection<?, ?> createDefaultModelProjection(
      @NotNull DatumType type,
      @NotNull OpDeleteModelProjection<?, ?> op,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull TextLocation location = EpigraphPsiUtil.getLocation(locationPsi);

    switch (type.kind()) {
      case RECORD:
        OpDeleteRecordModelProjection opRecord = (OpDeleteRecordModelProjection) op;
        final Map<String, OpDeleteFieldProjectionEntry> opFields = opRecord.fieldProjections();

        @NotNull final Map<String, ReqDeleteFieldProjectionEntry> fields;

        if (opFields.isEmpty()) {
          fields = Collections.emptyMap();
        } else {
          fields = new LinkedHashMap<>();

        }

        return new ReqDeleteRecordModelProjection(
            (RecordType) type,
            params,
            annotations,
            fields,
            location
        );
      case MAP:
        OpDeleteMapModelProjection opMap = (OpDeleteMapModelProjection) op;

        MapType mapType = (MapType) type;
        final ReqDeleteVarProjection valueVarProjection = createDefaultVarProjection(
            mapType.valueType(),
            opMap.itemsProjection(),
            locationPsi,
            errors
        );

        return new ReqDeleteMapModelProjection(
            mapType,
            params,
            annotations,
            null,
            valueVarProjection,
            location
        );
      case LIST:
        OpDeleteListModelProjection opList = (OpDeleteListModelProjection) op;
        ListType listType = (ListType) type;

        final ReqDeleteVarProjection itemVarProjection = createDefaultVarProjection(
            listType.elementType(),
            opList.itemsProjection(),
            locationPsi,
            errors
        );

        return new ReqDeleteListModelProjection(
            listType,
            params,
            annotations,
            itemVarProjection,
            location
        );
      case UNION:
        throw new PsiProcessingException(
            "Was expecting to get datum model kind, got: " + type.kind(),
            locationPsi,
            errors
        );
      case ENUM:
        // todo
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), locationPsi, errors);
      case PRIMITIVE:
        return new ReqDeletePrimitiveModelProjection(
            (PrimitiveType) type,
            params,
            annotations,
            location
        );
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), locationPsi, errors);
    }
  }

  @NotNull
  private static ReqDeleteVarProjection createDefaultVarProjection(
      @NotNull DataType type,
      @NotNull OpDeleteVarProjection op,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    return createDefaultVarProjection(type.type, op, locationPsi, errors);
  }

  @NotNull
  private static ReqDeleteVarProjection createDefaultVarProjection(
      @NotNull Type type,
      @NotNull OpDeleteVarProjection op,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    Type.@Nullable Tag defaultTag = findDefaultTag(type, op, locationPsi, errors);
    List<Type.Tag> tags = defaultTag == null ?
                          Collections.emptyList() :
                          Collections.singletonList(defaultTag);
    return createDefaultVarProjection(type, tags, op, locationPsi, errors);
  }

  /**
   * Creates default var projection with explicitly specified list of tags
   */
  private static ReqDeleteVarProjection createDefaultVarProjection(
      @NotNull Type type,
      @NotNull List<Type.Tag> tags,
      @NotNull OpDeleteVarProjection op,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    LinkedHashMap<String, ReqDeleteTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    for (Type.Tag tag : tags) {
      final OpDeleteTagProjectionEntry opDeleteTagProjection = op.tagProjections().get(tag.name());
      if (opDeleteTagProjection != null) {
        tagProjections.put(
            tag.name(),
            new ReqDeleteTagProjectionEntry(
                tag,
                createDefaultModelProjection(
                    tag.type,
                    opDeleteTagProjection.projection(),
                    ReqParams.EMPTY,
                    Annotations.EMPTY,
                    locationPsi,
                    errors
                ),
                EpigraphPsiUtil.getLocation(locationPsi)
            )
        );
      }
    }

    return new ReqDeleteVarProjection(
        type,
        tagProjections,
        null,
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

  @Nullable
  private static TypeKind findProjectionKind(@NotNull UrlReqDeleteModelProjection psi) {
    if (psi.getReqDeleteRecordModelProjection() != null) return TypeKind.RECORD;
    if (psi.getReqDeleteMapModelProjection() != null) return TypeKind.MAP;
    if (psi.getReqDeleteListModelProjection() != null) return TypeKind.LIST;
    return null;
  }

  @NotNull
  public static ReqDeleteRecordModelProjection parseRecordModelProjection(
      @NotNull OpDeleteRecordModelProjection op,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull UrlReqDeleteRecordModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final Map<String, ReqDeleteFieldProjectionEntry> fieldProjections = new LinkedHashMap<>();

    for (final UrlReqDeleteFieldProjectionEntry entryPsi : psi.getReqDeleteFieldProjectionEntryList()) {
      @NotNull final String fieldName = entryPsi.getQid().getCanonicalName();

      @Nullable final OpDeleteFieldProjectionEntry opFieldProjectionEntry = op.fieldProjection(fieldName);
      if (opFieldProjectionEntry == null) {
        errors.add(
            new PsiProcessingError(
                String.format(
                    "Field '%s' is not supported by the operation. Supported fields: {%s}",
                    fieldName,
                    String.join(", ", op.fieldProjections().keySet())
                ), entryPsi.getQid()
            )
        );
      } else {
        try {
          final RecordType.Field field = opFieldProjectionEntry.field();
          @NotNull final OpDeleteFieldProjection opFieldProjection = opFieldProjectionEntry.projection();
          @NotNull final UrlReqDeleteFieldProjection fieldProjectionPsi = entryPsi.getReqDeleteFieldProjection();
          @NotNull final DataType fieldType = field.dataType();

          final ReqDeleteFieldProjection fieldProjection =
              parseFieldProjection(
                  fieldType,
                  opFieldProjection,
                  fieldProjectionPsi,
                  resolver,
                  errors
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
          errors.add(e.toError());
        }
      }
    }

    return new ReqDeleteRecordModelProjection(
        op.model(),
        params,
        annotations,
        fieldProjections,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  public static ReqDeleteFieldProjection parseFieldProjection(
      final DataType fieldType,
      final @NotNull OpDeleteFieldProjection op,
      final @NotNull UrlReqDeleteFieldProjection psi,
      final @NotNull TypesResolver resolver, final @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    ReqParams fieldParams = parseReqParams(psi.getReqParamList(), op.params(), resolver, errors);

    Annotations fieldAnnotations = parseAnnotations(psi.getReqAnnotationList(), errors);

    @NotNull UrlReqDeleteVarProjection psiVarProjection = psi.getReqDeleteVarProjection();
    @NotNull ReqDeleteVarProjection varProjection =
        parseVarProjection(
            fieldType,
            op.projection(),
            psiVarProjection,
            resolver,
            errors
        );

    return new ReqDeleteFieldProjection(
        fieldParams,
        fieldAnnotations,
        varProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  public static ReqDeleteMapModelProjection parseMapModelProjection(
      @NotNull OpDeleteMapModelProjection op,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull UrlReqDeleteMapModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {


    final List<ReqDeleteKeyProjection> keyProjections;
    if (psi.getReqDeleteKeysProjection().getStar() != null) keyProjections = null;
    else {
      @NotNull final List<UrlReqDeleteKeyProjection> keyProjectionsPsi =
          psi.getReqDeleteKeysProjection().getReqDeleteKeyProjectionList();

      keyProjections = new ArrayList<>(keyProjectionsPsi.size());

      for (final UrlReqDeleteKeyProjection keyProjectionPsi : keyProjectionsPsi) {
        try {
          @NotNull final UrlDatum keyValuePsi = keyProjectionPsi.getDatum();
          final @Nullable Datum keyValue =
              getDatum(keyValuePsi, op.model().keyType(), resolver, "Error processing map key:", errors);

          if (keyValue == null) errors.add(new PsiProcessingError("Null keys are not allowed", keyValuePsi));
          else {
            keyProjections.add(
                new ReqDeleteKeyProjection(
                    keyValue,
                    parseReqParams(keyProjectionPsi.getReqParamList(), op.keyProjection().params(), resolver, errors),
                    parseAnnotations(keyProjectionPsi.getReqAnnotationList(), errors),
                    EpigraphPsiUtil.getLocation(keyProjectionPsi)
                )
            );
          }
        } catch (PsiProcessingException e) {
          errors.add(e.toError());
        }
      }
    }

    @Nullable final UrlReqDeleteVarProjection elementsVarProjectionPsi = psi.getReqDeleteVarProjection();
    @NotNull final ReqDeleteVarProjection elementsVarProjection;
    if (elementsVarProjectionPsi == null) {
      @NotNull final Type type = op.model().valueType.type;
      elementsVarProjection = getDefaultVarProjection(type, psi);
    } else {
      elementsVarProjection = parseVarProjection(
          op.model().valueType,
          op.itemsProjection(),
          elementsVarProjectionPsi,
          resolver,
          errors
      );
    }

    return new ReqDeleteMapModelProjection(
        op.model(),
        params,
        annotations,
        keyProjections,
        elementsVarProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  public static ReqDeleteListModelProjection parseListModelProjection(
      @NotNull OpDeleteListModelProjection op,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull UrlReqDeleteListModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @Nullable final UrlReqDeleteVarProjection elementsVarProjectionPsi = psi.getReqDeleteVarProjection();

    @NotNull final ReqDeleteVarProjection elementsVarProjection;
    if (elementsVarProjectionPsi == null) {
      @NotNull final Type type = op.model().elementType().type;
      elementsVarProjection = getDefaultVarProjection(type, psi);
    } else {
      elementsVarProjection = parseVarProjection(
          op.model().elementType,
          op.itemsProjection(),
          elementsVarProjectionPsi,
          resolver,
          errors
      );
    }

    return new ReqDeleteListModelProjection(
        op.model(),
        params,
        annotations,
        elementsVarProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  public static ReqDeletePrimitiveModelProjection parsePrimitiveModelProjection(
      @NotNull OpDeletePrimitiveModelProjection op,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull PsiElement locationPsi) throws PsiProcessingException {

    return new ReqDeletePrimitiveModelProjection(
        op.model(),
        params,
        annotations,
        EpigraphPsiUtil.getLocation(locationPsi)
    );

  }
}
